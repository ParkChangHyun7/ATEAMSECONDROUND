<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>서울시 공영주차장 지도</title>

    <!--  Kakao Maps SDK: autoload false로 설정 -->
    <script src="https://dapi.kakao.com/v2/maps/sdk.js?appkey=0756a2cf5e5556917df83822336fd9b5&autoload=false"></script>

    <style>
        #map {
            width: 100%;
            height: 400px;
            border: 1px solid #aaa;
        }
    </style>
</head>
<body>

    <h2>서울시 공영주차장 지도</h2>
    <div id="map"></div>

    <script>
        kakao.maps.load(function () {
            var container = document.getElementById('map');
            var options = {
                center: new kakao.maps.LatLng(37.5665, 126.9780), // 서울시청 좌표
                level: 3
            };
            var map = new kakao.maps.Map(container, options);

            var marker = new kakao.maps.Marker({
                map: map,
                position: new kakao.maps.LatLng(37.5665, 126.9780)
            });
        });
    </script>

</body>
</html>
