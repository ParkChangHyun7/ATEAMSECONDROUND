<!-- 백엔드에서는 EUC-KR로 응답하게 만들고 프론트에서는 UTF-8로 읽어와서 깨짐 증상 생김. -->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
  <!-- 위 코드를 없애더라도 base 자체가 UTF-8로 로드하고, base에서도 위 코드를 제거한다 하더라도  -->
  <!-- 인코딩 타입을 명시하지 않은 경우 한글 브라우저들은 대체로 UTF-8로 로드함 -->
  <!-- 고로... EUC-KR로 응답하게 만들고 인코딩 타입 변경 없이 js로 불러오니 깨졌음 -->
  <!-- 근데 그거랑 또 다르게 vue 모듈이 utf-8을 명시해주지 않으면 다른 인코딩 타입을 사용해서 -->
  <!-- 코드 내의 console.log 출력 결과가 깨지기도 함. 그러니... 준 그대로 사용합시다! -->
  <script type="module" src="/js/content_pages/traffic/parking/parkingMap.js"></script>