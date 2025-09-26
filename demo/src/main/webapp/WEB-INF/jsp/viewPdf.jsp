<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>View PDF</title>
</head>
<body>
<h2>Uploaded PDFs</h2>

<c:if test="${not empty pdfFiles}">
    <ul>
        <c:forEach var="file" items="${pdfFiles}">
            <li>
                <a href="view-pdf/${file}" target="_blank">${file}</a>
            </li>
        </c:forEach>
    </ul>
</c:if>

</body>
</html>
