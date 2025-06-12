function initializeCCTVMap() {
  let openInfoWindow = null;

  function closeInfowindow() {
    if (openInfoWindow) {
      openInfoWindow.close();
      openInfoWindow = null;
    }
  }

  const map = new kakao.maps.Map(document.getElementById("map"), {
    center: new kakao.maps.LatLng(37.55, 126.98),
    level: 7
  });

  // ✅ CCTV 마커 로드 (기존 코드 유지)
  fetch("/api/cctv/list")
    .then(res => res.json())
    .then(data => {
      data.forEach(cctv => {
        const position = new kakao.maps.LatLng(cctv.coordY, cctv.coordX);
        const marker = new kakao.maps.Marker({ position, map });

        const infowindow = new kakao.maps.InfoWindow({
          content: `
            <div style="width: 380px; padding: 10px;">
              <div style="text-align: right;">
                <button onclick="closeInfowindow()">❌</button>
              </div>
              <div style="text-align: center;">
                <strong>${cctv.cctvname}</strong><br/>
                <video id="video_${cctv.coordX}" width="340" height="220" controls autoplay muted></video>
              </div>
            </div>`
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

  // ✅ 개선된 장소 검색 기능
  const placesService = new kakao.maps.services.Places();
  const searchInput = document.getElementById("searchKeyword");
  const searchButton = document.getElementById("searchButton");
  let marker = null;

  if (searchInput && searchButton) {
    searchInput.addEventListener("keydown", (e) => {
      if (e.key === "Enter") searchButton.click();
    });

    searchButton.addEventListener("click", () => {
      const keyword = searchInput.value.trim();
      if (!keyword) {
        alert("검색어를 입력해 주세요.");
        return;
      }

      placesService.keywordSearch(keyword, (data, status) => {
        if (status === kakao.maps.services.Status.OK) {
          const place = data[0];
          const latlng = new kakao.maps.LatLng(place.y, place.x);

          // 부드러운 지도 이동
          map.panTo(latlng);

          if (marker) {
            marker.setPosition(latlng);
          } else {
            marker = new kakao.maps.Marker({
              position: latlng,
              map: map
            });
          }
        } else {
          alert("검색 결과가 없습니다. 다른 키워드를 시도해 주세요.");
        }
      });
    });
  }
}