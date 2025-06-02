import { createApp, ref, onMounted } from 'vue';

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

        let quill = null; // Quill 에디터 인스턴스를 저장할 변수

        // Quill에서 사용할 사용자 지정 폰트 목록을 정의합니다.
        const availableFonts = {
            '': 'Default', // 기본 폰트
            'noto-sans-kr': 'Noto Sans KR',
            'nanum-gothic': 'Nanum Gothic',
            'spoqa-han-sans-neo': 'Spoqa Han Sans Neo'
        };

        // Quill의 Font 모듈에 사용자 지정 폰트들을 등록합니다.
        const Font = Quill.import('formats/font');
        Font.whitelist = Object.keys(availableFonts);
        Quill.register(Font, true);

        // Vue 컴포넌트가 마운트된 후 Quill 초기화
        const initializeQuill = () => {
             // Quill 에디터 초기화
            quill = new Quill('#editor', {
                theme: 'snow', // 'snow' 또는 'bubble' 테마 선택
                placeholder: '내용을 입력하세요...',
                modules: {
                    toolbar: [
                        [{ 'header': [1, 2, 3, 4, 5, 6, false] }],
                        [{ 'font': Object.keys(availableFonts) }],
                        ['bold', 'italic', 'underline', 'strike'],
                        [{ 'color': [] }, { 'background': [] }],
                        [{ 'script': 'sub' }, { 'script': 'super' }],
                        ['blockquote', 'code-block'],
                        [{ 'list': 'ordered' }, { 'list': 'bullet' }],
                        [{ 'indent': '-1' }, { 'indent': '+1' }],
                        [{ 'direction': 'rtl' }],
                        [{ 'align': [] }],
                        ['link', 'image', 'video'],
                        ['clean']
                    ]
                }
            });

            // Quill 에디터 내용이 변경될 때 Vue 데이터 업데이트
            quill.on('text-change', () => {
                post.value.content = quill.root.innerHTML; // HTML 내용을 가져와 저장
            });
             // 초기값 설정 (만약 수정 모드라면)
             // if (post.value.content) {
             //    quill.root.innerHTML = post.value.content;
             // }
        };
        
        // 컴포넌트가 마운트된 후에 Quill 초기화
        onMounted(() => {
            initializeQuill();
        });


        // 폼 제출 처리
        const submitForm = async () => {
            // Quill 에디터의 내용을 post.content에 최종적으로 반영
            post.value.content = quill.root.innerHTML; // 제출 직전에 한번 더 업데이트

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