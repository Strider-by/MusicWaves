<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ctg" uri="customtags"%>
<%@ page isELIgnored="false" %>
<%@ page session="true" %>
<jsp:include page="parts/locale_setup.jsp"/>
<fmt:setBundle basename="internationalization.jsp.listen" var="page"  scope="request"/>
<jsp:include page="parts/page_title_setup.jsp"/>
<jsp:include page="parts/service_response_processing.jsp"/>
<%--
Setting variables
--%>
<fmt:message bundle="${page}" key="artist" var="artist"/>
<fmt:message bundle="${page}" key="album" var="album"/>
<fmt:message bundle="${page}" key="track" var="track"/>
<fmt:message bundle="${page}" key="playlist" var="playlist"/>
<fmt:message bundle="${page}" key="random" var="random"/>
<fmt:message bundle="${page}" key="repeat" var="repeat"/>
<html>
<head>
    <title id = "page_title">
        ${title}
    </title>
    <link type="text/css" rel="stylesheet" href="/static/css/listen.css" />
    <link type="text/css" rel="stylesheet" href="/static/css/messages.css" />
    <link rel="icon" type="image/png" href="/static/images/favicon-200x200.png" sizes="200x200">
    <script src="/static/js/common.js" charset="utf-8"></script>
    <script src="/static/js/listen.js" charset="utf-8"></script>
    <script src="/static/js/messages.js" charset="utf-8"></script>
    <script>
            window.ctx = "${contextPath}";
            window.textbundle = {};
            window.onload = function () {
                init();
            };
            window.artistImagePath = "/artist-images/";
            window.albumImagePath = "/album-images/";
            window.trackFilePath = "/music/";

            window.textbundle = {};
            window.textbundle.notLoggedIn = "not logged in";
            window.textbundle.requestFailed = "request failed";
            window.textbundle.invalidData = "invalid data";
            window.textbundle.insufficientRights = "not enough rights";
            window.textbundle.serverSideError = "server side error";
            window.textbundle.userNotFound = "user not found";

            window.textbundle.albums = "albums";
            window.textbundle.artists = "artists";
            window.textbundle.tracks = "tracks";

            window.textbundle.album = "album";
            window.textbundle.artist = "artist";
            window.textbundle.track = "track";
            window.textbundle.year = "year";


        </script>
</head>

<body>

        <%--<div class="upper_menu menu">
            <a href="./" id="goto_index_page" title="site map">&#127968;</a>
        </div> --%>
        <div id="heading_menu">
            <p>${title}</p>
            <div id="heading_menu_button_section">
                <button class="heading_menu_button">1</button>
                <button class="heading_menu_button">2</button>
                <button id="logout_button" class="heading_menu_button">[X]</button>
            </div>
        </div>



        <div id="main" class="flex">
            <div id="form_container">
                <div id="playlists">
                    <div class="area_header noselect">${playlist}</div>
                    <select id="playlists_list"></select>
                    <div id="tracks"></div>
                </div>
                <div id="music_player_container">
                    <div id="music_player">
                        <audio id="player" class="undisplayable">
                            <source src="">
                        </audio>
                        <div id="playlistId" class="undisplayable"></div>
                        <div id="playlistItemNumber" class="undisplayable"></div>

                        <div id="control_buttons">
                            <div id="buttons_1">
                                <button id="play_b">&#9658;</button>
                                <button id="pause_b">&#10074;&#10074;</button>
                                <button id="stop_b">&#11035;</button>
                            </div>
                            <div id="buttons_2">
                                <button id="play_prev_b">❮</button>
                                <button id="play_next_b">❯</button>
                            </div>
                        </div>
                        <div id=sound_controls>
                            <button id="decr_volume">-</button>
                            <div id="sound_container">
                                <div id="sound_bar"></div>
                            </div>
                            <button id="encr_volume">+</button>
                        </div>
                        <div id="progress_container">
                            <div id="progress_bar"></div>
                        </div>
                        <div id="timeScope">

                            <div id="player_time" class="noselect">
                                <span id="currentTime">0:00:00</span>
                                <span>&nbsp;|&nbsp;</span>
                                <span id="totalTime">0:00:00</span>
                            </div>
                        </div>

                        <div id="options">
                            <div>
                                <input type="checkbox" id="option_random">
                                <label for="option_random" class="noselect">${random}</label>
                            </div>
                            <div>
                                <input type="checkbox" id="option_repeat">
                                <label for="option_repeat" class="noselect">${repeat}</label>
                            </div>
                        </div>

                    </div>
                </div>
                <div id="track_props">
                    <div id="track_block">
                        <div class="area_header noselect">${track}</div>
                        <div id="track_name_block">
                        </div>
                    </div>
                    <div id="artist_block">
                        <div class="area_header noselect">${artist}</div>
                        <div class="image_container"></div>
                        <div class="prop_field"></div>
                    </div>
                    <div id="album_block">
                        <div class="area_header noselect">${album}</div>
                        <div class="image_container"></div>
                        <div class="prop_field"></div>
                    </div>
                </div>
            </div>
        </div>

</body>
</html>
