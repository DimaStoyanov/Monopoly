'use strict';
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');


var scoreTable = document.getElementById('score-table');

var game = null;
var selfInfo = null;
var stompClient = null;
var cellsRect = [];
var playersCircle = [];
var myMap = null;

var cellLength = 0.01;


var ownsBtn = document.getElementById('btn_owns');
var auctionBtn = document.getElementById('btn_auction');
var scoreBtn = document.getElementById('btn_score');
var offersBtn = document.getElementById('btn_offers');
var finishStepBtn = null;

var auctionContainer = document.getElementById('auction_container');
var offersContainer = document.getElementById('offers_container');
var ownsContainer = document.getElementById('owns_container');
var scoreContainer = document.getElementById('score_container');

var streetTable = $("#owns-table").get(0);
var offersList = $(".offers-list").get(0);
var noOfferTitle = $('#no_offers_frame');

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
var neutralColor = '#999966';

function errorHandler(error) {
    var resp = error.responseJSON;
    console.log(resp);
    alert(resp.message)
}

$.get('/player/game', function (gameId) {
    if (!gameId) {
        $(location).attr('href', '/');
    }
}).fail(errorHandler);

$.get('/player/info', function (data) {
    selfInfo = data;
}).fail(errorHandler);

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

setInterval(drawScoreTable, 5000);


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
        connectSocket();

        game = data;
        game.playersMap = {};


        game.players.forEach(function (item) {
            if (item.name === selfInfo.nickname) {
                selfInfo.profileId = selfInfo.id;
                selfInfo.id = item.id
            }
        });


        game.players.forEach(function (item) {
            game.playersMap[item.id] = item;
        });
        game.field.forEach(function (item) {
            var cell = buildRectangle(item.cellCoordinates, neutralColor, item.imgPath);
            myMap.geoObjects.add(cell);
            cellsRect.push(cell);
            var route = buildRoute(item.routeCoordinates, '#54d6f6');
            myMap.geoObjects.add(route);
        });

        drawScoreTable();
        drawFinishStepButton();
        drawShowButtons();
        drawBtnAutoFocus();
        var fullscreenControl = new ymaps.control.FullscreenControl();
        myMap.controls.add(fullscreenControl, {float: 'left'});
        updateCells();
        drawPlayers();
        updateTurnOfFrame();
        drawPlayers();
        drawStreetFrame();

        if (isYourTurn()) {
            finishStepBtn.enable();
        } else {
            finishStepBtn.disable();
        }

    }).fail(errorHandler);

}

function isYourTurn() {
    return game.turnOf.id === selfInfo.id;
}

function updateCells() {
    cellsRect.forEach(function (cellRect, index) {
        var canSell = false, canBuy = false, canPay = false;
        var cell = game.field[index];

        if (cell.owner) {
            cellRect.options.set('strokeColor', playersColors[cell.owner.order])
        } else {
            cellRect.options.set('strokeColor', neutralColor)
        }

        if (game.currentState === 'NOT_STARTED' || game.currentState === 'FINISHED') {
            setBalloon(cellRect, index, canBuy, canSell);
        }
        if (game.turnOf.id === selfInfo.id) {
            if (cell.owner) {
                if (cell.owner.id === selfInfo.id) {
                    canSell = true;
                } else if (game.currentState === 'NEED_TO_PAY_OWNER') {
                    canPay = true;
                }
            } else {
                if (game.currentState === 'CAN_BUY_STREET' && game.turnOf.position === cell.position) {
                    canBuy = true;
                }
            }
        }

        setBalloon(cellRect, index, canBuy, canSell, canPay)
    });

}

function drawPlayers() {
    playersCircle.forEach(function (value) {
        myMap.geoObjects.remove(value)
    });
    var playerInCells = {};


    for (var playerId in game.playersMap) {
        var player = game.playersMap[playerId];
        var position = player.position;
        if (!playerInCells[position]) {
            playerInCells[position] = []
        }
        playerInCells[position].push(player)
    }


    for (position in playerInCells) {
        var playerList = playerInCells[position];
        var startCoords = jQuery.extend(true, {}, game.field[position].cellCoordinates[0]);
        var endCoords = jQuery.extend(true, {}, game.field[position].cellCoordinates[1]);
        startCoords[1] -= cellLength / 5;
        endCoords[1] += cellLength / 5;
        startCoords[0] = endCoords[0];
        var useStart = true;
        playerList.forEach(function (player) {
            var circle = null;
            if (useStart) {
                startCoords[0] -= cellLength / 3;
                circle = buildCircle(startCoords, player, playersColors[player.order]);
                useStart = false;
            } else {
                endCoords[0] -= cellLength / 3;
                circle = buildCircle(endCoords, player, playersColors[player.order]);
                useStart = true;
            }
            myMap.geoObjects.add(circle);
            playersCircle.push(circle);
        });
    }
}

function showActivePlayer() {
    var position = game.turnOf.position;
    var coords = game.field[position].cellCoordinates[0];
    myMap.panTo(
        coords
    ).then(function () {
        myMap.setZoom(13, {duration: 500})
    }, function (reason) {
        alert(reason)
    }, this);
}

function showCurrentCellBalloon() {
    var position = game.turnOf.position;
    var cell = cellsRect[position];
    cell.balloon.open()
}

function drawStreetFrame() {
    streetTable.innerHTML = '';
    game.field.forEach(function (cell) {
        if (cell.type === 'STREET') {
            var row = streetTable.insertRow();

            var nameColumn = row.insertCell();
            nameColumn.innerHTML = cell.name;

            var costColumn = row.insertCell();
            costColumn.innerHTML = cell.cost;

            var ownerColumn = row.insertCell();
            ownerColumn.innerHTML = cell.owner ? cell.owner.name : 'No owner';
        }
    })


}


function startGame() {
    if (game.currentState === 'NOT_STARTED') {
        $.ajax({
            url: '/api/v1/game.start',
            type: 'PUT'
        }).fail(errorHandler)
    }
}