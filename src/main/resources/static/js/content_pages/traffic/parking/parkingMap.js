document.addEventListener("DOMContentLoaded", async () => {
  const mapContainer = document.getElementById("map");
  if (!mapContainer) {
    console.error(" can't find #map.");
    return;
  }
  const map = new kakao.maps.Map(mapContainer, {
    center: new kakao.maps.LatLng(37.5665, 126.9780), 
    level: 5
  });

  try {
    const response = await fetch("/api/parking");
    const parkingList = await response.json();

    parkingList.forEach(parking => {
      const lat = parseFloat(parking.LAT);
      const lng = parseFloat(parking.LNG);

      const marker = new kakao.maps.Marker({
        position: new kakao.maps.LatLng(lat, lng),
        map: map
      });

      const infoWindow = new kakao.maps.InfoWindow({
        content: `
          <div style="padding:5px;font-size:14px;">
            <b>${parking.PKLT_NM}</b><br/>
            ${parking.ADDR}<br/>
             ${parking.TELNO}<br/>
            per hr: ${parking.PRK_HM}min / additional: ${parking.ADD_CRG}won
          </div>
        `
      });

      kakao.maps.event.addListener(marker, 'click', () => {
        infoWindow.open(map, marker);
      });
    });
  } catch (error) {
    console.error(" failed:", error);
  }
});
