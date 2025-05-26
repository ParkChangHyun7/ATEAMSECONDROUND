document.addEventListener("DOMContentLoaded", () => {
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
      const list = data?.response?.data || [];

      list.forEach((cctv) => {
        if (!cctv?.coordx || !cctv?.coordy || !cctv?.cctvurl) return;

        const lat = parseFloat(String(cctv.coordy).replace(",", "."));
        const lon = parseFloat(String(cctv.coordx).replace(",", "."));
        if (isNaN(lat) || isNaN(lon)) return;

        const videoId = `video-${Math.random().toString(36).substring(2, 10)}`;
        const marker = new kakao.maps.Marker({
          map,
          position: new kakao.maps.LatLng(lat, lon),
          title: defaultText(cctv.cctvname),
        });

        const content = `
          <div class="info-window" style="width:320px;">
            <strong>${defaultText(cctv.cctvname)}</strong><br>
            (${lat.toFixed(4)}, ${lon.toFixed(4)})<br><br>
            <video id="${videoId}" controls autoplay muted width="300" height="200" style="background:black"></video>
          </div>
        `;

        const info = new kakao.maps.InfoWindow({ content, removable: true });

        kakao.maps.event.addListener(marker, "click", () => {
          if (openInfoWindow) openInfoWindow.close();
          info.open(map, marker);
          openInfoWindow = info;

          setTimeout(() => {
            const videoEl = document.getElementById(videoId);
            if (!videoEl) return;

            const proxiedUrl = "/cctv-proxy?url=" + encodeURIComponent(cctv.cctvurl);

            if (window.Hls && Hls.isSupported()) {
              const hls = new Hls();
              hls.loadSource(proxiedUrl);           // ✅ 반드시 필요
              hls.attachMedia(videoEl);

              hls.on(Hls.Events.ERROR, (event, data) => {
                console.error("❌ HLS.js 에러 발생:", data);
                if (data.fatal) {
                  videoEl.outerHTML = '<p style="color:red;">⚠ 재생할 수 없는 영상입니다.</p>';
                  hls.destroy();
                }
              });

            } else if (videoEl.canPlayType('application/vnd.apple.mpegurl')) {
              videoEl.src = proxiedUrl;
              videoEl.addEventListener("error", () => {
                videoEl.outerHTML = '<p style="color:red;">⚠ 영상 재생에 실패했습니다.</p>';
              });
            } else {
              videoEl.outerHTML = '<p style="color:red;">⚠ 브라우저가 이 CCTV 영상을 지원하지 않습니다.</p>';
            }
          }, 300);
        });
      });
    })
    .catch((err) => {
      console.error("❌ CCTV API 호출 실패:", err);
      showError("CCTV 데이터를 불러오지 못했습니다.");
    });

  console.log("✅ cctvMap.js 로드 완료!");
});
