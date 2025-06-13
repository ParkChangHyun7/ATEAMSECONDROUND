// ✅ DOMContentLoaded 보장
function initializeCCTVMap() {
  let openInfoWindow = null;
  const map = new kakao.maps.Map(document.getElementById("map"), {
    center: new kakao.maps.LatLng(37.55, 126.98),
    level: 7
  });

  const cctvListEl = document.getElementById("cctvList");
  const filterButtons = document.querySelectorAll(".filter-btn");
  const searchInput = document.getElementById("cctvSearchInput");
  const searchKeyword = document.getElementById("searchKeyword");
  const searchButton = document.getElementById("searchButton");
  const scrollTopBtn = document.getElementById("scrollToTopBtn");
  const cctvListWrapper = document.getElementById("cctvListWrapper");

  let allData = [];
  let allMarkers = [];
  let currentFilter = "all";
  let markerToListItemMap = new Map();
  let searchMarker = null;

  const placesService = new kakao.maps.services.Places();

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
    markerToListItemMap.clear();
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

      const li = document.createElement("li");
      li.innerHTML = `
        <span style="font-weight:bold">${cctv.cctvname}</span>
        <span style="color:gray; font-size:12px;"> [${cctv.type === 'ex' ? '고속도로' : '국도'}]</span>
      `;

      li.addEventListener("click", () => {
        document.querySelectorAll("#cctvList li").forEach(el => el.classList.remove("active-list-item"));
        li.classList.add("active-list-item");
        map.setCenter(position);
        setTimeout(() => kakao.maps.event.trigger(marker, 'click'), 200);
      });

      kakao.maps.event.addListener(marker, 'click', () => {
        if (openInfoWindow) {
          openInfoWindow.close();
          document.querySelectorAll("#cctvList li").forEach(el => el.classList.remove("active-list-item"));
        }
        infowindow.open(map, marker);
        openInfoWindow = infowindow;
		map.setCenter(position); // ✅ 마커 클릭 시 지도 중심으로 이동
		
		
        const video = document.getElementById(`video_${cctv.coordX}`);
        if (Hls.isSupported()) {
          const hls = new Hls();
          hls.loadSource(cctv.cctvurl);
          hls.attachMedia(video);
        } else if (video.canPlayType("application/vnd.apple.mpegurl")) {
          video.src = cctv.cctvurl;
        }

        const targetLi = markerToListItemMap.get(marker);
        if (targetLi) {
          targetLi.classList.add("active-list-item");
          targetLi.scrollIntoView({ behavior: "smooth", block: "center" });
        }
      });

      markerToListItemMap.set(marker, li);
      cctvListEl.appendChild(li);
    });
  }

  filterButtons.forEach(btn => {
    btn.addEventListener("click", () => {
      filterButtons.forEach(b => b.classList.remove("active"));
      btn.classList.add("active");
      renderCCTVList(btn.dataset.type, searchInput.value.trim());
    });
  });

  searchInput.addEventListener("input", () => {
    renderCCTVList(currentFilter, searchInput.value.trim());
  });

  if (searchKeyword && searchButton) {
    searchKeyword.addEventListener("keydown", e => {
      if (e.key === "Enter") searchButton.click();
    });

    searchButton.addEventListener("click", () => {
      const keyword = searchKeyword.value.trim();
      if (!keyword) return alert("검색어를 입력해 주세요.");
      placesService.keywordSearch(keyword, (data, status) => {
        if (status === kakao.maps.services.Status.OK) {
          const latlng = new kakao.maps.LatLng(data[0].y, data[0].x);
          map.panTo(latlng);
          if (searchMarker) searchMarker.setPosition(latlng);
          else searchMarker = new kakao.maps.Marker({ position: latlng, map });
        } else {
          alert("검색 결과가 없습니다.");
        }
      });
    });
  }

  kakao.maps.event.addListener(map, 'click', () => {
    if (openInfoWindow) {
      openInfoWindow.close();
      openInfoWindow = null;
      document.querySelectorAll("#cctvList li").forEach(el => el.classList.remove("active-list-item"));
    }
  });

  // ✅ TOP 버튼 동작 추가
  if (cctvListWrapper && scrollTopBtn) {
    cctvListWrapper.addEventListener("scroll", () => {
      scrollTopBtn.style.display = cctvListWrapper.scrollTop > 150 ? "block" : "none";
    });

    scrollTopBtn.addEventListener("click", () => {
      cctvListWrapper.scrollTo({ top: 0, behavior: "smooth" });
    });
  }
}
