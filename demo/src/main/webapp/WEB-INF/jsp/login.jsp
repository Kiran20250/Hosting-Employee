<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>Login Page</title>

  <!-- External CSS -->
  <link rel="stylesheet" href="<c:url value='/css/login.css'/>"/>

  <!-- Google Fonts -->
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link href="https://fonts.googleapis.com/css2?family=Poppins&display=swap" rel="stylesheet">

  <!-- FontAwesome -->
  <script src="https://kit.fontawesome.com/a076d05399.js" crossorigin="anonymous"></script>
</head>
<body>

  <!-- Header -->
  <header class="header">
    <div class="logo">
      <img class="img" src="${pageContext.request.contextPath}/img/CRBIXLOGO.png" alt="Logo">
    </div>
  </header>

  <!-- Login Card -->
  <div class="login-container">
    <!-- Left Illustration -->
    <div class="illustration">
      <img class="img" src="${pageContext.request.contextPath}/img/CRBIXLOGO.png" alt="Logo">
    </div>

    <!-- Right Login Form -->
    <div class="login-form">

      <!--
      <div class="logo-circle">
        <p class="logocircleP">OK</p>
      </div>
        -->
        
      <!-- Form -->
      <form action="login" method="post">
        <input type="text" name="username" placeholder="Enter Username" value="${user.username}" required/>
        <input type="password" name="password" placeholder="Enter Password" value="${user.password}" required/>

        <button type="submit" id="loginBtn">Login</button>
      </form>

      <div class="register">
        <p>If you don't have an account?</p>
        <form action="register" method="get">
          <button type="submit" class="register-button">Register</button>
        </form>
      </div>

      <!-- Error Messages -->
      <c:if test="${not empty error}">
        <p class="error-msg">${error}</p>
      </c:if>

      <!-- Optional: client-side hint for email format
      <c:if test="${empty error}">
        <p class="hint-msg">Username must end with <strong>@crbix.in</strong></p>
      </c:if>
-->
    </div>
  </div>

</body>
</html>
