import { createApp, ref, onMounted, computed } from 'vue';

const commentsApp = {
    setup() {
        const commentsAppElement = document.getElementById('comments-app');
        const postJson = commentsAppElement.dataset.post;
        const boardConfigJson = commentsAppElement.dataset.boardConfig;
        const currentUserJson = commentsAppElement.dataset.currentUser;

        const post = ref(postJson ? JSON.parse(postJson) : null);
        const boardConfig = ref(boardConfigJson ? JSON.parse(boardConfigJson) : null);
        const currentUser = ref(currentUserJson ? JSON.parse(currentUserJson) : null);

        const comments = ref([]);
        const newCommentContent = ref('');

        // 날짜 형식 변환 함수 (YYYY-MM-DD hh:mm:ss)
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

        // 댓글 목록을 가져옴
        const fetchComments = async () => {
            if (!post.value || !post.value.boardId || !post.value.id) return;
            try {
                const url = `/boards/${post.value.boardId}/posts/${post.value.id}/comments`;
                const response = await window.apiService.getRequest(url);
                if (response.success) {
                    comments.value = response.data;
                } else {
                    console.error('댓글 로드 실패:', response.message);
                    comments.value = [];
                }
            } catch (error) {
                console.error('댓글 로드 중 오류 발생:', error);
                comments.value = [];
            }
        };

        // 새 댓글을 작성함
        const submitComment = async () => {
            if (!post.value || !post.value.boardId || !post.value.id || !newCommentContent.value.trim() || !currentUser.value || !currentUser.value.userId) {
                alert('댓글 내용 또는 사용자 정보가 유효하지 않습니다.');
                return;
            }

            const commentData = {
                content: newCommentContent.value,
                // 익명 댓글 여부 (TODO: 나중에 필요시 UI 추가 및 값 설정)
                isAnonymous: 0 
            };

            try {
                const url = `/boards/${post.value.boardId}/posts/${post.value.id}/comments`;
                const response = await window.apiService.postRequest(url, commentData);
                if (response.success) {
                    newCommentContent.value = ''; // 작성 후 입력 필드를 초기화함
                    fetchComments(); // 댓글 목록을 다시 불러옴
                } else {
                    alert(`댓글 작성 실패: ${response.message || '알 수 없는 오류'}`);
                }
            } catch (error) {
                console.error('댓글 작성 중 오류 발생:', error);
                alert('댓글 작성 중 오류가 발생했습니다.');
            }
        };

        // 댓글을 수정함 (TODO: 기능 구현 예정)
        const editComment = (comment) => {
            console.log('댓글 수정 버튼 클릭:', comment);
            alert('댓글 수정 기능은 아직 구현되지 않았습니다.');
        };

        // 댓글을 삭제함
        const deleteComment = async (commentId) => {
            if (!confirm('정말 이 댓글을 삭제하시겠습니까?')) return;
            if (!post.value || !post.value.boardId || !post.value.id) return;
            try {
                const url = `/boards/${post.value.boardId}/posts/${post.value.id}/comments/${commentId}`;
                const response = await window.apiService.deleteRequest(url);
                if (response.success) {
                    alert('댓글이 삭제되었습니다.');
                    fetchComments(); // 댓글 목록을 다시 불러옴
                } else {
                    alert(`댓글 삭제 실패: ${response.message || '알 수 없는 오류'}`);
                }
            } catch (error) {
                console.error('댓글 삭제 중 오류 발생:', error);
                alert('댓글 삭제 중 오류가 발생했습니다.');
            }
        };

        onMounted(() => {
            fetchComments(); // 컴포넌트 마운트 시 댓글을 불러옴
        });

        return {
            comments,
            newCommentContent,
            currentUser,
            formatDisplayDate,
            submitComment,
            editComment,
            deleteComment,
        };
    }
};

document.addEventListener('DOMContentLoaded', () => {
    createApp(commentsApp).mount('#comments-app');
});

