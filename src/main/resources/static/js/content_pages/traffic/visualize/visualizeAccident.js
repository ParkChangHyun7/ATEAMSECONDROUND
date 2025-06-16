import { createApp, ref, computed, watch, onMounted, nextTick } from "vue";

const ALL_YEARS = ["2020", "2021", "2022", "2023", "2024"];

const app = createApp({
  setup() {
    // --- 1. 상태 (State) ---
    const trafficData = ref([]);
    const selectedAccidentTypes = ref([]);
    const selectedChartType = ref('bar');
    const selectedYearsCheckbox = ref([]);
    const selectedCasualtyMetrics = ref([]);
    
    const userQuestion = ref('');
    const chatMessages = ref([]);
    const isLoading = ref(false);
    const chatMessagesContainer = ref(null);
    
    let myChart = null;
    const isDataLoaded = ref(false);

    // --- 2. 계산된 속성 (Computed) ---
    const currentYearForPie = computed(() => selectedYearsCheckbox.value[0] || "2024");
    
    const chartData = computed(() => {
        const labels = [];
        const datasets = [];

        if (selectedChartType.value === 'bar') {
            // 막대 그래프의 X축 라벨과 데이터셋 구성 로직을 시나리오별로 분리
            const isSingleAccidentTypeSelected = selectedAccidentTypes.value.length === 1;

            if (isSingleAccidentTypeSelected) {
                // 시나리오 1: 사고 유형이 1개만 선택된 경우 (전체 포함)
                labels.push(...ALL_YEARS); // X축: 연도

                const accidentType = selectedAccidentTypes.value[0];
                selectedCasualtyMetrics.value.forEach((metric) => {
                    const dataValues = ALL_YEARS.map(year => {
                        const item = trafficData.value.find(d => String(d.연도) === year && d["대상사고 구분명"] === accidentType);
                        return item ? item[metric] : 0;
                    });

                    datasets.push({
                        label: `${accidentType} - ${metric}`,
                        data: dataValues,
                        backgroundColor: getRandomColor(),
                        borderColor: getRandomColor(),
                        borderWidth: 1,
                        type: 'bar', // 막대 차트 타입 명시
                    });
                });
            } else { // selectedAccidentTypes.value.length > 1
                // 시나리오 2: 사고 유형이 두 개 이상 선택된 경우
                const yearToDisplay = selectedYearsCheckbox.value[0] || ALL_YEARS[ALL_YEARS.length - 1];
                labels.push(...selectedAccidentTypes.value); // X축: 대상사고 구분명

                selectedCasualtyMetrics.value.forEach((metric) => {
                    const dataValues = selectedAccidentTypes.value.map(accidentType => {
                        const item = trafficData.value.find(d => String(d.연도) === yearToDisplay && d["대상사고 구분명"] === accidentType);
                        return item ? item[metric] : 0;
                    });

                    datasets.push({
                        label: `${yearToDisplay}년 - ${metric}`,
                        data: dataValues,
                        backgroundColor: getRandomColor(),
                        borderColor: getRandomColor(),
                        borderWidth: 1,
                        type: 'bar', // 막대 차트 타입 명시
                    });
                });
            }
        } else if (selectedChartType.value === 'pie') {
            const detailedFields = ["과속", "중앙선 침범", "신호위반", "안전거리 미확보", "안전운전 의무 불이행", "보행자 보호의무 위반", "기타", "차대사람", "차대차", "차량단독"];
            const currentYearData = trafficData.value.find(item => item["대상사고 구분명"] === "전체" && item.연도 === Number(currentYearForPie.value));
            if (currentYearData) {
                labels.push(...detailedFields);
                datasets.push({
                    label: `${currentYearForPie.value}년 사고 원인`,
                    data: detailedFields.map((field) => currentYearData[field] || 0),
                    backgroundColor: ["rgba(255, 99, 132, 0.6)", "rgba(54, 162, 235, 0.6)", "rgba(255, 206, 86, 0.6)", "rgba(75, 192, 192, 0.6)", "rgba(153, 102, 255, 0.6)", "rgba(255, 159, 64, 0.6)", "rgba(199, 199, 199, 0.6)", "rgba(83, 102, 200, 0.6)", "rgba(12, 202, 100, 0.6)", "rgba(72, 122, 12, 0.6)"],
                    borderColor: ["rgba(255, 99, 132, 1)", "rgba(54, 162, 235, 1)", "rgba(255, 206, 86, 1)", "rgba(75, 192, 192, 1)", "rgba(153, 102, 255, 1)", "rgba(255, 159, 64, 1)", "rgba(199, 199, 199, 1)", "rgba(83, 102, 200, 1)", "rgba(12, 202, 100, 1)", "rgba(72, 122, 12, 1)"],
                    borderWidth: 1,
                });
            }
        } else if (selectedChartType.value === 'line') {
            labels.push(...ALL_YEARS);
            const typesForLine = selectedAccidentTypes.value.length > 0 ? selectedAccidentTypes.value : ["전체"];
            selectedCasualtyMetrics.value.forEach((field) => {
                typesForLine.forEach((accidentType) => {
                    const dataValues = ALL_YEARS.map(year => {
                        const item = trafficData.value.find(d => String(d.연도) === year && d["대상사고 구분명"] === accidentType);
                        return item ? item[field] : 0;
                    });
                    datasets.push({ label: `${accidentType} - ${field}`, data: dataValues, backgroundColor: getRandomColor(), borderColor: getRandomColor(), borderWidth: 2, tension: 0, fill: false, type: 'line' });
                });
            });
        }
        return { labels, datasets };
    });

    // --- 3. 메서드 (Methods) ---
    const fetchData = async () => {
      try {
        const response = await fetch("/com/json/trafficAccident.json");
        trafficData.value = await response.json();
        isDataLoaded.value = true;
      } catch (error) {
        console.error("데이터 로딩 오류:", error);
      }
    };

    const renderChart = () => {
        if (!document.getElementById("accidentChart")) return;
        const ctx = document.getElementById("accidentChart").getContext("2d");
        if (myChart) myChart.destroy();
        myChart = new Chart(ctx, {
            type: selectedChartType.value,
            data: chartData.value,
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    x: {
                        stacked: false,
                        barPercentage: 0.8,
                        categoryPercentage: 0.7,
                    },
                    y: {
                        stacked: false,
                        beginAtZero: true,
                    },
                },
                plugins: {
                    tooltip: {
                        mode: 'index',
                        intersect: false,
                    },
                },
            },
        });
    };

    const getRandomColor = () => `#${Math.floor(Math.random()*16777215).toString(16).padStart(6, '0')}`;
    
    const scrollToBottom = () => {
        nextTick(() => {
            if (chatMessagesContainer.value) {
                chatMessagesContainer.value.scrollTop = chatMessagesContainer.value.scrollHeight;
            }
        });
    };
    
    const addMessage = (type, text) => {
      chatMessages.value.push({ type, text, timestamp: new Date() });
      scrollToBottom();
    };

    const formatTime = (timestamp) => timestamp.toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit' });

    const sendQuestion = () => {
        if (!userQuestion.value.trim() || isLoading.value) return;

        const question = userQuestion.value;
        addMessage('user', question); // 사용자 메시지 추가
        userQuestion.value = '';
        isLoading.value = true;

        // AI 응답을 위한 자리 표시자(placeholder) 메시지 추가
        const aiMessageIndex = chatMessages.value.length;
        chatMessages.value.push({ type: 'ai', text: '', timestamp: new Date() });
        scrollToBottom();

        const eventSource = new EventSource(`/api/chat/dataAnalyze-stream?userQuestion=${encodeURIComponent(question)}`);

        eventSource.onmessage = (event) => {
            try {
                const data = JSON.parse(event.data);
                if (data.response) {
                    // 자리 표시자 메시지의 text를 직접 업데이트
                    chatMessages.value[aiMessageIndex].text += data.response;
                    scrollToBottom();
                }
                if (data.done) {
                    isLoading.value = false;
                    eventSource.close();
                }
            } catch (e) {
                console.error("Error parsing stream data", e);
            }
        };

        eventSource.onerror = (err) => {
            isLoading.value = false;
            eventSource.close();
            // 마지막 메시지에 에러 표시 (선택적)
            const lastMessage = chatMessages.value[chatMessages.value.length - 1];
            if (lastMessage && lastMessage.type === 'ai' && lastMessage.text.trim() === '') {
                 lastMessage.text = "[스트림 연결 중 오류가 발생했습니다.]";
            }
            console.error("EventSource failed or stream ended:", err);
        };
    };

    // --- 4. 감시자 (Watchers) ---
    // 모든 상태 변경 로직을 하나의 통합된 감시자에서 처리하여 무한 루프를 방지합니다.
    watch([selectedAccidentTypes, selectedYearsCheckbox, selectedChartType, selectedCasualtyMetrics], ([newTypes, newYears, newChartType, newMetrics], [oldTypes, oldYears, oldChartType, oldMetrics]) => {
        
        // 차트 타입이 변경되면 관련 상태를 초기화합니다.
        if (newChartType !== oldChartType) {
            if (newChartType === 'bar') {
                selectedAccidentTypes.value = ['전체'];
                selectedYearsCheckbox.value = [...ALL_YEARS];
                selectedCasualtyMetrics.value = ["사고건수", "사망자수", "부상자수"];
            } else if (newChartType === 'line') {
                selectedAccidentTypes.value = ['전체'];
                selectedYearsCheckbox.value = [...ALL_YEARS];
                selectedCasualtyMetrics.value = ["사고건수", "사망자수", "부상자수"];
            } else if (newChartType === 'pie') {
                selectedAccidentTypes.value = ['전체'];
                selectedYearsCheckbox.value = [ALL_YEARS[ALL_YEARS.length - 1]];
            }
            return; // 상태 변경 후 즉시 종료하여, 다음 틱에서 다시 watch 실행
        }

        // 막대 그래프의 복잡한 선택 로직
        if (newChartType === 'bar') {
            const types = [...newTypes];
            const oldSelection = oldTypes || [];
            
            // 사용자가 '전체'를 새로 클릭한 경우
            if (types.includes("전체") && !oldSelection.includes("전체")) {
                selectedAccidentTypes.value = ["전체"];
                selectedYearsCheckbox.value = [...ALL_YEARS]; // '전체' 선택 시 모든 연도 허용
                return;
            }

            // '전체'가 포함된 상태에서의 로직
            if (types.includes("전체")) {
                if (types.length > 2) {
                    // '전체'와 다른 항목 1개가 있는 상태에서 추가 선택 시 '전체'를 제거
                    selectedAccidentTypes.value = types.filter(t => t !== "전체");
                    return;
                }
            } else { // '전체'가 포함되지 않은 상태에서의 로직
                if (types.length > 5) {
                    // 5개 초과 시 가장 오래된 항목(첫번째) 제거
                    selectedAccidentTypes.value = types.slice(1);
                    return;
                }
            }
            
            // --- 대장님 요청 핵심 수정: 사고 구분명 개수에 따른 연도 선택 조정 ---
            if (selectedAccidentTypes.value.length > 1 || selectedAccidentTypes.value.includes("전체")) {
                // 사고 유형이 2개 이상 선택됐거나 '전체'가 포함된 경우: 연도 1개만 선택 강제
                if (newYears.length > 1) {
                    selectedYearsCheckbox.value = [newYears[newYears.length-1]];
                }
            } else if (selectedAccidentTypes.value.length === 1 && !selectedAccidentTypes.value.includes("전체")) {
                // 사고 유형이 1개만 선택된 경우 (전체 제외): 모든 연도 허용
                selectedYearsCheckbox.value = [...ALL_YEARS];
            }
        }
        
        // 파이 그래프 연도 선택 제약
        if (newChartType === 'pie') {
            if (newYears.length > 1) {
                // 파이차트는 하나의 연도만 선택 가능하므로, 가장 최근 선택된 연도만 유지합니다.
                // newYears 배열의 마지막 요소를 선택 (사용자가 마지막으로 클릭한 연도)
                selectedYearsCheckbox.value = [newYears[newYears.length - 1]];
                return;
            }
        }

        // 최종적으로 차트 렌더링
        nextTick(() => {
            renderChart();
        });

    }, { deep: true });

    // 사상자 수 변경은 항상 차트를 다시 그립니다.
    watch(selectedCasualtyMetrics, () => {
        renderChart();
    }, { deep: true });
    
    // --- 5. 생명주기 훅 (Lifecycle Hooks) ---
    onMounted(async () => {
      await fetchData();
      selectedAccidentTypes.value = ['전체'];
      selectedYearsCheckbox.value = [...ALL_YEARS];
      selectedCasualtyMetrics.value = ["사고건수", "사망자수", "부상자수"];
    });

    // --- 6. 반환 (Return) ---
    return {
      selectedAccidentTypes,
      selectedChartType,
      selectedYearsCheckbox,
      selectedCasualtyMetrics,
      userQuestion,
      chatMessages,
      isLoading,
      chatMessagesContainer,
      isDataLoaded,
      sendQuestion,
      formatTime
    };
  }
});

app.mount("#visualizeApp");
