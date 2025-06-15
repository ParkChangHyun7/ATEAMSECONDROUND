import { createApp, onMounted, ref } from 'vue';

const app = createApp({
    setup() {
        const trafficEvents = ref([]);
        const currentEventIndex = ref(0);
        const currentEventHtml = ref('');
        const noticePosts = ref([]);
        const naverNews = ref([]);
        let slideInterval = null;

        const formatDistance = (dist) => {
            const distance = parseFloat(dist);
            if (isNaN(distance)) {
                return '';
            }
            if (distance < 1000) {
                return `${distance.toFixed(0)}m`;
            } else {
                return `${(distance / 1000).toFixed(1)}km`;
            }
        };

        const formatDateTime = (dateStr, timeStr) => {
            if (!dateStr || !timeStr) return '';

            const year = dateStr.substring(2, 4);
            const month = dateStr.substring(4, 6);
            const day = dateStr.substring(6, 8);
            const hour = timeStr.substring(0, 2);
            const minute = timeStr.substring(2, 4);

            return `${year}.${month}.${day} ${hour}:${minute}`;
        };

        const displayCurrentEvent = () => {
            if (trafficEvents.value.length === 0) {
                currentEventHtml.value = '표시할 교통 이벤트 정보가 없습니다.';
                return;
            }

            const event = trafficEvents.value[currentEventIndex.value];
            if (!event) {
                currentEventHtml.value = '이벤트 정보를 불러오는 중 오류가 발생했습니다.';
                return;
            }
            
            const formattedStartTime = formatDateTime(event.starttime ? event.starttime.substring(0, 8) : '', event.starttime ? event.starttime.substring(8, 12) : '');
            const formattedEndTime = formatDateTime(event.endtime ? event.endtime.substring(0, 8) : '', event.endtime ? event.endtime.substring(8, 12) : '');
            const formattedDist = formatDistance(event.distmeter);
            const accInfoWithLineBreaks = event.accinfo ? event.accinfo.replace(/\r\n?|\n/g, '<br>') : '';

            currentEventHtml.value = `
                <p>🏳️ ${event.startfrom || ''} 부터</p>
                <p>🏴 ${event.endat || ''} 까지 총 🌌 ${formattedDist} 구간</p>
                <br/>
                <p>🛣️ 도로명: ${event.roadname || ''}</p>
                <br/>
                <p>🕒 일시: ${formattedStartTime} 부터</p>
                <p>${formattedEndTime} 까지</p>
                <p>🚨 진행 상황: ${accInfoWithLineBreaks}</p>
            `;
        };

        const resetSlideTimer = () => {
            if (slideInterval) {
                clearInterval(slideInterval);
            }
            slideInterval = setInterval(() => {
                showNextEvent();
            }, 5000);
        };

        const showNextEvent = () => {
            currentEventIndex.value = (currentEventIndex.value + 1) % trafficEvents.value.length;
            displayCurrentEvent();
            resetSlideTimer();
        };

        const showPrevEvent = () => {
            currentEventIndex.value = (currentEventIndex.value - 1 + trafficEvents.value.length) % trafficEvents.value.length;
            displayCurrentEvent();
            resetSlideTimer();
        };

        onMounted(() => {
            kakao.maps.load(() => {
                const mapContainer = document.getElementById('vmap');
                const mapOption = { 
                    center: new kakao.maps.LatLng(37.5666805, 126.9784147),
                    level: 7
                };
                
                const map = new kakao.maps.Map(mapContainer, mapOption);

                // TODO: 여기에 지도 관련 추가 기능 (마커, 컨트롤 등) 구현해야 함.
            });

            const menuBtn = document.querySelector('.menu-btn');
            const nav = document.querySelector('.nav ul');
            
            if (menuBtn && nav) {
                menuBtn.addEventListener('click', () => {
                    nav.style.display = nav.style.display === 'none' || nav.style.display === '' 
                        ? 'flex' 
                        : 'none';
                });
            }

            fetch('/api/index/trafficevent')
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`HTTP 오류! 상태: ${response.status}`);
                    }
                    return response.json();
                })
                .then(data => {
                    trafficEvents.value = data;
                    displayCurrentEvent();
                    resetSlideTimer();
                })
                .catch(error => {
                    currentEventHtml.value = '교통 이벤트 데이터를 불러오는데 실패했습니다. 오류: ' + error.message;
                });
            
            fetch('/api/index/notices')
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`HTTP 오류! 상태: ${response.status}, 본문: ${response.text()}`);
                    }
                    return response.json();
                })
                .then(data => {
                    noticePosts.value = data;
                })
                .catch(error => {
                    // 공지사항 데이터 조회 오류는 개발자 도구 콘솔에서만 확인
                });

            fetch('http://localhost:8000/news/naver')
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`HTTP 오류! 상태: ${response.status}, 본문: ${response.text()}`);
                    }
                    return response.json();
                })
                .then(data => {
                    naverNews.value = data;
                })
                .catch(error => {
                    // 네이버 뉴스 데이터 조회 오류는 개발자 도구 콘솔에서만 확인
                });
        });

        return {
            currentEventHtml,
            showNextEvent,
            showPrevEvent,
            noticePosts,
            naverNews
        };
    }
});

app.mount('#app');