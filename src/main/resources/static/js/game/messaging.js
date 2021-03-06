function connectSocket() {
    var socket = new SockJS('/lobby');

    stompClient = Stomp.over(socket);
    stompClient.connect({}, onConnected, onError);
    messageForm.addEventListener('submit', sendMessage, true);
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
            profileId: selfInfo.profileId,
            avatarUrl: selfInfo.avatarUrl
        })
    );

    connectingElement.classList.add('hidden');
    setTimeout(startGame(), 2000)

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


    } else if (message.type === 'CHANGE' || message.type === 'FINISH') {
        if (currentBalloon) {
            currentBalloon.close();
        }

        var gameChange = message.gameChange;
        game.currentState = gameChange.currentState;

        if (game.currentState === "FINISHED") {
            finishStepBtn.disable();
        }


        gameChange.gamersChange.forEach(function (player) {
            game.playersMap[player.id] = player;
        });
        drawPlayers();

        if (gameChange.turnOf) {
            game.turnOf = game.playersMap[gameChange.turnOf];
            updateTurnOfFrame();
            clearOffers()
        } else {
            game.turnOf = game.playersMap[game.turnOf.id]
        }

        if (game.turnOf.id === selfInfo.id) {
            finishStepBtn.enable();
        } else {
            finishStepBtn.disable();
        }

        var streetChanges = gameChange.streetChanges;
        streetChanges.forEach(function (streetChange) {
            game.field[streetChange.position] = streetChange;
        });

        updateCells();
        drawStreetFrame();

        if (message.cancelledOffersRqId) {
            for (var offerId in message.cancelledOffersRqId) {
                removeOffer(offerId)
            }
        }

        if (gameChange.turnOf) {
            if (autofocus || isYourTurn()) {
                showActivePlayer(isYourTurn());
            }
        }

        gameChange.changeDescriptions.forEach(function (descr) {
            addGameChangeMessage(descr)
        });


        setTimeout(function () {
            if (game.currentState === 'FINISHED' && confirm('Game is finished. Do you want to leave?')) {
                $(location).attr('href', '/');
            }
        }, 1000);
        return

    } else if (message.type === 'OFFER') {
        if (message.receiverId === selfInfo.id) {
            $.ajax({
                url: '/api/v1/street.sell-offer.get?rqId=' + message.offerRqId,
                type: 'GET',
                success: addOffer
            }).fail(errorHandler)
        }
        return
    } else if (message.type === 'DECLINE_OFFER') {
        if (message.receiverId === selfInfo.id) {
            messageElement.classList.add('event-message');
            var buyer = game.playersMap[message.idFrom];
            // TODO Show details of accepted/declined offers in offers tab
            var description = buyer.name + ' declined your offer';
            if (message.content) {
                description += ' with comment: "' + message.content + '"'
            }
            message.content = description;
        } else return
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

function addGameChangeMessage(text) {
    var messageElement = document.createElement('li');
    messageElement.classList.add('event-message');

    var textElement = document.createElement('p');
    var messageText = document.createTextNode(text);
    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;

}

function clearOffers() {
    while (offersList.firstChild) {
        offersList.removeChild(offersList.firstChild);
    }
    noOfferTitle.css({visibility: 'inherit'});
    scoreBtn.click()
}

function removeOffer(offerId) {
    var offerItem = offers[offerId];
    if (offerItem) {
        offerItem.remove();
        if (offersList.childElementCount === 0) {
            noOfferTitle.css({visibility: 'inherit'});
            scoreBtn.click()
        }
    }
}

function addOffer(offer) {

    var offerItem = document.createElement('div');

    offerItem.className = 'offer-item';
    var offerDescription = document.createElement('h4');

    var seller = game.playersMap[offer.sellerId];
    var cost = offer.cost;
    var streetName = game.field[offer.streetPosition].name;
    offerDescription.innerHTML = '<i>' + seller.name + '</i> offers to buy <b>' +
        streetName + '</b> for <b>' + cost + '</b>';
    offerItem.appendChild(offerDescription);

    var acceptBtn = document.createElement('button');
    acceptBtn.className = 'primary';
    acceptBtn.innerHTML = 'Accept';
    $(acceptBtn).css('margin-right', '5px');
    offersBtn.click();
    noOfferTitle.css({visibility: 'hidden'});

    acceptBtn.addEventListener('click', function () {
        $.ajax({
            url: '/api/v1/street.sell-offer.accept?rqId=' + offer.rqId,
            type: 'PUT'
        }).fail(errorHandler)
    });
    offerItem.appendChild(acceptBtn);


    var declineBtn = document.createElement('button');
    declineBtn.className = 'accent';
    declineBtn.innerHTML = 'Decline';
    $(declineBtn).css('margin-left', '5px');
    declineBtn.addEventListener('click', function () {
        $.ajax({
            url: '/api/v1/street.sell-offer.decline?rqId=' + offer.rqId,
            type: 'PUT',
            success: function () {
                offerItem.remove();
                if (offersList.childElementCount === 0) {
                    noOfferTitle.css({visibility: 'inherit'});
                    scoreBtn.click()
                }
            }
        }).fail(errorHandler)
    });
    offerItem.appendChild(declineBtn);

    var offersDelimiter = document.createElement('div');
    offersDelimiter.className = 'chat-header small-header';
    offerItem.appendChild(offersDelimiter);

    offers[offer.rqId] = offerItem;
    offersList.appendChild(offerItem);
}

function showPB() {
    var progress = 90;
    var pb = $('.progress-bar');
    pb.removeClass('progress-bar-danger');
    var id = setInterval(function () {
        progress--;
        if (progress === 0)
            clearInterval(id);
        pb.css({width: (Math.floor(progress * 100 / 90)) + '%'});
        if (progress < 20) {
            pb.html('');
            pb.addClass('progress-bar-danger')
        } else {
            var m = Math.floor(progress / 60);
            var s = progress % 60;
            s = Math.floor(s / 10) === 0 ? '0' + s : s;
            pb.html(m + ':' + s)
        }
    }, 1000)
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

