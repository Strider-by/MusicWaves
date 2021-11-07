<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isELIgnored="false" %>
<%--
Setting locale and locale bundles
--%>
<c:set var="language" value="${not empty locale ? locale : pageContext.request.locale}" scope="request" />
<fmt:setLocale value="${language}" scope="request"/>
<fmt:setBundle basename="internationalization.jsp.shared" var="shared"  scope="request"/>
<fmt:setBundle basename="internationalization.common" var="common"  scope="request"/>
