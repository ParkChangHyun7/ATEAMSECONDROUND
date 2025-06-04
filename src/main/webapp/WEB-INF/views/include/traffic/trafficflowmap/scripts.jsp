<!-- <script src="/js/content_pages/traffic/trafficflowmap/trafficFlowMap.js"></script>
 -->
 
<script>
    // 지도 초기화
    var container = document.getElementById('map');
    var options = {
        center: new kakao.maps.LatLng(37.5665, 126.9751), // 서울 중심
        level: 5 // 줌 레벨
    };
    var map = new kakao.maps.Map(container, options);

    // GeoJSON 데이터 가져오기
    fetch('/api/trafficflowmap_geojson')
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to load GeoJSON: ' + response.statusText);
            }
            return response.json();
        })
        .then(data => {
            if (data.error) {
                console.error('Server error:', data.error);
                alert('데이터를 불러오는 중 오류가 발생했습니다: ' + data.error);
                return;
            }

            console.log('Loaded GeoJSON:', data); // 디버깅용 로그
            var features = data.features;
            if (!features) {
                console.error('No features found in GeoJSON');
                alert('GeoJSON 데이터에 features가 없습니다.');
                return;
            }

            // 각 Feature를 Polyline으로 그리기
            features.forEach(feature => {
                var coordinates = feature.geometry.coordinates;
                var speed = feature.properties.speed || 40; // 기본값 MAX_SPD
                var congestion = 1 - (speed / 40); // 혼잡도 정규화 (40이 최대 속도)

                // LatLng 배열 생성 (WGS84이므로 [위도, 경도] 순서 유지)
                var path = coordinates.map(coord => new kakao.maps.LatLng(coord[1], coord[0]));

                // Polyline 생성
                var polyline = new kakao.maps.Polyline({
                    path: path,
                    strokeWeight: 5,
                    strokeColor: getColor(congestion),
                    strokeOpacity: 0.8,
                    strokeStyle: 'solid'
                });

                // 지도에 Polyline 추가
                polyline.setMap(map);
            });
        })
        .catch(error => {
            console.error('Error loading GeoJSON:', error);
            alert('데이터를 불러오는 중 오류가 발생했습니다: ' + error.message);
        });

    // 혼잡도에 따른 색상 설정
    function getColor(congestion) {
        return congestion > 0.8 ? '#ff0000' : // 매우 혼잡
               congestion > 0.5 ? '#ff9900' : // 중간 혼잡
               '#00ff00'; // 원활
    }
</script>