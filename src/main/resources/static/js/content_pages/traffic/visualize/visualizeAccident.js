import { createApp, ref, computed, watch, onMounted } from "vue";

const app = createApp({
  setup() {
    const trafficData = ref([]);
    const selectedYear = ref("2024");
    const selectedAccidentTypes = ref(["전체"]);
    const selectedChartType = ref("bar");
    let myChart = null;

    const fetchData = async () => {
      try {
        const response = await fetch("/com/json/trafficAccident.json");
        const data = await response.json();
        trafficData.value = data;
        console.log("데이터 불러오기 성공함:", trafficData.value);
      } catch (error) {
        console.error("데이터를 불러오는 중 오류 발생함:", error);
      }
    };

    const filteredData = computed(() => {
      let data = trafficData.value;

      if (selectedYear.value) {
        data = data.filter((item) => item.연도 === Number(selectedYear.value));
      }

      if (selectedAccidentTypes.value.length > 0) {
        data = data.filter((item) =>
          selectedAccidentTypes.value.includes(item["대상사고 구분명"])
        );
      }
      console.log("필터링된 데이터:", data);
      return data;
    });

    const chartData = computed(() => {
      const labels = [];
      const datasets = [];

      if (selectedAccidentTypes.value.includes("전체")) {
        // '전체'가 선택된 경우 상세 데이터 (사망자수 이후)
        const detailedFields = [
          "과속",
          "중앙선 침범",
          "신호위반",
          "안전거리 미확보",
          "안전운전 의무 불이행",
          "보행자 보호의무 위반",
          "기타",
          "차대사람",
          "차대차",
          "차량단독",
        ];

        const currentYearData = filteredData.value.find(
          (item) =>
            item["대상사고 구분명"] === "전체" &&
            item.연도 === Number(selectedYear.value)
        );

        if (currentYearData) {
          if (selectedChartType.value === "pie") {
            labels.push(...detailedFields);
            datasets.push({
              label: `${selectedYear.value}년 사고 원인`,
              data: detailedFields.map((field) => currentYearData[field] || 0),
              backgroundColor: [
                "rgba(255, 99, 132, 0.6)",
                "rgba(54, 162, 235, 0.6)",
                "rgba(255, 206, 86, 0.6)",
                "rgba(75, 192, 192, 0.6)",
                "rgba(153, 102, 255, 0.6)",
                "rgba(255, 159, 64, 0.6)",
                "rgba(199, 199, 199, 0.6)",
                "rgba(83, 102, 200, 0.6)",
                "rgba(12, 202, 100, 0.6)",
                "rgba(72, 122, 12, 0.6)",
              ],
              borderColor: [
                "rgba(255, 99, 132, 1)",
                "rgba(54, 162, 235, 1)",
                "rgba(255, 206, 86, 1)",
                "rgba(75, 192, 192, 1)",
                "rgba(153, 102, 255, 1)",
                "rgba(255, 159, 64, 1)",
                "rgba(199, 199, 199, 1)",
                "rgba(83, 102, 200, 1)",
                "rgba(12, 202, 100, 1)",
                "rgba(72, 122, 12, 1)",
              ],
              borderWidth: 1,
            });
          } else {
            labels.push(...detailedFields);
            datasets.push({
              label: "사고 건수",
              data: detailedFields.map((field) => currentYearData[field] || 0),
              backgroundColor: "rgba(54, 162, 235, 0.6)",
              borderColor: "rgba(54, 162, 235, 1)",
              borderWidth: 1,
            });
          }
        }
      } else {
        // '전체'가 선택되지 않은 경우 요약 데이터 (사고건수, 사망자수, 부상자수)
        const summaryFields = ["사고건수", "사망자수", "부상자수"];
        labels.push(...selectedAccidentTypes.value);

        summaryFields.forEach((field) => {
          const dataValues = [];
          selectedAccidentTypes.value.forEach((type) => {
            const item = filteredData.value.find(
              (d) =>
                d["대상사고 구분명"] === type &&
                d.연도 === Number(selectedYear.value)
            );
            dataValues.push(item ? item[field] : 0);
          });
          datasets.push({
            label: field,
            data: dataValues,
            backgroundColor: getRandomColor(),
            borderColor: getRandomColor(),
            borderWidth: 1,
            type: selectedChartType.value === "line" ? "line" : "bar",
            fill: false,
          });
        });
      }

      return { labels, datasets };
    });

    const renderChart = () => {
      const ctx = document.getElementById("accidentChart").getContext("2d");

      if (myChart) {
        myChart.destroy();
      }

      if (filteredData.value.length === 0) {
        console.warn("표시할 데이터가 없음.");
        return;
      }

      myChart = new Chart(ctx, {
        type:
          selectedChartType.value === "pie" ? "pie" : selectedChartType.value,
        data: chartData.value,
        options: {
          responsive: true,
          maintainAspectRatio: false,
          scales: {
            x: {
              display: selectedChartType.value !== "pie",
            },
            y: {
              display: selectedChartType.value !== "pie",
              beginAtZero: true,
            },
          },
          plugins: {
            legend: {
              display: true,
              position: "top",
            },
            title: {
              display: true,
              text: `${
                selectedYear.value
              }년 교통사고 현황 (${selectedAccidentTypes.value.join(", ")})`,
            },
          },
        },
      });
    };

    const getRandomColor = () => {
      const letters = "0123456789ABCDEF";
      let color = "#";
      for (let i = 0; i < 6; i++) {
        color += letters[Math.floor(Math.random() * 16)];
      }
      return color;
    };

    watch(
      [selectedYear, selectedAccidentTypes, selectedChartType],
      () => {
        renderChart();
      },
      { deep: true }
    );

    onMounted(() => {
      fetchData().then(() => {
        renderChart();
      });
    });

    return {
      selectedYear,
      selectedAccidentTypes,
      selectedChartType,
      filteredData,
      chartData,
    };
  },
});

app.mount("#visualizeApp");
