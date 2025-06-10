




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

  // ✅ 교통 흐름 오버레이 추가
  map.addOverlayMapTypeId(kakao.maps.MapTypeId.TRAFFIC);
});


  // ✅ 장소 검색 기능 추가
  const placesService = new kakao.maps.services.Places();
  const searchInput = document.getElementById("searchKeyword");
  const searchButton = document.getElementById("searchButton");
  let marker = new kakao.maps.Marker({ map: map });

  if (searchButton && searchInput) {
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
  }

