import { createApp, ref, onMounted, computed } from "vue";

const postReadApp = {
  setup() {
    const postReadAppElement = document.getElementById("post-read-app");
    const postJson = postReadAppElement.dataset.post;
    const boardConfigJson = postReadAppElement.dataset.boardConfig;
    const currentUserJson = postReadAppElement.dataset.currentUser;

    // JSON 데이터를 파싱하여 반응형 변수에 저장
    const post = ref(postJson ? JSON.parse(postJson) : null);
    const boardConfig = ref(
      boardConfigJson ? JSON.parse(boardConfigJson) : null
    );
    const currentUser = ref(
      currentUserJson && currentUserJson !== "null"
        ? JSON.parse(currentUserJson)
        : null
    );

    // 좋아요 상태 변수
    const isLiked = ref(false);

    // 게시글 제목 (공지 표시 포함)
    const formattedTitle = computed(() => {
      if (!post.value) return "";
      return post.value.isNotice === 1
        ? "[공지] " + post.value.title
        : post.value.title;
    });

    // 게시글 수정/삭제 권한 여부 판단
    const canEdit = computed(() => {
      if (!post.value || !currentUser.value) return false; // currentUser가 null이면 false 반환

      // 수정은 '본인이 작성한 글일 경우'가 유일한 조건 (익명글이 아닌 경우)
      const isAuthor =
        post.value.isAnonymous !== 1 &&
        post.value.userId === currentUser.value.userId;
      return isAuthor;
    });

    const canDelete = computed(() => {
      if (!post.value || !currentUser.value) return false; // currentUser가 null이면 false 반환

      const isAdmin =
        currentUser.value.role != null && currentUser.value.role >= 100;
      const isAuthor =
        post.value.isAnonymous !== 1 &&
        post.value.userId === currentUser.value.userId;

      // 삭제는 '본인이 작성한 글일 경우' 또는 '관리자이면서 작성자 레벨이 본인 레벨보다 낮거나 같은 경우'
      // 댓글 작성자 역할이 null이거나 숫자가 아닌 경우를 고려하여 처리
      const postWriterRoleNum =
        post.value.writerRole != null && !isNaN(parseInt(post.value.writerRole))
          ? parseInt(post.value.writerRole)
          : 0;
      const canAdminDelete =
        isAdmin && currentUser.value.role >= postWriterRoleNum;

      return isAuthor || canAdminDelete;
    });

    // 날짜 형식 변환 함수 (조건에 따라 MM-DD HH:MM 또는 YY-MM-DD HH:MM)
    const formatDisplayDate = (dateString) => {
      if (!dateString) return "";
      const date = new Date(dateString);
      if (isNaN(date.getTime())) {
        return dateString;
      }

      const currentYear = new Date().getFullYear();
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, "0");
      const day = String(date.getDate()).padStart(2, "0");
      const hours = String(date.getHours()).padStart(2, "0");
      const minutes = String(date.getMinutes()).padStart(2, "0");

      if (year === currentYear) {
        // 당해년도인 경우: MM-DD HH:MM
        return `${month}-${day} ${hours}:${minutes}`;
      } else {
        // 당해년도가 아닌 경우: YY-MM-DD HH:MM
        return `${String(year).slice(-2)}-${month}-${day} ${hours}:${minutes}`;
      }
    };

    // TODO: 수정, 삭제 기능 메소드
    const editPost = () => {
      console.log("수정 버튼 클릭");
      // TODO: 수정 페이지로 이동 또는 수정 폼 표시
      if (post.value) {
        window.location.href = `/boards/${post.value.boardId}/posts/modify/${post.value.id}`;
      }
    };

    const deletePost = async () => {
      console.log("삭제 버튼 클릭");
      // TODO: 삭제 확인 모달 또는 메시지 표시 후 삭제 API 호출
      if (post.value && confirm("게시글을 정말 삭제하시겠습니까?")) {
        try {
          const response = await window.apiService.deleteRequest(
            `/boards/${post.value.boardId}/posts/${post.value.id}`
          );
          if (response.success) {
            alert("게시글이 삭제되었습니다.");
            // 목록 페이지로 이동
            window.location.href = `/boards/${post.value.boardId}/posts`;
          } else {
            alert(`게시글 삭제 실패: ${response.message}`);
          }
        } catch (error) {
          console.error("게시글 삭제 중 오류 발생:", error);
          alert("게시글 삭제 중 오류가 발생했습니다.");
        }
      }
    };

    // 좋아요 상태 가져오기
    const fetchLikeStatus = async () => {
      if (
        !post.value ||
        !post.value.boardId ||
        !post.value.id ||
        !currentUser.value
      ) {
        isLiked.value = false;
        return;
      }
      try {
        const url = `/boards/${post.value.boardId}/posts/${post.value.id}/likes/status`;
        const response = await window.apiService.getRequest(url);
        if (response.success) {
          isLiked.value = response.liked;
        } else {
          console.error("좋아요 상태 로드 실패:", response.message);
          isLiked.value = false;
        }
      } catch (error) {
        console.error("좋아요 상태 로드 중 오류 발생:", error);
        isLiked.value = false;
      }
    };

    // 좋아요 토글
    const togglePostLike = async () => {
      if (
        !post.value ||
        !post.value.boardId ||
        !post.value.id ||
        !currentUser.value
      ) {
        alert("로그인이 필요합니다.");
        return;
      }

      try {
        const url = `/boards/${post.value.boardId}/posts/${post.value.id}/likes`;
        const response = await window.apiService.postRequest(url, {}); // 빈 바디 전송
        if (response.success) {
          isLiked.value = !isLiked.value; // 상태 토글
          // 좋아요 수 업데이트 (post.likeCount가 null일 경우 0으로 초기화)
          post.value.likeCount =
            (post.value.likeCount || 0) + (isLiked.value ? 1 : -1);
        } else {
          alert(`좋아요 처리 실패: ${response.message || "알 수 없는 오류"}`);
        }
      } catch (error) {
        console.error("좋아요 처리 중 오류 발생:", error);
        alert("좋아요 처리 중 오류가 발생했습니다.");
      }
    };

    // 댓글 관련 로직
    const comments = ref([]);
    const newCommentContent = ref("");
    const commentErrorMessage = ref("");
    const editingCommentId = ref(null);
    const editedCommentContent = ref("");

    // 댓글 목록을 가져옴
    const fetchComments = async () => {
      if (!post.value || !post.value.boardId || !post.value.id) return;
      try {
        const url = `/boards/${post.value.boardId}/posts/${post.value.id}/comments`;
        const response = await window.apiService.getRequest(url);
        if (response.success) {
          comments.value = response.data.map((comment) => ({
            ...comment,
            // comment.userId를 숫자로 변환하여 currentUser.userId와 타입 일관성을 맞춥니다.
            userId: parseInt(comment.userId, 10),
          }));
          // Debugging: Log comment user IDs and current user ID
          comments.value.forEach((comment) => {});
        } else {
          console.error("댓글 로드 실패:", response.message);
          comments.value = [];
        }
      } catch (error) {
        console.error("댓글 로드 중 오류 발생:", error);
        comments.value = [];
      }
    };

    // 새 댓글을 작성함
    const submitComment = async () => {
      if (
        !post.value ||
        !post.value.boardId ||
        !post.value.id ||
        !newCommentContent.value.trim() ||
        !currentUser.value ||
        !currentUser.value.userId
      ) {
        commentErrorMessage.value =
          "댓글 작성에 필요한 정보가 유효하지 않습니다.";
        return;
      }

      if (newCommentContent.value.trim().length < 4) {
        commentErrorMessage.value = "댓글은 4글자 이상 작성해야 합니다.";
        return;
      }

      commentErrorMessage.value = "";

      const commentData = {
        content: newCommentContent.value,
        isAnonymous: 0,
      };

      try {
        const url = `/boards/${post.value.boardId}/posts/${post.value.id}/comments`;
        const response = await window.apiService.postRequest(url, commentData);
        if (response.success) {
          newCommentContent.value = "";
          fetchComments();
        } else {
          commentErrorMessage.value = `댓글 작성 실패: ${
            response.message || "알 수 없는 오류"
          }`;
        }
      } catch (error) {
        console.error("댓글 작성 중 오류 발생:", error);
        commentErrorMessage.value = "댓글 작성 중 오류가 발생했습니다.";
      }
    };

    // 댓글을 수정함
    const editComment = (comment) => {
      editingCommentId.value = comment.id;
      editedCommentContent.value = comment.content;
    };

    const cancelEdit = () => {
      editingCommentId.value = null;
      editedCommentContent.value = "";
    };

    const saveCommentEdit = async (commentId) => {
      if (
        !post.value ||
        !post.value.boardId ||
        !post.value.id ||
        !editedCommentContent.value.trim()
      ) {
        commentErrorMessage.value = "수정할 댓글 내용이 유효하지 않습니다.";
        return;
      }

      if (editedCommentContent.value.trim().length < 4) {
        commentErrorMessage.value = "댓글은 4글자 이상 작성해야 합니다.";
        return;
      }

      commentErrorMessage.value = "";

      const commentData = {
        content: editedCommentContent.value,
      };

      try {
        const url = `/boards/${post.value.boardId}/posts/${post.value.id}/comments/${commentId}`;
        const response = await window.apiService.putRequest(url, commentData);
        if (response.success) {
          alert("댓글이 성공적으로 수정되었습니다.");
          cancelEdit(); // 편집 모드 종료
          fetchComments(); // 댓글 목록 새로고침
        } else {
          // 백엔드에서 error: true를 보낸 경우 메시지 사용
          commentErrorMessage.value = `댓글 수정 실패: ${
            response.message || "알 수 없는 오류"
          }`;
        }
      } catch (error) {
        console.error("댓글 수정 중 오류 발생:", error);
        commentErrorMessage.value = "댓글 수정 중 오류가 발생했습니다.";
      }
    };

    // 댓글을 삭제함
    const deleteComment = async (commentId) => {
      if (!confirm("정말 이 댓글을 삭제하시겠습니까?")) return;
      if (!post.value || !post.value.boardId || !post.value.id) return;
      try {
        const url = `/boards/${post.value.boardId}/posts/${post.value.id}/comments/${commentId}`;
        const response = await window.apiService.deleteRequest(url);
        if (response.success) {
          alert("댓글이 삭제되었습니다.");
          fetchComments();
        } else {
          alert(`댓글 삭제 실패: ${response.message || "알 수 없는 오류"}`);
        }
      } catch (error) {
        console.error("댓글 삭제 중 오류 발생:", error);
        alert("댓글 삭제 중 오류가 발생했습니다.");
      }
    };

    onMounted(() => {
      fetchComments();
      fetchLikeStatus(); // 좋아요 상태 불러오기 추가
    });

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
      isLiked, // isLiked 변수 노출
      togglePostLike, // togglePostLike 메서드 노출
      comments,
      newCommentContent,
      commentErrorMessage,
      editingCommentId,
      editedCommentContent,
      submitComment,
      editComment,
      cancelEdit,
      saveCommentEdit,
      deleteComment,
    };
  },
};

createApp(postReadApp).mount("#post-read-app");
