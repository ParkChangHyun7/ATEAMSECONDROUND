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
