'use strict';
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');


var scoreTable = document.getElementById('score-table');

var game = null;
var playersMap = {};
var selfInfo = null;
var stompClient = null;
var cells = [];

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

    stompClient.send('/app/status', {}, JSON.stringify({
        place: "GAME",
        status: "ONLINE",
        playerId: selfInfo.id
    }));


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


    var player = playersMap[message.idFrom];

    if (message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = player.name + ' joined!';

    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = player.name + ' left!';


    } else if (message.type === 'STEP') {
        messageElement.classList.add('event-message');
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
    game.players.forEach(function (player) {
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
    })
}

drawScoreTable();
setInterval(drawScoreTable, 10000);


ymaps.ready(init);

function init() {
    var myMap = new ymaps.Map('map', {
        center: [55.675, 37.658],
        zoom: 12
    }, {
        searchControlProvider: 'yandex#search'
    });


    $.get('/player/game', function (gameId) {
        if (!gameId) {
            $(location).attr('href', '/');
            return;
        }
        $.get('/api/game/' + gameId, function (data) {
            game = data;
            connectSocket();
            game.players.forEach(function (item) {
                playersMap[item.id] = item;
            });
            game.field.forEach(function (item) {
                var cell = buildRectangle(item.cellCoordinates, '#999966', item.imgPath);
                myMap.geoObjects.add(cell);
                cells.push(cell);
                var route = buildRoute(item.routeCoordinates, '#54d6f6');
                myMap.geoObjects.add(route);
            })
        })
    });

    var buildRectangle = function (coords, strokeColor, imgHref) {
        return new ymaps.Rectangle(coords, null, {
            // Опции.
            // Цвет и прозрачность заливки.
            fillColor: '#7df9ff33',
            // Дополнительная прозрачность заливки..
            // Итоговая прозрачность будет не #33(0.2), а 0.1(0.2*0.5).
            fillOpacity: 0.8,
            // Цвет обводки.
            strokeColor: strokeColor,
            fillMethod: 'stretch',
            fillImageHref: imgHref,
            // Прозрачность обводки.
            strokeOpacity: 0.5,
            // Ширина линии.
            strokeWidth: 2,
            // Радиус скругления углов.
            // Данная опция принимается только прямоугольником.
            borderRadius: 6
        });

    };

    var myRectangle = buildRectangle([[55.70, 37.55], [55.72, 37.53]], '#0000FF',
        'game/images/arbat.png'),
        myRectangle2 = buildRectangle([[55.66, 37.60], [55.68, 37.62]], '#0000FF',
            'https://pp.userapi.com/c846419/v846419978/37f0/PXkX-6KioZs.jpg'),
        myRectangle3 = buildRectangle([[55.65, 37.55], [55.67, 37.53]], '#0000FF',
            'https://pp.userapi.com/c846419/v846419978/37d2/mQO76o1afHw.jpg'),
        myRectangle4 = buildRectangle([[55.60, 37.59], [55.62, 37.57]], '#0000FF',
            'https://pp.userapi.com/c846419/v846419978/37cb/WLLZYFaEjcg.jpg'),
        myRectangle5 = buildRectangle([[55.62, 37.66], [55.60, 37.69]], '#FF0000',
            'https://pp.userapi.com/c846419/v846419978/37b0/uNV-aWK8C7s.jpg');

    myRectangle5.events.add('click', function (e) {
        // Если меню метки уже отображено, то убираем его.
        if ($('#menu').css('display') === 'block') {
            $('#menu').remove();
        } else {
            // HTML-содержимое контекстного меню.
            var menuContent =
                '<div id="menu">\
            <div align="center"><input id="buysmth" value="Buy"/></div>\
            <div align="center"><input id="passsmth" value="Pass"/></div>\
            </div>';

            // Размещаем контекстное меню на странице
            $('body').append(menuContent);

            // Задаем позицию меню.
            $('#menu').css({
                left: e.get('pagePixels')[0],
                top: e.get('pagePixels')[1]
            });


            $('#menu').find('input[id="buysmth"]').click(function () {
                alert("Buy");
                // Удаляем контекстное меню.
                $('#menu').remove();
            });
            $('#menu').find('input[id="passsmth"]').click(function () {
                alert("Pass");
                // Удаляем контекстное меню.
                $('#menu').remove();
            });
        }
    });


    myMap.events.add('boundschange', function () {
        if ($('#menu').css('display') === 'block') {
            $('#menu').remove()
        }
    });

    myMap.events.add("click", function () {
        if ($('#menu').css('display') === 'block') {
            $('#menu').remove()
        }
    });

    var buildCircle = function (coords, imgHref, name, hintContent, strokeColor) {
        // Создаем круг.
        return new ymaps.Circle([
            coords,
            // Радиус круга в метрах.
            300
        ], {
            // Описываем свойства круга.
            // Содержимое балуна.
            balloonContent: '<img height="100px" src=' + imgHref + '>\<' +
            'br><div align="center">' + name + '</div>',
            // Содержимое хинта.
            hintContent: hintContent
        }, {
            // Задаем опции круга.
            // Включаем возможность перетаскивания круга.
            // draggable: true,
            // Цвет заливки.
            // Последний байт (77) определяет прозрачность.
            // Прозрачность заливки также можно задать используя опцию "fillOpacity".
            fillOpacity: 0.3,
            fillColor: "#DB7377",
            // Цвет обводки.
            strokeColor: strokeColor,
            // Прозрачность обводки.
            strokeOpacity: 0.5,
            // Ширина обводки в пикселях.
            strokeWidth: 5

        });
    };


    // Создаем круг.
    var myCircle = buildCircle([55.71, 37.53], "https://pp.userapi.com/c841133/v841133895/1a3d4/TxeNVM5X_RA.jpg",
        "Dmitriy Stoyanov", "Player1", "#391066"),
        myCircle2 = buildCircle([55.61, 37.69], "https://pp.userapi.com/c636620/v636620219/75c85/bzeG7tSUYdw.jpg",
            "Konstantin Risov", "Player2", "#5FA000");


    var balloonLayout = ymaps.templateLayoutFactory.createClass("", {}
    );

    var buildRoute = function (coords, routeColor) {
        return new ymaps.multiRouter.MultiRoute({
            referencePoints: coords
        }, {
            wayPointFinishIconImageSize: [1, 1],
            pinVisble: false,
            balloonLayout: balloonLayout,
            // balloonPanelMaxMapArea: 0
            pinIconFillColor: "#000088",
            pinActiveIconFillColor: "#B3B3B3",
            wayPointVisible: false,

            routeStrokeWidth: 0,
            routeStrokeColor: "#000000",
            routeActiveStrokeWidth: 3,
            routeActiveStrokeColor: routeColor

        });
    };


}
