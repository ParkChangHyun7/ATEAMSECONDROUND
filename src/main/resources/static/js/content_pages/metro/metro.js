import {createApp, ref, onMounted} from 'vue';

    createApp({
        setup(){
            const accidents =ref([]);

            onMounted(()=> {
                const appDiv = document.getElementById('subway-accident-app');
                let accidentData = [];
                if (appDiv) {
                    const accidentsJsonString = appDiv.dataset.accidents;
                    if (accidentsJsonString) {
                        accidentData = JSON.parse(accidentsJsonString);
                        accidents.value = accidentData;
                    }
                }

                const canvas = document.getElementById('accidentChart');
                if (!canvas) {
                    console.error('Canvas element with id "accidentChart" not found.');
                    return;
                }
                const ctx = canvas.getContext('2d');

                const labels = accidentData.map(data => data.year);
                const counts = accidentData.map(data => data.count);

                new Chart(ctx,{
                    type: 'bar',
                    data:{
                        labels: labels,
                        datasets:[{
                        label: '출입문 사고 건수',
                        data: counts,
                        backgroundColor: 'rgba(255,99,132,0.5)',
                    }]
                    },
                    Option: {
                        reponsive:true,
                        Plugins:{
                            legend: {display:true}
                        },

                        scales:{
                            y:{beginAtZero:true}
                        }
                    }


                });

            });


          return{};

        }

    }).mount('#subway-accident-app');