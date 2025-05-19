<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<div class="topdiv-list">
    <div class="container" id="post-read-app" v-cloak
         data-post='${postJson}'
         data-board-config='${boardConfigJson}'
         data-current-user='${currentUserJson}'>

    <div v-if="post" class="post-container">
        <div class="board-info">
            <!-- 게시판 이름 -->
            <h2><a :href="'/boards/' + post.boardId + '/posts'">{{ boardConfig ? boardConfig.name : '게시판' }}</a></h2>
            <hr style="margin-top: 4px; border: 0px; border-top: 1px solid #eaeaea; margin-bottom: 20px;" />
        </div>
        <!-- 게시글 상세 정보 -->
        <div class="post-header">
            <!-- 제목 (공지 표시 포함) -->
            <h2 class="h2">{{ formattedTitle }}</h2>
            <div class="post-meta">
                <span class="post-writer">작성자: {{ post.isAnonymous === 1 ? '익명' : post.writer }}</span>
                <span class="post-date">작성일: {{ formatDisplayDate(post.createdAt) }}</span>
                    <span v-if="post.updatedAt" class="post-date-updated">수정일: {{ formatDisplayDate(post.updatedAt) }}</span>
                        <span class="material-symbols-outlined read">
                            visibility
                            </span><span class="post-views">{{ post.viewCount }}</span>
                            <span class="material-symbols-outlined read">
                                thumb_up
                                </span><span v-if="post.likeCount != null" class="post-likes">{{ post.likeCount }}</span>
                                <span class="material-symbols-outlined read">
                                    bring_your_own_ip
                                    </span><span class="post-ip">{{ maskedIpAddress }}</span>
            </div>
        </div>

        <div class="post-body">
            <!-- 첨부 파일, 이미지 아이콘 -->
            <div class="attachment-icons">
                <span v-if="post.fileIncluded === 1" class="material-symbols-outlined">attach_file</span>
                <span v-if="post.imageIncluded === 1" class="material-symbols-outlined">image</span>
            </div>

            <!-- 내용 -->
            <div class="post-content" v-html="post.content"></div>

            <!-- TODO: 첨부 파일 목록 -->

            <!-- TODO: 댓글 목록 -->

        </div>

        <!-- 수정, 삭제, 목록 버튼 -->
        <div class="post-actions">
            <a :href="'/boards/' + post.boardId + '/posts'" class="btn btn-secondary">목록</a>
            <!-- TODO: 현재 사용자가 작성자인 경우 또는 관리자인 경우에만 수정/삭제 버튼 표시 -->
            <!-- <button v-if="canEdit" @click="editPost" class="btn btn-primary">수정</button> -->
            <!-- <button v-if="canDelete" @click="deletePost" class="btn btn-danger">삭제</button> -->
        </div>
    </div>

    <div v-else>
        <p>게시글 정보를 불러오지 못했습니다.</p>
    </div>
</div>
