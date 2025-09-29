<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>View PDFs</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        h1 { color: #333; }
        ul { list-style-type: none; padding: 0; }
        li { margin: 10px 0; }
        a { text-decoration: none; color: #007bff; }
        a:hover { text-decoration: underline; }
    </style>
</head>
<body>
<h1>Available PDF Files</h1>

<c:choose>
    <c:when test="${not empty pdfFiles}">
        <ul>
            <c:forEach var="file" items="${pdfFiles}">
                <li>
                    <a href="${pageContext.request.contextPath}/view-pdf/${file}" target="_blank">
                        ${file}
                    </a>
                </li>
            </c:forEach>
        </ul>
    </c:when>
    <c:otherwise>
        <p>No PDF files found.</p>
    </c:otherwise>
</c:choose>

</body>
</html>
