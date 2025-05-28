<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
  <title>CCTV Map</title>
  <%@ include file="/WEB-INF/views/include/traffic/cctvMap/resources.jsp" %>
</head>
<body>
  <div id="map" style="width: 100%; height: 600px;"></div>
  <%@ include file="/WEB-INF/views/include/traffic/cctvMap/scripts.jsp" %>
</body>
</html>





<%-- 
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
  <title>CCTV Map</title>
  <!-- resources.jsp 포함 -->
  <%@ include file="/WEB-INF/views/include/traffic/cctvMap/resources.jsp" %>
</head>
<body>
  <div id="app">
    <div id="map" style="width: 100%; height: 500px;"></div>
    <div id="error" style="color: red;"></div>
  </div>

  <!-- Vue 및 cctvMap.js 로드 -->
  <script src="https://unpkg.com/vue@3"></script>
  <script src="/js/content_pages/traffic/cctv/cctvMap.js"></script>
  <script>
    const { createApp } = Vue;

    createApp({
      mounted() {
        // cctvMap.js의 초기화 함수 호출
        if (typeof initializeCCTVMap === 'function') {
          initializeCCTVMap();
        } else {
          console.error('initializeCCTVMap 함수를 찾을 수 없습니다. cctvMap.js가 로드되었는지 확인하세요.');
        }
      },
    }).mount('#app');
  </script>
</body>
</html> --%>


<%-- <%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>

<!-- 지도 컨테이너 -->
<div id="map" style="width: 100%; height: 600px;"></div>

</body>
</html> --%>