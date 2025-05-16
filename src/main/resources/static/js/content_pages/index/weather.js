// 날씨 정보를 가져오는 함수
async function fetchWeatherData() {
    try {
        console.log('fetchWeatherData 함수 호출됨');
        const response = await fetch('/api/indexWeather');
        console.log('날씨 API 응답 상태:', response.status);
        
        if (!response.ok) {
            throw new Error('날씨 정보를 가져오는데 실패했습니다.');
        }
        
        const weatherData = await response.json();
        console.log('날씨 데이터:', weatherData);
        updateWeatherInfo(weatherData);
    } catch (error) {
        console.error('날씨 데이터를 가져오는 중 오류 발생:', error);
    }
}

// 날씨 정보를 업데이트하는 함수
function updateWeatherInfo(weatherData) {
    console.log('updateWeatherInfo 함수 호출됨');
    const weatherTemp = document.querySelector('.weather-temp');
    const weatherSky = document.querySelector('.weather-sky');
    const weatherRain = document.querySelector('.weather-rain');
    const weatherDetails = document.querySelector('.weather-details');
    
    // 기온 정보 업데이트
    const temp = weatherData.temp;
    const skyStatus = weatherData.skyStatus;
    const rainStatus = weatherData.rainStatus;
    console.log('기온:', temp, '하늘 상태:', skyStatus, '비 상태:', rainStatus);
    
    weatherTemp.textContent = `${temp}°C`;
    weatherRain.textContent = `${rainStatus}`;
    
    // 날씨 아이콘 업데이트
    weatherSky.innerHTML = ''; // 기존 내용 삭제
    const weatherIcon = document.createElement('div');
    weatherIcon.className = 'weather-icon';
    
    // 날씨 상태에 따른 아이콘 클래스와 툴팁 추가
    switch(skyStatus) {
        case '맑음':
            weatherIcon.classList.add('sunny');
            weatherIcon.setAttribute('data-tooltip', '맑은 날씨에요.');
            break;
        case '구름조금':
            weatherIcon.classList.add('partly-cloudy');
            weatherIcon.setAttribute('data-tooltip', '하늘에 구름이 조금 껴 있어요.');
            break;
        case '구름많음':
            weatherIcon.classList.add('mostly-cloudy');
            weatherIcon.setAttribute('data-tooltip', '하늘에 구름이 많아요.');
            break;
        case '흐림':
            weatherIcon.classList.add('cloudy');
            weatherIcon.setAttribute('data-tooltip', '하늘이 흐려 보이네요.');
            break;
        default:
            weatherIcon.classList.add('sunny');
            weatherIcon.setAttribute('data-tooltip', '날씨 정보를 가져오는데 실패했어요.');
    }
    
    weatherSky.appendChild(weatherIcon);
    console.log('날씨 아이콘 업데이트됨:', skyStatus);
    
    // 미세먼지 정보 업데이트
    const pm10 = weatherData.pm10;
    const pm25 = weatherData.pm25;
    console.log('미세먼지:', pm10, '초미세먼지:', pm25);
    
    // 미세먼지 상태 판단
    const pm10Status = getDustStatus(pm10, 'pm10');
    const pm25Status = getDustStatus(pm25, 'pm25');
    
    // HTML 업데이트
    weatherDetails.innerHTML = `
        <span class="wds-default">미세</span>
        <span class="dust-value-${pm10Status.class}">${pm10Status.text}</span>
        <span class="wds-default">초미세</span>
        <span class="dust-value-${pm25Status.class}">${pm25Status.text}</span>
    `;
}

// 먼지 상태 판단 함수
function getDustStatus(value, type) {
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
}

console.log('index_weather.js 로드됨');

// 페이지 로드 시 날씨 정보 가져오기
window.onload = function() {
    console.log('window.onload 이벤트 발생');
    fetchWeatherData();
};

// 매시간 정각+1분에 날씨 정보 갱신
function scheduleWeatherUpdate() {
    const now = new Date();
    const minutesUntilNextHour = 61 - now.getMinutes();
    const millisecondsUntilNextHour = minutesUntilNextHour * 60 * 1000;
    
    setTimeout(() => {
        fetchWeatherData();
        // 다음 시간을 위해 다시 스케줄링
        setInterval(fetchWeatherData, 60 * 60 * 1000);
    }, millisecondsUntilNextHour);
}

// 스케줄링 시작
scheduleWeatherUpdate(); 