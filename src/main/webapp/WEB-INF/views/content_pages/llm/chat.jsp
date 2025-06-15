<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
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