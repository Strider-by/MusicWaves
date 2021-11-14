<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ctg" uri="customtags"%>
<%@ page isELIgnored="false" %>
<jsp:include page="parts/locale_setup.jsp"/>
<fmt:setBundle basename="internationalization.jsp.users" var="page"  scope="request"/>
<jsp:include page="parts/page_title_setup.jsp"/>
<jsp:include page="parts/service_response_processing.jsp"/>
<%--
    Setting values for this page fields
--%>

<%--
    Setting values that depend on locale used
--%>
<fmt:message bundle="${shared}" key="wait_message" var="waitMsg"/>
<fmt:message bundle="${shared}" key="asc" var="asc"/>
<fmt:message bundle="${shared}" key="desc" var="desc"/>
<%-- field names --%>
<fmt:message bundle="${page}" key="id" var="id"/>
<fmt:message bundle="${page}" key="role" var="role"/>
<fmt:message bundle="${page}" key="login" var="login"/>
<fmt:message bundle="${page}" key="register_date" var="registerDate"/>
<%-- roles --%>
<fmt:message bundle="${shared}" key="user" var="userRoleName"/>
<fmt:message bundle="${shared}" key="curator" var="curatorRoleName"/>
<fmt:message bundle="${shared}" key="administrator" var="adminRoleName"/>
<%-- filter variants --%>
<fmt:message bundle="${shared}" key="equals" var="equals"/>
<fmt:message bundle="${shared}" key="contains" var="contains"/>
<fmt:message bundle="${shared}" key="before" var="before"/>
<fmt:message bundle="${shared}" key="after" var="after"/>
<fmt:message bundle="${shared}" key="ignore" var="ignore"/>


<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8" />
        <link rel="stylesheet" href="../static/css/users.css">
        <link rel="stylesheet" href="../static/css/common.css">
        <link rel="stylesheet" href="../static/css/main_structure.css">
        <script src="../static/js/users.js"></script>
        <script src="../static/js/common.js"></script>
        <title>${title}</title>
        <style>
        </style>
    </head>
    <body>
        <div class="hidden" id="locale_set_properties">
            <input id="delete_button_value" value="Delete"/>
            <input id="change_user_role_button_value" value="Change role"/>
            <input id="cancel_button_value" value="Cancel"/>
            <input id="apply_button_value" value="Apply"/>

            <input id="contains_option_value" value="contains"/>
            <input id="equals_option_value" value="equals"/>
        </div>
        <div class="hidden">
            <select class="" id="role_selector_example">
                <ctg:roles/>
            </select>
        </div>
        <div id="heading_menu">
            <p>${pageName}</p>
            <div id="heading_menu_button_section">
                <button class="heading_menu_button">1</button>
                <button class="heading_menu_button">2</button>
                <button id="logout_button" class="heading_menu_button">[X]</button>
            </div>
        </div>
        <div id="filter_menu">
            <div class="nav_search_block">
                <label for="id_filter" class="search_param_label">id</label>
                <div id="id_search_params">
                    <input id="id_filter" type="number", value=""/>
                    <button id="clear_id_filter">x</button>
                </div>
            </div>
            <div class="nav_search_block">
                <label for="login_filter" class="search_param_label">login</label>
                <div id="login_search_params">
                    <select id="login_filter_rule">
                        <ctg:similarity-type-options />
                    </select>
                    <input id="login_filter" type="text"/>
                    <button id="clear_login_filter">x</button>
                </div>
            </div>
            <div class="nav_search_block">
                <label for="role_filter" class="search_param_label">role</label>
                <div id="role_search_params">
                    <select id="role_filter">
                        <option value=""></option>
                        <ctg:roles />
                    </select>
                    <button id="clear_role_filter">x</button>
                </div>
            </div>
            <div class="nav_search_block">
                <label for="reg_date_filter" class="search_param_label">register date</label>
                <div id="reg_date_search_params">
                    <select id="reg_date_filter_rule">
                        <ctg:date-compare-type-options />
                    </select>
                    <input id="reg_date_filter" type="date"/>
                    <button id="clear_reg_date_filter">x</button>
                </div>
            </div>
            <div class="nav_search_block">
                <label for="sort_by" class="search_param_label">sort by</label>
                <div id="sort_params">
                    <select id="sort_by">
                        <ctg:users-sort-field-options />
                    </select>
                </div>
            </div>
            <div class="nav_search_block">
                <label for="order_of_sorting" class="search_param_label">sort order</label>
                <div id="sort_orders">
                    <select id="order_of_sorting">
                        <ctg:sort-order-options />
                    </select>
                </div>
            </div>
            <button id="apply_filters" class="filter_panel_button">V</button>
            <button id="clear_filters" class="filter_panel_button">X</button>
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
                    <div id="generic_action_info" class="hidden">
                        <p>You are about to do something.</p>
                        <p>Please, don't.</p>
                    </div>
                </div>
                <div id="users_block">
                    <div class="users_headers">
                        <div class="user_id">id</div>
                        <div class="user_login">Login</div>
                        <div class="user_role">Role</div>
                        <div class="registered">Registered</div>
                    </div>
                </div>
            </div>
        </div>
        <div id="pages_navigation">
            <div class="found_items_property">
                <label for="current_page">page</label>
                <input id="current_page" type="number" value="1" min="1"/>
                <button id="reload_page_button">&#8635;</button>
            </div>
            <div class="found_items_property">
                <button id="prev_page_button">&lt;</button>
                <button id="next_page_button">&gt;</button>
            </div>
        </div>
    </body>
</html>
