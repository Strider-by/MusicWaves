<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ctg" uri="customtags"%>
<%@ page isELIgnored="false" %>
<jsp:include page="parts/locale_setup.jsp"/>
<fmt:setBundle basename="internationalization.jsp.music_compound" var="page"  scope="request"/>
<jsp:include page="parts/page_title_setup.jsp"/>
<jsp:include page="parts/service_response_processing.jsp"/>
<%--
    Setting variables
--%>
<fmt:message bundle="${shared}" key="wait_message" var="waitMsg"/>
<fmt:message bundle="${shared}" key="request_failed_message" var="requestFailedMessage"/>
<%-- buttons --%>
<fmt:message bundle="${page}" key="cancel" var="cancelButtonText"/>
<fmt:message bundle="${page}" key="update" var="updateButtonText"/>
<fmt:message bundle="${page}" key="delete" var="deleteButtonText"/>
<fmt:message bundle="${page}" key="close" var="closeButtonText"/>
<fmt:message bundle="${page}" key="use" var="useButtonText"/>
<fmt:message bundle="${page}" key="shift_up" var="shiftUpButtonText"/>
<fmt:message bundle="${page}" key="shift_down" var="shiftDownButtonText"/>
<fmt:message bundle="${page}" key="upload" var="uploadButtonText"/>
<%-- labels --%>
<fmt:message bundle="${page}" key="all" var="all"/>
<fmt:message bundle="${page}" key="visible" var="visible"/>
<fmt:message bundle="${page}" key="invisible" var="invisible"/>
<fmt:message bundle="${page}" key="contains" var="contains"/>
<fmt:message bundle="${page}" key="equals" var="equals"/>

<fmt:message bundle="${page}" key="search" var="search"/>
<fmt:message bundle="${page}" key="navigation" var="navigation"/>
<fmt:message bundle="${page}" key="name" var="name"/>
<fmt:message bundle="${page}" key="year" var="year"/>
<fmt:message bundle="${page}" key="number" var="number"/>
<fmt:message bundle="${page}" key="pages" var="pages"/>
<fmt:message bundle="${page}" key="records" var="records"/>
<fmt:message bundle="${page}" key="current_page" var="currentPage"/>
<fmt:message bundle="${page}" key="current_file" var="currentFile"/>

<fmt:message bundle="${page}" key="artists" var="artists"/>
<fmt:message bundle="${page}" key="artist" var="artist"/>
<fmt:message bundle="${page}" key="create_artist" var="createArtist"/>
<fmt:message bundle="${page}" key="edit_artist" var="editArtist"/>

<fmt:message bundle="${page}" key="album" var="album"/>
<fmt:message bundle="${page}" key="albums" var="albums"/>
<fmt:message bundle="${page}" key="create_album" var="createAlbum"/>
<fmt:message bundle="${page}" key="edit_album" var="editAlbum"/>

<fmt:message bundle="${page}" key="tracks" var="tracks"/>
<fmt:message bundle="${page}" key="create_track" var="createTrack"/>
<fmt:message bundle="${page}" key="edit_track" var="edittrack"/>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <link rel="icon" type="image/png" href="/static/img/favicon-200x200.png" sizes="200x200">
    <link rel="stylesheet" href="../static/css/music_compound.css">
    <link rel="stylesheet" href="../static/css/common.css">
    <link rel="stylesheet" href="../static/css/main_structure.css">
    <script src="../static/js/music_compound.js"></script>
    <script src="../static/js/music_compound_artists.js"></script>
    <script src="../static/js/music_compound_albums.js"></script>
    <script src="../static/js/music_compound_tracks.js"></script>
    <script src="../static/js/common.js"></script>
    <title>${title}</title>
    <style>
    </style>
