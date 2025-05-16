<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8" %>
<div class="login-body">
  <div id="loginApp" class="login-container">
    <h1 class="h1">로그인</h1>
    <hr style="border: none; border-top: 1px solid #eee; margin-bottom: 35px" />

    <form action="/user/login" method="post" class="login-form">
      <input
        type="hidden"
        id="csrfTokenInput"
        name="${_csrf.parameterName}"
        value="${_csrf.token}"
      />

      <div class="form-section">
        <label for="loginId" class="section-title">아이디</label>
        <input
          type="text"
          id="loginId"
          name="login_id"
          required
          class="form-input"
        />
        <div
          class="save-id-container"
          style="
            display: none;
            align-items: center;
            margin-top: 5px;
            gap: 5px;
            justify-content: left;
            padding-left: 10px;
          "
        >
          <input
            type="checkbox"
            id="autoLogin"
            name="autoLogin"
            class="auto-login-checkbox"
            style=""
          />
          <label
            for="autoLogin"
            class="auto-login-label"
            style="color: gray; font-size: 13px; padding-bottom: 2px"
            >아이디 저장</label
          >
        </div>
      </div>

      <div class="form-section">
        <label for="password" class="section-title">비밀번호</label>
        <input
          type="password"
          id="password"
          name="password"
          required
          class="form-input"
        />
      </div>

      <button type="submit" class="login-button">
        <span>로그인</span>
      </button>
    </form>
    <div class="login-links">
      <a href="/user/findId" class="login-link"><span>아이디 찾기</span> </a
      ><span class="ver-line">|</span>
      <a href="/user/findPassword" class="login-link"
        ><span>비밀번호 초기화</span> </a
      ><span class="ver-line">|</span>
      <a href="/user/join" class="login-link"><span>회원가입</span> </a>
    </div>
    <hr
      style="
        border: none;
        border-top: 1px solid #f0f0f0;
        margin-top: 20px;
        margin-bottom: 20px;
      "
    />

    <div class="api-login-container" style="justify-content: center">
      <div class="api-login-buttons">
        <button class="api-login-button kakao">
          <img src="/images/login/kakaotalk65px.png" alt="카카오 로그인" />
        </button>
        <button class="api-login-button naver">
          <img src="/images/login/naver65px.png" alt="네이버 로그인" />
        </button>
        <button class="api-login-button google">
          <img src="/images/login/google65px.png" alt="구글 로그인" />
        </button>
      </div>
    </div>
    <div class="login-message" style="display: none"></div>
  </div>
</div>
