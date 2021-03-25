<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix='c' uri='http://java.sun.com/jsp/jstl/core' %>
<html>
<head>

</head>
<body>
<a target="_blank" rel="noopener noreferrer" href="<c:out value="${url}"/>">new random link</a>
<p><c:out value="${url}"></c:out></p>
</body>
</html>