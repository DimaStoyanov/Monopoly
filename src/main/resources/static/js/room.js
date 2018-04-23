'use strict';
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');


var tableInRoom = document.getElementById('room');
var friendNickname = document.getElementById('nickname');
var startButton = document.getElementById('btn_start');

var selfInfo = null;
var hostId = null;
var roomId = null;
var stompClient = null;
var inRoom = [];

$.get('/player/info', function (data) {
        selfInfo = data;
        $.get('/player/room', function (data) {
            roomId = data;
            if (!roomId) {
                $(location).attr('href', '/');
                return
            }
            init()
        });
    }
);


function init() {
    var socket = new SockJS('/lobby');
    socket.onclose = onClose;
    stompClient = Stomp.over(socket);
    stompClient.connect({}, onConnected, onError);

    getAndDrawFriends();
    setInterval(getAndDrawFriends, 10000);

    $.get('/rooms/' + roomId + '/participants', function (players) {
        players.forEach(addInRoom)
    });


    messageForm.addEventListener('submit', sendMessage, true);
}


function onAddFriendClick() {
    $.ajax({
        url: '/player/add_friend?nickname=' + friendNickname.value,
        type: 'PUT',
        success: function (data) {
            alert(data)
        }
    }).fail(function () {
        alert("Player not found")
    });
    friendNickname.value = ''
}


function getAndDrawFriends() {
    $.get('/player/friends', function (friends) {
        var table = document.getElementById('friends');
        table.innerHTML = '';
        friends.forEach(function (item) {
            var row = table.insertRow();

            var img = new Image();
            img.src = item.avatarUrl;
            img.height = 60;
            var avatarCell = row.insertCell();
            avatarCell.appendChild(img);

            var nicknameCell = row.insertCell();
            nicknameCell.innerHTML = item.nickname;

            var onlineStatCell = row.insertCell();
            onlineStatCell.innerHTML = item.status;

            var inviteCell = row.insertCell();
            if (item.status === "Online" || item.status === "In room"
                && inRoom.indexOf(item.nickname) > -1) {
                var inviteButton = document.createElement("BUTTON");
                inviteButton.className = 'green';
                inviteButton.innerHTML = "Invite";
                inviteButton.addEventListener("click", function () {
                    stompClient.send('/app/invite', {}, JSON.stringify({
                        from: selfInfo.nickname,
                        to: item.id,
                        roomId: roomId
                    }));
                    alert("Invited");
                });

                inviteCell.appendChild(inviteButton);
            }

        });

    });

}

function deleteFromRoomTableIfExist(item) {
    for (var i = 0, row; row = tableInRoom.rows[i]; i++) {
        if (row.cells[1].innerHTML === item.nickname) {
            tableInRoom.deleteRow(i);
            break;
        }
    }

}

function addInRoom(playerInfo) {
    deleteFromRoomTableIfExist(playerInfo);
    var row = tableInRoom.insertRow();

    var img = new Image();
    img.src = playerInfo.avatarUrl;
    img.height = 80;
    var avatarCell = row.insertCell();
    avatarCell.appendChild(img);

    var nicknameCell = row.insertCell();
    nicknameCell.innerHTML = playerInfo.nickname;


    var kickCell = row.insertCell();
    if (playerInfo.id === selfInfo.id) {
        var leaveButton = document.createElement("BUTTON");
        leaveButton.className = 'accent';
        leaveButton.innerHTML = "Leave";
        leaveButton.addEventListener('click', function () {
            $(location).attr('href', '/')
        });
        kickCell.appendChild(leaveButton);
    } else if (hostId === selfInfo.id) {
        var kickButton = document.createElement("BUTTON");
        kickButton.className = 'accent';
        kickButton.innerHTML = "Kick";
        kickButton.addEventListener("click", function () {
            if (confirm("Kick " + playerInfo.nickname + "?")) {
                var msg = {
                    nickname: playerInfo.nickname,
                    type: 'KICK',
                    playerId: playerInfo.id,
                    sendAt: new Date()
                };
                stompClient.send('/app/rooms/' + roomId, {}, JSON.stringify(msg))
            }
        });

        kickCell.appendChild(kickButton);
    }

}


