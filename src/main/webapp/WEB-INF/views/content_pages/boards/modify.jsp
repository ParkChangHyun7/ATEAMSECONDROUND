<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="container" id="post-write-app" v-cloak
     data-board-config='${boardConfig}'
     data-current-user='${currentUser}'
     data-post='${postJson}'>
    <h1 class="h1">글 수정</h1>
    
    <form @submit.prevent="updateForm" class="post-form">
        <div class="form-group">
            <label for="title">제목</label>
            <input type="text" id="title" v-model="post.title" class="form-control" required>
        </div>
        
        <div class="form-group">
            <label for="content">내용</label>
            <div id="editor" style="height: 300px; resize: vertical; overflow: auto;"></div>
        </div>
        
        <div class="form-group form-check" v-if="boardConfig.isAnonymous === 1">
            <input type="checkbox" id="isAnonymous" v-model="post.isAnonymous" class="form-check-input">
            <label for="isAnonymous" class="form-check-label">익명으로 작성</label>
        </div>
        
        <div class="form-group form-check" v-if="isAdmin">
            <input type="checkbox" id="isNotice" v-model="post.isNotice" class="form-check-input">
            <label for="isNotice" class="form-check-label">공지사항</label>
        </div>
        
        <div class="form-actions">
            <button type="button" @click="cancel" class="btn btn-secondary">취소</button>
            <button type="submit" class="btn btn-primary">수정</button>
        </div>
    </form>
</div>