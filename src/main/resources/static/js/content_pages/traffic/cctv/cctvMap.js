
// cctvMap.js
function initializeCCTVMap() {
  const mapContainer = document.getElementById('map');
  if (!mapContainer) {
    console.error('지도 컨테이너를 찾을 수 없습니다! "map" 요소가 HTML에 존재하는지 확인하세요.');
    return;
  }

  const map = new kakao.maps.Map(mapContainer, {
    center: new kakao.maps.LatLng(37.5665, 126.978),
    level: 7,
  });
  console.log('지도 초기화 완료:', map);

  function showError(msg) {
    const el = document.getElementById('error');
    if (el) {
      el.textContent = msg;
      el.style.color = 'red';
    }
  }

  function defaultText(v, f = '정보 없음') {
    if (!v) return f;
    const s = v.trim();
    return s === '' ? f : s;
  }

  let openInfoWindow = null;

  fetch('/api/traffic/cctv')
    .then((res) => {
      if (!res.ok) {
        throw new Error(`HTTP 오류: ${res.status}`);
      }
      return res.json();
    })
    .then((data) => {
      console.log('API 응답:', data);

      if (!data?.response) {
        console.error('응답에 "response" 객체가 없습니다.');
        showError('CCTV 데이터를 불러오지 못했습니다. 응답 형식을 확인하세요.');
        return;
      }

      const list = data?.response?.data || [];
      if (data.response.datacount === 0) {
        console.warn('⚠ CCTV 데이터가 없습니다. 좌표 범위나 조건을 확인하세요.');
        showError('CCTV 데이터를 찾을 수 없습니다.');
        return;
      }

      list.forEach((cctv, index) => {
        console.log(`CCTV #${index + 1}:`, cctv);

        if (!cctv?.coordx || !cctv?.coordy || !cctv?.cctvurl) {
          console.warn(`CCTV #${index + 1}: 좌표 또는 URL이 누락되었습니다.`, cctv);
          return;
        }

        const lat = parseFloat(String(cctv.coordy).replace(',', '.'));
        const lon = parseFloat(String(cctv.coordx).replace(',', '.'));
        console.log(`CCTV #${index + 1} 파싱된 좌표: lat=${lat}, lon=${lon}`);

        if (isNaN(lat) || isNaN(lon)) {
          console.warn(`CCTV #${index + 1}: 유효하지 않은 좌표입니다.`, { lat, lon });
          return;
        }

        const marker = new kakao.maps.Marker({
          map: map,
          position: new kakao.maps.LatLng(lat, lon),
          title: defaultText(cctv.cctvname),
        });
        console.log(`CCTV #${index + 1}: 마커 생성 완료`, marker);

        const videoId = `video-${Math.random().toString(36).substring(2, 10)}`;
        const content = `
          <div class="info-window" style="width:320px;">
            <strong>${defaultText(cctv.cctvname)}</strong><br>
            (${lat.toFixed(4)}, ${lon.toFixed(4)})<br><br>
            <video id="${videoId}" controls autoplay muted width="300" height="200" style="background:black"></video>
          </div>
        `;

        const info = new kakao.maps.InfoWindow({ content, removable: true });

        kakao.maps.event.addListener(marker, 'click', () => {
          if (openInfoWindow) openInfoWindow.close();
          info.open(map, marker);
          openInfoWindow = info;
          console.log(`CCTV #${index + 1}: 정보 창 열림`);

          setTimeout(() => {
            const videoEl = document.getElementById(videoId);
            if (!videoEl) {
              console.error(`CCTV #${index + 1}: 비디오 요소를 찾을 수 없습니다.`, videoId);
              return;
            }

            const proxiedUrl = '/cctv-proxy?url=' + encodeURIComponent(cctv.cctvurl);
            console.log(`CCTV #${index + 1}: 프록시 URL:`, proxiedUrl);

            if (typeof Hls === 'undefined') {
              console.error('HLS.js 라이브러리가 로드되지 않았습니다. resources.jsp에 스크립트가 추가되었는지 확인하세요.');
              videoEl.outerHTML = '<p style="color:red;">⚠ HLS.js를 로드할 수 없습니다.</p>';
              return;
            }

            if (Hls.isSupported()) {
              const hls = new Hls({
                xhrSetup: (xhr, url) => {
                  const proxiedSegmentUrl = '/cctv-proxy?url=' + encodeURIComponent(url);
                  console.log(`HLS 세그먼트 요청: ${url} -> ${proxiedSegmentUrl}`);
                  xhr.open('GET', proxiedSegmentUrl, true);
                },
              });
              hls.loadSource(proxiedUrl);
              hls.attachMedia(videoEl);

              hls.on(Hls.Events.ERROR, (event, data) => {
                console.error(`CCTV #${index + 1}: HLS.js 에러 발생:`, data);
                if (data.fatal) {
                  videoEl.outerHTML = '<p style="color:red;">⚠ 재생할 수 없는 영상입니다.</p>';
                  hls.destroy();
                }
              });
            } else if (videoEl.canPlayType('application/vnd.apple.mpegurl')) {
              videoEl.src = proxiedUrl;
              videoEl.addEventListener('error', () => {
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
      console.error('❌ CCTV API 호출 실패:', err);
      showError('CCTV 데이터를 불러오지 못했습니다.');
    });

  console.log('✅ cctvMap.js 로드 완료!');
}

// Vue에서 호출할 수 있도록 함수 내보내기
if (typeof module !== 'undefined' && module.exports) {
  module.exports = { initializeCCTVMap };
} else {
  window.initializeCCTVMap = initializeCCTVMap;
}






/*document.addEventListener("DOMContentLoaded", () => {
  const mapContainer = document.getElementById("map");
  if (!mapContainer) {
    console.error("지도 컨테이너를 찾을 수 없습니다! 'map' 요소가 HTML에 존재하는지 확인하세요.");
    return;
  }

  // 지도 초기화
  const map = new kakao.maps.Map(mapContainer, {
    center: new kakao.maps.LatLng(37.5665, 126.978), // 서울 중심 좌표
    level: 7,
  });
  console.log("지도 초기화 완료:", map);

  // 에러 메시지 표시 함수
  function showError(msg) {
    const el = document.getElementById("error");
    if (el) {
      el.textContent = msg;
      el.style.color = "red";
    }
  }

  // 기본 텍스트 처리 함수
  function defaultText(v, f = "정보 없음") {
    if (!v) return f;
    const s = v.trim();
    return s === "" ? f : s;
  }

  let openInfoWindow = null;

  // API 호출
  fetch("/api/traffic/cctv")
    .then((res) => {
      if (!res.ok) {
        throw new Error(`HTTP 오류: ${res.status}`);
      }
      return res.json();
    })
    .then((data) => {
      console.log("API 응답:", data); // 응답 데이터 확인

      // 데이터가 없거나 구조가 예상과 다른 경우 처리
      if (!data?.response) {
        console.error("응답에 'response' 객체가 없습니다.");
        showError("CCTV 데이터를 불러오지 못했습니다. 응답 형식을 확인하세요.");
        return;
      }

      const list = data?.response?.data || [];
      if (data.response.datacount === 0) {
        console.warn("⚠ CCTV 데이터가 없습니다. 좌표 범위나 조건을 확인하세요.");
        showError("CCTV 데이터를 찾을 수 없습니다.");
        return;
      }

      // 데이터가 있는 경우 마커 생성
      list.forEach((cctv, index) => {
        console.log(`CCTV #${index + 1}:`, cctv); // 개별 데이터 확인

        // 필수 필드 확인
        if (!cctv?.coordx || !cctv?.coordy || !cctv?.cctvurl) {
          console.warn(`CCTV #${index + 1}: 좌표 또는 URL이 누락되었습니다.`, cctv);
          return;
        }

        // 좌표 파싱
        const lat = parseFloat(String(cctv.coordy).replace(",", "."));
        const lon = parseFloat(String(cctv.coordx).replace(",", "."));
        console.log(`CCTV #${index + 1} 파싱된 좌표: lat=${lat}, lon=${lon}`);

        // 좌표 유효성 검사
        if (isNaN(lat) || isNaN(lon)) {
          console.warn(`CCTV #${index + 1}: 유효하지 않은 좌표입니다.`, { lat, lon });
          return;
        }

        // 마커 생성
        const marker = new kakao.maps.Marker({
          map: map,
          position: new kakao.maps.LatLng(lat, lon),
          title: defaultText(cctv.cctvname),
        });
        console.log(`CCTV #${index + 1}: 마커 생성 완료`, marker);

        // 정보 창 내용
        const videoId = `video-${Math.random().toString(36).substring(2, 10)}`;
        const content = `
          <div class="info-window" style="width:320px;">
            <strong>${defaultText(cctv.cctvname)}</strong><br>
            (${lat.toFixed(4)}, ${lon.toFixed(4)})<br><br>
            <video id="${videoId}" controls autoplay muted width="300" height="200" style="background:black"></video>
          </div>
        `;

        // 정보 창 생성
        const info = new kakao.maps.InfoWindow({ content, removable: true });

        // 마커 클릭 이벤트
        kakao.maps.event.addListener(marker, "click", () => {
          if (openInfoWindow) openInfoWindow.close();
          info.open(map, marker);
          openInfoWindow = info;
          console.log(`CCTV #${index + 1}: 정보 창 열림`);

          setTimeout(() => {
            const videoEl = document.getElementById(videoId);
            if (!videoEl) {
              console.error(`CCTV #${index + 1}: 비디오 요소를 찾을 수 없습니다.`, videoId);
              return;
            }

            const proxiedUrl = "/cctv-proxy?url=" + encodeURIComponent(cctv.cctvurl);
            console.log(`CCTV #${index + 1}: 프록시 URL:`, proxiedUrl);

            if (window.Hls && Hls.isSupported()) {
              const hls = new Hls();
              hls.loadSource(proxiedUrl);
              hls.attachMedia(videoEl);

              hls.on(Hls.Events.ERROR, (event, data) => {
                console.error(`CCTV #${index + 1}: HLS.js 에러 발생:`, data);
                if (data.fatal) {
                  videoEl.outerHTML = '<p style="color:red;">⚠ 재생할 수 없는 영상입니다.</p>';
                  hls.destroy();
                }
              });
            } else if (videoEl.canPlayType("application/vnd.apple.mpegurl")) {
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
    const s = (v).trim();
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
			    const hls = new Hls({
			        xhrSetup: (xhr, url) => {
			            // 이미 인코딩된 URL을 그대로 사용
			            xhr.open("GET", url, true);
			        }
			    });
			    const proxiedUrl = "/cctv-proxy?url=" + encodeURIComponent(cctv.cctvurl);
			    hls.loadSource(proxiedUrl);
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
*/