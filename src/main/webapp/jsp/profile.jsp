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
<c:set var="userLanguageNativeName" value="${not empty user ? user.language.nativeName.toLowerCase() : not_set}" scope="page" />
<c:set var="registerDate" value="${not empty user ? user.created.toLocalDate() : not_set}" scope="page" />
<%--
    Setting values that depend on locale used
--%>
<fmt:message bundle="${shared}" key="wait_message" var="waitMsg"/>


<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8" />
        <link rel="stylesheet" href="../static/css/profile.css">
        <link rel="stylesheet" href="../static/css/common.css">
        <link rel="stylesheet" href="../static/css/main_structure.css">
        <script src="../static/js/profile.js"></script>
        <script src="../static/js/common.js"></script>
        <title>${title}</title>
        <style>
        </style>
    </head>
    <body>
        <div id="heading_menu">
            <p>Profile</p>
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
                        <p>You are about to change password.</p>
                        <p>Do make sure it is strong enough. Or make it simple and live with that.</p>
                        <p>After the password change you will have to log in again.</p>
                    </div>
                    <div id="login_change_info" class="hidden">
                        <p>You are about to change your login.</p>
                        <p>Your new login cannot be occupied and there may be some restrictions it must follow.</p>
                    </div>
                    <div id="account_delete_info" class="hidden">
                        <p class="warning_text">You are about to delete your account!</p>
                        <p>This action will lead to all your local data loss.</p>
                    </div>
                </div>
                <div id="properties">
                    <div class="property_block" id="register_date_property_block">
                        <div class="property_data_block">
                            <label for="user_register_date">register date</label>
                            <input id="user_register_date" value="${registerDate}" readonly>
                        </div>
                        <div class="property_controls_block">
                            <button class="button_2 dangerous_button" id="delete_account_mode_button">Delete account</button>
                        </div>
                    </div>
                    <div class="property_block" id="role_property_block">
                        <div class="property_data_block">
                            <label for="user_role">role</label>
                            <input id="user_role" value="${user.role}" readonly>
                        </div>
                        <div class="property_controls_block">
                        </div>
                    </div>
                    <div class="property_block" id="login_property_block">
                        <div class="property_data_block">
                            <label for="user_login">login</label>
                            <input id="user_login" value="${user.login}" readonly>
                        </div>
                        <div class="property_controls_block">
                            <button id="change_login_mode_button" class="button_2">Change</button>
                        </div>
                    </div>
                    <div class="property_block" id="password_property_block">
                        <div class="property_data_block">
                            <label for="user_password">password</label>
                            <input id="user_password" value="" type="password" disabled>
                        </div>
                        <div class="property_controls_block">
                            <button id="change_password_mode_button" class="button_2">Change</button>
                        </div>
                    </div>
                    <div class="property_block" id="language_property_block">
                        <div class="property_data_block">
                            <label for="language">language</label>
                            <input id="language" class="" value="${userLanguageNativeName}" type="text" disabled>
                            <select name="language_selector" id="language_selector" class="hidden">
                                <ctg:languages/>
                            </select>
                        </div>
                        <div class="property_controls_block">
                            <button id="change_language_mode_button" class="button_2">Change</button>
                        </div>
                        <div id="language_mode_controls" class="property_controls_block hidden">
                            <button id="apply_language_change_button" class="button_2 warning_required_button">Apply</button>
                            <button id="cancel_language_change_button" class="button_2 back_to_safety_button">Cancel</button>
                        </div>
                    </div>
                    <div class="property_block hidden" id="password_change_block">
                        <div class="multiple_properties_block">
                            <div class="property_data_block">
                                <label for="old_password_for_password_change" class="required">old password</label>
                                <input id="old_password_for_password_change" value="" type="password">
                            </div>
                            <div class="property_data_block">
                                <label for="new_password_1" class="required">new password</label>
                                <input id="new_password_1" value="" type="password">
                            </div>
                            <div class="property_data_block">
                                <label for="new_password_2"  class="required">new password (repeat)</label>
                                <input id="new_password_2" value="" type="password">
                            </div>
                        </div>
                        <div class="property_controls_block">
                            <button class="button_2 warning_required_button" id="apply_password_change_button">Apply</button>
                            <button class="button_2 back_to_safety_button" id="cancel_password_change_button">Cancel</button>
                        </div>
                    </div>
                    <div class="property_block hidden" id="login_change_block">
                        <div class="multiple_properties_block">
                            <div class="property_data_block">
                                <label for="password_for_login_change" class="required">password</label>
                                <input id="password_for_login_change" value="" type="password">
                            </div>
                            <div class="property_data_block">
                                <label for="new_login" class="required">new login</label>
                                <input id="new_login" value="">
                            </div>
                        </div>
                        <div class="property_controls_block">
                            <button class="button_2" id="check_new_login_button">Check</button>
                            <button class="button_2 warning_required_button" id="apply_login_change_button">Apply</button>
                            <button class="button_2 back_to_safety_button" id="cancel_login_change_button">Cancel</button>
                        </div>
                    </div>
                    <div class="property_block hidden" id="delete_account_block">
                        <div class="property_data_block">
                            <label for="account_delete_password" class="required">password</label>
                            <input id="account_delete_password" value="" type="password">
                        </div>
                        <div class="property_controls_block">
                            <button class="button_2 dangerous_button" id="apply_account_deletion_button">Delete</button>
                            <button class="button_2 back_to_safety_button" id="cancel_account_deletion_button">Cancel</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
