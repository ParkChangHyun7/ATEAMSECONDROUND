<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE jsp>
        <html lang="ko">

        <head>
            <!-- CSRF 보안 설정에 따라 POST 요청 작성 시 필수로 필요한 영역. 절대 삭제 금지 - 남성욱 -->
            <meta name="_csrf" content="${_csrf.token}" />
            <meta name="_csrf_header" content="${_csrf.headerName}" />

            <!-- 헤드 및 스크립트는 웹 표준의 로드 순서에 따라 1번 부터 순서대로 작성함 임의 순서 변경 금지 - 남성욱 -->

            <!-- 1. 문서 필수 메타 정보 -->
            <meta charset="UTF-8" />
            <meta name="viewport" content="width=device-width, initial-scale=1.0" />

            <!-- 2. 페이지 타이틀 (컨트롤러에서 pageTitle을 스트링으로 받아 표시)-->
            <title>${not empty pageTitle ? pageTitle : '서울교통정보센터'}</title>

            <!-- 3. rel 속성이 preconnect라 외부 리소스 미리 연결 -->
            <link rel="preconnect" href="https://fonts.googleapis.com" />
            <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />

            <!-- 4. 파비콘 -->
            <link rel="icon" href="/images/favicon.ico" />

            <!-- 5. 핵심 스타일 -->
            <link rel="stylesheet" href="/css/common/body/body.css" />
            <link rel="stylesheet" href="/css/common/header/header.css" />
            <link rel="stylesheet" href="/css/common/footer/footer.css" />
            <link rel="stylesheet" href="/css/content_pages/llm/chat.css">

            <!-- 6. 폰트 리소스 -->
            <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@100..900&display=swap"
                rel="stylesheet" />
            <!-- 7. 구글 폰트 아이콘 리소스 & 가변 설정 초기화 일반 폰트랑 별개 사항으로 아이콘 라이브러리 같은 겁니당 -->
            <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@48,200,0,0" />
            <style>

                .meesage-from-server {
                    position: fixed;
                    left: 50%;
                    transform: translateX(-50%);
                    background-color: #333;
                    color: #fff;
                    border-radius: 5px;
                    z-index: 10;

                    top: 5%;
                    opacity: 0;
                    transition: top 1s ease-in-out, opacity 1s ease-in-out;
                    pointer-events: none;
                }

                .meesage-from-server span {
                    display: inline-block;
                    padding: 30px 50px;
                }

                .meesage-from-server.show {
                    top: 10%;
                    opacity: 1;
                    pointer-events: auto;
                }
            </style>

            <!-- Import Map: Bare module specifiers를 CDN URL로 매핑 -->
            <script type="importmap">
            {
              "imports": {
                "vue": "https://unpkg.com/vue@3/dist/vue.esm-browser.js"
              }
            }
            </script>

            <!-- 8. 페이지별 추가 리소스 (컨트롤러에서 인자값으로 받아옴. 없으면 생략됩니당) -->
            <c:if test="${not empty resourcesPage}">
                <c:import url="${resourcesPage}" />
            </c:if>
        </head>

        <body>
            <div class="base-wrapper">
                <!-- 헤더 영역 -->
                <jsp:include page="common/header.jsp" />
                <!-- 서버에서 페이지별 메시지가 있을 때 "message"로 전달 됨 -->
                 <!-- 곧 vue로 매 페이지 로딩 메시지가 있는지 요청하는 방식으로 변경 예정. 임시 코드임 -->
                <c:if test="${not empty message}">
                    <div class="meesage-from-server" id="serverMessage">
                        <span>${message}</span>
                    </div>

                    <script>
                        document.addEventListener("DOMContentLoaded", function () {
                            const serverMessageDiv = document.getElementById("serverMessage");
                            if (serverMessageDiv) {
                                void serverMessageDiv.offsetWidth;
                                requestAnimationFrame(() => {
                                    setTimeout(() => {
                                        serverMessageDiv.classList.add("show");
                                    }, 10);
                                });

                                const handleTransitionEnd = (event) => {
                                    if (
                                        event.propertyName === "opacity" &&
                                        !serverMessageDiv.classList.contains("show")
                                    ) {
                                        serverMessageDiv.style.display = "none";
                                        serverMessageDiv.removeEventListener(
                                            "transitionend",
                                            handleTransitionEnd
                                        );
                                    }
                                };

                                const hideTimeout = setTimeout(() => {
                                    serverMessageDiv.classList.remove("show");
                                    serverMessageDiv.addEventListener(
                                        "transitionend",
                                        handleTransitionEnd
                                    );
                                }, 3000);
                                serverMessageDiv.dataset.hideTimeoutId = hideTimeout;
                            }
                        });
                    </script>
                    <!-- 여기까지가 임시코드 끝 -->
                </c:if>
                <!-- 컨텐츠 영역 -->
               <main>
                <jsp:include page="${contentPage}" />
               </main>
                <!-- 푸터 영역 -->
                <jsp:include page="common/footer.jsp" />
            </div>

            <!-- 챗봇 위젯 래퍼 -->
            <div id="chat-widget-wrapper">
                <!-- 챗봇 토글 버튼 -->
                <button class="chat-toggle-button" @click="toggleChat">
                    <span class="material-symbols-outlined">chat</span>
                </button>

                <!-- 챗봇 컨테이너 (chat.jsp 내용 이식) -->
                <div id="chat-app" class="chat-container" :class="{ 'show': isChatOpen }">
                    <div class="chat-header">
                        날씨 정보 알림이
                        <button class="chat-close-button" @click="toggleChat">
                            <span class="material-symbols-outlined">close</span>
                        </button>
                    </div>
                    <div class="chat-messages" ref="messagesContainer">
                        <!-- 챗 메시지 표시 위치 -->
                        <div v-for="(message, index) in messages" :key="index" :class="['message', message.type]">
                            {{ message.text }}
                        </div>
                    </div>
                    <div class="chat-input">
                        <input type="text" v-model="userMessage" @keyup.enter="sendMessage" placeholder="날씨를 물어보세요...">
                        <button @click="sendMessage">전송</button>
                    </div>
                </div>
            </div>

            <!-- CSRF 유틸리티 공통 호출 영역 삭제 금지 - 남성욱 -->
            <script src="/js/commonUtils/csrf-util.js"></script>
            <script src="/js/commonUtils/apiService.js"></script>
            <!-- vue 사용을 위해 scriptPages보다 먼저 호출되어야함 -->
            <!-- vue 도입 개발 버전 (자세한 경고 포함) -->
            <script src="https://unpkg.com/vue@3/dist/vue.esm-browser.js" type="module"></script>
            <!-- 개발 끝나고 변경할 프로덕션 버전 (최적화됨) -->
            <!-- <script src="https://unpkg.com/vue@3/dist/vue.esm-browser.prod.js" type="module"></script> -->
            <script src="/vue.js/common/header/main-header.vue.js" type="module"></script>
            <script src="/vue.js/common/header/header.vue.js" type="module"></script>
            <script src="/vue.js/common/header/main-header.js" type="module"></script>
            <!-- 9. 페이지별 스크립트 (컨트롤러에서 인자값으로 받아옴. 이것도 없으면 생략됩니당) -->
            <script src="/js/content_pages/llm/chat.js" type="module"></script>
            <c:if test="${not empty scriptsPage}">
                <c:import url="${scriptsPage}" />
        </c:if>
        </body>

        </html>