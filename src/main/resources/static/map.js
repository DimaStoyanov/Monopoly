ymaps.ready(init);
var myMap, myRectangle, myRectangle2, myRectangle3, myRectangle4, myRectangle5;

function init() {
    myMap = new ymaps.Map('map', {
        center: [55.674, 37.601],
        zoom: 11
    }, {
        searchControlProvider: 'yandex#search'
    });

    myRectangle = new ymaps.Rectangle([
        // Задаем координаты диагональных углов прямоугольника.
        [55.70, 37.55],
        [55.72, 37.53]
    ], null, {
        // Опции.
        // Цвет и прозрачность заливки.
        fillColor: '#7df9ff33',
        // Дополнительная прозрачность заливки..
        // Итоговая прозрачность будет не #33(0.2), а 0.1(0.2*0.5).
        fillOpacity: 0.8,
        // Цвет обводки.
        strokeColor: '#0000FF',
        fillMethod: 'stretch',
        fillImageHref: 'https://pp.userapi.com/c846419/v846419978/37d9/gVOIsb6FV1A.jpg',
        // Прозрачность обводки.
        strokeOpacity: 0.5,
        // Ширина линии.
        strokeWidth: 2,
        // Радиус скругления углов.
        // Данная опция принимается только прямоугольником.
        borderRadius: 6
    });

    myRectangle2 = new ymaps.Rectangle([
        [55.66, 37.60],
        [55.68, 37.62]
    ], null, {
        fillColor: '#7df9ff33',
        fillOpacity: 0.8,
        strokeColor: '#0000FF',
        fillMethod: 'stretch',
        fillImageHref: 'https://pp.userapi.com/c846419/v846419978/37f0/PXkX-6KioZs.jpg',
        strokeOpacity: 0.5,
        strokeWidth: 2,
        borderRadius: 6
    });


    myRectangle3 = new ymaps.Rectangle([
        [55.65, 37.55],
        [55.67, 37.53]
    ], null, {
        fillColor: '#7df9ff33',
        fillOpacity: 0.8,
        strokeColor: '#0000FF',
        fillMethod: 'stretch',
        fillImageHref: 'https://pp.userapi.com/c846419/v846419978/37d2/mQO76o1afHw.jpg',
        strokeOpacity: 0.5,
        strokeWidth: 2,
        borderRadius: 6
    });

    myRectangle4 = new ymaps.Rectangle([
        [55.60, 37.59],
        [55.62, 37.57]
    ], null, {
        fillColor: '#7df9ff33',
        fillOpacity: 0.8,
        strokeColor: '#0000FF',
        fillMethod: 'stretch',
        fillImageHref: 'https://pp.userapi.com/c846419/v846419978/37cb/WLLZYFaEjcg.jpg',
        strokeOpacity: 0.5,
        strokeWidth: 2,
        borderRadius: 6
    });

    myRectangle5 = new ymaps.Rectangle([
        [55.62, 37.66],
        [55.60, 37.69]
    ], null, {
        fillColor: '#7df9ff33',
        fillOpacity: 0.8,
        strokeColor: '#FF0000',
        fillMethod: 'stretch',
        fillImageHref: 'https://pp.userapi.com/c846419/v846419978/37b0/uNV-aWK8C7s.jpg',
        // 'https://pp.userapi.com/c834303/v834303410/dcfdd/tQAOSeI41oc.jpg',
        strokeOpacity: 0.5,
        strokeWidth: 4,
        borderRadius: 6
    });


    myRectangle5.events.add('click', function (e) {
        // Если меню метки уже отображено, то убираем его.
        if ($('#menu').css('display') === 'block') {
            $('#menu').remove();
        } else {
            // HTML-содержимое контекстного меню.
            var menuContent =
                '<div id="menu">\
            <div align="center"><input id="buysmth" type="submit" value="Buy"/></div>\
            <div align="center"><input id="passsmth" type="submit" value="Pass"/></div>\
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


    // Создаем круг.
    var myCircle = new ymaps.Circle([
        // Координаты центра круга.
        // [55.67, 37.62],
        // [55.70, 37.55],
        [55.71, 37.53],

        // Радиус круга в метрах.
        300
    ], {
        // Описываем свойства круга.
        // Содержимое балуна.
        balloonContent: '<img height="100px" src="https://pp.userapi.com/c841133/v841133895/1a3d4/TxeNVM5X_RA.jpg">\<' +
        'br><div align="center">Dmitriy Stoyanov</div>',
        // Содержимое хинта.
        hintContent: "Player1"
    }, {
        // Задаем опции круга.
        // Включаем возможность перетаскивания круга.
        // draggable: true,
        // Цвет заливки.
        // Последний байт (77) определяет прозрачность.
        // Прозрачность заливки также можно задать используя опцию "fillOpacity".
        iconImageHref: 'https://pp.userapi.com/c841133/v841133895/1a3d4/TxeNVM5X_RA.jpg',
        fillOpacity: 0.3,

        fillColor: "#DB709377",
        // Цвет обводки.
        strokeColor: "#391066",
        // Прозрачность обводки.
        strokeOpacity: 0.5,
        // Ширина обводки в пикселях.
        strokeWidth: 5

    });

    var myCircle2 = new ymaps.Circle([
        // Координаты центра круга.
        // [55.67, 37.62],
        // [55.70, 37.55],
        // [55.71, 37.55],
        [55.61, 37.69],

        // Радиус круга в метрах.
        300
    ], {
        // Описываем свойства круга.
        // Содержимое балуна.
        balloonContent: '<img height="100px" src="https://pp.userapi.com/c636620/v636620219/75c85/bzeG7tSUYdw.jpg">\<' +
        'br><div align="center">Konstantin Risov</div>',
        // Содержимое хинта.
        hintContent: "Player2"
    }, {
        // Задаем опции круга.
        // Включаем возможность перетаскивания круга.
        // draggable: true,
        // Цвет заливки.
        // Последний байт (77) определяет прозрачность.
        // Прозрачность заливки также можно задать используя опцию "fillOpacity".
        // iconImageHref: 'https://pp.userapi.com/c841133/v841133895/1a3d4/TxeNVM5X_RA.jpg',
        fillOpacity: 0.3,

        fillColor: "#DB709377",
        // Цвет обводки.
        strokeColor: "#5FA000",
        // Прозрачность обводки.
        strokeOpacity: 0.5,
        // Ширина обводки в пикселях.
        strokeWidth: 5

    });

    var balloonLayout = ymaps.templateLayoutFactory.createClass("", {}
    );

    var multiRoute = new ymaps.multiRouter.MultiRoute({
        referencePoints: [
            [55.60, 37.66],
            [55.61, 37.59]
        ]
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
        routeActiveStrokeColor: "#214212"

    });

    var multiRoute2 = new ymaps.multiRouter.MultiRoute({
        referencePoints: [
            [55.62, 37.69],
            [55.666, 37.622]

        ]
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
        routeActiveStrokeColor: "#0AAF3F"

    });

    var multiRoute3 = new ymaps.multiRouter.MultiRoute({
        referencePoints: [
            [55.68, 37.60],
            [55.70, 37.551]

        ]
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
        routeActiveStrokeColor: "#133E92"

    });

    var multiRoute5 = new ymaps.multiRouter.MultiRoute({
        referencePoints: [
            [55.62, 37.58],
            [55.65, 37.55]
        ]
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
        routeActiveStrokeColor: "#E61200"

    });

    var multiRoute4 = new ymaps.multiRouter.MultiRoute({
        referencePoints: [
            [55.699, 37.535],
            [55.67, 37.54]

        ]
    }, {
        balloonLayout: balloonLayout,
        pinIconFillColor: "#000088",
        pinActiveIconFillColor: "#B3B3B3",
        wayPointVisible: false,

        routeStrokeWidth: 0,
        // routeStrokeColor: "#000000",
        routeActiveStrokeWidth: 3,
        routeActiveStrokeColor: "#E63E92"

    });

    myMap.geoObjects
        .add(myRectangle)
        .add(myRectangle2)
        .add(myRectangle3)
        .add(myRectangle4)
        .add(myRectangle5)
        .add(myCircle)
        .add(myCircle2)
        .add(multiRoute)
        .add(multiRoute2)
        .add(multiRoute3)
        .add(multiRoute4)
        .add(multiRoute5);


}
