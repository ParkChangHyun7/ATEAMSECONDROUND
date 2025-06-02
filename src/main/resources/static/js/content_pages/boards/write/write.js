import { createApp, ref } from 'vue';

const writeApp = {
    setup() {
        const postWriteAppElement = document.getElementById('post-write-app');
        
        // CSRF 토큰 가져오기
        const csrfToken = document.querySelector('meta[name="_csrf"]').content;
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
        
        // 게시판 설정 및 사용자 정보 파싱
        const boardConfig = ref(JSON.parse(postWriteAppElement.dataset.boardConfig || '{}'));
        const currentUser = ref(JSON.parse(postWriteAppElement.dataset.currentUser || 'null'));
        const isAdmin = ref(currentUser.value?.role === 100); // 관리자 여부

        // 게시글 데이터
        const post = ref({
            title: '',
            content: '',
            isAnonymous: boardConfig.value.isAnonymous === 1 ? 1 : 0,
            isNotice: 0
        });

        // 폼 제출 처리
        const submitForm = async () => {
            try {
                const response = await fetch(`/boards/${boardConfig.value.boardId}/posts`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        [csrfHeader]: csrfToken
                    },
                    body: JSON.stringify({
                        ...post.value,
                        boardId: boardConfig.value.boardId,
                        isAnonymous: post.value.isAnonymous ? 1 : 0,
                        isNotice: post.value.isNotice ? 1 : 0
                    })
                });
                
                if (response.ok) {
                    const result = await response.json();
                    // 글 목록 페이지로 리다이렉트
                    window.location.href = `/boards/${boardConfig.value.boardId}/posts`;
                } else {
                    const error = await response.json();
                    alert('글 작성 중 오류가 발생했습니다: ' + (error.message || '알 수 없는 오류'));
                }
            } catch (error) {
                console.error('Error:', error);
                alert('글 작성 중 오류가 발생했습니다.');
            }
        };

        // 취소 처리
        const cancel = () => {
            if (confirm('작성 중인 내용이 사라집니다. 정말로 취소하시겠습니까?')) {
                window.history.back();
            }
        };
        
        return {
            post,
            boardConfig,
            isAdmin,
            submitForm,
            cancel
        };
    }
};

// Vue 앱 마운트
createApp(writeApp).mount('#post-write-app');