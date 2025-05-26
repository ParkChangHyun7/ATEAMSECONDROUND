// static 리소스 파일
window.addEventListener("DOMContentLoaded", () => {
  const mapContainer = document.getElementById("map");
  if (!mapContainer) return;

  const map = new kakao.maps.Map(mapContainer, {
    center: new kakao.maps.LatLng(37.5665, 126.978),
    level: 7,
  });

  function showError(msg) {
    const el = document.getElementById("error");
    if (el) el.textContent = msg;
  }

  function defaultText(v, f = "정보 없음") {
    if (!v) return f;
    const s = String(v).trim();
    return s === "" ? f : s;
  }

  let openInfoWindow = null;

  fetch("/api/traffic/cctv")
    .then((res) => res.json())
    .then((data) => {
		console.log("📡 CCTV API 응답 도착:", data);  // 👈 이거 추가
      const list = data?.response?.body?.items?.item || [];
		console.log("📍 마커 찍을 CCTV 수:", list.length);  // 👈 이것도 추가
      list.forEach((cctv) => {
        const lat = parseFloat((cctv.coordY || "0").replace(",", "."));
        const lon = parseFloat((cctv.coordX || "0").replace(",", "."));
        if (isNaN(lat) || isNaN(lon)) return;

        const marker = new kakao.maps.Marker({
          map,
          position: new kakao.maps.LatLng(lat, lon),
          title: defaultText(cctv.cctvName),
        });

        const content = `
          <div class="info-window">
            <strong>${defaultText(cctv.cctvName)}</strong><br>
            ${defaultText(cctv.roadName)}<br>
            (${lat}, ${lon})
          </div>
        `;
        const info = new kakao.maps.InfoWindow({ content, removable: true });

        kakao.maps.event.addListener(marker, "click", () => {
          if (openInfoWindow) openInfoWindow.close();
          info.open(map, marker);
          openInfoWindow = info;
        });
      });
    })
    .catch((err) => {
      console.error(err);
      showError("CCTV 데이터를 불러오지 못했습니다.");
    });
	console.log("✅ cctvMap.js 로드됨!");
});
