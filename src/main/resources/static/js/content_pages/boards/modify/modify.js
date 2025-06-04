import { createApp, ref, onMounted, computed, nextTick } from 'vue';
// import Quill from 'quill'; // Quill 모듈 임포트 제거

const postModifyApp = {
    setup() {
        const postModifyAppElement = document.getElementById('post-write-app');
        // CSRF 토큰 가져오기
        const csrfToken = document.querySelector('meta[name="_csrf"]').content;
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
        
        const boardConfigJson = postModifyAppElement.dataset.boardConfig;
        const currentUserJson = postModifyAppElement.dataset.currentUser;
        const postJson = postModifyAppElement.dataset.post;

        const boardConfig = ref(boardConfigJson ? JSON.parse(boardConfigJson) : {});
        const currentUser = ref(currentUserJson ? JSON.parse(currentUserJson) : null);
        const post = ref(postJson ? JSON.parse(postJson) : {
            title: '',
            content: '',
            isAnonymous: boardConfig.value?.isAnonymous === 1 ? 1 : 0,
            isNotice: 0,
            fileIncluded: 0,
            imageIncluded: 0
        });

        let quill = null;

        const isAdmin = computed(() => {
            return currentUser.value && currentUser.value.role >= 100;
        });

        onMounted(() => {
            initializeQuill();
        });

        const initializeQuill = () => {
            const toolbarOptions = [
                ['bold', 'italic', 'underline', 'strike'],
                ['blockquote', 'code-block'],
                [{ 'header': 1 }, { 'header': 2 }],
                [{ 'list': 'ordered'}, { 'list': 'bullet' }],
                [{ 'script': 'sub'}, { 'script': 'super' }],
                [{ 'indent': '-1'}, { 'indent': '+1' }],
                [{ 'direction': 'rtl' }],
                [{ 'size': ['small', false, 'large', 'huge'] }],
                [{ 'header': [1, 2, 3, 4, 5, 6, false] }],
                [{ 'color': [] }, { 'background': [] }],
                [{ 'font': [] }],
                [{ 'align': [] }],
                ['link', 'image', 'video'],
                ['clean']
            ];

            quill = new window.Quill('#editor', {
                modules: {
                    toolbar: {
                        container: toolbarOptions,
                        handlers: {
                            image: imageHandler
                        }
                    }
                },
                theme: 'snow'
            });

            // 기존 게시글 내용 로드
            if (post.value && post.value.content) {
                quill.root.innerHTML = post.value.content;
            }

            quill.on('text-change', () => {
                post.value.content = quill.root.innerHTML;
            });
        };

        const imageHandler = () => {
            const input = document.createElement('input');
            input.setAttribute('type', 'file');
            input.setAttribute('accept', 'image/*');
            input.click();

            input.onchange = async () => {
                const file = input.files[0];
                if (!file) return;

                const formData = new FormData();
                formData.append('file', file);
                // TODO: uploadFrom, parentId를 동적으로 가져와서 설정해야 함.
                // 현재는 게시글 수정이므로 uploadFrom은 0 (boards), parentId는 현재 게시글 ID
                formData.append('uploadFrom', 0); 
                formData.append('parentId', post.value.id); // 현재 게시글 ID 사용

                try {
                    const response = await window.apiService.postRequest('/api/upload/image', formData);

                    if (response.success) {
                        const data = response.data;
                        const imageUrl = data.imageUrl;
                        if (imageUrl) {
                            const range = quill.getSelection();
                            quill.insertEmbed(range.index, 'image', imageUrl);
                            quill.setSelection(range.index + 1); // 이미지 삽입 후 커서 이동
                        } else {
                            alert('이미지 URL을 받아오지 못했습니다.');
                        }
                    } else {
                        alert(`이미지 업로드 실패: ${response.message}`);
                    }
                } catch (error) {
                    console.error('이미지 업로드 중 오류 발생:', error);
                    alert('이미지 업로드 중 오류가 발생했습니다.');
                }
            };
        };

        const updateForm = async () => {
            if (!post.value.title || !post.value.content) {
                alert('제목과 내용을 입력해주세요.');
                return;
            }

            try {
                const response = await window.apiService.putRequest(`/boards/${post.value.boardId}/posts/${post.value.id}`, {
                    title: post.value.title,
                    content: post.value.content,
                    isAnonymous: post.value.isAnonymous ? 1 : 0,
                    isNotice: post.value.isNotice ? 1 : 0,
                    // fileIncluded 및 imageIncluded는 서버에서 처리될 수 있음.
                    // 여기서는 단순히 내용에 이미지가 있는지 여부로 판단하여 보낼 수도 있으나, 현재는 백엔드 로직에 따라 생략
                });

                if (response.success) {
                    alert('게시글이 성공적으로 수정되었습니다!');
                    window.location.href = `/boards/${post.value.boardId}/posts/read/${post.value.id}`; // 수정된 게시글 상세 페이지로 이동
                } else {
                    alert(`게시글 수정 실패: ${response.message}`);
                }
            } catch (error) {
                console.error('게시글 수정 중 오류 발생:', error);
                alert('게시글 수정 중 오류가 발생했습니다.');
            }
        };

        const cancel = () => {
            if (confirm('수정을 취소하시겠습니까? 작성된 내용은 저장되지 않습니다.')) {
                window.location.href = `/boards/${post.value.boardId}/posts/${post.value.id}`; // 상세 페이지로 돌아가기
            }
        };

        return {
            post,
            boardConfig,
            currentUser,
            isAdmin,
            updateForm,
            cancel,
        };
    }
};

createApp(postModifyApp).mount('#post-write-app');