</head>
<body>
<div class="hidden" id="locale_set_properties">
</div>
<div id="heading_menu">
    <p>${title}</p>
    <div id="heading_menu_button_section">
        <ctg:administrator-only-accessible-pages-menu-buttons/>
        <ctg:curator-and-higher-accessible-pages-menu-buttons/>
        <ctg:user-accessible-pages-menu-buttons/>
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
        <div id="msg_close_button_container"><button class="" id="close_message_box_button">${closeButtonText}</button></div>
    </div>
    <div id="wait_message_box_container" class="hidden">
        <div>${waitMsg}</div>
    </div>
    <div id="request_failed_message_holder" class="hidden">
        ${requestFailedMessage}
    </div>
    <div id="main_window" class="">
        <div id="current_action_info">
            <div id="generic_action_info" class="hidden">
                <p>You are about to do something.</p>
                <p>Please, don't.</p>
            </div>
        </div>
        <div id="multitab_window">
            <div id="tabs">
                <div id="artists_tab_label" class="tab current active">${artist}</div>
                <div id="albums_tab_label" class="tab inactive">${album}</div>
                <div id="tracks_tab_label" class="tab inactive">${tracks}</div>
            </div>
            <%-- ARTISTS --%>
            <div class="tabs_content_container" id="artists_tab">
                <div class="left_side_block">
                    <div class="search_nav_block">
                        <div class="search_block">
                            <label>${search}</label>
                            <div class="block_content">
                                <div class="subblock_content">
                                    <label>${name}</label>
                                    <input id="artist_name_filter" class="name_filter">
                                    <div class="name_filter_controls">
                                        <select id="artist_name_search_type">
                                            <ctg:similarity-type-options />
                                        </select>
                                        <button id="clear_artist_name_filter">x</button>
                                    </div>
                                </div>
                                <div class="subblock_content visibility_filter">
                                    <label>
                                        <input type="radio" name="artists_search_visible" value="0" checked>
                                        <span>${all}</span>
                                    </label>
                                    <label>
                                        <input type="radio" name="artists_search_visible" value="1">
                                        <span>${visible}</span>
                                    </label>
                                    <label>
                                        <input type="radio" name="artists_search_visible" value="2">
                                        <span>${invisible}</span>
                                    </label>
                                </div>
                            </div>
                        </div>
                        <div class="nav_block">
                            <label>${navigation}</label>
                            <div class="block_content">
                                <div class="subblock_content">
                                    <label>${pages} | ${records} </label>
                                    <input id="artists_search_results_count" class="results_count">
                                </div>
                                                            </div>
                            <div class="block_content pages_navigation">
                                <div class="subblock_content">
                                    <label>${currentPage}</label>
                                    <input id="artists_current_page" class="current_page" type="number" min="1" value="1">
                                </div>
                                <div class="subblock_content nav_buttons">
                                    <button class="nav_button" id="1st_artist_page">&lt;&lt;</button>
                                    <button class="nav_button" id="last_artist_page">&gt;&gt;</button>
                                    <button class="nav_button" id="prev_artist_page">&lt;</button>
                                    <button class="nav_button" id="next_artist_page">&gt;</button>
                                    <button class="nav_button" id="reload_artist_page">&#8635;</button>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="search_results_block">
                        <label>${artists}</label>
                        <div class="block_content" id="found_artists_data_rows">
                        </div>
                    </div>
                </div>
                <div class="right_side_block">
                    <div class="new_edit_block">
                        <div class="new_instance-block">
                            <label>${createArtist}</label>
                            <div class="block_content">
                                <div class="subblock_content new_instance_name_setting">
                                    <label>${name}</label>
                                    <div class="new_instance_name_and_controls">
                                        <input class="new_instance_name" id="new_artist_name">
                                        <button id="applyArtistCreation">v</button>
                                        <button id="clearNewArtistName">x</button>
                                    </div>
                                </div>
                                <div class="subblock_content new_instance_visibility_setting">
                                    <label>
                                        <input type="radio" name="new_artist_visibility" value="1">
                                        <span>${visible}</span>
                                    </label>
                                    <label>
                                        <input type="radio" name="new_artist_visibility" value="2" checked>
                                        <span>${invisible}</span>
                                    </label>
                                </div>
                            </div>
                        </div>
                        <%-- EDIT ARTIST AREA --%>
                        <div class="edit_instance_block" id="edit_artist_block">
                            <label>${editArtist}</label>
                            <div class="block_content">
                                <div class="upper_part_edit_instance_block">
                                    <div class="instance_edit_image_block">
                                        <img id="artistImage" src="" class="instance_image">
                                    </div>
                                    <div class="use_button_container">
                                        <button class="use_selected_instance" id="useCurrentArtist">${useButtonText}</button>
                                    </div>
                                </div>
                                <div class="lower_part_edit_instance_block">
                                    <div class="upload_file_block">
                                        <input name="track_file" id="artist_file_input" type="file" class="hidden">
                                        <button class="upload_file_button" id="uploadArtistImage">${uploadButtonText}</button>
                                    </div>
                                    <div class="edit_instance_block_properties_controls">
                                        <div class="edit_instance_block_properties">
                                            <div class="edit_instance_block_property">
                                                <label>id</label>
                                                <input id="artist_in_use_id" type="text" readonly>
                                            </div>
                                            <div class="edit_instance_block_property">
                                                <label>${name}</label>
                                                <input id="artist_in_use_name" type="text">
                                            </div>
                                            <div class="edit_instance_block_property_v2">
                                                <label>
                                                    <input type="radio" name="current_artist_visibility" value="2" checked>
                                                    <span>${invisible}</span>
                                                </label>
                                                <label>
                                                    <input type="radio" name="current_artist_visibility" value="1">
                                                    <span>${visible}</span>
                                                </label>
                                            </div>
                                        </div>
                                        <div class="edit_instance_block_controls">
                                            <button class="close_instance_button" id="closeCurrentArtist">${closeButtonText}</button>
                                            <button class="update_instance_button" id="updateCurrentArtist">${updateButtonText}</button>
                                            <button class="delete_instance_button" id="deleteCurrentArtist">${deleteButtonText}</button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <%-- ALBUMS --%>
            <div class="tabs_content_container hidden" id="albums_tab">
                <div class="left_side_block">
                    <div class="search_nav_block">
                        <div class="search_block">
                            <label>${search}</label>
                            <div class="block_content">
                                <div class="subblock_content">
                                    <label>${name}</label>
                                    <input id="album_name_filter" class="name_filter">
                                    <label id="album_year_filter_label">${year}</label>
                                    <div class="albums_year_search_block">
                                        <input id="album_year_filter" type="number" class="year_filter">
                                        <button id="clear_album_name_filter">x</button>
                                    </div>
                                </div>
                                <div class="subblock_content visibility_filter" id="album_visibility_filter">
                                    <label>
                                        <input type="radio" name="albums_search_visible" value="0" checked>
                                        <span>${all}</span>
                                    </label>
                                    <label>
                                        <input type="radio" name="albums_search_visible" value="1">
                                        <span>${visible}</span>
                                    </label>
                                    <label>
                                        <input type="radio" name="albums_search_visible" value="2">
                                        <span>${invisible}</span>
                                    </label>
                                </div>
                            </div>
                        </div>
                        <div class="nav_block">
                            <label>${navigation}</label>
                            <div class="block_content">
                                <div class="subblock_content">
                                    <label>${pages} | ${records} </label>
                                    <input id="albums_search_results_count" class="results_count">
                                </div>
                            </div>
                            <div class="block_content pages_navigation">
                                <div class="subblock_content">
                                    <label>${currentPage}</label>
                                    <input id="albums_current_page" class="current_page" value="1" min="1">
                                </div>
                                <div class="subblock_content nav_buttons">
                                    <button class="nav_button" id="1st_album_page">&lt;&lt;</button>
                                    <button class="nav_button" id="last_album_page">&gt;&gt;</button>
                                    <button class="nav_button" id="prev_album_page">&lt;</button>
                                    <button class="nav_button" id="next_album_page">&gt;</button>
                                    <button class="nav_button" id="reload_album_page">&#8635;</button>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="search_results_block">
                        <label>${albums}</label>
                        <div class="block_content" id="found_albums_data_rows">
                        </div>
                    </div>
                </div>
                <div class="right_side_block">
                    <div class="new_edit_block">
                        <div class="new_instance-block">
                            <label>${createAlbum}</label>
                            <div class="block_content">
                                <div class="subblock_content new_instance_name_setting">
                                    <label>${name}</label>
                                    <div class="new_instance_name_and_controls">
                                        <input class="new_instance_name" id="new_album_name">
                                    </div>
                                </div>
                                <div class="subblock_content new_instance_name_setting">
                                    <label>${year}</label>
                                    <div class="new_instance_name_and_controls">
                                        <input class="new_instance_year" id="new_album_year" type="number">
                                    </div>
                                </div>
                                <div class="subblock_content new_instance_visibility_setting" id="new_album_visibility_setting">
                                    <label>
                                        <input type="radio" name="new_album_visibility" value="1">
                                        <span>${visible}</span>
                                    </label>
                                    <label>
                                        <input type="radio" name="new_album_visibility" value="2" checked>
                                        <span>${invisible}</span>
                                    </label>
                                </div>
                                <div class="subblock_content" id="new_album_controls">
                                    <button id="applyAlbumCreation">v</button>
                                    <button id="clearNewAlbumInputs">x</button>
                                </div>
                            </div>
                        </div>
                        <div class="edit_instance_block" id="edit_album_block">
                            <label>${editAlbum}</label>
                            <div class="block_content">
                                <div class="upper_part_edit_instance_block">
                                    <div class="instance_edit_image_block">
                                        <img id="albumImage" src="" class="instance_image">
                                    </div>
                                    <div class="use_button_container">
                                        <button class="use_selected_instance" id="useCurrentAlbum">${useButtonText}</button>
                                    </div>
                                </div>
                                <div class="lower_part_edit_instance_block">
                                    <div class="upload_file_block">
                                        <input name="track_file" id="album_file_input" type="file" class="hidden">
                                        <button class="upload_file_button" id="uploadAlbumImage">${uploadButtonText}</button>
                                    </div>
                                    <div class="edit_instance_block_properties_controls">
                                        <div class="edit_instance_block_properties">
                                            <div class="edit_instance_block_property">
                                                <label>id</label>
                                                <input id="album_in_use_id" type="text" readonly>
                                            </div>
                                            <div class="edit_instance_block_property">
                                                <label>${name}</label>
                                                <input id="album_in_use_name" type="text">
                                            </div>
                                            <div class="edit_instance_block_property">
                                                <label>${year}</label>
                                                <input id="album_in_use_year" type="number">
                                            </div>
                                            <div class="edit_instance_block_property_v2">
                                                <label>
                                                    <input type="radio" name="current_album_visibility" value="2" checked>
                                                    <span>${invisible}</span>
                                                </label>
                                                <label>
                                                    <input type="radio" name="current_album_visibility" value="1">
                                                    <span>${visible}</span>
                                                </label>
                                            </div>
                                        </div>
                                        <div class="edit_instance_block_controls">
                                            <button class="close_instance_button" id="closeCurrentAlbum">${closeButtonText}</button>
                                            <button class="update_instance_button" id="updateCurrentAlbum">${updateButtonText}</button>
                                            <button class="delete_instance_button" id="deleteCurrentAlbum">${deleteButtonText}</button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <%-- TRACKS --%>
            <div class="tabs_content_container hidden" id="tracks_tab">
                <div class="left_side_block">
                    <div class="search_results_block" id="tracks_in_album">
                        <label>${tracks}</label>
                        <div class="block_content" id="found_tracks_data_rows">
                        </div>
                    </div>
                </div>
                <div class="right_side_block">
                    <div class="new_edit_block">
                        <div class="new_instance-block">
                            <label>${createTrack}</label>
                            <div class="block_content">
                                <div class="subblock_content new_instance_name_setting">
                                    <label>${name}</label>
                                    <div class="new_instance_name_and_controls">
                                        <input class="new_instance_name" id="new_track_name">
                                        <button id="applyTrackCreation">v</button>
                                        <button id="clearNewTrackName">x</button>
                                    </div>
                                </div>
                                <div class="subblock_content new_instance_visibility_setting">
                                    <label>
                                        <input type="radio" name="new_track_visibility" value="1">
                                        <span>${visible}</span>
                                    </label>
                                    <label>
                                        <input type="radio" name="new_track_visibility" value="2" checked>
                                        <span>${invisible}</span>
                                    </label>
                                </div>
                            </div>
                        </div>
                        <div class="edit_instance_block" id="edit_track_block">
                            <label>${editTrack}</label>
                            <div class="block_content" id="edit_track_content_block">
                                <div class="upper_part_edit_instance_block" id="upper_part_edit_track_block">
                                    <div class="instance_edit_file_block" id="edit_track_file_block">
                                        <label for="current_track_file">${currentFile}</label>
                                        <input readonly id="current_track_file">
                                        <input name="track_file" id="track_file_input" type="file" class="hidden">
                                        <button class="upload_file_button" id="uploadTrackFile">${uploadButtonText}</button>
                                    </div>
                                </div>
                                <hr class="line"/>
                                <div class="middle_part_edit_instance_block">
                                    <div id="track_number_holder">
                                        <label>${number}</label>
                                        <input readonly id="current_track_number">
                                    </div>
                                    <div id="track_number_controls">
                                        <button id="shift_track_up_button">${shiftUpButtonText}</button>
                                        <button id="shift_track_down_button">${shiftDownButtonText}</button>
                                    </div>
                                </div>
                                <hr class="line"/>
                                <div class="lower_part_edit_instance_block" id="lower_part_edit_track_block">

                                    <div class="edit_instance_block_properties_controls">
                                        <div class="edit_instance_block_properties">
                                            <div class="edit_instance_block_property">
                                                <label>id</label>
                                                <input id="track_in_use_id" type="text" readonly>
                                            </div>
                                            <div class="edit_instance_block_property">
                                                <label>${name}</label>
                                                <input id="track_in_use_name" type="text">
                                            </div>
                                            <div class="edit_instance_block_property_v2">
                                                <label>
                                                    <input type="radio" name="current_track_visibility" value="2" checked>
                                                    <span>${invisible}</span>
                                                </label>
                                                <label>
                                                    <input type="radio" name="current_track_visibility" value="1">
                                                    <span>${visible}</span>
                                                </label>
                                            </div>
                                        </div>
                                        <div class="edit_instance_block_controls">
                                            <button class="close_instance_button" id="closeCurrentTrack">${closeButtonText}</button>
                                            <button class="update_instance_button" id="updateCurrentTrack">${updateButtonText}</button>
                                            <button class="delete_instance_button" id="deleteCurrentTrack">${deleteButtonText}</button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

        </div>
    </div>
</div>
</body>
</html>
