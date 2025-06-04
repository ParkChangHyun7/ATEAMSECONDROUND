import { createApp, ref, onMounted, onUnmounted } from 'vue';

const app = createApp({
    template: `
        <div class="weather-temp-sky-rain">
            <span class="weather-temp">{{ weatherData.temp }}°C</span>
            <div :class="'weather-icon ' + weatherData.skyIconClass" :data-tooltip="weatherData.skyTooltip"></div>
            <span class="weather-rain">{{ weatherData.rainStatus }}</span>
        </div>
        <div class="weather-details">
            <span class="wds-default">미세</span>
            <span :class="'dust-value-' + weatherData.pm10Status.class">{{ weatherData.pm10Status.text }}</span>
            <span class="wds-default">초미세</span>
            <span :class="'dust-value-' + weatherData.pm25Status.class">{{ weatherData.pm25Status.text }}</span>
        </div>
    `,
    setup() {
        const weatherData = ref({
            temp: '',
            skyStatus: '',
            rainStatus: '',
            pm10: null,
            pm25: null,
            skyIconClass: 'sunny',
            skyTooltip: '날씨 정보를 가져오고 있습니다.',
            pm10Status: { class: 0, text: '' },
            pm25Status: { class: 0, text: '' },
        });

        let updateInterval = null; // 날씨 정보 갱신을 위한 인터벌 ID 저장함.

        // 먼지 상태 판단 함수.
        const getDustStatus = (value, type) => {
            if (type === 'pm10') {
                if (value <= 30) return { class: 0, text: '좋음' };
                if (value <= 80) return { class: 1, text: '보통' };
                if (value <= 150) return { class: 2, text: '나쁨' };
                return { class: 3, text: '매우나쁨' };
            } else { // pm25
                if (value <= 15) return { class: 0, text: '좋음' };
                if (value <= 50) return { class: 1, text: '보통' };
                if (value <= 100) return { class: 2, text: '나쁨' };
                return { class: 3, text: '매우나쁨' };
            }
        };

        // 날씨 아이콘 및 툴팁 설정 함수.
        const getWeatherIconInfo = (skyStatus) => {
            switch(skyStatus) {
                case '맑음': return { class: 'sunny', tooltip: '맑은 날씨에요.' };
                case '구름조금': return { class: 'partly-cloudy', tooltip: '하늘에 구름이 조금 껴 있어요.' };
                case '구름많음': return { class: 'mostly-cloudy', tooltip: '하늘에 구름이 많아요.' };
                case '흐림': return { class: 'cloudy', tooltip: '하늘이 흐려 보이네요.' };
                default: return { class: 'sunny', tooltip: '날씨 정보를 가져오는데 실패했어요.' };
            }
        };

        // 날씨 정보를 가져오는 함수.
        const fetchWeatherData = async () => {
            try {
                console.log('날씨 데이터를 가져오는 중...');
                const response = await fetch('/api/indexWeather');
                
                if (!response.ok) {
                    throw new Error('날씨 정보를 가져오는데 실패함.');
                }
                
                const data = await response.json();
                
                const skyInfo = getWeatherIconInfo(data.skyStatus);

                weatherData.value.temp = data.temp;
                weatherData.value.skyStatus = data.skyStatus;
                weatherData.value.rainStatus = data.rainStatus;
                weatherData.value.pm10 = data.pm10;
                weatherData.value.pm25 = data.pm25;
                weatherData.value.skyIconClass = skyInfo.class;
                weatherData.value.skyTooltip = skyInfo.tooltip;
                weatherData.value.pm10Status = getDustStatus(data.pm10, 'pm10');
                weatherData.value.pm25Status = getDustStatus(data.pm25, 'pm25');

                console.log('날씨 데이터 업데이트됨.');
            } catch (error) {
                console.error('날씨 데이터를 가져오는 중 오류 발생함:', error);
                weatherData.value.skyTooltip = '날씨 정보를 가져오는데 실패했어요.';
            }
        };

        // 매시간 정각+1분에 날씨 정보 갱신 예약함.
        const scheduleWeatherUpdate = () => {
            const now = new Date();
            const minutesUntilNextHour = (61 - now.getMinutes()) % 60; // 다음 시간 1분 후까지 남은 분 계산함.
            const secondsUntilNextMinute = (60 - now.getSeconds()) % 60;
            const millisecondsUntilNextUpdate = (minutesUntilNextHour * 60 + secondsUntilNextMinute) * 1000;
            
            console.log(`다음 날씨 업데이트까지 ${millisecondsUntilNextUpdate / 1000}초 남음.`);

            setTimeout(() => {
                fetchWeatherData();
                // 이후 매시간 갱신.
                updateInterval = setInterval(fetchWeatherData, 60 * 60 * 1000); 
            }, millisecondsUntilNextUpdate);
        };

        onMounted(() => {
            fetchWeatherData(); // 컴포넌트 마운트 시 즉시 날씨 정보 가져옴.
            scheduleWeatherUpdate(); // 날씨 정보 갱신 스케줄링함.
        });

        onUnmounted(() => {
            if (updateInterval) {
                clearInterval(updateInterval); // 컴포넌트 언마운트 시 인터벌 해제함.
            }
        });

        return { weatherData };
    },
});

// Vue 앱을 #weather-app 엘리먼트에 마운트함.
app.mount('#weather-app'); 