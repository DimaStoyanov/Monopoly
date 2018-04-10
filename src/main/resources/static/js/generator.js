ymaps.ready(init);

function init() {
    var center = [55.674, 37.601];

    var myMap = new ymaps.Map('map', {
        center: center,
        zoom: 11,
        controls: ['routePanelControl']
    }, {
        searchControlProvider: 'yandex#search'
    });

    var cells = [];
    var routeCount = 0;

    $.ajax({
        url: "/admin/images",
        error: function () {
            alert("Need admin role");
            $(location).attr('href', '/admin/upgrade')
        },
        success: function (data) {
            data.forEach(function (item) {
                var startCoords = [center[0] + Math.random() * 0.2,
                    center[1] + Math.random() * 0.2];
                var cell = buildRectangle([
                    startCoords,
                    [startCoords[0] + 0.02, startCoords[1] + 0.02]
                ], item);
                cells.push({
                    imgPath: item,
                    cell: cell
                });
                myMap.geoObjects.add(cell)
            })

        }
    });

    var buildRectangle = function (coords, imgHref) {
        return new ymaps.Rectangle(coords, null, {
            // Опции.
            // Цвет и прозрачность заливки.
            fillColor: '#7df9ff33',
            // Дополнительная прозрачность заливки..
            // Итоговая прозрачность будет не #33(0.2), а 0.1(0.2*0.5).
            fillOpacity: 0.8,
            // Цвет обводки.
            // strokeColor: strokeColor,
            fillMethod: 'stretch',
            fillImageHref: imgHref,
            // Прозрачность обводки.
            strokeOpacity: 0.5,
            draggable: true,
            // Ширина линии.
            strokeWidth: 2,
            // Радиус скругления углов.
            // Данная опция принимается только прямоугольником.
            borderRadius: 6
        });

    };


    var balloonLayout = ymaps.templateLayoutFactory.createClass("", {}
    );

    var buildRoute = function (coords) {
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
            routeActiveStrokeColor: getRandomColor()

        });
    };

    function getRandomColor() {
        var letters = '0123456789ABCDEF';
        var color = '#';
        for (var i = 0; i < 6; i++) {
            color += letters[Math.floor(Math.random() * 16)];
        }
        return color;
    }


    var control = myMap.controls.get('routePanelControl');

    // Зададим состояние панели для построения машрутов.
    control.routePanel.state.set({
        // Тип маршрутизации.
        // type: 'masstransit',
        // Выключим возможность задавать пункт отправления в поле ввода.
        fromEnabled: true,
        // Адрес или координаты пункта отправления.
        // from: 'Москва, Льва Толстого 16',
        // Включим возможность задавать пункт назначения в поле ввода.
        toEnabled: true
        // Адрес или координаты пункта назначения.
        //to: 'Петербург'
    });


    var addRouteButton = document.getElementById("btn_add_route");
    addRouteButton.addEventListener('click', function () {
        var from = control.routePanel.state.get('from');
        var to = control.routePanel.state.get('to');
        control.routePanel.state.set('from', '');
        control.routePanel.state.set('to', '');

        var coords = [from, to];
        var route = buildRoute(coords);
        myMap.geoObjects.add(route);
        cells[routeCount]["routeCoords"] = coords;
        routeCount++;


    });

    var table = document.getElementById("result_table");
    var showTableButton = document.getElementById("btn_show_table");
    showTableButton.addEventListener('click', function () {
        table.innerHTML = '';

        var headerRow = table.insertRow();

        var path = headerRow.insertCell();
        path.innerHTML = "Image path";

        var cellCoords = headerRow.insertCell();
        cellCoords.innerHTML = "Cell coordinates";

        var routeCoords = headerRow.insertCell();
        routeCoords.innerHTML = "Route coordinates";


        cells.forEach(function (item) {
            var row = table.insertRow();

            var path = row.insertCell();
            path.innerHTML = item.imgPath;

            var cellCoords = row.insertCell();
            cellCoords.innerHTML = item.cell.geometry.getCoordinates().join("\n");

            var routeCoords = row.insertCell();
            routeCoords.innerHTML = item.routeCoords.join("\n")

        })
    })


}
