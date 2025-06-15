import { createApp, ref, onMounted, nextTick } from 'vue';

document.addEventListener('DOMContentLoaded', () => {
    createApp({
        setup() {
            const userMessage = ref('');
            const messages = ref([]);
            const messagesContainer = ref(null);
            const isLoading = ref(false);
            const isChatOpen = ref(false);

            // 메시지 스크롤을 항상 아래로 유지하는 함수
            const scrollToBottom = () => {
                nextTick(() => {
                    if (messagesContainer.value) {
                        messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
                    }
                });
            };

            const toggleChat = () => {
                isChatOpen.value = !isChatOpen.value;
                if (isChatOpen.value) {
                    scrollToBottom();
                }
            };

            const sendMessage = async () => {
                if (userMessage.value.trim() === '') return;

                const messageText = userMessage.value;
                messages.value.push({ type: 'user', text: messageText });
                userMessage.value = ''; // 입력 필드 초기화
                scrollToBottom();

                isLoading.value = true;
                // 챗봇 로딩 메시지 추가
                const loadingMessage = { type: 'bot', text: '챗봇이 생각 중입니다...' };
                messages.value.push(loadingMessage);
                scrollToBottom();

                try {
                    // CSRF 토큰과 헤더 이름 가져오기
                    const csrfToken = window.MyApp.utils.getCsrfToken();
                    const csrfHeader = window.MyApp.utils.getCsrfHeader();

                    // fetch API를 사용하여 백엔드 API 호출
                    const response = await fetch('/api/classifier', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                            [csrfHeader]: csrfToken, // CSRF 헤더 추가
                        },
                        body: JSON.stringify(messageText),
                    });

                    // 응답 처리
                    if (!response.ok) {
                        // HTTP 오류 상태 (4xx, 5xx)일 경우
                        const errorText = await response.text(); // 오류 메시지를 텍스트로 읽음
                        throw new Error(`HTTP error! status: ${response.status}, message: ${errorText}`);
                    }

                    let botResponseText = '죄송합니다, 알 수 없는 응답이 도착했습니다.';
                    try {
                        // 응답이 비어있지 않다면 JSON으로 파싱
                        if (response.headers.get('content-length') !== '0') {
                            botResponseText = await response.text(); // 챗봇 응답은 순수 텍스트이므로 text() 사용
                        } else {
                            botResponseText = "응답 내용이 없습니다."; // 204 No Content 등의 경우
                        }
                    } catch (jsonError) {
                        console.warn('챗봇 응답이 JSON 형식이 아닐 수 있습니다. 원본 텍스트로 처리합니다.', jsonError);
                        // JSON 파싱 실패 시, 응답을 텍스트로 시도
                        botResponseText = await response.text();
                    }
                    
                    // 로딩 메시지 제거
                    if (messages.value.includes(loadingMessage)) {
                        messages.value.pop();
                    }

                    messages.value.push({ type: 'bot', text: botResponseText });
                } catch (error) {
                    console.error('챗봇 응답 오류:', error);
                    // 로딩 메시지 제거 (에러 발생 시에도)
                    if (messages.value.includes(loadingMessage)) {
                        messages.value.pop();
                    }
                    // 사용자에게 더 친화적인 에러 메시지 제공
                    let errorMessage = '잠시 후 다시 시도해 주세요.';
                    if (error.message) {
                        errorMessage += ` (오류: ${error.message})`;
                    }
                    messages.value.push({ type: 'bot', text: errorMessage });
                } finally {
                    isLoading.value = false;
                    scrollToBottom();
                }
            };

            // JSP에서 메시지를 렌더링하기 위한 Computed Property 또는 Watcher 필요
            // 여기서는 직접 메시지를 HTML로 생성하여 DOM에 추가하는 방식을 사용하지 않고,
            // Vue의 렌더링 시스템을 활용하기 위해 `v-for`를 사용할 것임을 가정
            // 따라서 messages 배열을 직접 사용하는 방식으로 `chat.jsp`도 수정이 필요

            onMounted(() => {
                scrollToBottom(); // 초기 로드 시 스크롤 위치 조정
            });

            return {
                userMessage,
                messages,
                messagesContainer,
                sendMessage,
                isLoading,
                isChatOpen,
                toggleChat,
            };
        },
    }).mount('#chat-widget-wrapper');
}); 