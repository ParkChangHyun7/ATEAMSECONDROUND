/** 
 * CSRF 헤더를 포함해서 API 호출을 하지 않으면 보안 필터링으로 Forbidden 403 에러 발생함!!!
 * 
 * 주석 작성일 기준으로 현재 프로젝트에서는 모든 요청에 대해서 CSRF 헤더를 포함하고 있음.
 * 
 * CSRF는 사용자 의도와 무관하게 공격자 의도대로 움직이게 만드는.. 뭐 스크립트 파싱, 이상 데이터 전송 등을
 * 
 * 막아주는 필터링 기능을 제공해서 요즘은 어느 사이트를 막론하고 필수로 적용하는 추세..
 * 
 * 불편해도 꼭 사용해야됨요. 해당 처리하는 모듈은 src/main/common/security/SecurityConfig.java를
 * 
 * 통해서 활성화 됐음. 스프링 부트 시큐리티 사용하면 기본적으로 딸려서 적용 됨.
 * 
 * CSRF 추가 설정을 하고 싶다면 해당 파일에 설정을 추가하거나 CSRF 자바 모듈 만들어서 관리하면 됨..
 * 
 * 나도 자세히는 모르는데 그냥 그렇다. A=B라고 하면 그런가 보다.. 하고 있음....
 */

