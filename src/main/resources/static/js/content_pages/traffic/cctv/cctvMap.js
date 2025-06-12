function initializeCCTVMap() {
  let openInfoWindow = null;
  const map = new kakao.maps.Map(document.getElementById("map"), {
    center: new kakao.maps.LatLng(37.55, 126.98),
    level: 7
  });

  const cctvListEl = document.getElementById("cctvList");
  const filterButtons = document.querySelectorAll(".filter-btn");

  let allData = [];
  let allMarkers = [];

  // ✅ 데이터 및 마커 불러오기
  fetch("/api/cctv/list")
    .then(res => res.json())
    .then(data => {
      allData = data;

      renderCCTVList("all");

	  document.querySelector('.filter-btn[data-type="all"]').classList.add("active");

	  
	      });

  // ✅ CCTV 목록 + 마커 출력 함수
  function renderCCTVList(filterType) {
    cctvListEl.innerHTML = "";
    allMarkers.forEach(m => m.setMap(null)); // 기존 마커 제거
    allMarkers = [];

    const filtered = filterType === "all" ? allData : allData.filter(c => c.type === filterType);

    filtered.forEach(cctv => {
      const position = new kakao.maps.LatLng(cctv.coordY, cctv.coordX);

      // ✅ 마커 (기본 파란색)
      const marker = new kakao.maps.Marker({ position, map });
      allMarkers.push(marker);

      // ✅ 인포윈도우
      const infowindow = new kakao.maps.InfoWindow({
        content: `
          <div style="width: 380px; padding: 10px;">
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

      // ✅ 리스트 항목 추가
      const li = document.createElement("li");
      li.innerHTML = `
        <span style="font-weight:bold">${cctv.cctvname}</span>
        <span style="color:gray; font-size:12px;"> [${cctv.type === 'ex' ? '고속도로' : '국도'}]</span>
      `;
      li.addEventListener("click", () => {
        map.panTo(position);
        kakao.maps.event.trigger(marker, 'click');
      });

      cctvListEl.appendChild(li);
    });
  }

  // ✅ 필터 버튼 클릭 이벤트
  filterButtons.forEach(btn => {
    btn.addEventListener("click", () => {
      filterButtons.forEach(b => b.classList.remove("active"));
      btn.classList.add("active");

      const type = btn.dataset.type;
      renderCCTVList(type);
    });
  });

  // ✅ 지도 클릭 시 인포윈도우 닫기
  kakao.maps.event.addListener(map, 'click', () => {
    if (openInfoWindow) {
      openInfoWindow.close();
      openInfoWindow = null;
    }
  });

  // ✅ 장소 검색 유지
  const placesService = new kakao.maps.services.Places();
  const searchInput = document.getElementById("searchKeyword");
  const searchButton = document.getElementById("searchButton");
  let searchMarker = null;

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

          map.panTo(latlng);

          if (searchMarker) {
            searchMarker.setPosition(latlng);
          } else {
            searchMarker = new kakao.maps.Marker({
              position: latlng,
              map: map
            });
          }
        } else {
          alert("검색 결과가 없습니다.");
        }
      });
    });
  }
}
