<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>서울시 공영주차장 지도</title>

  <!-- Kakao Maps JavaScript API (JavaScript 키 사용, autoLoad=true) -->
  <script src="https://dapi.kakao.com/v2/maps/sdk.js?appkey=aab2edfe9eec692e184054a28765cd1e&autoload=true"></script>

  <style>
    #map {
      width: 100%;
      height: 500px;
      border: 1px solid #aaa;
    }
    #controls {
      margin-top: 10px;
    }
    button {
      padding: 5px 10px;
      margin-right: 5px;
      font-weight: bold;
      cursor: pointer;
    }
  </style>
</head>
<body>

  <h2>서울시 공영주차장 지도</h2>

  <div id="map"></div>

  <!-- 확대/축소 컨트롤 -->
  <div id="controls">
    <button id="zoomIn">지도 확대</button>
    <button id="zoomOut">지도 축소</button>
  </div>

  <script>
    // 지도 컨테이너
    var container = document.getElementById('map');

    // 지도 옵션
    var options = {
      center: new kakao.maps.LatLng(37.5665, 126.9780), // 서울시청 좌표
      level: 5,
      disableWheel: true // 마우스 휠로 확대/축소 비활성화 (버튼으로만 조작)
    };

    // 지도 생성
    var map = new kakao.maps.Map(container, options);

    // 마커 추가 (서울시청)
    var marker = new kakao.maps.Marker({
      map: map,
      position: new kakao.maps.LatLng(37.5665, 126.9780)
    });

    // 확대 버튼
    document.getElementById('zoomIn').onclick = function () {
      var level = map.getLevel();
      map.setLevel(level - 1, { animate: true }); // 애니메이션 줌
    };

    // 축소 버튼
    document.getElementById('zoomOut').onclick = function () {
      var level = map.getLevel();
      map.setLevel(level + 1, { animate: true }); // 애니메이션 줌
    };
  </script>

</body>
</html>
