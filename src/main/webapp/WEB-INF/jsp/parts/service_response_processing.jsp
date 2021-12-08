<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>
<%--
Processing service request object possibly set in session.
If it contains any errors or messages, they will be shown in the message box.
After serviceResponse object is processed, it will be cleaned out of memory.
--%>
<c:if test="${not empty serviceResponse}">
    <c:if test="${!serviceResponse.errorMessages.isEmpty()}">
        <c:set var="errors" value="${serviceResponse.errorMessages}" scope="request"/>
    </c:if>
    <c:if test="${!serviceResponse.messages.isEmpty()}">
        <c:set var="messages" value="${serviceResponse.messages}" scope="request"/>
    </c:if>
    <c:remove var="serviceResponse" scope="session"/>
</c:if>
<c:set var="openMessageBox" value="${not empty errors || not empty messages}" scope="request"/>