let openInfoWindow = null;

function closeInfowindow() {
  if (openInfoWindow) {
    openInfoWindow.close();
    openInfoWindow = null;
  }
}

document.addEventListener("DOMContentLoaded", () => {
  const map = new kakao.maps.Map(document.getElementById("map"), {
    center: new kakao.maps.LatLng(37.55, 126.98),
    level: 7
  });

  fetch("/api/cctv/list")
    .then(res => res.json())
    .then(data => {
      data.forEach(cctv => {
        const position = new kakao.maps.LatLng(cctv.coordY, cctv.coordX);
        const isEx = cctv.type === 'ex';
/*        const markerImage = new kakao.maps.MarkerImage(
          isEx ? 'http://t1.daumcdn.net/localimg/localimages/07/mapapidoc/marker_red.png' //고속도로
		    : 'http://t1.daumcdn.net/localimg/localimages/07/mapapidoc/marker_pink.png', //국도
          new kakao.maps.Size(32, 40)
        );
		  */

        const marker = new kakao.maps.Marker({
          position,
          map,
/*          image: markerImage
*/        });

        const infowindow = new kakao.maps.InfoWindow({
          content: `
            <div style="width: 380px; padding: 10px; box-sizing: border-box;">
              <div style="text-align: right;">
                <button onclick="closeInfowindow()" style="background: none; border: none; font-size: 18px; cursor: pointer;">❌</button>
              </div>
              <div style="text-align: center;">
                <strong>${cctv.cctvname}</strong><br/>
                <video id="video_${cctv.coordX}" width="340" height="220" controls autoplay muted style="margin-top: 6px;"></video>
              </div>
            </div>
          `
        });

        kakao.maps.event.addListener(marker, 'click', () => {
          if (openInfoWindow) openInfoWindow.close();
          infowindow.open(map, marker);
          openInfoWindow = infowindow;

          const video = document.getElementById(`video_${cctv.coordX}`);
          const videoUrl = cctv.cctvurl;

          if (Hls.isSupported()) {
            const hls = new Hls();
            hls.loadSource(videoUrl);
            hls.attachMedia(video);
          } else if (video.canPlayType("application/vnd.apple.mpegurl")) {
            video.src = videoUrl;
          }
        });
      });
    });
});
