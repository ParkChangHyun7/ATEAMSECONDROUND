// 페이지가 완전히 로드된 후 실행
document.addEventListener("DOMContentLoaded", async () => {
  // HTML 이스케이프 유틸 함수 (한글 깨짐 방지 및 보안)
  function escapeHtml(str) {
    if (!str) return "";
    return str.replace(/&/g, "&amp;")
              .replace(/</g, "&lt;")
              .replace(/>/g, "&gt;")
              .replace(/"/g, "&quot;")
              .replace(/'/g, "&#039;");
  }

  // 1. 지도를 표시할 컨테이너 요소 찾기
  const mapContainer = document.getElementById("map");
  if (!mapContainer) {
    console.error("#map 요소를 찾을 수 없습니다.");
    return;
  }

  // 2. 지도 생성 (초기 위치는 서울 시청 부근)
  const map = new kakao.maps.Map(mapContainer, {
    center: new kakao.maps.LatLng(37.5665, 126.9780),
    level: 5,
  });

  // 3. 마커 이미지 정의 (카카오 스프라이트)
  const markerImageSrc = "https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/category.png";

  // 공영 주차장용 마커 (P 아이콘)
  const publicMarkerImage = new kakao.maps.MarkerImage(
    markerImageSrc,
    new kakao.maps.Size(22, 26),
    {
      spriteOrigin: new kakao.maps.Point(10, 72),
      spriteSize: new kakao.maps.Size(36, 98),
    }
  );

  // 민영 주차장용 마커 (기타 아이콘 사용)
  const privateMarkerImage = new kakao.maps.MarkerImage(
    markerImageSrc,
    new kakao.maps.Size(22, 26),
    {
      spriteOrigin: new kakao.maps.Point(10, 0),
      spriteSize: new kakao.maps.Size(36, 98),
    }
  );

  try {
    // 공영/민영 주차장 데이터 로드
    const response = await fetch("/api/parking");
    const responseJson = await response.json();
    const parkingList = responseJson?.GetParkingInfo?.row ?? [];

    const bounds = new kakao.maps.LatLngBounds();

    parkingList.forEach((parking) => {
      const lat = parseFloat(parking.LAT);
      const lng = parseFloat(parking.LOT);

      if (isNaN(lat) || isNaN(lng)) {
        console.warn("위치값 오류로 제외됨: ", parking);
        return;
      }

      // 공영 여부 판단
      const isPublic = parking.PARKING_TYPE?.includes("공영");
      const markerImage = isPublic ? publicMarkerImage : privateMarkerImage;

      // 마커 생성
      const marker = new kakao.maps.Marker({
        position: new kakao.maps.LatLng(lat, lng),
        image: markerImage,
        map: map,
      });

      // 마커 클릭 시 정보창
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

      kakao.maps.event.addListener(marker, "click", () => {
        infowindow.open(map, marker);
      });

      bounds.extend(new kakao.maps.LatLng(lat, lng));
    });

    map.setBounds(bounds);
  } catch (error) {
    console.error("주차장 데이터를 불러오는 데 실패했습니다.", error);
  }
});
