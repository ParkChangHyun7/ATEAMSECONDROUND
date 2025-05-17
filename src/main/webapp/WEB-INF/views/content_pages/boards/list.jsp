<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<div class="topdiv-list">
    <%-- Controller에서 넘겨준 초기 데이터를 data-* 속성으로 Vue 앱에 전달 --%>
    <div class="container" id="post-list-app" v-cloak
         data-posts='${postsJson}'
         data-total-regular-posts='${totalRegularPosts}'
         data-current-page='${currentPage}'
         data-page-size='${pageSize}'
         data-board-id='${boardId}'
         data-can-write="${canWrite}"
         data-page-title='${pageTitle}'>
        <h1 class="h1">{{ pageTitleVue }}</h1> <%-- Vue 변수로 변경 --%>

        <%-- 게시글 목록 테이블 --%>
        <table>
            <thead>
                <tr>
                    <th>번호</th>
                    <th>제목</th>
                    <th>작성자</th>
                    <th>작성일</th>
                    <th>조회수</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="post in posts" :key="post.id" :class="{ 'notice-row': post.isNotice === 1 }">
                    <td>{{ post.likeCount >= 99 ? '99+' : (post.likeCount > 0 ? post.likeCount : '') }}</td>
                    <td class="td-separator">|</td>
                    <td>
                        <a :href="'/boards/' + boardIdVue + '/posts/' + post.id">{{ post.title }}</a>
                         <span v-if="post.fileIncluded === 1" class="material-symbols-outlined">attach_file</span>
                         <span v-if="post.imageIncluded === 1" class="material-symbols-outlined">image</span>
                    </td>
                    <td class="td-separator">|</td>
                    <td>{{ post.writer }}</td>
                    <td class="td-separator">|</td>
                    <td>{{ formatDisplayDate(post.createdAt) }} <span v-if="post.updatedAt && post.createdAt !== post.updatedAt">*</span></td> <%-- 날짜 포맷팅 함수 및 수정 여부 개선 --%>
                    <td class="td-separator">|</td>
                    <td v-html="formatViewCount(post.viewCount)"></td> <%-- v-html 사용하여 조회수 렌더링 --%>
                </tr>
                <tr v-if="posts.length === 0">
                    <td colspan="9" style="text-align: center;">게시글이 없습니다.</td> <%-- colspan 수정 --%>
                </tr>
            </tbody>
        </table>

        <%-- 페이지네이션 컨트롤 --%>
        <div class="pagination" v-if="shouldShowPagination">
            <button @click="prevPageBlock" :disabled="startPageInBlock === 1">이전</button>
            <button
                v-for="n in (endPageInBlock - startPageInBlock + 1)"
                :key="startPageInBlock + n - 1"
                @click="goToPage(startPageInBlock + n - 1)"
                :class="{ active: (startPageInBlock + n - 1) === currentPageVue }"
            >
                {{ startPageInBlock + n - 1 }}
            </button>
            <button @click="nextPageBlock" :disabled="endPageInBlock === totalPages">다음</button>
        </div>

        <div class="list-top-controls">
            <div class="search-controls">
                <%-- TODO: 검색 기능 추가 예정 --%>
            </div>
            <div class="write-button-container" v-if="canWriteVue">
                <a :href="'/boards/' + boardIdVue + '/write'" class="btn btn-primary">글쓰기</a>
            </div>
        </div>

    </div>
</div>
