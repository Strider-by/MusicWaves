<%@ page import="java.io.*,java.util.Locale" %>
<%@ page import="javax.servlet.*,javax.servlet.http.* "%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isELIgnored="false" %>
<c:set var="language" value="${not empty locale ? locale : pageContext.request.locale}" scope="session" />
<fmt:setLocale value="${language}" scope="session"/>
<fmt:setBundle basename="internationalization.jsp.index" var="page" scope="session"/>
<fmt:setBundle basename="internationalization.common" var="common" scope="session"/>


<%
Locale locale2 = request.getLocale();
String language2 = locale2.getLanguage();
String country2 = locale2.getCountry();
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8"/>
    <link rel="stylesheet" href="../static/css/index.css">
    <link rel="stylesheet" href="../static/css/common.css">
    <script src="../static/js/index.js"></script>
    <script src="../static/js/cookie-worker.js"></script>
    <title><fmt:message key="app.name" bundle="${common}" /> :: <fmt:message key="title" bundle="${page}"/></title>
    <style>
    </style>
</head>
<body>
    <h1>Here we go...</h1>
    <h1><fmt:message key="app.name" bundle="${common}" /></h1>
    <%
    out.println("Locale : " + locale2  + "<br />");
    out.println("Language : " + language2  + "<br />");
    out.println("Country  : " + country2   + "<br />");
    %>
    <p><strong>User:</strong> ${user}</p>
    <p><strong>Locale set in session:</strong> ${locale}</p>
    <p><strong>Type of locale object set in session:</strong> ${locale.getClass()}</p>
    <p><strong>Calculated language value:</strong> ${language}</p>
    <p><strong>Calculated language type:</strong> ${language.getClass()}</p>
    <p><strong>Response locale:</strong> ${pageContext.response.locale}</p>
    <p><strong>Request locale:</strong> ${pageContext.request.locale}</p>
    <p><strong>Locale in actual use:</strong> ${page.getLocale()}</p>
</body>
</html>