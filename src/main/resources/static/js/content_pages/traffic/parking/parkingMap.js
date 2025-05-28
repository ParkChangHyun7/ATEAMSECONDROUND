//  주차장 지도 렌더링 스크립트 (Vanilla JS + Kakao Map API)
//  위치: static/js/content_pages/traffic/parking/parkingMap.js

// DOM이 모두 로드되었을 때 실행
document.addEventListener("DOMContentLoaded", async () => {
  // 1. HTML 요소에서 지도 뿌릴 컨테이너 가져오기
  const mapContainer = document.getElementById("map");
  if (!mapContainer) {
    console.error(" #map 요소를 찾을 수 없습니다.");
    return;
  }

  // 2. 카카오맵 생성
  const map = new kakao.maps.Map(mapContainer, {
    center: new kakao.maps.LatLng(37.5665, 126.9780), // 서울 시청 중심 좌표
    level: 5
  });

  try {
    // 3. 주차장 JSON 데이터 fetch
    const response = await fetch("/api/parking");
    const parkingList = await response.json();

    // 4. 마커 및 정보창 생성
    parkingList.forEach(parking => {
      const lat = parseFloat(parking.LAT);
      const lng = parseFloat(parking.LNG);

      // 마커 생성
      const marker = new kakao.maps.Marker({
        position: new kakao.maps.LatLng(lat, lng),
        map: map
      });

      // 클릭 시 보여줄 정보창
      const infoWindow = new kakao.maps.InfoWindow({
        content: `
          <div style="padding:5px;font-size:14px;">
            <b>${parking.PKLT_NM}</b><br/>
            ${parking.ADDR}<br/>
            ☎ ${parking.TELNO}<br/>
            시간당: ${parking.PRK_HM}분 / 추가요금: ${parking.ADD_CRG}원
          </div>
        `
      });

      // 마커 클릭 이벤트 등록
      kakao.maps.event.addListener(marker, 'click', () => {
        infoWindow.open(map, marker);
      });
    });
  } catch (error) {
    console.error(" 주차장 데이터를 불러오는 데 실패했습니다:", error);
  }
});
