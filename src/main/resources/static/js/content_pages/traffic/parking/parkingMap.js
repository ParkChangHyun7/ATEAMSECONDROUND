window.addEventListener("DOMContentLoaded", async()=>{
    const mapContainer = document.getElementById("map");
    if(!mapContainer) return;

    //kakao지도 초기 설정
    const map = new kakao.maps.Map(mapContainer,{
        center: new kakao.maps.LatLng(37.5665, 126.978), //지도 중심 좌표 (서울시청 기준)
        level: 6 //지도의 확대/축소 수준 (숫자가 클수록 축소됨)
       });

       try{
        //서버에서 주차장 정보를 비동기 요청(REST API 방식)
        const res = await fetch("/api/parking"); //주차장 API 호출
        const parkingList = await res.json(); //응답 데이터를 JSON 형태로 파싱

        //받아온 주차장 리스트를 하나씩 순회하면서 마커 생성
        parkingList.forEach(parking =>{
            //지도 위에 표시할 마커 생성
            const marker = new kakao.maps.Marker({
              map, //마커를 표시할 지도 객체
              position: new kako.maps.LatLng(parking.lat,parking.lng), //위도/경도 위치
              title: parking.name //마우스 올렸을 때 나올 툴팁
            });

            //마커 클릭 시 표시될 인포윈도우 (정보창)
            const info = new kako.maps.InfoWindow({
                content:`
                <div style="padding:5px;font-size:14px;">
                  <b>${parking.name}</b><br/>
                  ${parking.address}
                  </div>
                `, //HTML 형식의 내용
                removable:true //닫기 버튼 표시 여부
            });

            //마커 클릭 이벤트 등록: 클릭하면 인포윈도우가 열림
            kakao.maps.event.addListener(marker, "click", () =>{
                info.open(map, marker); //인포윈도우를 지도 위 마커 위치에 표시
            });
        });
        
        //마커 렌더링이 완료되었음을 콘솔에 출력
        console.log("주차장 마커 렌더링 완료");

      } catch (e){
        //에러 발생 시 콘솔에 출력
        console.error("주차장 데이터를 불러오지 못했습니다.");
      }
    });