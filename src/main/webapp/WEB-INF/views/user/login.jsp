<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>로그인</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<body>
    <h1>로그인</h1>
    <form action="/user/login" method="post">
       <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <div>
            <label for="userId">아이디:</label>
            <input type="text" id="userId" name="userId" required>
        </div>
        <div>
            <label for="password">비밀번호:</label>
            <input type="password" id="password" name="password" required>
        </div>
        <button type="submit">로그인</button>
    </form>
    <form action="/user/login" method="post">
    	아이디:<input type="text" name="loginId"/><br/>
    	비밀번호:<input type="password" name="password"/><br/>
    	<button type="submit">로그인</button>
   		 <!-- 로그인 실패 메세지 표시 -->
    	<c:if test="${not empty error}">
    		<p style="color:red;">${error}</p>
    	</c:if>
    </form>
    <a href="/user/findId">아이디 찾기</a>
    <a href="/user/findPassword">비밀번호 찾기</a>
    <script src="/js/script.js"></script>
</body>
</html> 