ymaps.ready(init);

function init() {
    var myMap = new ymaps.Map('map', {
        center: [55.674, 37.601],
        zoom: 11
    }, {
        searchControlProvider: 'yandex#search'
    });

    var cells = [];

    $.get('/player/game', function (gameId) {
        $.get('/api/game/' + gameId, function (game) {
            game.field.forEach(function (item) {
                var cell = buildRectangle(item.cellCoordinates, '#999966', item.imgPath);
                myMap.geoObjects.add(cell);
                cells.push(cell);
                var route = buildRoute(item.routeCoordinates, '#99ff99');
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

            // alert(e.get('pagePixels'))
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

    var multiRoute = buildRoute([[55.60, 37.66], [55.61, 37.59]], '#214212'),
        multiRoute2 = buildRoute([[55.62, 37.69], [55.666, 37.622]], '#0AAF3F'),
        multiRoute3 = buildRoute([[55.68, 37.60], [55.70, 37.551]], '#133E92'),
        multiRoute4 = buildRoute([[55.699, 37.535], [55.67, 37.54]], '#E63E92'),
        multiRoute5 = buildRoute([[55.62, 37.58], [55.65, 37.55]], '#E61200');

    // myMap.geoObjects
    //     .add(myRectangle)
    //     .add(myRectangle2)
    //     .add(myRectangle3)
    //     .add(myRectangle4)
    //     .add(myRectangle5)
    //     .add(myCircle)
    //     .add(myCircle2)
    //     .add(multiRoute)
    //     .add(multiRoute2)
    //     .add(multiRoute3)
    //     .add(multiRoute4)
    //     .add(multiRoute5);


}
