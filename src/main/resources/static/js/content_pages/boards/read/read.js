import { createApp, ref, onMounted, computed } from 'vue';

const postReadApp = {
    setup() {
        const postReadAppElement = document.getElementById('post-read-app');
        const postJson = postReadAppElement.dataset.post;
        const boardConfigJson = postReadAppElement.dataset.boardConfig;
        const currentUserJson = postReadAppElement.dataset.currentUser;

        // JSON 데이터를 파싱하여 반응형 변수에 저장
        const post = ref(postJson ? JSON.parse(postJson) : null);
        const boardConfig = ref(boardConfigJson ? JSON.parse(boardConfigJson) : null);
        const currentUser = ref(currentUserJson ? JSON.parse(currentUserJson) : null);

        // 게시글 제목 (공지 표시 포함)
        const formattedTitle = computed(() => {
            if (!post.value) return '';
            return post.value.isNotice === 1 ? '[공지] ' + post.value.title : post.value.title;
        });

        // 게시글 수정/삭제 권한 여부 판단 (TODO: 필요시 권한 로직 추가)
        const canEdit = computed(() => {
            // 예시: 현재 사용자가 작성자 본인이거나 관리자 권한이 있는지 확인
            // 실제 권한 로직은 서버에서 더 정확히 판단해야 하지만, 여기서는 UI 표시용으로 간단히 구현
            if (!post.value || !currentUser.value || currentUser.value.role == null) return false; // currentUser 또는 role이 없으면 false

            // 관리자 역할 임계값 (예: 100) 이상이면 수정/삭제 가능
            const isAdmin = currentUser.value.role >= 100;

            // 작성자 본인인지 확인 (익명글이 아닌 경우)
            const isAuthor = post.value.isAnonymous !== 1 && post.value.userId === currentUser.value.userId;

            return isAdmin || isAuthor;
        });

        const canDelete = computed(() => {
            // 수정 권한과 동일하게 설정하거나 별도 로직 구현
            return canEdit.value; // 예시로 수정 권한과 동일하게 설정
        });

        // 날짜 형식 변환 함수 (항상 YYYY-MM-DD hh:mm:ss)
        const formatDisplayDate = (dateString) => {
            if (!dateString) return '';
            const date = new Date(dateString);
            if (isNaN(date.getTime())) {
                return dateString;
            }
            const year = date.getFullYear();
            const month = String(date.getMonth() + 1).padStart(2, '0');
            const day = String(date.getDate()).padStart(2, '0');
            const hours = String(date.getHours()).padStart(2, '0');
            const minutes = String(date.getMinutes()).padStart(2, '0');
            const seconds = String(date.getSeconds()).padStart(2, '0');
            return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
        };

        // TODO: 수정, 삭제 기능 메소드
        const editPost = () => {
            console.log('수정 버튼 클릭');
            // TODO: 수정 페이지로 이동 또는 수정 폼 표시
            if (post.value) {
                window.location.href = `/boards/${post.value.boardId}/posts/modify/${post.value.id}`;
            }
        };

        const deletePost = async () => {
            console.log('삭제 버튼 클릭');
            // TODO: 삭제 확인 모달 또는 메시지 표시 후 삭제 API 호출
            if (post.value && confirm('게시글을 정말 삭제하시겠습니까?')) {
                try {
                    const response = await fetch(`/boards/${post.value.boardId}/posts/${post.value.id}`, {
                        method: 'DELETE',
                    });
                    if (response.ok) {
                        alert('게시글이 삭제되었습니다.');
                        // 목록 페이지로 이동
                        window.location.href = `/boards/${post.value.boardId}/posts`;
                    } else {
                        const errorText = await response.text();
                        alert(`게시글 삭제 실패: ${errorText}`);
                    }
                } catch (error) {
                    console.error('게시글 삭제 중 오류 발생:', error);
                    alert('게시글 삭제 중 오류가 발생했습니다.');
                }
            }
        };

        // TODO: 댓글 관련 로직 (가져오기, 작성, 삭제 등)

        // TODO: 파일, 이미지 표시 관련 로직

        return {
            post,
            boardConfig,
            currentUser,
            formattedTitle,
            canEdit,
            canDelete,
            formatDisplayDate,
            editPost,
            deletePost,
        };
    }
};

createApp(postReadApp).mount('#post-read-app');
