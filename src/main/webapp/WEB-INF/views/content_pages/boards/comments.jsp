<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<div id="comments-app" v-cloak
     data-post="${param.postJsonParam}"
     data-board-config="${param.boardConfigJsonParam}"
     data-current-user="${param.currentUserJsonParam}">

    <div class="comments-section">
        <h3>댓글 <span class="comment-count">{{ comments.length }}</span></h3>
        <hr/>

        <!-- 댓글 작성 폼 -->
        <div v-if="currentUser && currentUser.userId" class="comment-form">
            <textarea v-model="newCommentContent" placeholder="댓글을 입력하세요..." rows="3"></textarea>
            <button @click="submitComment" :disabled="!newCommentContent.trim()" class="btn btn-primary">댓글 작성</button>
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
                    <span v-if="comment.updatedAt" class="comment-date-updated">(수정: {{ formatDisplayDate(comment.updatedAt) }})</span>
                </div>
                <div class="comment-content" v-html="comment.content"></div>
                <div v-if="currentUser && currentUser.userId === comment.userId" class="comment-actions">
                    <button @click="editComment(comment)" class="btn btn-sm btn-info">수정</button>
                    <button @click="deleteComment(comment.id)" class="btn btn-sm btn-danger">삭제</button>
                </div>
            </div>
        </div>
        <div v-else class="no-comments">
            <p>아직 댓글이 없습니다.</p>
        </div>
    </div>
</div>
