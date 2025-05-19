<%@ page contentType="text/html"; charset=UTF-8" language="java"%>
<!DOCTYPE html>
<html>
<head>
    <title>서울시 주차장 지도</title>
    <!--Kakao Map API 불러오기-->
    <script src="http://dapi.kakao.com/v2/maps/sdk.js?appkey=0756a2cf5e5556917df83822336fd9b5&autoload=false"></script>
    <script src="/js/content_pages/traffic/parking/parkingMap.js"></script>
    <!-- api 키를 직접 명시하기 보다는 변수에 입력된 값으로 나타내는 방법이 권장됨. (카카오 지도만 하더라도 하루 30만회 호출 제한) -->
     <!-- 실제 서비스 같은 경우 악의적인 목적으로 해당 api키를 먹통으로 만들 수 있음 = 일종의 보안 사고 -->
      <!-- 지도 같이 공개적으로 보이는 서비스의 경우에는 api키가 공개되는 경우가 많지만, 공개 접근성이라도 떨어트리는 것이 좋음 -->
</head>
<body>
    <div id="map" style="width:100%;height:400px;"></div>
</body>
</html>