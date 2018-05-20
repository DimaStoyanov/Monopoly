var autofocus = false;



function drawBtnAutoFocus() {
    var btnAutoFocus = new ymaps.control.Button({
            data: {
                content: "Auto focus",
                title: "When selected change center of map to show active player every step.\n" +
                "Otherwise change center of map only in your step"

            },
            options: {
                maxWidth: 200
            }
        }
    );


    btnAutoFocus.events.add(['select', 'deselect'], function () {
        autofocus = !autofocus;
    });

    btnAutoFocus.select();

    myMap.controls
        .add(btnAutoFocus, {float: 'left'})
}

function buildRectangle(coords, strokeColor, imgHref) {
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
        strokeOpacity: 0.7,
        // Ширина линии.
        strokeWidth: 3,
        // Радиус скругления углов.
        // Данная опция принимается только прямоугольником.
        borderRadius: 3
    });

}


function setBalloon(rectangle, index, canBuy, canSell, canPay) {
    var cell = game.field[index];
    var content = '<p>' + cell.name + '</p>';


    if (cell.type === 'STREET') {
        content += '<p>Current cost: M' + cell.cost + '</p>';
        if (cell.owner) {
            content += '<p>Owner: ' + cell.owner.name + '</p>';
        } else {
            content += '<p>No owner</p>'
        }
        if (canBuy) {
            content += '<button id="btn_buy" class="primary">Buy</button>'
        }
        if (canSell) {
            content += '<h3>Sell street</h3>';
            content += '<form id="form_offer" style="width: 130px; align-items: center; text-align: center;">';
            content += '<input style="width: 130px;" placeholder="Cost" id="cost" type="number" required>';
            content += '<select style="width: 130px; overflow-x: auto" id="buyer" required>';
            for (var playerId in game.playersMap) {
                var player = game.playersMap[playerId];
                if (player.id === selfInfo.id) {
                    continue;
                }

                content += '<option value="' + player.id + '">' + player.name + '</option>'
            }
            content += '</select>';
            content += '<button class="accent" id="btn_sell">Send offer</button>';
            content += '</form>'
        }
        if (canPay) {
            content += '<h3>Pay for rent</h3>';
            content += '<p>Rent price: M' + cell.cost + '</p>';
            content += '<button id="btn_pay" class="yellow">Pay</button>'
        }
    }


    rectangle.properties.set('balloonContent', content);

    $(rectangle).off();
    rectangle.events.add('balloonopen', function () {
        var btnBuy = $('#btn_buy');
        btnBuy.off();
        btnBuy.click(function () {
            console.log('Try to buy ' + cell.name);
            $.ajax({
                url: '/api/v1/street.buy',
                type: 'PUT'
            }).fail(errorHandler);
        });

        $('#form_offer').off();
        $(document).on('submit', '#form_offer', function (event) {
            console.log('Try to sell ' + cell.name);
            var cost = $('#cost').val();
            var buyer = $('#buyer').val();
            $.ajax({
                url: '/api/v1/street.sell-offer.send?buyer=' + buyer + '&cost=' + cost,
                type: 'PUT',
                success: function () {
                    alert("Offer sent");
                    rectangle.balloon.close();
                }
            }).fail(errorHandler);
            event.preventDefault();
        });

        var btnPay = $('#btn_pay');
        btnPay.off();
        btnPay.click(function () {
            $.ajax({
                url: '/api/v1/street.pay',
                type: 'PUT',
                success: function () {
                    alert('Rent successfully paid');
                    rectangle.balloon.close();
                }
            })
        })

    });


}


function buildCircle(coords, player, strokeColor) {
    // Создаем круг.
    return new ymaps.Circle([
        coords,
        // Радиус круга в метрах.
        200
    ], {
        // Описываем свойства круга.
        // Содержимое балуна.
        balloonContent: '<div style="text-align: center;">' +
        '<img height="100px" src=' + player.avatarUrl + '>' +
        '<p>' + player.name + '</p>' +
        '<p>Score: ' + player.score + '</p>' +
        '<p>Money: ' + player.money + '</p>',
        // Содержимое хинта.
        hintContent: player.name
    }, {
        // Задаем опции круга.
        // Включаем возможность перетаскивания круга.
        // draggable: true,
        // Цвет заливки.
        // Последний байт (77) определяет прозрачность.
        // Прозрачность заливки также можно задать используя опцию "fillOpacity".
        // fillOpacity: 0.,
        // fillColor: "#DB7377",
        fillImageHref: player.avatarUrl,
        // Цвет обводки.
        strokeColor: strokeColor,
        // Прозрачность обводки.
        strokeOpacity: 0.5,
        // Ширина обводки в пикселях.
        strokeWidth: 5

    });
}

