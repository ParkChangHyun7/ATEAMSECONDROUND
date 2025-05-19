const map = new kakao.maps.Map(document.getElementById('map'), {
  center: new kakao.maps.LatLng(35.0, 127.3), // 중심 좌표
  level: 9
});

function showError(msg) {
  const el = document.getElementById("error");
  if (el) el.textContent = msg;
}

function defaultText(val, fallback = "정보 없음") {
  try {
    if (val === undefined || val === null) return fallback;
    const str = String(val).trim();
    return str === "" ? fallback : str;
  } catch {
    return fallback;
  }
}

fetch("https://openapi.its.go.kr:9443/cctvInfo?apiKey=9a5ffdb76417458d88b7d2add9348307&type=its&cctvType=1&minX=126.8&maxX=127.89&minY=34.9&maxY=35.1&getType=json")
  .then(res => {
    if (!res.ok) throw new Error(`HTTP 오류: ${res.status}`);
    return res.json();
  })
  .then(data => {
    const items = data?.body?.items || [];

    if (items.length === 0) {
      showError("표시할 CCTV 정보가 없습니다.");
      return;
    }

    const bounds = new kakao.maps.LatLngBounds();

    items.forEach((item, idx) => {
      const lat = parseFloat(item.coordY?.replace(",", "."));
      const lon = parseFloat(item.coordX?.replace(",", "."));
      if (isNaN(lat) || isNaN(lon)) return;

      const marker = new kakao.maps.Marker({
        map,
        position: new kakao.maps.LatLng(lat, lon),
        title: item.cctvName || `CCTV ${idx + 1}`
      });

      const infoContent = `
        <div class="info-window">
          <strong>CCTV 이름: ${defaultText(item.cctvName)}</strong><br>
          영상 주소: <a href="${item.cctvUrl}" target="_blank">보기</a><br>
          좌표: (${lat.toFixed(5)}, ${lon.toFixed(5)})
        </div>
      `;

      const info = new kakao.maps.InfoWindow({
        content: infoContent,
        removable: true
      });

      kakao.maps.event.addListener(marker, 'click', () => {
        info.open(map, marker);
      });

      bounds.extend(new kakao.maps.LatLng(lat, lon));
    });

    map.setBounds(bounds);
  })
  .catch(err => {
    console.error("CCTV API 오류:", err);
    showError("CCTV 데이터를 불러오지 못했습니다.");
  });