const ValidationUtils = {
  /**
   * 함수 호출 지연용 디바운스 메서드.
   * func - 실행 시킬 함수
   * delay - 지연 시간
   * return - 디바운스 완료 된 함수
   */
  debounce(func, delay) {
    let timeoutId;
    return function (...args) {
      clearTimeout(timeoutId);
      timeoutId = setTimeout(() => {
        func.apply(this, args);
      }, delay);
    };
  },

  // CSRF 헤더 요청부
  getCsrfHeaders() {
    const tokenElement = document.querySelector('meta[name="_csrf"]');
    const headerElement = document.querySelector('meta[name="_csrf_header"]');

    if (tokenElement && headerElement) {
      const token = tokenElement.getAttribute("content");
      const header = headerElement.getAttribute("content");
      if (token && header) {
        return { [header]: token };
      }
    }
    console.error("CSRF 메타 태그를 찾을 수 없거나 비어있습니다.");
    return null;
  },

  /**
   * 서버 API 호출을 위한 공통 비동기 함수. CSRF 토큰 자동 포함 (POST, PUT, PATCH).
   * @param {string} endpoint - API 엔드포인트 URL
   * @param {string} [method='GET'] - HTTP 메서드
   * @param {object|null} [body=null] - 요청 본문 객체 (JSON으로 변환됨)
   * @returns {Promise<object>} 서버 응답 Promise (성공 시 JSON 파싱된 객체 또는 { success: true, status: number }, 실패 시 reject)
   * @throws {Error} 네트워크 오류, 서버 오류 (HTTP 상태 코드 >= 400), CSRF 토큰 누락 시 Error 객체 throw. 에러 객체는 status, data 속성을 포함할 수 있음.
   */
  async apiCall(endpoint, method = "GET", body = null) {
    const csrfHeaders = this.getCsrfHeaders();
    // GET 요청이 아니면서 CSRF 토큰이 없으면 에러 발생
    if (method.toUpperCase() !== "GET" && !csrfHeaders) {
      throw new Error("CSRF 토큰이 없습니다. 비정상적인 요청입니다.");
    }

    const options = {
      method: method.toUpperCase(),
      headers: {
        // GET이 아니고 body가 있으면 Content-Type 설정
        ...(method.toUpperCase() !== "GET" && body
          ? { "Content-Type": "application/json" }
          : {}),
        // GET이 아니면 CSRF 헤더 포함
        ...(method.toUpperCase() !== "GET" ? csrfHeaders : {}),
      },
    };

    // body가 있고, 데이터 전송 메서드일 경우 body를 JSON 문자열로 변환하여 추가
    if (body && ["POST", "PUT", "PATCH"].includes(method.toUpperCase())) {
      options.body = JSON.stringify(body);
    }

    try {
      const response = await fetch(endpoint, options);

      const contentType = response.headers.get("content-type");
      let responseData = null;
      // 응답이 JSON 형식일 경우 파싱
      if (contentType && contentType.includes("application/json")) {
        responseData = await response.json();
      } else {
        console.log("응답이 JSON 형식이 아닙니다. Content-Type:", contentType);
      }

      // 응답 상태 코드가 2xx가 아니면 에러 처리
      if (!response.ok) {
        // 서버가 JSON 형식으로 에러 메시지를 보냈다면 사용, 아니면 기본 메시지
        const errorMessage =
          responseData?.message || `HTTP error! Status: ${response.status}`;
        const error = new Error(errorMessage);
        error.status = response.status; // 에러 객체에 상태 코드 추가
        error.data = responseData; // 에러 객체에 응답 데이터 추가
        throw error; // 커스텀 에러 객체 throw
      }

      // 성공 응답 처리 (JSON 데이터가 있으면 해당 데이터 반환, 없으면 성공 상태 객체 반환)
      return responseData || { success: true, status: response.status };
    } catch (error) {
      console.error(`API call to ${endpoint} failed:`, error);
      // 이미 status 속성이 있는 커스텀 에러가 아니면 (네트워크 오류 등) 새로운 에러 객체 생성
      if (!(error instanceof Error && "status" in error)) {
        throw new Error(
          error.message || "API 호출 중 예상치 못한 에러가 발생했습니다."
        );
      }
      throw error; // 서버에서 발생시킨 커스텀 Error 객체 또는 네트워크 에러를 다시 throw
    }
  },
  // 폐기됨. 원래 display 설정 관리하는 유틸리티로 활용 할랬는데 block 말고 flex나 inline-block 등
  // 다른 거 적용해야 되는 경우가 세부적으로 계속 생겨서 폐기. 나중에 필요하면 재활용.
  // updateTextStyle(element, isValid) {
  //   if (element) {
  //     // isValid가 true면 display: none, false면 display: block
  //     element.style.display = isValid ? "none" : "block";
  //   }
  // },

  /**
   * @param {string} containerSelector - 입력 필드를 감싸는 컨테이너 요소의 CSS 선택자. 필요할때 보이고/안 보이고 조절용으로 필요함 (예: ".phone_verification_numbers")
   * @param {string} inputSelector - 인증번호 입력 input 5개에 대한 css 선택자 (예: ".phone-vnum")
   * @param {number} codeLength - 필요한 입력 필드/코드의 길이. 지금은 5 고정값이긴 한데, 코드 재활용 가능성 생각해서 인자로 넣음.
   * @param {function} onCompleteDebounced - 인증번호 모두 입력 되면 실행될 콜백 함수(주석 작성일 기준으론 서버에 인증 요청하는 함수만 고정 값 임) 호출 및 결합된 값 전달
   * @param {number} [debounceDelay=500] - 디바운스 지연 시간 (밀리초). 고정값 500으로 설정해둠. 넘 굼뜨다 싶으면 나중에 요소별로 받는 방안도 고려 가능.
   */
  setupVerificationCodeInput(containerSelector, inputSelector, codeLength, onCompleteDebounced, debounceDelay = 500) {
    const container = document.querySelector(containerSelector);
    if (!container) {
      console.error(`ValidationUtils: 인증번호 컨테이너 요소('${containerSelector}')를 찾을 수 없습니다.`);
      return;
    }
    const inputs = container.querySelectorAll(inputSelector);
    if (inputs.length !== codeLength) {
        console.error(`ValidationUtils: '${inputSelector}' 선택자에 해당하는 입력 필드를 ${codeLength}개 찾을 수 없습니다 (찾은 개수: ${inputs.length}).`);
        return;
    }
    console.log(`ValidationUtils: Setting up verification code input for ${containerSelector}`);

    const debouncedCallback = this.debounce(() => {
        let combinedCode = "";
        let allFilled = true;
        inputs.forEach(input => {
            // 각 필드가 한 자리 숫자인지 확인
            if (input.value.length === 1 && /^\d$/.test(input.value)) {
                combinedCode += input.value;
            } else {
                allFilled = false;
            }
        });

        if (allFilled && combinedCode.length === codeLength) {
            console.log(`ValidationUtils: 모든(${codeLength}) 입력 필드가 채워졌습니다. 콜백 실행 (코드: ${combinedCode}).`);
            onCompleteDebounced(combinedCode); // 콜백 함수에 결합된 코드 전달
        } else {
             console.log(`ValidationUtils: 디바운스 콜백 실행 조건 미충족 (모든 필드가 채워지지 않았거나 유효하지 않음).`);
        }
    }, debounceDelay);

    inputs.forEach((input, index) => {
      // Input 이벤트: 입력 값 검증 및 다음 필드로 포커스 이동
      input.addEventListener("input", (e) => {
        let value = e.target.value;

        // 숫자 외 입력 방지 (붙여넣기 등 대비)
        e.target.value = value.replace(/[^0-9]/g, "");
        value = e.target.value; // 정제된 값으로 업데이트

        // 한 자리 수 초과 입력 방지
        if (value.length > 1) {
            e.target.value = value.slice(0, 1);
            value = e.target.value; // 한 자리로 자른 값 업데이트
        }

        // 입력 값이 있고 다음 필드가 존재하면 포커스 이동
        if (value.length === 1 && index < inputs.length - 1) {
          inputs[index + 1].focus();
        }

        // 모든 필드가 채워졌는지 확인 후 디바운스 콜백 트리거
        let allFilledCheck = true;
        inputs.forEach(inp => { if (inp.value.length !== 1) allFilledCheck = false; });
        if (allFilledCheck) {
            console.log("ValidationUtils: 모든 필드 채워짐 감지, 디바운스 타이머 시작.");
            debouncedCallback(); // 디바운스 시작/재시작
        }
      });

      // Keydown 이벤트: Backspace 처리 (이전 필드로 포커스 이동)
      input.addEventListener("keydown", (e) => {
        if (e.key === "Backspace") {
            // 현재 필드가 비어있고 첫 번째 필드가 아니면
            if (!e.target.value && index > 0) {
                // 이전 필드로 포커스 이동 (값은 지우지 않음)
                inputs[index - 1].focus();
            }
            // 현재 필드에 값이 있을 때 Backspace 누르면 현재 값만 지워짐 (input 이벤트에서 처리됨)
        }
        // 선택적: 좌우 화살표 키로 이동
        else if (e.key === 'ArrowLeft' && index > 0) {
            inputs[index - 1].focus();
        } else if (e.key === 'ArrowRight' && index < inputs.length - 1) {
            inputs[index + 1].focus();
        }
      });

      // Paste 이벤트: 붙여넣기 처리
      input.addEventListener('paste', (e) => {
            e.preventDefault(); // 기본 붙여넣기 동작 방지
            const pasteData = e.clipboardData?.getData('text').replace(/\D/g, ''); // 숫자만 추출
            if (!pasteData) return;
            console.log(`ValidationUtils: Paste detected in field ${index}. Data: ${pasteData}`);

            let currentInputIndex = index;
            for (let i = 0; i < pasteData.length && currentInputIndex < inputs.length; i++) {
                inputs[currentInputIndex].value = pasteData[i]; // 한 글자씩 순차적으로 채우기
                if (currentInputIndex < inputs.length - 1) {
                    inputs[currentInputIndex + 1].focus(); // 다음 칸으로 포커스 이동
                }
                currentInputIndex++;
            }

             // 붙여넣기 후 모든 필드가 채워졌는지 확인
            let allFilledCheck = true;
            inputs.forEach(inp => { if (inp.value.length !== 1) allFilledCheck = false; });
            if (allFilledCheck) {
                console.log("ValidationUtils: 붙여넣기 후 모든 필드 채워짐 감지, 디바운스 타이머 시작.");
                debouncedCallback();
            }
        });
    });

    console.log(`ValidationUtils: ${containerSelector} 내부 입력 필드 이벤트 리스너 설정 완료.`);
  },
};
