<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri = "http://java.sun.com/jsp/jstl/core"%>
<%@ page session="true" %>

<html>
<head>
    <title id = "page_title">
        <ctg:loc property="main.app_title"/>: <ctg:loc property="music_search.labels.form_name"/>
    </title>
    <link type="text/css" rel="stylesheet" href="../static/css/music_search.css" />
    <script src="../static/js/common.js" charset="utf-8"></script>
    <script src="../static/js/long-press.js" charset="utf-8"></script>
    <script src="../static/js/music_search.js" charset="utf-8"></script>
    <script>
            window.ctx = "${contextPath}";
            window.textbundle = {};
            window.onload = function () {
                run();
            };
            window.artistImagePath = "../static/images/artists/";
            window.albumImagePath = "../static/images/albums/";
            window.trackFilePath = "../static/tracks/";

            window.textbundle = {};
            window.textbundle.notLoggedIn = "not logged in";
            window.textbundle.requestFailed = "request failed";
            window.textbundle.invalidData = "invalid data";
            window.textbundle.insufficientRights = "insufficient rights";
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
        <div class="upper_menu menu">
            <a href="./" id="goto_index_page" title="map of the site">&#127968;</a>
        </div>



        <div id="main" class="flex">
            <div id="outer_container">
                <div id ="header_container">
                    <div id="form_name_container">
                        <div class="form_name noselect">
                            music search form name
                        </div>
                    </div>
                    <div id="music_player">
                        <audio id="player" class="undisplayable">
                            <source src="">
                        </audio>
                        <div id="progress_container" class="undisplayable">
                            <div id="progress_bar"></div>
                        </div>
                        <div id=sound_controls>
                            <button id="decr_volume">-</button>
                            <div id="sound_container">
                                <div id="sound_bar"></div>
                            </div>
                            <button id="encr_volume">+</button>
                        </div>
                        <div id="timeScope">
                            <div id="control_buttons">
                                <button id="play_b">&#9658;</button>
                                <button id="pause_b">&#10074;&#10074;</button>
                                <button id="stop_b">&#11035;</button>
                            </div>
                            <div id="player_time">
                                <span id="currentTime">0:00:00</span>
                                <span class="invisible">&nbsp;|&nbsp;</span>
                                <span class="invisible" id="totalTime">0:00:00</span>
                            </div>
                        </div>

                    </div>
                </div>
                <div class ="flex">
                    <div class="flex">
                        <div id="search_area">
                            <div id="search_input">
                                <div id="controls">
                                    <div id="search_string_block">
                                        <input id="search_string">
                                        <button id="cleanse_search_button">&#9747;</button>
                                        <button id="execute_search_button" class="undisplayable">&#128269;</button>
                                    </div>
                                    <div id="search_pages_control_area">
                                        <input type="number" min="1" id="page_number" value="1">
                                        <button id="apply_page_number">&crarr; | &orarr;</button>
                                        <button id="goto_start_button">&LeftArrowBar;</button>
                                        <button id="goto_back_button">&slarr;</button>
                                        <button id="goto_forward_button">&srarr;</button>
                                        <button id="goto_end_button" class="undisplayable">&RightArrowBar;</button>
                                    </div>
                                </div>
                                <div id="items_found">
                                    <div class="search_subblock noselect" id="artsits_found">artists: <span id="artists_found_val">0</span>
                                    </div>
                                    <div class="search_subblock noselect" id="albums_found">albums: <span id="albums_found_val">0</span>
                                    </div>
                                    <div class="search_subblock noselect" id="tracks_found">tracks: <span id="tracks_found_val">0</span>
                                    </div>
                                </div>
                                <div id="search_results">

                                </div>
                            </div>
                        </div>
                        <div id="playlists_area">
                            <div>
                                <div id="select_playlist_area">
                                    <div class="area_inner_header">playlists</div>
                                    <div class="flex">
                                        <input id="playlist_name_filter" maxlength="20">
                                        <button id="clean_playlists_filter">&#10005;</button>
                                        <button id="create_new_playlist">+</button>
                                        <div class="questionmark"
                                             title="20 symbols maax">?</div>
                                </div>
                                <div class="flex">
                                    <select id="availible_playlists">
                                    </select>
                                    <button id="reload_playlists_list">&orarr;</button>
                                    <button id="use_playlist">&crarr;</button>
                                </div>
                            </div>
                            <div id="tracks_list">
                                <div class="area_inner_header" id="current_playlist_name">
                                    <span id="used_playlist_name"></span>
                                    <div id="used_playlist_id" class="undisplayable"></div>
                                    <div id="playlist_control_block">
                                        <button id="save_playlist">&#128190;</button>
                                        <button id="reload_playlist">&orarr;</button>
                                        <button id="cleanse_playlist">&#9249;*</button>
                                    </div>
                                </div>
                                <div id="track_items">

                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        </div>

        <dialog id="dialog_window" class="message">
            <div id="dialog_window_header">
                <span id="close_dialog_window" class="noselect">&#10005;</span>
            </div>
            <div id="dialog_window_content_container">
                <h3></h3>
                <p></p>
            </div>
        </dialog>
</body>
</html>
