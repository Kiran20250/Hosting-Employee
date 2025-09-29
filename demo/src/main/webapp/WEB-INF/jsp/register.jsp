<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>Register Page</title>

  <!-- Google Fonts -->
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link href="https://fonts.googleapis.com/css2?family=Poppins&display=swap" rel="stylesheet">

  <!-- External CSS -->
  <link rel="stylesheet" type="text/css" href="<c:url value='/css/register.css'/>"/>
</head>
<body>

  <!-- Header -->
  <header class="header">
    <div class="logo">
      <img class="img" src="<c:url value='/img/CRBIXLOGO.png'/>" alt="CRBIX LOGO"/>
    </div>
    <div class="power-icon">
      <i class="fas fa-power-off"></i>
    </div>
  </header>

  <!-- Register Card -->
  <div class="register-container">
    <!-- Left Illustration -->
    <div class="illustration">
      <img src="<c:url value='/img/CRBIXLOGO.png'/>" alt="Employee working"/>
    </div>

    <!-- Right Register Form -->
    <div class="register-form">
      <div class="avatar-circle"><p class="logocircleP">OK</p></div>

      <!-- Register Form -->
      <form action="${pageContext.request.contextPath}/register" method="post" enctype="multipart/form-data">
        <input type="text" name="firstName" placeholder="Enter First Name" value="${user.firstName}" required/>
        <input type="text" name="lastName" placeholder="Enter Last Name" value="${user.lastName}" required/>
        <input type="text" name="username" placeholder="Enter Username" value="${user.username}" required/>
        <input type="password" name="password" placeholder="Enter Password" value="${user.password}" required/>
        <input type="password" name="confirmPassword" placeholder="Confirm Password" value="${user.confirmPassword}" required/>
        <input type="text" name="phone" placeholder="Phone No" value="${user.phone}" required/>

        <!-- Upload Section -->
        <div class="upload-section">
          <label><strong class="uplodText">Upload Documents:</strong></label>

          <div class="file-upload">
            <label for="aadharFile" class="upload-btn">Upload Aadhar Card</label>
            <span id="aadharFileName" class="file-name">No file chosen</span>
            <input type="file" id="aadharFile" name="aadharFile" accept=".jpg,.jpeg,.png,.pdf"/>
          </div>

          <div class="file-upload">
            <label for="panFile" class="upload-btn">Upload PAN Card</label>
            <span id="panFileName" class="file-name">No file chosen</span>
            <input type="file" id="panFile" name="panFile" accept=".jpg,.jpeg,.png,.pdf"/>
          </div>

          <div class="file-upload">
            <label for="marksheetFile" class="upload-btn">Upload Marksheet</label>
            <span id="marksheetFileName" class="file-name">No file chosen</span>
            <input type="file" id="marksheetFile" name="marksheetFile" accept=".pdf"/>
          </div>

          <p class="note">
            PLEASE COMPILE YOUR 10th, 12th and GRADUATION MARKSHEET INTO SINGLE PDF
          </p>
        </div>

        <!-- Script to show selected file name -->
        <script>
          document.querySelectorAll("input[type=file]").forEach(input => {
            input.addEventListener("change", function() {
              let fileName = this.files.length > 0 ? this.files[0].name : "No file chosen";
              document.getElementById(this.id + "Name").textContent = fileName;
            });
          });
        </script>

        <!-- Register Button -->
        <button id="registerBtn" type="submit">Register</button>
      </form>

      <!-- Link to Login -->
      <div class="login-link">
        <p>If you already have an account?</p>
        <form action="${pageContext.request.contextPath}/login" method="get">
          <button type="submit">Login</button>
        </form>
      </div>

      <!-- Error Message -->
      <c:if test="${not empty error}">
        <p style="color:red">${error}</p>
      </c:if>
    </div>
  </div>

  <!-- FontAwesome -->
  <script src="https://kit.fontawesome.com/a076d05399.js" crossorigin="anonymous"></script>
</body>
</html>
