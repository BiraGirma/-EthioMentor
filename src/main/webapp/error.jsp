<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<!DOCTYPE html>
<html>
<head>
    <title>EthioMentor | Something went wrong</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/global.css">
</head>
<body>
<main style="max-width:480px;margin:15vh auto;padding:0 1rem;text-align:center;font-family:sans-serif">
    <h1>
        <%= response.getStatus() == 403 ? "Access denied"
          : response.getStatus() == 404 ? "Page not found"
          : "Something went wrong" %>
    </h1>
    <p>Please go back, or return to the <a href="${pageContext.request.contextPath}/">home page</a>.</p>
</main>
</body>
</html>
