import { createApp, ref, onMounted, computed } from 'vue';

const postListApp = {
    setup() {
        const postListAppElement = document.getElementById('post-list-app');
        const postsJson = postListAppElement.dataset.posts;
        const initialPosts = postsJson ? JSON.parse(postsJson) : [];
        const posts = ref(initialPosts);
        const totalRegularPosts = ref(parseInt(postListAppElement.dataset.totalRegularPosts || '0', 10));
        const currentPageVue = ref(parseInt(postListAppElement.dataset.currentPage || '1', 10));
        const pageSize = ref(parseInt(postListAppElement.dataset.pageSize || '20', 10));
        const boardIdVue = ref(postListAppElement.dataset.boardId || null);
        const pageTitleVue = ref(postListAppElement.dataset.pageTitle || '게시판');
        const initialCanWrite = postListAppElement.dataset.canWrite === 'true';
        const canWriteVue = ref(initialCanWrite);

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
            return totalRegularPosts.value > 0 && totalPages.value > 0;
        });


        // 서버로부터 게시글 목록 데이터를 비동기적으로 가져옴.
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
                posts.value = [];
                totalRegularPosts.value = 0;
            }
        };


        // 지정된 페이지로 이동하고 해당 페이지의 게시글 목록을 불러옴.
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


        // 날짜 문자열을 조건에 따라 HH:MM, MM-DD, YY-MM-DD 형식으로 변환함.
        const formatDisplayDate = (dateString) => {
            if (!dateString) return '';
            const date = new Date(dateString);
            if (isNaN(date.getTime())) {
                return dateString;
            }
            const today = new Date();
            const todayForCompare = new Date();
            todayForCompare.setHours(0, 0, 0, 0);
            const inputDateOnly = new Date(date);
            inputDateOnly.setHours(0, 0, 0, 0);
            const year = date.getFullYear();
            const month = String(date.getMonth() + 1).padStart(2, '0');
            const day = String(date.getDate()).padStart(2, '0');
            const hours = String(date.getHours()).padStart(2, '0');
            const minutes = String(date.getMinutes()).padStart(2, '0');
            if (inputDateOnly.getTime() === todayForCompare.getTime()) {
                return `${hours}:${minutes}`;
            } else if (year === todayForCompare.getFullYear()) {
                return `${month}-${day}`;
            } else {
                return `${String(year).slice(-2)}-${month}-${day}`;
            }
        };


        // 조회수를 조건에 따라 숫자 또는 'k' 접미사를 붙인 문자열로 변환함.
        const formatViewCount = (count) => {
            if (count === null || count === undefined) return '';
            let numPart = '';
            let kSuffix = '';
            if (count < 1000) {
                numPart = count.toString();
            } else if (count < 10000) {
                numPart = (count / 1000).toFixed(2).replace(/\.00$/, '');
                kSuffix = 'k';
            } else {
                numPart = (count / 1000).toFixed(1).replace(/\.0$/, '');
                kSuffix = 'k';
            }
            if (kSuffix) {
                return `<span>${numPart}</span><span class="k-suffix">${kSuffix}</span>`;
            }
            return `<span>${numPart}</span>`;
        };


        // 댓글 수를 조건에 따라 숫자 또는 '99+' 문자열로 변환함.
        const formatCommentCount = (count) => {
            if (count === null || count === undefined || count < 1) return '';
            if (count > 99) return '99+';
            return count.toString();
        };

        // isNotice가 1이면 [공지]를 붙여주는 함수
        const formattedTitle = (post) => {
            if (!post) return '';
            return post.isNotice === 1 ? '[공지] ' + post.title : post.title;
        };

        return {
            posts,
            currentPageVue,
            pageSize,
            totalRegularPosts,
            totalPages,
            startPageInBlock,
            endPageInBlock,
            goToPage,
            nextPageBlock,
            prevPageBlock,
            boardIdVue,
            pageTitleVue,
            formatDisplayDate,
            shouldShowPagination,
            canWriteVue,
            formatViewCount,
            formatCommentCount,
            formattedTitle
        };
    }
};

createApp(postListApp).mount('#post-list-app');