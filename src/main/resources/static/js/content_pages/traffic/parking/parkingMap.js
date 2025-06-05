// 전체 경로: src/main/resources/static/js/content_pages/traffic/parking/parkingMap.js

//  페이지가 완전히 로드된 후 실행
document.addEventListener("DOMContentLoaded", async () => {
  //  HTML 이스케이프 유틸 함수 (XSS 방지 및 null 대응)
  function escapeHtml(str) {
    if (!str || typeof str !== 'string') return "";
    return str.replace(/&/g, "&amp;")
              .replace(/</g, "&lt;")
              .replace(/>/g, "&gt;")
              .replace(/"/g, "&quot;")
              .replace(/'/g, "&#039;");
  }

  //  1. 지도 컨테이너 DOM 요소 찾기
  const mapContainer = document.getElementById("map");
  if (!mapContainer) {
    console.error("#map 요소를 찾을 수 없습니다.");
    return;
  }

  //  2. 카카오 지도 생성 (초기 중심 좌표: 서울시청)
  const map = new kakao.maps.Map(mapContainer, {
    center: new kakao.maps.LatLng(37.5665, 126.9780),
    level: 5,
  });

  //  3. 마커 아이콘(스프라이트 이미지) 설정
  const markerImageSrc = "https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/category.png";

  // 공영 주차장 마커 (P 아이콘)
  const publicMarkerImage = new kakao.maps.MarkerImage(
    markerImageSrc,
    new kakao.maps.Size(22, 26),
    {
      spriteOrigin: new kakao.maps.Point(10, 72), // 아이콘 위치
      spriteSize: new kakao.maps.Size(36, 98),
    }
  );

  // 민영 주차장 마커 (기타 아이콘)
  const privateMarkerImage = new kakao.maps.MarkerImage(
    markerImageSrc,
    new kakao.maps.Size(22, 26),
    {
      spriteOrigin: new kakao.maps.Point(10, 0), // 아이콘 위치
      spriteSize: new kakao.maps.Size(36, 98),
    }
  );

  try {
    //  4. 주차장 JSON 데이터 fetch
    const response = await fetch("/api/parking");

    // ❗❗ 중요: text()로 받은 후 JSON.parse() 직접 수행 (한글 깨짐 방지)
    const text = await response.text();
    const responseJson = JSON.parse(text);

    //  5. 데이터 추출 (row 배열)
    const parkingList = responseJson?.GetParkInfo?.row ?? [];

    // 지도 영역 자동 조절을 위한 bounds 객체
    const bounds = new kakao.maps.LatLngBounds();

    //  6. 주차장 목록 반복
    parkingList.forEach((parking) => {
      const lat = parseFloat(parking.LAT);
      const lng = parseFloat(parking.LOT);

      if (isNaN(lat) || isNaN(lng)) {
        console.warn("위치값 오류로 제외됨: ", parking);
        return;
      }

      // 공영/민영 여부에 따라 마커 이미지 선택
      const isPublic = parking.PARKING_TYPE?.includes("공영");
      const markerImage = isPublic ? publicMarkerImage : privateMarkerImage;

      //  마커 생성
      const marker = new kakao.maps.Marker({
        position: new kakao.maps.LatLng(lat, lng),
        image: markerImage,
        map: map,
      });

      //  마커 클릭 시 정보창 표시
      const infowindow = new kakao.maps.InfoWindow({
        content: `
          <div style="padding:5px;font-size:14px;">
            <b>${escapeHtml(parking.PARK_NM)}</b><br/>
            ${escapeHtml(parking.ADDR)}<br/>
            ${escapeHtml(parking.TELNO)}<br/>
            <b>이용요금:</b> ${escapeHtml(parking.PRK_HM)}분 / 추가 ${escapeHtml(parking.ADD_CRG)}원
          </div>
        `,
      });

      // 클릭 이벤트 등록
      kakao.maps.event.addListener(marker, "click", () => {
        infowindow.open(map, marker);
      });

      // 지도 영역 확장
      bounds.extend(new kakao.maps.LatLng(lat, lng));
    });

    //  7. 전체 마커가 보이도록 지도 범위 자동 조절
    map.setBounds(bounds);
  } catch (error) {
    //  에러 처리
    console.error("주차장 데이터를 불러오는 데 실패했습니다.", error);
  }
});