function onConnected() {
    stompClient.subscribe('/topic/rooms/' + roomId, onMessageReceived);
    stompClient.subscribe('/topic/invite/' + selfInfo.id, function (payload) {
        var msg = JSON.parse(payload.body);
        if (confirm("Player " + msg.from + " invite you. Join?")) {
            $.ajax({
                url: '/player/room?roomId=' + msg.roomId,
                type: 'PUT',
                success: function () {
                    $(location).attr('href', '/room.html');
                }
            })

        }
    });

    // Tell your username to the server
    stompClient.send("/app/rooms/" + roomId,
        {},
        JSON.stringify({
            nickname: selfInfo.nickname,
            type: 'JOIN',
            sendAt: new Date(),
            playerId: selfInfo.id,
            avatarUrl: selfInfo.avatarUrl
        })
    );

    stompClient.send('/app/status', {}, JSON.stringify({
        place: "ROOM",
        status: "ONLINE",
        playerId: selfInfo.id
    }));


    connectingElement.classList.add('hidden');
    setTimeout(function () {

        $.get('/rooms/' + roomId + '/host', function (data) {
            hostId = data;
            if (hostId === selfInfo.id) {
                startButton.removeAttribute("hidden");
                startButton.addEventListener('click', function () {
                    $.post('/rooms/' + roomId + '/start', function (gameId) {

                        stompClient.send('/app/rooms/' + roomId, {}, JSON.stringify({
                            nickname: selfInfo.nickname,
                            type: 'START',
                            playerId: selfInfo.id,
                            sendAt: new Date(),
                            gameId: gameId
                        }))

                    })
                })

            }
        });
    }, 1000)

}


function onError() {
    connectingElement.textContent = 'Could not connect to WebSocket server. ' +
        'Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}


function onClose() {
    init();
}

function sendMessage(event) {
    var messageContent = messageInput.value.trim();
    if (messageContent && stompClient) {
        var chatMessage = {
            nickname: selfInfo.nickname,
            content: messageInput.value,
            playerId: selfInfo.id,
            type: 'CHAT',
            avatarUrl: selfInfo.avatarUrl,
            sendAt: new Date()
        };
        stompClient.send("/app/rooms/" + roomId, {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}


function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);
    var messageElement = document.createElement('li');

    if (message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.nickname + ' joined!';
        var playerInfo = {
            id: message.playerId,
            nickname: message.nickname,
            avatarUrl: message.avatarUrl
        };
        inRoom.push(playerInfo.nickname);
        addInRoom(playerInfo)

    } else if (message.type === 'LEAVE') {
        var index = inRoom.indexOf(message.nickname);
        if (index > -1) {
            deleteFromRoomTableIfExist(message);
            messageElement.classList.add('event-message');
            message.content = message.nickname + ' left!';
            inRoom.splice(index, 1)
        }

    } else if (message.type === 'KICK') {
        index = inRoom.indexOf(message.nickname);
        if (message.playerId === selfInfo.id) {
            alert("You was kicked from room");
            $(location).attr('href', '/');
        }
        if (index > -1) {
            inRoom.splice(index, 1)
        }
        messageElement.classList.add('event-message');
        message.content = message.nickname + ' was kicked!';
        deleteFromRoomTableIfExist(message);
    } else if (message.type === 'START') {
        $.ajax({
            url: '/player/game?gameId=' + message.gameId,
            type: 'PUT',
            success: function () {
                $(location).attr('href', '/game.html');
            }

        });
        messageElement.classList.add('event-message');
        message.content = message.nickname + ' started game!';
    } else {
        messageElement.classList.add('chat-message');


        var avatarElement = document.createElement('img');
        avatarElement.src = message.avatarUrl;

        messageElement.appendChild(avatarElement);

        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(message.nickname);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
    }

    var textElement = document.createElement('p');
    var messageText = document.createTextNode(message.content);
    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}


