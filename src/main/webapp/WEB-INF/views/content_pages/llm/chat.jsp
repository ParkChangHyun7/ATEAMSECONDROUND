<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="_csrf" content="${_csrf.token}" />
    <meta name="_csrf_header" content="${_csrf.headerName}" />
    <title>날씨 챗봇</title>
    <link rel="stylesheet" href="/css/content_pages/llm/chat.css">
    <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;700&display=swap" rel="stylesheet">
</head>
<body>
    <div id="chat-app" class="chat-container">
        <div class="chat-header">날씨 정보 알림이</div>
        <div class="chat-messages" ref="messagesContainer">
            <!-- 챗 메시지 표시 위치 -->
            <div v-for="(message, index) in messages" :key="index" :class="['message', message.type]">
                {{ message.text }}
            </div>
        </div>
        <div class="chat-input">
            <input type="text" v-model="userMessage" @keyup.enter="sendMessage" placeholder="날씨를 물어보세요...">
            <button @click="sendMessage">전송</button>
        </div>
    </div>

    <!-- Vue.js 및 챗봇 스크립트 임포트 -->
    <script src="https://unpkg.com/vue@3/dist/vue.global.prod.js"></script>
    <script src="/js/commonUtils/csrf-util.js"></script>
    <script src="/js/content_pages/llm/chat.js"></script>
</body>
</html> 