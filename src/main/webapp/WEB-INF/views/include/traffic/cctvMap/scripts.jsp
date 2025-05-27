<script src="https://dapi.kakao.com/v2/maps/sdk.js?appkey=ddc3a5089cd2e1974490b196aab220ec"></script>
<script src="/js/content_pages/traffic/cctv/cctvMap.js" charset="UTF-8"></script>


<script>
  document.addEventListener("DOMContentLoaded", () => {
    if (typeof initializeCCTVMap === "function") {
      initializeCCTVMap();
    } else {
      console.error("⚠ initializeCCTVMap 함수가 정의되지 않았습니다.");
    }
  });
</script>
