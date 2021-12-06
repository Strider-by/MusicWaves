<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ctg" uri="customtags"%>
<%@ page isELIgnored="false" %>
<jsp:include page="parts/locale_setup.jsp"/>
<fmt:setBundle basename="internationalization.jsp.profile" var="page"  scope="request"/>
<jsp:include page="parts/page_title_setup.jsp"/>
<jsp:include page="parts/service_response_processing.jsp"/>
<%--
    Setting values for this page fields
--%>
<c:if test="${not empty user}">
    <c:set var="userLanguageNativeName" value="${user.language.nativeName.toLowerCase()}" scope="page" />
    <c:set var="registerDate" value="${user.created.toLocalDate()}" scope="page" />
    <fmt:message bundle="${shared}" key="${user.role.getPropertyKey()}" var="userRole"/>
</c:if>
<%--
    Setting values that depend on locale used
--%>
<fmt:message bundle="${shared}" key="wait_message" var="waitMsg"/>
<%-- buttons --%>
<fmt:message bundle="${page}" key="cancel_button" var="cancelButtonText"/>
<fmt:message bundle="${page}" key="change_button" var="changeButtonText"/>
<fmt:message bundle="${page}" key="apply_button" var="applyButtonText"/>
<fmt:message bundle="${page}" key="check_button" var="checkButtonText"/>
<fmt:message bundle="${page}" key="delete_button" var="deleteButtonText"/>
<fmt:message bundle="${page}" key="delete_account_button" var="deleteAccountButtonText"/>
<%-- warnings --%>
<fmt:message bundle="${page}" key="password_change_mode_warning_pt_1" var="passwordChangeWarnPt1"/>
<fmt:message bundle="${page}" key="password_change_mode_warning_pt_2" var="passwordChangeWarnPt2"/>
<fmt:message bundle="${page}" key="password_change_mode_warning_pt_3" var="passwordChangeWarnPt3"/>
<fmt:message bundle="${page}" key="login_change_mode_warning_pt_1" var="loginChangeWarnPt1"/>
<fmt:message bundle="${page}" key="login_change_mode_warning_pt_2" var="loginChangeWarnPt2"/>
<fmt:message bundle="${page}" key="delete_mode_warning_pt_1" var="deleteModeWarnPt1"/>
<fmt:message bundle="${page}" key="delete_mode_warning_pt_2" var="deleteModeWarnPt2"/>
<%-- labels --%>
<fmt:message bundle="${page}" key="register_date" var="registerDateLabel"/>
<fmt:message bundle="${page}" key="role" var="roleLabel"/>
<fmt:message bundle="${page}" key="login" var="loginLabel"/>
<fmt:message bundle="${page}" key="new_login" var="newLoginLabel"/>
<fmt:message bundle="${page}" key="password" var="passwordLabel"/>
<fmt:message bundle="${page}" key="old_password" var="oldPasswordLabel"/>
<fmt:message bundle="${page}" key="new_password" var="newPasswordLabel"/>
<fmt:message bundle="${page}" key="new_password_repeat" var="repeatNewPasswordLabel"/>
<fmt:message bundle="${page}" key="language" var="languageLabel"/>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8" />
        <link rel="stylesheet" href="../static/css/profile.css">
        <link rel="stylesheet" href="../static/css/common.css">
        <link rel="stylesheet" href="../static/css/main_structure.css">
        <script src="../static/js/profile.js"></script>
        <script src="../static/js/common.js"></script>
        <script src="/static/js/messages.js" charset="utf-8"></script>
        <link type="text/css" rel="stylesheet" href="/static/css/messages.css" />
        <title>${title}</title>
        <style>
        </style>
    </head>
    <body>
        <div id="heading_menu">
            <p>${title}</p>
            <div id="heading_menu_button_section">
                <button class="heading_menu_button">1</button>
                <button class="heading_menu_button">2</button>
                <button id="logout_button" class="heading_menu_button">[X]</button>
            </div>
        </div>
        <div id="main_window_container">
            <div id="message_box_container" class="hidden">
                <div id="msg_show_required" class="hidden">${openMessageBox}</div>
                <div id="msg_title_container"></div>
                <div id="msg_body_container">
                    <ul>
                        <ctg:list list="${errors}" htmlElementTag="li"/>
                        <ctg:list list="${messages}" htmlElementTag="li"/>
                    </ul>
                </div>
                <div id="msg_close_button_container"><button class="" id="close_message_box_button">Close</button></div>
            </div>
            <div id="wait_message_box_container" class="hidden">
                <div>${waitMsg}</div>
            </div>
            <div id="main_window" class="">
                <div id="current_action_info">
                    <div id="password_change_info" class="hidden">
                        <p>${passwordChangeWarnPt1}</p>
                        <p>${passwordChangeWarnPt2}</p>
                        <p>${passwordChangeWarnPt3}</p>
                    </div>
                    <div id="login_change_info" class="hidden">
                        <p>${loginChangeWarnPt1}</p>
                        <p>${loginChangeWarnPt2}</p>
                    </div>
                    <div id="account_delete_info" class="hidden">
                        <p class="warning_text">${deleteModeWarnPt1}</p>
                        <p>${deleteModeWarnPt2}</p>
                    </div>
                </div>
                <div id="properties">
                    <div class="property_block" id="register_date_property_block">
                        <div class="property_data_block">
                            <label for="user_register_date">${registerDateLabel}</label>
                            <input id="user_register_date" value="${registerDate}" readonly>
                        </div>
                        <div class="property_controls_block">
                            <button class="button_2 dangerous_button" id="delete_account_mode_button">${deleteAccountButtonText}</button>
                        </div>
                    </div>
                    <div class="property_block" id="role_property_block">
                        <div class="property_data_block">
                            <label for="user_role">${roleLabel}</label>
                            <input id="user_role" value="${userRole}" readonly>
                        </div>
                        <div class="property_controls_block">
                        </div>
                    </div>
                    <div class="property_block" id="login_property_block">
                        <div class="property_data_block">
                            <label for="user_login">${loginLabel}</label>
                            <input id="user_login" value="${user.login}" readonly>
                        </div>
                        <div class="property_controls_block">
                            <button id="change_login_mode_button" class="button_2">${changeButtonText}</button>
                        </div>
                    </div>
                    <div class="property_block" id="password_property_block">
                        <div class="property_data_block">
                            <label for="user_password">${passwordLabel}</label>
                            <input id="user_password" value="" type="password" disabled>
                        </div>
                        <div class="property_controls_block">
                            <button id="change_password_mode_button" class="button_2">${changeButtonText}</button>
                        </div>
                    </div>
                    <div class="property_block" id="language_property_block">
                        <div class="property_data_block">
                            <label for="language">${languageLabel}</label>
                            <input id="language" class="" value="${userLanguageNativeName}" type="text" disabled>
                            <select name="language_selector" id="language_selector" class="hidden">
                                <ctg:languages/>
                            </select>
                        </div>
                        <div class="property_controls_block">
                            <button id="change_language_mode_button" class="button_2">${changeButtonText}</button>
                        </div>
                        <div id="language_mode_controls" class="property_controls_block hidden">
                            <button id="apply_language_change_button" class="button_2 warning_required_button">${applyButtonText}</button>
                            <button id="cancel_language_change_button" class="button_2 back_to_safety_button">${cancelButtonText}</button>
                        </div>
                    </div>
                    <div class="property_block hidden" id="password_change_block">
                        <div class="multiple_properties_block">
                            <div class="property_data_block">
                                <label for="old_password_for_password_change" class="required">${oldPasswordLabel}</label>
                                <input id="old_password_for_password_change" value="" type="password">
                            </div>
                            <div class="property_data_block">
                                <label for="new_password_1" class="required">${newPasswordLabel}</label>
                                <input id="new_password_1" value="" type="password">
                            </div>
                            <div class="property_data_block">
                                <label for="new_password_2"  class="required">${repeatNewPasswordLabel}</label>
                                <input id="new_password_2" value="" type="password">
                            </div>
                        </div>
                        <div class="property_controls_block">
                            <button class="button_2 warning_required_button" id="apply_password_change_button">${applyButtonText}</button>
                            <button class="button_2 back_to_safety_button" id="cancel_password_change_button">${cancelButtonText}</button>
                        </div>
                    </div>
                    <div class="property_block hidden" id="login_change_block">
                        <div class="multiple_properties_block">
                            <div class="property_data_block">
                                <label for="password_for_login_change" class="required">${passwordLabel}</label>
                                <input id="password_for_login_change" value="" type="password">
                            </div>
                            <div class="property_data_block">
                                <label for="new_login" class="required">${newLoginLabel}</label>
                                <input id="new_login" value="">
                            </div>
                        </div>
                        <div class="property_controls_block">
                            <button class="button_2" id="check_new_login_button">${checkButtonText}</button>
                            <button class="button_2 warning_required_button" id="apply_login_change_button">${applyButtonText}</button>
                            <button class="button_2 back_to_safety_button" id="cancel_login_change_button">${cancelButtonText}</button>
                        </div>
                    </div>
                    <div class="property_block hidden" id="delete_account_block">
                        <div class="property_data_block">
                            <label for="account_delete_password" class="required">${passwordLabel}</label>
                            <input id="account_delete_password" value="" type="password">
                        </div>
                        <div class="property_controls_block">
                            <button class="button_2 dangerous_button" id="apply_account_deletion_button">${deleteButtonText}</button>
                            <button class="button_2 back_to_safety_button" id="cancel_account_deletion_button">${cancelButtonText}</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
