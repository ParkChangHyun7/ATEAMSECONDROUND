// 지도 초기화 설정
window.addEventListener('load', function() {
    initMap();
});

function initMap() {
    vw.MapControllerOption = {
        container: "vmap",
        mapMode: "2d-map",
        basemapType: vw.ol3.BasemapType.GRAPHIC,
        controlDensity: vw.ol3.DensityType.FULL,
        interactionDensity: vw.ol3.DensityType.BASIC,
        controlsAutoArrange: true,
        homePosition: { center: [126.9784147, 37.5666805], zoom: 13 },
        initPosition: { center: [126.9784147, 37.5666805], zoom: 13 }
    };
    
    let mapController = new vw.MapController(vw.MapControllerOption);
}

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