<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script type ="module">
    import {createApp, ref, onMounted} from 'https://unpkg.com/vue@3/dist/vue.esm-browser.js';

    createApp({
        setup(){
            const accidents =ref([]);

            onMounted(()=> {
                accidents.value=JSON.parse('<%=accidents%>');

                //출입문 사고만 필터링 (혹시 다른 유형이 추가되더라도 유연하게 처리 가능)
                new Chart(ctx,{
                    type: 'bar',
                    data:{
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

</script>