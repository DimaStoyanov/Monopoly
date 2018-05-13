var autofocus = false;

var panOptions = {
    // flying: true
};


function setBoundsChangeEvents() {
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

}


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
        strokeOpacity: 0.5,
        // Ширина линии.
        strokeWidth: 2,
        // Радиус скругления углов.
        // Данная опция принимается только прямоугольником.
        borderRadius: 6
    });

}

function addMenu(rectangle, index) {
    rectangle.events.add('click', function (e) {
        if ($('#menu').css('display') === 'block') {
            $('#menu').remove();
        } else {
            // HTML-содержимое контекстного меню.
            var menuContent =
                '<div id="menu">\
            <div align="center"><input id="next" value="Next cell"/></div>\
            <div align="center"><input id="prev" value="Previous cell"/></div>\
            </div>';

            // Размещаем контекстное меню на странице
            $('body').append(menuContent);

            // Задаем позицию меню.
            $('#menu').css({
                left: e.get('pagePixels')[0],
                top: e.get('pagePixels')[1]
            });


            $('#menu').find('input[id="next"]').click(function () {
                var nextCell = cells[getNextIndexSafely(index)];
                var coords = nextCell.geometry.getCoordinates()[0];
                myMap.panTo(coords, panOptions)
                    .then(function () {
                        myMap.setZoom(13, {duration: 500})
                    }, function (reason) {
                        alert(reason)
                    }, this);
                $('#menu').remove();
            });
            $('#menu').find('input[id="prev"]').click(function () {
                var prevCell = cells[getPrevIndexSafely(index)];
                var coords = prevCell.geometry.getCoordinates()[0];
                myMap.panTo(coords, panOptions)
                    .then(function () {
                        myMap.setZoom(13, {duration: 500})
                    }, function (reason) {
                        alert(reason)
                    }, this);
                $('#menu').remove();
            });
        }
    });


    function getPrevIndexSafely(index) {
        return index - 1 < 0 ? cells.length - 1 : index - 1;
    }

    function getNextIndexSafely(index) {
        return index + 1 >= cells.length ? 0 : index + 1;
    }

}


function buildCircle(coords, imgHref, name, strokeColor, score) {
    // Создаем круг.
    return new ymaps.Circle([
        coords,
        // Радиус круга в метрах.
        200
    ], {
        // Описываем свойства круга.
        // Содержимое балуна.
        balloonContent: '<img height="100px" src=' + imgHref + '>\<' +
        'br><div align="center">' + name + '</div>' +
        '<br><div align="center">Score: ' + score + '</div>',
        // Содержимое хинта.
        hintContent: name
    }, {
        // Задаем опции круга.
        // Включаем возможность перетаскивания круга.
        // draggable: true,
        // Цвет заливки.
        // Последний байт (77) определяет прозрачность.
        // Прозрачность заливки также можно задать используя опцию "fillOpacity".
        // fillOpacity: 0.,
        // fillColor: "#DB7377",
        fillImageHref: imgHref,
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
            var coords = game.field[player.position].cellCoordinates[0];
            listBoxItems.push(new ymaps.control.ListBoxItem({
                data: {
                    content: player.name,
                    center: coords,
                    zoom: 13
                }
            }));
        }
        return listBoxItems;
    }

    function createCellsItems() {
        var listBoxItems = [];
        game.field.forEach(function (item) {
            var coords = item.cellCoordinates[0];
            listBoxItems.push(new ymaps.control.ListBoxItem({
                data: {
                    content: item.name,
                    center: coords,
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

    function addEventActions(listBox) {
        listBox.events.add('click', function (e) {
            // Получаем ссылку на объект, по которому кликнули.
            // События элементов списка пропагируются
            // и их можно слушать на родительском элементе.
            listBox.collapse();
            var item = e.get('target');
            // Клик на заголовке выпадающего списка обрабатывать не надо.
            if (item !== listBox) {
                myMap.panTo(
                    item.data.get('center'),
                    item.data.get('zoom')
                ).then(function () {
                    myMap.setZoom(13, {duration: 500})
                }, function (reason) {
                    alert(reason)
                }, this);
            }
        });
    }


    var playersListBox = createListBox('Show player', createPlayersItems(), 'players-listbox');
    var cellsListBox = createListBox('Show cell', createCellsItems(), 'cells-listbox');


    addEventActions(playersListBox);
    addEventActions(cellsListBox);


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