var balloonLayout = null;
var buildRoute = function (coords, routeColor) {

    if (balloonLayout == null) {
        balloonLayout = ymaps.templateLayoutFactory.createClass("", {});
    }

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

function drawFinishStepButton() {
    finishStepBtn = new ymaps.control.Button({
            data: {
                content: "Finish step"
            },
            options: {
                maxWidth: 200
            }
        }
    );


    finishStepBtn.events.add(['select'], function () {
        $.ajax({
            url: '/api/v1/step.finish',
            type: 'PUT'
        }).fail(errorHandler);
        finishStepBtn.deselect();
    });

    myMap.controls.add(finishStepBtn, {float: 'left'});
}


function drawShowButtons() {

    function createListBoxLayout(id) {

        // Создадим собственный макет выпадающего списка.
        var ListBoxLayout = ymaps.templateLayoutFactory.createClass(
            "<button id='my-listbox-header' style='min-height: 18px' class='btn btn-success dropdown-toggle' data-toggle='dropdown'>" +
            "{{data.title}} <span class='caret'></span>" +
            "</button>" +
            // Этот элемент будет служить контейнером для элементов списка.
            // В зависимости от того, свернут или развернут список, этот контейнер будет
            // скрываться или показываться вместе с дочерними элементами.
            "<ul id=" + id +
            " class='dropdown-menu' role='menu' aria-labelledby='dropdownMenu'" +
            " style='display: {% if state.expanded %}block{% else %}none{% endif %};'></ul>", {

                build: function () {
                    // Вызываем метод build родительского класса перед выполнением
                    // дополнительных действий.
                    ListBoxLayout.superclass.build.call(this);

                    this.childContainerElement = $('#' + id).get(0);
                    // Генерируем специальное событие, оповещающее элемент управления
                    // о смене контейнера дочерних элементов.
                    this.events.fire('childcontainerchange', {
                        newChildContainerElement: this.childContainerElement,
                        oldChildContainerElement: null
                    });
                },

                // Переопределяем интерфейсный метод, возвращающий ссылку на
                // контейнер дочерних элементов.
                getChildContainerElement: function () {
                    return this.childContainerElement;
                },

                clear: function () {
                    // Заставим элемент управления перед очисткой макета
                    // откреплять дочерние элементы от родительского.
                    // Это защитит нас от неожиданных ошибок,
                    // связанных с уничтожением dom-элементов в ранних версиях ie.
                    this.events.fire('childcontainerchange', {
                        newChildContainerElement: null,
                        oldChildContainerElement: this.childContainerElement
                    });
                    this.childContainerElement = null;
                    // Вызываем метод clear родительского класса после выполнения
                    // дополнительных действий.
                    ListBoxLayout.superclass.clear.call(this);
                }
            });
        return ListBoxLayout;
    }

    function createListBoxItemLayout() {
        // Также создадим макет для отдельного элемента списка.
        return ymaps.templateLayoutFactory.createClass(
            "<li><a>{{data.content}}</a></li>"
        );
    }

    function createPlayersItems() {
        var listBoxItems = [];
        for (var playerKey in game.playersMap) {
            var player = game.playersMap[playerKey];
            listBoxItems.push(new ymaps.control.ListBoxItem({
                data: {
                    content: player.name,
                    playerId: player.id,
                    zoom: 13
                }
            }));
        }
        return listBoxItems;
    }

    function createCellsItems() {
        var listBoxItems = [];
        game.field.forEach(function (item) {
            listBoxItems.push(new ymaps.control.ListBoxItem({
                data: {
                    content: item.name,
                    position: item.position,
                    zoom: 13
                }
            }))
        });
        return listBoxItems
    }

    function createListBox(title, items, id) {
        // Теперь создадим список, содержащий 2 пункта.
        return new ymaps.control.ListBox({
            items: items,
            data: {
                title: title
            },
            options: {
                // С помощью опций можно задать как макет непосредственно для списка,
                layout: createListBoxLayout(id),
                // так и макет для дочерних элементов списка. Для задания опций дочерних
                // элементов через родительский элемент необходимо добавлять префикс
                // 'item' к названиям опций.
                itemLayout: createListBoxItemLayout()
            }
        })
    }

    function addEventActions(listBox, centerFunc) {
        listBox.events.add('click', function (e) {
            // Получаем ссылку на объект, по которому кликнули.
            // События элементов списка пропагируются
            // и их можно слушать на родительском элементе.
            var item = e.get('target');

            // Клик на заголовке выпадающего списка обрабатывать не надо.

            if (item !== listBox) {
                myMap.panTo(
                    centerFunc(item.data)
                ).then(function () {
                    myMap.setZoom(item.data.get('zoom'), {duration: 500})
                }, function (reason) {
                    alert(reason)
                }, this);
            }
        });
        listBox.collapse();
    }


    var playersListBox = createListBox('Show player', createPlayersItems(), 'playersCircle-listbox');
    var cellsListBox = createListBox('Show cell', createCellsItems(), 'cellsRect-listbox');


    addEventActions(playersListBox, function (item) {
        var player = game.playersMap[item.get('playerId')];
        var position = player.position;
        return game.field[position].cellCoordinates[0];
    });
    addEventActions(cellsListBox, function (item) {
        var position = item.get('position');
        return game.field[position].cellCoordinates[0];
    });


    myMap.controls
        .add(playersListBox, {float: 'left'})
        .add(cellsListBox, {float: 'left'})

}

var turnOfImg = $('#turnof-img').get(0);
var turnOfName = $('#turnof-name').get(0);

function updateTurnOfFrame() {
    turnOfImg.src = game.turnOf.avatarUrl;
    turnOfName.innerHTML = game.turnOf.name;
}



