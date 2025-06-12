<!-- ① Kakao Maps SDK (서비스 포함, autoload false) -->
<script src="https://dapi.kakao.com/v2/maps/sdk.js?appkey=ddc3a5089cd2e1974490b196aab220ec&autoload=false&libraries=services"></script>

<!-- ② HLS.js (영상 재생용) -->
<script src="https://cdn.jsdelivr.net/npm/hls.js@latest"></script>

<!-- ④ Kakao 로딩 후에 실제 JS 로드 -->
<script>
  kakao.maps.load(() => {
    const script = document.createElement("script");
    script.src = "/js/content_pages/traffic/cctv/cctvMap.js?v=final1"; // ✅ 캐시 제거도 함께
    script.onload = () => {
      if (typeof initializeCCTVMap === "function") {
        initializeCCTVMap(); // 검색창과 지도가 함께 초기화되도록 호출
      } else {
        console.error("⚠️ initializeCCTVMap 함수가 없습니다.");
      }
    };
    document.body.appendChild(script);
  });
</script>