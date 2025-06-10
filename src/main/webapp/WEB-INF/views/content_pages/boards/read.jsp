<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8" %> <%@ taglib prefix="c"
uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<div class="topdiv-list">
  <div
    class="container"
    id="post-read-app"
    v-cloak
    data-post="${fn:escapeXml(postJson)}"
    data-board-config="${fn:escapeXml(boardConfigJson)}"
    data-current-user="${fn:escapeXml(currentUserJson)}"
  >
    <div v-if="post" class="post-container">
      <div class="board-info">
        <!-- 게시판 이름 -->
        <h2>
          <a :href="'/boards/' + post.boardId + '/posts'">{{
            boardConfig ? boardConfig.name : "게시판"
          }}</a>
        </h2>
        <hr
          style="
            margin-top: 4px;
            border: 0px;
            border-top: 1px solid #eaeaea;
            margin-bottom: 20px;
          "
        />
      </div>
      <!-- 게시글 상세 정보 -->
      <div class="post-header">
        <!-- 제목 (공지 표시 포함) -->
        <h2 class="h2">{{ formattedTitle }}</h2>
        <div class="post-meta">
          <span class="post-writer"
            >작성자: {{ post.isAnonymous === 1 ? "익명" : post.writer }}</span
          >
          <span class="post-date"
            >작성일: {{ formatDisplayDate(post.createdAt) }}</span
          >
          <span v-if="post.updatedAt" class="post-date-updated"
            >수정일: {{ formatDisplayDate(post.updatedAt) }}</span
          >
          <span class="material-symbols-outlined read"> visibility </span
          ><span class="post-views">{{ post.viewCount }}</span>
          <span class="material-symbols-outlined read"> thumb_up </span
          ><span v-if="post.likeCount != null" class="post-likes">{{
            post.likeCount
          }}</span>
          <span class="material-symbols-outlined read"> bring_your_own_ip </span
          ><span class="post-ip">{{ post.ipAddress }}</span>
        </div>
      </div>

      <div class="post-body">
        <!-- 첨부 파일, 이미지 아이콘 -->
        <div class="attachment-icons">
          <span v-if="post.fileIncluded === 1" class="material-symbols-outlined"
            >attach_file</span
          >
          <span
            v-if="post.imageIncluded === 1"
            class="material-symbols-outlined"
            >image</span
          >
        </div>

        <!-- 내용 -->
        <div class="post-content" v-html="post.content"></div>

        <!-- TODO: 첨부 파일 목록 -->
      </div>

      <!-- 수정, 삭제, 목록 버튼 -->
      <div class="post-actions">
        <a
          :href="'/boards/' + post.boardId + '/posts'"
          class="btn btn-secondary"
          >목록</a
        >
        <button v-if="canEdit" @click="editPost" class="btn btn-primary">
          수정
        </button>
        <button v-if="canDelete" @click="deletePost" class="btn btn-danger">
          삭제
        </button>
      </div>
      <div id="comments-app" v-cloak
     data-post="${fn:escapeXml(postJson)}"
     data-board-config="${fn:escapeXml(boardConfigJson)}"
     data-current-user="${fn:escapeXml(currentUserJson)}">

    <div class="comments-section">
        <h3>댓글 <span class="comment-count">{{ comments.length }}</span></h3>
        <hr/>

        <!-- 댓글 작성 폼 -->
        <div v-if="currentUser && currentUser.userId && (post.noReply === 0 || post.noReply === null || (currentUser.role != null && currentUser.role >= 100))" class="comment-form">
            <textarea v-model="newCommentContent" placeholder="댓글을 입력하세요..." rows="3"></textarea>
            <button @click="submitComment" :disabled="!newCommentContent.trim()" class="btn btn-primary">댓글 작성</button>
            <div v-if="commentErrorMessage" class="comment-error-message" style="color: red; margin-top: 5px;">{{ commentErrorMessage }}</div>
        </div>
        <div v-else-if="post.noReply !== 0 && post.noReply !== null" class="comment-login-prompt">
            <p>이 게시글은 댓글 작성이 금지되었습니다.</p>
        </div>
        <div v-else class="comment-login-prompt">
            <p>댓글을 작성하려면 <a href="/users/login">로그인</a> 해주세요.</p>
        </div>

        <!-- 댓글 목록 -->
        <div v-if="comments.length > 0" class="comment-list">
            <div v-for="comment in comments" :key="comment.id" class="comment-item">
                <div class="comment-header">
                    <span class="comment-writer">{{ comment.isAnonymous === 1 ? '익명' : comment.writer }}</span>
                    <span class="comment-date">{{ formatDisplayDate(comment.createdAt) }}</span>
                    <span v-if="comment.updatedAt" class="comment-date-updated" style="color: #cecece">( {{ formatDisplayDate(comment.updatedAt) }} 수정됨 )</span>
                </div>
                <div class="comment-content" v-html="comment.content"></div>
                <div class="comment-actions">
                    <button v-if="currentUser && currentUser.userId === comment.userId" @click="editComment(comment)" class="btn btn-sm btn-info">수정</button>
                    <button v-if="currentUser && (currentUser.userId === comment.userId || (currentUser.role != null && currentUser.role >= 100))" @click="deleteComment(comment.id)" class="btn btn-sm btn-danger">삭제</button>
                </div>
            </div>
        </div>
        <div v-else class="no-comments">
            <p>첫 댓글을 남겨주세요 😊</p>
        </div>
    </div>
</div>
    </div>

    <div v-else>
      <p>게시글 정보를 불러오지 못했습니다.</p>
    </div>
  </div>
</div>
