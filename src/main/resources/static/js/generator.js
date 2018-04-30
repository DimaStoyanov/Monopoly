ymaps.ready(init);

var images = null;

$.ajax({
    url: "/admin/images",
    error: function () {
        alert("Not allowed");
        $(location).attr('href', '/')
    },
    success: function (data) {
        images = data
    }
});


function init() {
    var center = [59.91, 30.33];
    var cellIndex = 0;


    var myMap = new ymaps.Map('map', {
        center: center,
        zoom: 12,
        controls: ['routePanelControl']
    }, {
        searchControlProvider: 'yandex#search'
    });

    var cells = [];
    var routeCount = 0;


    images.forEach(function (item) {
        var startCoords = [center[0] + Math.random() * 0.1,
            center[1] + Math.random() * 0.1];
        var cell = buildRectangle([
            startCoords,
            [startCoords[0] + 0.01, startCoords[1] + 0.01]
        ], item);
        var lastIndex = cells.push({
            imgPath: item,
            cell: cell
        }) - 1;
        addMenu(cell, cells[lastIndex]);
        myMap.geoObjects.add(cell)
    });


    function buildRectangle(coords, imgHref) {
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

    }


    function addMenu(rectangle, cell) {
        rectangle.events.add('click', function (e) {
            // Если меню метки уже отображено, то убираем его.
            if ($('#menu').css('display') === 'block') {
                $('#menu').remove();
            } else {
                // HTML-содержимое контекстного меню.
                var menuContent =
                    '<div id="menu">\
                <div align="center"><input id="setnext" value="Set next"/></div>\
                </div>';

                // Размещаем контекстное меню на странице
                $('body').append(menuContent);

                // Задаем позицию меню.
                $('#menu').css({
                    left: e.get('pagePixels')[0],
                    top: e.get('pagePixels')[1]
                });


                $('#menu').find('input[id="setnext"]').click(function () {
                    cell['position'] = cellIndex++;
                    // Удаляем контекстное меню.
                    $('#menu').remove();
                });
            }
        });

    }


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

    function sortCells() {
        cells.sort(function (a, b) {
            return !a.position ? b : !b.position ? a : a.position - b.position;
        })
    }


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

        var position = headerRow.insertCell();
        position.innerHTML = "Position";

        var path = headerRow.insertCell();
        path.innerHTML = "Image path";

        var cellCoords = headerRow.insertCell();
        cellCoords.innerHTML = "Cell coordinates";

        var routeCoords = headerRow.insertCell();
        routeCoords.innerHTML = "Route coordinates";

        sortCells();

        cells.forEach(function (item) {
            var row = table.insertRow();

            var position = row.insertCell();
            position.innerHTML = item.position;

            var path = row.insertCell();
            path.innerHTML = item.imgPath;

            var cellCoords = row.insertCell();
            cellCoords.innerHTML = item.cell.geometry.getCoordinates().join("\n");

            var routeCoords = row.insertCell();
            routeCoords.innerHTML = item.routeCoords.join("\n")

        })
    });

    var saveResultsButton = document.getElementById("btn_save_results");
    saveResultsButton.addEventListener('click', function () {

        sortCells();

        var reqBody = [];
        cells.forEach(function (value) {
            reqBody.push({
                position: value.position,
                imgPath: value.imgPath,
                cellCoordinates: value.cell.geometry.getCoordinates(),
                routeCoordinates: value.routeCoords
            })
        });
        $.ajax({
            url: '/admin/field',
            type: 'PUT',
            data: JSON.stringify(reqBody),
            // dataType: 'json',
            contentType: "application/json; charset=utf-8",
            success: function (data) {
                alert(data)
            },
            error: function (e) {
                alert(e.error)
            }
        })
    })


}
