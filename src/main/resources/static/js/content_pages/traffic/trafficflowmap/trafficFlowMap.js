document.addEventListener("DOMContentLoaded", () => {
  const mapContainer = document.getElementById("map");
  if (!mapContainer) return;

  const map = new kakao.maps.Map(mapContainer, {
    center: new kakao.maps.LatLng(37.5665, 126.978), // 서울 중심
    level: 6,
  });

  const zoomControl = new kakao.maps.ZoomControl();
  map.addControl(zoomControl, kakao.maps.ControlPosition.BOTTOMRIGHT);

  const mapTypeControl = new kakao.maps.MapTypeControl();
  map.addControl(mapTypeControl, kakao.maps.ControlPosition.TOPRIGHT);

  map.addOverlayMapTypeId(kakao.maps.MapTypeId.TRAFFIC);

  // ✅ 장소 검색 기능
  const placesService = new kakao.maps.services.Places();
  const searchInput = document.getElementById("searchKeyword");
  const searchButton = document.getElementById("searchButton");

  let marker = null; // ✅ 마커를 처음부터 생성하지 않음

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

          if (marker) {
            marker.setPosition(latlng);
          } else {
            marker = new kakao.maps.Marker({
              position: latlng,
              map: map,
            });
          }
        } else {
          alert("검색 결과가 없습니다.");
        }
      });
    });
  }
});
