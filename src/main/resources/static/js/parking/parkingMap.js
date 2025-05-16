//페이지 로딩되면 자동 실행
window.onload = function(){
    fetch("/api/parking")  //Controller에서 JSON데이터 가져오기
        .then(response=> response.json())
        .then(data=>{
            // 카카오맵 초기화
            const container = document.getElementById("map");
            const options = {
                center: new kakao.maps.LatLng(37.5665,126.9780), //서울 시청 좌표
                level: 5,
            };
            const map = new kakao.maps.Map(container, options);
        

        //받아온 데이터로 마커 생성
        data.forEach(item=>{
            const marker = new kakao.maps.Marker({
              map: map,
              position: new Kakao.maps.LatLng(item.lat, item.lng),
              title: item.name,
            });
        });
    });
};