
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!-- 카카오맵 API (services 포함) -->
  <script src="https://dapi.kakao.com/v2/maps/sdk.js?appkey=ddc3a5089cd2e1974490b196aab220ec&libraries=services"></script>

  <!-- 스타일 -->
  <style>
    #search-container {
      width: 100%;
      display: flex;
      justify-content: center;
      padding: 20px 0;
      background-color: #f8f9fa;
    }

    #search-inner {
      display: flex;
      gap: 10px;
      align-items: center;
    }

    #searchKeyword {
      width: 300px;
      height: 40px;
      padding: 0 15px;
      font-size: 16px;
      border-radius: 8px;
      border: 1px solid #ccc;
    }

    #searchButton {
      height: 40px;
      padding: 0 20px;
      font-size: 16px;
      border: none;
      border-radius: 8px;
      background-color: #007bff;
      color: white;
      cursor: pointer;
    }

    #map {
      width: 100%;
      height: 600px;
    }
  </style>
</head>

<body>

  <!-- 검색창 -->
  <div id="search-container">
    <div id="search-inner">
      <input type="text" id="searchKeyword" placeholder="도로명이나 장소 검색" />
      <button id="searchButton">검색</button>
    </div>
  </div>

  <!-- 지도 -->
  <div id="map"></div>

  <!-- JS -->
  <script>
    document.addEventListener("DOMContentLoaded", () => {
      const mapContainer = document.getElementById("map");
      if (!mapContainer) return;

      const map = new kakao.maps.Map(mapContainer, {
        center: new kakao.maps.LatLng(37.5665, 126.978), // 서울 중심
        level: 6,
      });

      // 줌 컨트롤 추가
      const zoomControl = new kakao.maps.ZoomControl();
      map.addControl(zoomControl, kakao.maps.ControlPosition.BOTTOMRIGHT);

      // 지도 타입 컨트롤 추가
      const mapTypeControl = new kakao.maps.MapTypeControl();
      map.addControl(mapTypeControl, kakao.maps.ControlPosition.TOPRIGHT);

      // 교통 흐름 오버레이
      map.addOverlayMapTypeId(kakao.maps.MapTypeId.TRAFFIC);

      // 검색 기능
      const placesService = new kakao.maps.services.Places();
      const searchInput = document.getElementById("searchKeyword");
      const searchButton = document.getElementById("searchButton");
      let marker = new kakao.maps.Marker({ map: map });

      searchInput.addEventListener("keydown", (e) => {
    	    if (e.key === "Enter") {
    	      searchButton.click();
    	    }
    	  });
      
      
      searchButton.addEventListener("click", () => {
        const keyword = searchInput.value.trim();
        if (!keyword) {
          alert("검색어를 입력하세요.");
          return;
        }

        placesService.keywordSearch(keyword, (data, status) => {
          if (status === kakao.maps.services.Status.OK) {
            const place = data[0];
            const latlng = new kakao.maps.LatLng(place.y, place.x);
            map.setCenter(latlng);
            marker.setPosition(latlng);
          } else {
            alert("검색 결과가 없습니다.");
          }
        });
      });
    });
  </script>
</body>
</html>
