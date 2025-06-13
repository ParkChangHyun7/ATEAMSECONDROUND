function initializeCCTVMap() {
  let openInfoWindow = null;

  const map = new kakao.maps.Map(document.getElementById("map"), {
    center: new kakao.maps.LatLng(37.55, 126.98),
    level: 7
  });

  const cctvListEl = document.getElementById("cctvList");
  const filterButtons = document.querySelectorAll(".filter-btn");
  const searchInput = document.getElementById("cctvSearchInput");

  let allData = [];
  let allMarkers = [];
  let currentFilter = "all";

  fetch("/api/cctv/list")
    .then(res => res.json())
    .then(data => {
      allData = data;
      renderCCTVList("all");
      document.querySelector('.filter-btn[data-type="all"]')?.classList.add("active");
    });

  function renderCCTVList(filterType, keyword = "") {
    cctvListEl.innerHTML = "";
    allMarkers.forEach(m => m.setMap(null));
    allMarkers = [];
    currentFilter = filterType;

    const filtered = allData
      .filter(c => filterType === "all" || c.type === filterType)
      .filter(c => c.cctvname.toLowerCase().includes(keyword.toLowerCase()));

    if (filtered.length === 0) {
      const li = document.createElement("li");
      li.innerHTML = `<em style="color: gray;">검색 결과가 없습니다.</em>`;
      cctvListEl.appendChild(li);
      return;
    }

    filtered.forEach(cctv => {
      const lat = parseFloat(cctv.coordY);
      const lng = parseFloat(cctv.coordX);
      const position = new kakao.maps.LatLng(lat, lng);

      const marker = new kakao.maps.Marker({ position, map });
      allMarkers.push(marker);

      const infowindow = new kakao.maps.InfoWindow({
        content: `
          <div style="width: 380px; padding: 10px;">
            <div style="text-align: center;">
              <strong>${cctv.cctvname}</strong><br/>
              <video id="video_${cctv.coordX}" width="340" height="220" controls autoplay muted></video>
            </div>
          </div>`
      });

      // ✅ marker 클릭 시 연결된 리스트 강조 + 스크롤
      kakao.maps.event.addListener(marker, 'click', () => {
        if (openInfoWindow) {
          openInfoWindow.close();
          document.querySelectorAll("#cctvList li").forEach(el => el.classList.remove("active-list-item"));
        }

        infowindow.open(map, marker);
        openInfoWindow = infowindow;

        const video = document.getElementById(`video_${cctv.coordX}`);
        if (Hls.isSupported()) {
          const hls = new Hls();
          hls.loadSource(cctv.cctvurl);
          hls.attachMedia(video);
        } else if (video.canPlayType("application/vnd.apple.mpegurl")) {
          video.src = cctv.cctvurl;
        }

        if (marker.__linkedListItem) {
          marker.__linkedListItem.classList.add("active-list-item");
          marker.__linkedListItem.scrollIntoView({ behavior: "smooth", block: "center" });
        }
      });

      const li = document.createElement("li");
      li.innerHTML = `
        <span style="font-weight:bold">${cctv.cctvname}</span>
        <span style="color:gray; font-size:12px;"> [${cctv.type === 'ex' ? '고속도로' : '국도'}]</span>
      `;

      // ✅ 리스트 클릭 시 지도 이동 + 마커 클릭
      li.addEventListener("click", () => {
        document.querySelectorAll("#cctvList li").forEach(el => el.classList.remove("active-list-item"));
        li.classList.add("active-list-item");
        map.panTo(position);
        setTimeout(() => kakao.maps.event.trigger(marker, 'click'), 200);
      });

      // ✅ 리스트와 마커 연결
      marker.__linkedListItem = li;
      cctvListEl.appendChild(li);
    });
  }

  // ✅ 필터 버튼 클릭
  filterButtons.forEach(btn => {
    btn.addEventListener("click", () => {
      filterButtons.forEach(b => b.classList.remove("active"));
      btn.classList.add("active");
      const type = btn.dataset.type;
      renderCCTVList(type, searchInput.value.trim());
    });
  });

  // ✅ CCTV 이름 검색
  searchInput.addEventListener("input", () => {
    renderCCTVList(currentFilter, searchInput.value.trim());
  });

  // ✅ 지도 클릭 시 인포윈도우 및 리스트 강조 해제
  kakao.maps.event.addListener(map, 'click', () => {
    if (openInfoWindow) {
      openInfoWindow.close();
      openInfoWindow = null;
      document.querySelectorAll("#cctvList li").forEach(el => el.classList.remove("active-list-item"));
    }
  });

  // ✅ 장소 검색 기능
  const placesService = new kakao.maps.services.Places();
  const searchKeyword = document.getElementById("searchKeyword");
  const searchButton = document.getElementById("searchButton");
  let searchMarker = null;

  if (searchKeyword && searchButton) {
    searchKeyword.addEventListener("keydown", (e) => {
      if (e.key === "Enter") searchButton.click();
    });

    searchButton.addEventListener("click", () => {
      const keyword = searchKeyword.value.trim();
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
            searchMarker = new kakao.maps.Marker({ position: latlng, map });
          }
        } else {
          alert("검색 결과가 없습니다.");
        }
      });
    });
  }
}
