<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isELIgnored="false" %>
<fmt:setBundle basename="internationalization.jsp.entrance" var="page"/>
<fmt:setBundle basename="internationalization.common" var="common"/>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8" />
        <link rel="stylesheet" href="../resources/css/entrance.css">
        <link rel="stylesheet" href="../static/css/common.css">
        <script src="../static/js/entrance.js"></script>
        <script src="../static/js/cookie-worker.js"></script>
        <title><fmt:message key="app.name" bundle="${common}" /> :: <fmt:message key="title" bundle="${page}"/></title>
        <style>
        </style>
    </head>
    <body>
        <div id="upper_menu">
        </div>
        <div id="central">
            <div id="message_box" class="hidden">
                <div id="message">TEST</div>
                <div id="message_controls">
                    <button>B1</button>
                    <button>B2</button>
                </div>
            </div>
            <div id="mode_selector" class="">
                <div id="reg_selector">
                    Register
                </div>
                <div id="log_selector">
                    Log in
                </div>
            </div>

            <div id="form_container" class="">
                <div id="forms">
                    <form id="register_form" class="hidden" autocomplete="off">
                        <div class="field_group">
                            <label for="reg_login_input">Login</label>
                            <input type="text" id="reg_login_input">
                        </div>
                        <div class="field_group">
                            <label for="reg_password_input_1">Password</label>
                            <input type="password" id="reg_password_input_1">
                        </div>
                        <div class="field_group">
                            <label for="reg_password_input_2">Password (repeat) </label>
                            <input type="password" id="reg_password_input_2">
                        </div>
                        <div class="field_group">
                            <label for="invite_code">Invite code </label>
                            <input type="text" id="invite_code">
                        </div>
                        <div class="button_container">
                            <div class="password_checkbox_container">
                                <input type="checkbox" id="register_password_checkbox">
                                <label for="register_password_checkbox" id="register_password_checkbox_label">show password</label>
                            </div>
                            <button type="button" class="submit_button" id="register_form_submit_button">Accept</button>
                        </div>
                    </form>
                    <form id="login_form" autocomplete="off" action="../action/">
                        <input type="hidden" name="command" value="login" />
                        <div class="field_group">
                            <label for="log_login_input">Login</label>
                            <input type="text" id="log_login_input">
                        </div>
                        <div class="field_group">
                            <label for="log_password_input">Password</label>
                            <input type="password" id="log_password_input">
                        </div>
                        <div class="button_container">
                            <div class="password_checkbox_container">
                                <input type="checkbox" id="login_password_checkbox">
                                <label for="login_password_checkbox" id="login_password_checkbox_label">show password</label>
                            </div>
                            <button type="button" class="submit_button" id="login_form_submit_button">Accept</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
        <div id="bottom_menu">
            <form action="" method="POST" id="preferred_language_form">
                <select id="preferred_language" name="preferred_language">
                    <option value="english">English</option>
                    <option value="беларуская">Беларуская</option>
                    <option value="русский">Русский</option>
                </select>
            </form>
        </div>
    </body>
</html>
