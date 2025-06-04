import { createApp, onMounted } from 'vue';

const app = createApp({
    template: '<div id="kakao-map-container" style="width:100%;height:100%;"></div>', // Vue 컴포넌트의 템플릿에 지도가 들어갈 div를 정의함.
    setup() {
        onMounted(() => {
            // Kakao Maps SDK가 로드된 후 지도 초기화 작업을 수행함.
            kakao.maps.load(() => {
                const mapContainer = document.getElementById('kakao-map-container'); // Vue 템플릿에 정의된 지도를 표시할 div를 가져옴.
                const mapOption = { 
                    center: new kakao.maps.LatLng(37.5666805, 126.9784147), // 지도의 중심좌표 (서울 시청)
                    level: 7 // 지도의 확대 레벨
                };
                
                // 지도를 생성함.
                const map = new kakao.maps.Map(mapContainer, mapOption);

                // TODO: 여기에 지도 관련 추가 기능 (마커, 컨트롤 등) 구현해야 함.
            });
        });
    }
});

// Vue 앱을 #vmap 엘리먼트에 마운트함. (index.jsp의 #vmap 엘리먼트를 Vue 앱이 관리하게 됨)
app.mount('#vmap');

// 모바일 메뉴 토글 기능
document.addEventListener('DOMContentLoaded', function() {
    const menuBtn = document.querySelector('.menu-btn');
    const nav = document.querySelector('.nav ul');
    
    if (menuBtn && nav) {
        menuBtn.addEventListener('click', function() {
            nav.style.display = nav.style.display === 'none' || nav.style.display === '' 
                ? 'flex' 
                : 'none';
        });
    }
}); 