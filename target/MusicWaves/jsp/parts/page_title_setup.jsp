<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isELIgnored="false" %>
<%--
    Building page name.
    We expect that page bundle ("page" variable) has already been set.
--%>
<fmt:message bundle="${common}" key="app.name" var="appName"/>
<fmt:message bundle="${page}" key="title" var="pageName"/>
<c:set value="${appName} :: ${pageName}" var="title" scope="request"/>