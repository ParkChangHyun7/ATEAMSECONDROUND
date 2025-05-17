import { createApp, ref, onMounted, computed } from 'vue';

// apiService는 현재 사용하지 않으므로 주석 처리 또는 삭제 가능
// import apiService from '../../utils/apiService'; // CSRF 토큰 및 공통 API 호출 로직
// import csrfTokenManager from '../../utils/csrf-util'; // CSRF 토큰 관리

const postListApp = {
    setup() {
        const postListAppElement = document.getElementById('post-list-app');

        // JSP의 data-* 속성에서 초기 데이터 읽기
        const postsJson = postListAppElement.dataset.posts;
        const initialPosts = postsJson ? JSON.parse(postsJson) : [];
        const posts = ref(initialPosts);
        const totalRegularPosts = ref(parseInt(postListAppElement.dataset.totalRegularPosts || '0', 10));
        const currentPageVue = ref(parseInt(postListAppElement.dataset.currentPage || '1', 10)); // JSP와 이름 맞춤
        const pageSize = ref(parseInt(postListAppElement.dataset.pageSize || '20', 10));
        const boardIdVue = ref(postListAppElement.dataset.boardId || null); // JSP와 이름 맞춤
        const pageTitleVue = ref(postListAppElement.dataset.pageTitle || '게시판'); // JSP와 이름 맞춤
        const initialCanWrite = postListAppElement.dataset.canWrite === 'true'; // canWrite 값 읽고 boolean으로 변환
        const canWriteVue = ref(initialCanWrite); // canWriteVue ref 생성

        const pageBlockSize = 10;

        const totalPages = computed(() => {
            return Math.ceil(totalRegularPosts.value / pageSize.value);
        });

        const startPageInBlock = computed(() => {
            return Math.floor((currentPageVue.value - 1) / pageBlockSize) * pageBlockSize + 1;
        });

        const endPageInBlock = computed(() => {
            const end = startPageInBlock.value + pageBlockSize - 1;
            return end > totalPages.value ? totalPages.value : end;
        });

        const shouldShowPagination = computed(() => {
            // 게시글이 있고, 전체 페이지 수가 0보다 클 때 페이지네이션을 표시 (즉, 1페이지 이상일 때)
            return totalRegularPosts.value > 0 && totalPages.value > 0;
        });

        const fetchPostList = async () => {
            if (!boardIdVue.value) {
                console.error('boardId가 정의되지 않았습니다.');
                return;
            }
            try {
                const response = await fetch(`/boards/${boardIdVue.value}/posts/data?page=${currentPageVue.value}&pageSize=${pageSize.value}`);
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                const result = await response.json();
                posts.value = result.posts;
                totalRegularPosts.value = result.totalRegularPosts;
            } catch (error) {
                console.error('게시글 목록 불러오기 실패:', error);
                posts.value = []; // 오류 발생 시 게시글 목록 비우기
                totalRegularPosts.value = 0;
            }
        };

        const goToPage = (page) => {
            if (page >= 1 && page <= totalPages.value) {
                currentPageVue.value = page;
                fetchPostList();
            }
        };

        const nextPageBlock = () => {
            const nextStartPage = startPageInBlock.value + pageBlockSize;
            goToPage(nextStartPage <= totalPages.value ? nextStartPage : totalPages.value);
        };

        const prevPageBlock = () => {
            const prevStartPage = startPageInBlock.value - pageBlockSize;
            goToPage(prevStartPage >= 1 ? prevStartPage : 1);
        };

        // 날짜 포맷팅 함수 (PostListDto.java의 주석 기반)
        const formatDisplayDate = (dateString) => {
            if (!dateString) return '';

            const date = new Date(dateString);

            if (isNaN(date.getTime())) {
                // console.error('Invalid date string:', dateString);
                return dateString; 
            }

            const today = new Date();
            today.setHours(0, 0, 0, 0);
            const inputDateOnly = new Date(date);
            inputDateOnly.setHours(0, 0, 0, 0);

            const year = date.getFullYear();
            const month = String(date.getMonth() + 1).padStart(2, '0');
            const day = String(date.getDate()).padStart(2, '0');
            const hours = String(date.getHours()).padStart(2, '0');
            const minutes = String(date.getMinutes()).padStart(2, '0');

            if (inputDateOnly.getTime() === today.getTime()) {
                return `${hours}:${minutes}`;
            } else if (year === today.getFullYear()) {
                return `${month}-${day}`;
            } else {
                return `${String(year).slice(-2)}-${month}-${day}`;
            }
        };

        const formatViewCount = (count) => {
            if (count === null || count === undefined) return ''; // 숫자 없으면 빈 문자열
            
            let numPart = '';
            let kSuffix = '';

            if (count < 1000) {
                numPart = count.toString();
            } else if (count < 10000) { // 1,000 ~ 9,999
                numPart = (count / 1000).toFixed(2).replace(/\.00$/, '');
                kSuffix = 'k';
            } else { // 10,000 이상
                numPart = (count / 1000).toFixed(1).replace(/\.0$/, '');
                kSuffix = 'k';
            }

            if (kSuffix) {
                return `<span>${numPart}</span><span class="k-suffix">${kSuffix}</span>`;
            }
            return `<span>${numPart}</span>`; // k가 없는 경우 숫자만 span으로 감쌈
        };

        // onMounted에서 초기 데이터를 이미 JSP에서 받았으므로 fetchPostList 호출 불필요
        // onMounted(() => {
        //    // 초기 로딩 시 필요한 작업이 있다면 여기에 추가
        // });

        return {
            posts,
            currentPageVue, // JSP와 이름 맞춤
            pageSize,
            totalRegularPosts,
            totalPages,
            startPageInBlock,
            endPageInBlock,
            goToPage,
            nextPageBlock,
            prevPageBlock,
            boardIdVue, // JSP와 이름 맞춤
            pageTitleVue, // JSP와 이름 맞춤
            formatDisplayDate, // 날짜 포맷팅 함수 반환
            shouldShowPagination, // 반환 객체에 추가
            canWriteVue, // canWriteVue 반환 객체에 추가
            formatViewCount // 조회수 포맷팅 함수 추가
        };
    }
};

createApp(postListApp).mount('#post-list-app');
