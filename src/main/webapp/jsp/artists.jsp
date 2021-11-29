<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ctg" uri="customtags"%>
<%@ page isELIgnored="false" %>
<jsp:include page="parts/locale_setup.jsp"/>
<fmt:setBundle basename="internationalization.jsp.artists" var="page"  scope="request"/>
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

<%-- roles --%>

<%-- filter variants --%>



<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8" />
        <link rel="stylesheet" href="../static/css/artists.css">
        <link rel="stylesheet" href="../static/css/common.css">
        <link rel="stylesheet" href="../static/css/main_structure.css">
        <script src="../static/js/artists.js"></script>
        <script src="../static/js/common.js"></script>
        <title>${title}</title>
        <style>
        </style>
    </head>
    <body>
        <div class="hidden" id="locale_set_properties">
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
                <label for="name_filter" class="search_param_label">name</label>
                <div id="name_search_params">
                    <select id="name_filter_rule">
                        <ctg:similarity-type-options />
                    </select>
                    <input id="name_filter" type="text"/>
                    <button id="clear_name_filter">x</button>
                </div>
            </div>
            <div class="nav_search_block">
                <label for="visibility_state_filter" class="search_param_label">visible</label>
                <div id="visibility_search_params">
                    <select id="visibility_state_filter">
                        <ctg:boolean-type-options />
                    </select>
                    <button id="clear_visibility_state_filter">x</button>
                </div>
            </div>
            <div class="nav_search_block">
                <label for="sort_by" class="search_param_label">sort by</label>
                <div id="sort_params">
                    <select id="sort_by">
                        <ctg:artists-sort-field-options />
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
                <div id="headers">
                    <div class="user_id id_prop">id</div>
                    <div class="user_login img_prop">image</div>
                    <div class="user_role name_prop">name</div>
                </div>
                <div id="data_rows">
                    <div class="single_row_block">
                        <div class="row_property id_prop">1</div>
                        <div class="row_property img_prop"><img src="../example.jpg"/></div>
                        <div class="row_property name_prop">Singer</div>
                    </div>
                </div>
            </div>
        </div>
        <div id="pages_navigation">
            <div class="navigation_block">
                <div class="navigation_group">
                    <div class="navigation_group_element">
                        <label for="total_elements">found elements</label>
                        <input id="total_elements" readonly/>
                    </div>
                    <div class="navigation_group_element">
                        <label for="pages_total">pages total</label>
                        <input id="pages_total" readonly/>
                    </div>
                </div>
            </div>
            <div class="navigation_block">
                <div class="navigation_group">
                    <div class="navigation_group_element">
                        <label for="current_page">page</label>
                        <input id="current_page" type="number" value="1" min="1"/>
                        <button id="reload_page_button">&#8635;</button>
                    </div>
                    <div class="navigation_group_element">
                        <button id="prev_page_button">&lt;</button>
                        <button id="next_page_button">&gt;</button>
                    </div>
                    <div class="navigation_group_element">
                        <button id="first_page_button">&laquo;</button>
                        <button id="last_page_button">&raquo;</button>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
