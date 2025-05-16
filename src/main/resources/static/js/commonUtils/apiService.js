// src/main/resources/static/vue.js/common/apiService.js
const apiService = {
  csrfToken: null,
  csrfHeader: null,

  initialize() {
    const tokenElement = document.querySelector('meta[name="_csrf"]');
    const headerElement = document.querySelector('meta[name="_csrf_header"]');
    if (tokenElement && headerElement) {
      this.csrfToken = tokenElement.getAttribute("content");
      this.csrfHeader = headerElement.getAttribute("content");
      console.log('ApiService 초기화 완료. CSRF 헤더 정보 설정 완료.');
    } else {
      console.error("CSRF 헤더 정보를 찾을 수 없습니다.");
    }
  },

  async postRequest(endpoint, bodyData) {
    const baseHeaders = { "Content-Type": "application/json" };
    const csrfHeaders = window.MyApp?.utils?.getCsrfHeadersAsObject(); // Optional chaining

    if (!csrfHeaders) {
      console.warn(`${endpoint} 요청에 CSRF 헤더 정보를 추가할 수 없습니다.`);
    }

    const headers = { ...baseHeaders, ...(csrfHeaders || {}) }; // CSRF 헤더 병합 (존재할 경우)

    try {
      const response = await fetch(endpoint, {
        method: "POST",
        headers: headers,
        body: JSON.stringify(bodyData),
      });

      let responseData = null;
      try {
        responseData = await response.json();
      } catch (e) {
        console.warn(`${endpoint} 로부터의 JSON 응답 파싱 실패`);
      }

      if (!response.ok) {
        const message = responseData?.message || response.statusText || `${response.status} 상태로 요청 실패`;
        console.error(`API 오류 (${endpoint}): ${response.status} - ${message}`);
        return { success: false, status: response.status, message: message, data: responseData };
      }

      return { success: true, status: response.status, message: responseData?.message, data: responseData };

    } catch (error) {
      console.error(`API 요청 중 네트워크 오류 또는 기타 오류 발생 (${endpoint}):`, error);
      return { success: false, status: null, message: '네트워크 오류 또는 fetch 실패', error: error };
    }
  }
};

apiService.initialize(); 