<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
  <title>CCTV Map</title>
  <%@ include file="/WEB-INF/views/include/traffic/cctvMap/resources.jsp" %>
  <style>
    #searchBox {
      position: absolute;
      top: 10px;
      left: 50%;
      transform: translateX(-50%);
      z-index: 999;
      background: white;
      padding: 10px;
      border-radius: 5px;
      box-shadow: 0 2px 5px rgba(0,0,0,0.3);
      display: block; /* 명시적으로 표시 설정 */
    }
    #map {
      width: 100%;
      height: 600px;
      position: relative; /* 검색창이 지도 위에 오도록 설정 */
    }
  </style>
</head>
<body>

  <!-- 🔍 검색창 -->
  <div id="searchBox">
    <input type="text" id="searchKeyword" placeholder="도로명이나 장소 검색" />
    <button id="searchButton">검색</button>
  </div>

  <!-- 🗺️ 지도 -->
  <div id="map"></div>

  <%@ include file="/WEB-INF/views/include/traffic/cctvMap/scripts.jsp" %>

</body>
</html>