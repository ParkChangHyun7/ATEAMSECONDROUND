<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8" %>
<div class="contact-body">
  <div id="contactApp" class="contact-container">
    <h1 class="contact-title">문의하기</h1>
    <hr style="border: none; border-top: 1px solid #eee; margin-bottom: 35px" />
    <form
      @submit.prevent="submitContact"
      class="contact-form"
      enctype="multipart/form-data"
    >
      <div class="form-section name">
        <label for="name">이름</label>
        <input
          type="text"
          id="name"
          v-model="name"
          required
          class="form-input"
        />
      </div>
      <div class="form-section email">
        <label for="email">이메일</label>
        <input type="email" id="email" v-model="email" class="form-input" />
      </div>
      <div class="form-section phone">
        <label for="phone">연락처</label>
        <input
          type="text"
          id="phone"
          v-model="phone"
          class="form-input"
          placeholder="선택 입력"
        />
      </div>
      <div class="form-section subject">
        <label for="subject">제목</label>
        <input
          type="text"
          id="subject"
          v-model="subject"
          required
          class="form-input"
        />
      </div>
      <div class="form-section message">
        <label for="message">내용</label>
        <textarea
          id="message"
          v-model="message"
          required
          class="form-textarea"
          rows="5"
        ></textarea>
      </div>
      <div class="form-section attachment">
        <label for="attachment">첨부파일 (선택)</label>
        <input
          type="file"
          id="attachment"
          ref="attachment"
          class="form-input attach"
          @change="onFileChange"
        />
      </div>
      <div
        class="contact-message"
        :class="messageType"
        v-if="messageText"
        v-html="messageText"
      ></div>
      <button type="submit" :disabled="isLoading" class="contact-button">
        <span v-if="isLoading && attachment && uploadProgress > 0"
          >전송 중... ({{ uploadProgress }}%)</span
        >
        <span v-else-if="isLoading">전송 중...</span>
        <span v-else>문의하기</span>
      </button>
    </form>
  </div>
</div>
