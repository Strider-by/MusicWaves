<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri = "http://java.sun.com/jsp/jstl/core"%>
<%@ page session="true" %>
<c:set var="avatarsDir" scope="page" value="${contextPath}/static/images/avatars/"/>

<html>
<head>
    <title id = "page_title">
        App title: form name
    </title>
    <link type="text/css" rel="stylesheet" href="static/css/playlists.css" />
    <link type="text/css" rel="stylesheet" href="static/css/access_denied_msg_box.css" />
    <link rel="icon" type="image/png" href="static/images/favicon-200x200.png" sizes="200x200">
    <script src="/static/js/playlists.js" charset="utf-8"></script>
    <script>
            window.ctx = "${contextPath}";
            window.textbundle = {};
            window.onload = function () {
                run();
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
        <div class="upper_menu menu">
            <a href="./" id="goto_index_page" title="site_map>">&#127968;</a>
        </div>



        <div id="main" class="flex">
            <div id="outer_container">
                <div id ="header_container">
                    <div id="form_name_container">
                        <div class="form_name noselect">
                            playlists.labels.form_name
                        </div>
                    </div>
                    <div id="music_player">
                        <audio id="player" class="undisplayable">
                            <source src="">
                        </audio>
                        <div id="playlistAreaNumber" class="undisplayable"></div>
                        <div id="playlistItemNumber" class="undisplayable"></div>
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
                                <button id="play_prev_b">&#10502;</button>
                                <button id="play_next_b">&#10503;</button>
                            </div>
                            <div id="player_time">
                                <span id="currentTime">0:00:00</span>
                                <span class="undisplayable">&nbsp;|&nbsp;</span>
                                <span class="undisplayable" id="totalTime">0:00:00</span>
                            </div>
                        </div>

                    </div>
                </div>
                <div id="content_type_selection">
                    <button id="playlists_mode_button">general.labels.playlists</button>
                    <button id="favourites_mode_button">general.labels.favourites</button>
                </div>
                <div id ="content_container">

                    <div id="playlists_list_area">
                        <div class="heading_part">
                            <div id="playlists_list_header">
                                general.labels.playlists
                            </div>
                            <div id="playlists_filter_area">
                                <input id="playlists_filter_input">
                                <button id="clean_playlists_filter">&#10005;</button>
                            </div>
                            <div id="playlists_list_controls">
                                <button id="new_playlist_button">&plus;</button>
                                <button id="delete_playlist_button">&minus;</button>
                                <button id="rename_playlist_button">&hellip;</button>
                                <button id="reload_playlists_list_button">&orarr;</button>
                            </div>
                        </div>
                        <div id="new_playlist_area" class="undisplayable">
                            <div class="action_type noselect">&plus;</div>
                            <input id="new_playlist_name" maxlength="45">
                            <button id="apply_playlist_creation">&check;</button>
                            <button id="cancel_playlist_creation">&#9243;</button>
                            <div class="questionmark noselect" title="naming riles">?</div>
                    </div>
                    <div id="rename_playlist_area" class="undisplayable">
                        <div class="action_type noselect">&hellip;</div>
                        <input class="undisplayable" id="renamed_playlist_id">
                        <input id="edit_playlist_name" maxlength="45">
                        <button id="process_playlist_rename">&#128190;</button>
                        <button id="cancel_playlist_rename">&#9243;</button>
                        <div class="questionmark noselect" title="common_naming_rules">?</div>
                </div>
                <div id="delete_playlist_area" class="undisplayable">
                    <div class="action_type noselect">&minus;</div>
                    <button id="select_all_playlists_button">&check;&check;</button>
                    <button id="unselect_all_playlists_button">&check;&check;</button>
                    <button id="delete_selected_playlists_button">&#9249;</button>
                    <button id="exit_delete_playlists_mode_button">&#9243;</button>
                </div>
                <div id="playlists">

                </div>
            </div>
            <div id="favourites_area" class="undisplayable">
                <div id="favourites_header">
                    <div id="current_fav_mode" class="noselect"></div>
                    general.labels.favourites
                    <div id="fav_search_mode">
                        <button id="fav_artists">&#128104;</button>
                        <button id="fav_albums">&#128191;</button>
                        <button id="fav_tracks">&#127925;</button>
                    </div>
                </div>
                <div id="search_string_block">
                    <input id="search_string">
                    <button id="cleanse_search_button">&#10005;</button>
                </div>
                <div id="search_pages_control_area">
                    <input type="number" min="1" id="page_number" value="1">
                    <button id="apply_page_number">&crarr; | &orarr;</button>
                    <button id="goto_start_button">&LeftArrowBar;</button>
                    <button id="goto_back_button">&slarr;</button>
                    <button id="goto_forward_button">&srarr;</button>
                </div>
                <div id="favourited_items">

                </div>
            </div>
            <div id="playlist_1_area">
                <div id="playlist_1_header">
                    <div class="header_button_container">
                        <span class="playlist_area_number">&#10102;</span>
                        <button id="save_pl1_button">&#128190;</button>
                    </div>
                    <div id="pl1_name"></div>
                    <div id="pl1_id" class="undisplayable"></div>
                    <div class="header_button_container">
                        <button id="reload_pl1_button">&orarr;</button>
                        <span class="playlist_area_number">&#10102;</span>
                    </div>
                </div>
                <div id="playlist_1_controls">
                    <div id="pl1_row1">
                        <button id="select_all_tracks_pl1_button">&check;&check;</button>
                        <button id="unselect_all_tracks_pl1_button">&check;&check;</button>
                        <button id="copy_selected_to_pl2">&plus;&rarr;</button>
                        <button id="move_selected_to_pl2">&minus;&rarr;</button>
                        <button id="delete_selected_tracks_p1_button">&#9249;</button>
                    </div>
                    <div id="pl1_row2">
                        <button id="p1_move_up">&uarr;</button>
                        <button id="p1_move_down">&darr;</button>
                        <button id="p1_move_top">&UpArrowBar;</button>
                        <button id="p1_move_bottom">&DownArrowBar;</button>
                    </div>
                </div>
                <div id="playlist_1_items">

                </div>
            </div>
            <div id="playlist_2_area">
                <div id="playlist_2_header">
                    <div class="header_button_container">
                        <span class="playlist_area_number">&#10103;</span>
                        <button id="save_pl2_button">&#128190;</button>
                    </div>
                    <div id="pl2_name"></div>
                    <div id="pl2_id" class="undisplayable"></div>
                    <div class="header_button_container">
                        <button id="reload_pl2_button">&orarr;</button>
                        <span class="playlist_area_number">&#10103;</span>
                    </div>
                </div>
                <div id="playlist_2_controls">
                    <div id="pl2_row1">
                        <button id="select_all_tracks_pl2_button">&check;&check;</button>
                        <button id="unselect_all_tracks_pl2_button">&check;&check;</button>
                        <button id="copy_selected_to_pl1">&plus;&larr;</button>
                        <button id="move_selected_to_pl1">&minus;&larr;</button>
                        <button id="delete_selected_tracks_p2_button">&#9249;</button>
                    </div>
                    <div id="pl2_row2">
                        <button id="p2_move_up">&uarr;</button>
                        <button id="p2_move_down">&darr;</button>
                        <button id="p2_move_top">&UpArrowBar;</button>
                        <button id="p2_move_bottom">&DownArrowBar;</button>
                    </div>
                </div>
                <div id="playlist_2_items">

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
                <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Earum, inventore!</p>
            </div>
        </dialog>

        <div class="bottom_menu menu">
        </div>
</body>
</html>
