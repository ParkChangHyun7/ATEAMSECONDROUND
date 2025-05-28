document.addEventListener("DOMContentLoaded", () => {
  const map = new kakao.maps.Map(document.getElementById("map"), {
    center: new kakao.maps.LatLng(37.55, 126.98),
    level: 8
  });

  const API_URL = "https://openapi.its.go.kr:9443/trafficInfo?apiKey=9a5ffdb76417458d88b7d2add9348307&type=all&drcType=all&minX=126.7&maxX=127.2&minY=37.4&maxY=37.7&getType=json";

  fetch(API_URL)
    .then(res => res.json())
    .then(json => {
      console.log("📦 받은 데이터:", json);

      const data = json.body?.items || [];

	  data.forEach(item => {
	    const { startX, startY, endX, endY, speed } = item;

	    // 좌표값이 비어있으면 건너뜀
	    if (!startX || !startY || !endX || !endY) {
	      console.warn("❌ 좌표 없음 (스킵):", item);
	      return;
	    }

	    const startLat = parseFloat(startY);
	    const startLng = parseFloat(startX);
	    const endLat = parseFloat(endY);
	    const endLng = parseFloat(endX);

	    if (
	      isNaN(startLat) || isNaN(startLng) ||
	      isNaN(endLat) || isNaN(endLng)
	    ) {
	      console.warn("❌ 좌표 파싱 실패 (스킵):", item);
	      return;
	    }

	    const path = [
	      new kakao.maps.LatLng(startLat, startLng),
	      new kakao.maps.LatLng(endLat, endLng)
	    ];

	    const polyline = new kakao.maps.Polyline({
	      path: path,
	      strokeWeight: 6,
	      strokeColor: getColorBySpeed(parseFloat(speed)),
	      strokeOpacity: 0.9,
	      strokeStyle: "solid"
	    });

	    polyline.setMap(map);
	  });

    })
    .catch(err => {
      console.error("🚨 API 통신 오류:", err);
      document.getElementById("error").textContent = "🚨 교통 정보 로딩 실패!";
    });
});

function getColorBySpeed(speed) {
  if (speed >= 60) return "#00FF00";  // 초록
  if (speed >= 30) return "#FFFF00";  // 노랑
  return "#FF0000";                   // 빨강
}
