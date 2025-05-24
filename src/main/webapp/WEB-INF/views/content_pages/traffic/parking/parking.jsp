    <!--Kakao Map API 불러오기(지도 기능 사용 시 필수수)-->
    <script src="http://dapi.kakao.com/v2/maps/sdk.js?appkey=0756a2cf5e5556917df83822336fd9b5&autoload=false"></script>
    <!--이 JSP에서만 사용하는 지도 관련 JS파일 (지도 생성 및 마커 처리 등) -->
    <script src="/js/content_pages/traffic/parking/parkingMap.js"></script>
    <!--지도 표시할 영역(id=map인 div가 있어야 JavaScript가 이걸 찾아서 지도 렌더링함) -->
    <div id="map" style="width:100%; height: 400px;"></div>