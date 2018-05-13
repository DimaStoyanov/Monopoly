'use strict';
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');


var scoreTable = document.getElementById('score-table');

var game = null;
var selfInfo = null;
var stompClient = null;
var cells = [];
var myMap = null;

var cellLength = 0.01;


var ownsBtn = document.getElementById('btn_owns');
var auctionBtn = document.getElementById('btn_auction');
var scoreBtn = document.getElementById('btn_score');
var offersBtn = document.getElementById('btn_offers');


var auctionContainer = document.getElementById('auction_container');
var offersContainer = document.getElementById('offers_container');
var ownsContainer = document.getElementById('owns_container');
var scoreContainer = document.getElementById('score_container');

function onFrameButtonClick(activeBtn, passiveBtn, activeFrame, passiveFrame) {
    activeBtn.style.opacity = 1;
    passiveBtn.style.opacity = 0.3;
    activeFrame.style.visibility = "visible";
    passiveFrame.style.visibility = "hidden";
}

ownsBtn.addEventListener('click', function () {
    return onFrameButtonClick(ownsBtn, auctionBtn, ownsContainer, auctionContainer)
});
auctionBtn.addEventListener('click', function () {
    onFrameButtonClick(auctionBtn, ownsBtn, auctionContainer, ownsContainer)
});
scoreBtn.addEventListener('click', function () {
    onFrameButtonClick(scoreBtn, offersBtn, scoreContainer, offersContainer)
});
offersBtn.addEventListener('click', function () {
    onFrameButtonClick(offersBtn, scoreBtn, offersContainer, scoreContainer)
});

var playersColors = ['#5d80ca', '#3cc72d', '#b52dc7', '#b52dc7'];


$.get('/player/game', function (gameId) {
    if (!gameId) {
        $(location).attr('href', '/');
    }
});

function connectSocket() {
    $.get('/player/info', function (data) {
            selfInfo = data;
            game.players.forEach(function (item) {
                if (item.name === selfInfo.nickname) {
                    selfInfo.id = item.id
                }
            });

            var socket = new SockJS('/lobby');
            stompClient = Stomp.over(socket);
            stompClient.connect({}, onConnected, onError);
            messageForm.addEventListener('submit', sendMessage, true);
        }
    );
}


function onConnected() {
    stompClient.subscribe('/topic/games/' + game.id, onMessageReceived);


    // Tell your username to the server
    stompClient.send("/app/games/" + game.id,
        {},
        JSON.stringify({
            type: 'JOIN',
            sendAt: new Date(),
            idFrom: selfInfo.id,
            avatarUrl: selfInfo.avatarUrl
        })
    );

    connectingElement.classList.add('hidden');

}


function onError() {
    connectingElement.textContent = 'Could not connect to WebSocket server. ' +
        'Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}


function onMessageReceived(payload) {

    var message = JSON.parse(payload.body);
    var messageElement = document.createElement('li');


    var player = game.playersMap[message.idFrom];

    if (message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = player.name + ' joined!';

    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = player.name + ' left!';


    } else if (message.type === 'CHANGE') {
        game.turnOf = game.playersMap[message.turnOf];

        message.gamersChange.forEach(function (player) {
            game.playersMap[player.id] = player;
        });

        var streetChange = message.streetChange;
        game.field[streetChange.position] = streetChange;

        messageElement.classList.add('event-message');
        message.content = '';
        message.changeDescriptions.forEach(function (item) {
            message.content += item;
            message.content += '\n';
        });

        updateTurnOfFrame();
    } else {
        messageElement.classList.add('chat-message');


        var avatarElement = document.createElement('img');
        avatarElement.src = player.avatarUrl;

        messageElement.appendChild(avatarElement);

        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(player.name);
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


function sendMessage(event) {
    var messageContent = messageInput.value.trim();
    if (messageContent && stompClient) {
        var chatMessage = {
            content: messageInput.value,
            idFrom: selfInfo.id,
            type: 'CHAT',
            sendAt: new Date()
        };
        stompClient.send("/app/games/" + game.id, {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}

function drawScoreTable() {
    scoreTable.innerHTML = '';
    for (var playerKey in game.playersMap) {
        var player = game.playersMap[playerKey];
        var row = scoreTable.insertRow();

        var avatarCell = row.insertCell();
        var avatar = new Image();
        avatar.src = player.avatarUrl;
        avatar.height = 40;
        avatarCell.appendChild(avatar);

        var nameCell = row.insertCell();
        nameCell.innerHTML = player.name;

        var scoreCell = row.insertCell();
        scoreCell.innerHTML = player.score;
    }
}

setInterval(drawScoreTable, 10000);


ymaps.ready(init);


function init() {
    myMap = new ymaps.Map('map', {
        center: [
            59.929732867914836,
            30.338352053926247
        ],
        zoom: 11,
        controls: ['zoomControl']
    }, {
        minZoom: 11,
        maxZoom: 14
    });


    $.get('/api/v1/game.get', function (data) {
        game = data;
        game.playersMap = {};

        connectSocket();
        game.players.forEach(function (item) {
            game.playersMap[item.id] = item;
        });
        game.field.forEach(function (item) {
            var cell = buildRectangle(item.cellCoordinates, '#999966', item.imgPath);
            myMap.geoObjects.add(cell);
            cells.push(cell);
            var route = buildRoute(item.routeCoordinates, '#54d6f6');
            myMap.geoObjects.add(route);
        });

        drawScoreTable();
        setBoundsChangeEvents();
        drawShowButtons();
        drawBtnAutoFocus();
        var fullscreenControl = new ymaps.control.FullscreenControl();
        myMap.controls.add(fullscreenControl, {float: 'left'});
        cells.forEach(addMenu);
        drawPlayers();
        updateTurnOfFrame();
    });


    function drawPlayers() {
        var playerInCells = {};
        game.players.forEach(function (player) {
            var position = player.position;
            if (playerInCells[player.position] === undefined) {
                playerInCells[position] = [];
            }
            playerInCells[position].push(player);

        });


        for (var position in playerInCells) {
            var playerList = playerInCells[position];
            var startCoords = game.field[position].cellCoordinates[0];
            var endCoords = game.field[position].cellCoordinates[1];
            startCoords[1] -= cellLength / 5;
            endCoords[1] += cellLength / 5;
            startCoords[0] = endCoords[0];
            var useStart = true;
            playerList.forEach(function (player) {
                var circle = null;
                if (useStart) {
                    startCoords[0] -= cellLength / 3;
                    circle = buildCircle(startCoords, player.avatarUrl, player.name,
                        playersColors[player.order], player.score);
                    useStart = false;
                } else {
                    endCoords[0] -= cellLength / 3;
                    circle = buildCircle(endCoords, player.avatarUrl, player.name,
                        playersColors[player.order], player.score);
                    useStart = true;
                }
                myMap.geoObjects.add(circle);
            });
        }
    }


}
