<script>
     window.onload = function () {
    var container = document.getElementById('map');
    var options = {
        center: new kakao.maps.LatLng(37.5665, 126.9780), // 서울시청 위치
        level: 3
    };

    var map = new kakao.maps.Map(container, options);

    var marker = new kakao.maps.Marker({
        map: map,
        position: new kakao.maps.LatLng(37.5665, 126.9780) // 마커도 서울시청 위치
      });
    };
</script>