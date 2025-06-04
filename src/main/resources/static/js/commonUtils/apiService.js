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

  _prepareRequestOptions(method, bodyData) {
    let headers = {};
    let body = bodyData;

    // CSRF 헤더 추가
    const csrfHeaders = window.MyApp?.utils?.getCsrfHeadersAsObject();
    if (csrfHeaders) {
      headers = { ...headers, ...csrfHeaders };
    } else {
      console.warn(`요청에 CSRF 헤더 정보를 추가할 수 없습니다. (메소드: ${method})`);
    }

    // FormData 여부에 따라 Content-Type 및 body 처리
    if (bodyData instanceof FormData) {
      // FormData를 사용하는 경우, Content-Type 헤더를 명시적으로 설정하지 않음. 브라우저가 자동으로 multipart/form-data로 설정함.
    } else {
      // JSON 데이터인 경우
      headers['Content-Type'] = 'application/json';
      body = JSON.stringify(bodyData);
    }

    return {
      method: method,
      headers: headers,
      body: body,
    };
  },

  async postRequest(endpoint, bodyData) {
    const options = this._prepareRequestOptions('POST', bodyData);
    return this._sendRequest(endpoint, options);
  },

  async putRequest(endpoint, bodyData) {
    const options = this._prepareRequestOptions('PUT', bodyData);
    return this._sendRequest(endpoint, options);
  },

  async deleteRequest(endpoint, bodyData = {}) {
    const options = this._prepareRequestOptions('DELETE', bodyData);
    return this._sendRequest(endpoint, options);
  },

  async _sendRequest(endpoint, options) {
    try {
      const response = await fetch(endpoint, options);

      let responseData = null;
      // 응답이 JSON 형식이 아닐 수 있으므로 파싱 시도
      try {
        responseData = await response.json();
      } catch (e) {
        console.warn(`${endpoint} 로부터의 JSON 응답 파싱 실패 또는 응답 없음.`);
        // JSON 파싱 실패 시, 응답 텍스트를 메시지로 사용
        responseData = { message: await response.text() };
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

// 외부에서 apiService 객체에 접근할 수 있도록 내보냅니다.
window.apiService = apiService; 