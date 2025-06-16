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
	
	if (openInfoWindow) {
	  openInfoWindow.close();
	  openInfoWindow = null;
	}

	
	
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

      // CCTV 커스텀 마커 이미지 설정
      const imageSrc = '/images/cctv/cctv.png';
      const imageSize = new kakao.maps.Size(32, 32); // 마커 크기
      const imageOption = { offset: new kakao.maps.Point(16, 32) }; // 마커 기준점
      
      const markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize, imageOption);
      const marker = new kakao.maps.Marker({ 
        position, 
        map,
        image: markerImage
      });
      
      // 마우스 오버 효과 추가
      const hoverImageSize = new kakao.maps.Size(40, 40); // 호버 시 크기 증가
      const hoverImageOption = { offset: new kakao.maps.Point(20, 40) };
      const hoverMarkerImage = new kakao.maps.MarkerImage(imageSrc, hoverImageSize, hoverImageOption);
      
      // 마커에 마우스 이벤트 추가
      kakao.maps.event.addListener(marker, 'mouseover', () => {
        marker.setImage(hoverMarkerImage);
      });
      
      kakao.maps.event.addListener(marker, 'mouseout', () => {
        marker.setImage(markerImage);
      });
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

        // ✅ 스르륵 중앙 이동
        setTimeout(() => {
          map.panTo(position);
        }, 10);

        setTimeout(() => kakao.maps.event.trigger(marker, 'click'), 200);
      });

      kakao.maps.event.addListener(marker, 'click', () => {
        if (openInfoWindow) {
          openInfoWindow.close();
          document.querySelectorAll("#cctvList li").forEach(el => el.classList.remove("active-list-item"));
        }

        // ✅ 마커 클릭 시 부드럽게 중앙 이동
        setTimeout(() => {
          map.panTo(position);
        }, 10);

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
          if (searchMarker) {
            searchMarker.setPosition(latlng);
          } else {
            // 검색 마커용 CCTV 이미지
            const searchImageSrc = '/images/cctv/cctv.png';
            const searchImageSize = new kakao.maps.Size(40, 40); // 검색 마커는 조금 더 크게
            const searchImageOption = { offset: new kakao.maps.Point(20, 40) };
            const searchMarkerImage = new kakao.maps.MarkerImage(searchImageSrc, searchImageSize, searchImageOption);
            
            searchMarker = new kakao.maps.Marker({ 
              position: latlng, 
              map,
              image: searchMarkerImage
            });
          }
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