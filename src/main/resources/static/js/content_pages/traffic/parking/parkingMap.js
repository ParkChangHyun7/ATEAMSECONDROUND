// static/js/parkingMap.js 또는 Vue3 모듈 파일 내부
import { onMounted } from 'vue';

onMounted(async () => {
  const mapContainer = document.getElementById('map');
  if (!mapContainer) return;

  const mapOption = {
    center: new kakao.maps.LatLng(37.5665, 126.9780),
    level: 5
  };
  const map = new kakao.maps.Map(mapContainer, mapOption);

  try {
    const res = await fetch('/api/parking');
    const parkingList = await res.json();

    parkingList.forEach(parking => {
      const lat = parseFloat(parking.LAT);
      const lng = parseFloat(parking.LNG);

      const marker = new kakao.maps.Marker({
        position: new kakao.maps.LatLng(lat, lng),
        map: map
      });

      const info = new kakao.maps.InfoWindow({
        content: `
          <div style="padding:5px;font-size:14px;">
            <b>${parking.PKLT_NM}</b><br/>
            ${parking.ADDR}<br/>
            ☎ ${parking.TELNO}<br/>
            시간당: ${parking.PRK_HM}분 / 추가요금: ${parking.ADD_CRG}원
          </div>
        `
      });

      kakao.maps.event.addListener(marker, 'click', () => {
        info.open(map, marker);
      });
    });
  } catch (e) {
    console.error('주차장 데이터를 불러오는 데 실패했습니다.', e);
  }
});