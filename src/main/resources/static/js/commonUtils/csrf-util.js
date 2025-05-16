
window.MyApp = window.MyApp || {};

window.MyApp.utils = (function () {
  let csrfToken = null;
  let csrfHeader = null;
  let initialized = false;

  function init() {

    if (initialized) return;

    console.log("CSRF 토큰 초기화 중...");

    const tokenMeta = document.querySelector('meta[name="_csrf"]');
    const headerMeta = document.querySelector('meta[name="_csrf_header"]');

    if (tokenMeta && headerMeta) {
      csrfToken = tokenMeta.getAttribute("content");
      csrfHeader = headerMeta.getAttribute("content");
      initialized = true;
      console.log("CSRF 토큰 인식 완료: ", csrfToken);
      console.log("CSRF 헤더 인식 완료: ", csrfHeader);
    } else {
      console.error(
        "CSRF 토큰/헤더 메타 태그를 찾을 수 없습니다. HTML을 확인하세요."
      );
    }
  }

  // 스크립트가 로드될 때 즉시 초기화(CSRF 토큰, 헤더 인식용) 함수 호출
  init();

  return {
    getCsrfToken: function () {
      return csrfToken;
    },

    getCsrfHeader: function () {
      return csrfHeader;
    },

    getCsrfHeadersAsObject: function () {
      if (!csrfHeader || !csrfToken) {
        console.error(
          "CSRF 토큰/헤더가 초기화되지 않았습니다. 헤더 객체를 생성할 수 없습니다."
        );
        return null;
      }
      return { [csrfHeader]: csrfToken };
    },
  };
})();

console.log("CSRF 유틸리티 모듈 로드 완료.");
