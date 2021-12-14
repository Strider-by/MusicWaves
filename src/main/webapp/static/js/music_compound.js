window.onload = () =>
{
    init();
    initArtistsPart();
    initAlbumsPart();
    initTracksPart();
}

function init()
{
    window.tabs = {};
    window.tabs.artists = document.getElementById("artists_tab");
    window.tabs.albums = document.getElementById("albums_tab");
    window.tabs.tracks = document.getElementById("tracks_tab");
    window.tabs.labels = {};
    window.tabs.labels.artists = document.getElementById("artists_tab_label");
    window.tabs.labels.albums = document.getElementById("albums_tab_label");
    window.tabs.labels.tracks = document.getElementById("tracks_tab_label");

    window.tabs.labels.artists.onclick = switchToArtistsTab;



    window.valueHolders = {};
    window.mainMenu = {};
    window.mainMenu.logout = document.getElementById("logout_button");
    window.mainMenu.logout.addEventListener("click", () => logout());

}

function switchToArtistsTab()
{
    showTab(window.tabs.artists);
    hideTab(window.tabs.albums);
    hideTab(window.tabs.tracks);

    hideEditAlbumBlock();
    hideEditTrackBlock();
    clearAlbumFilters();
    clearNewAlbumInputs();
    clearNewTrackInputs();


    window.tabs.labels.albums.onclick = null;
    setTabLabelInactive(window.tabs.labels.albums);
    setTabLabelInactive(window.tabs.labels.tracks);
    setTabLabelCurrent(window.tabs.labels.artists);
}

function switchToAlbumsTab()
{
    hideTab(window.tabs.artists);
    showTab(window.tabs.albums);
    hideTab(window.tabs.tracks);

    hideEditTrackBlock();
    clearNewTrackInputs();

    setTabLabelInactive(window.tabs.labels.tracks);
    setTabLabelCurrent(window.tabs.labels.albums);
}

function switchToTracksTab()
{
    hideTab(window.tabs.artists);
    hideTab(window.tabs.albums);
    showTab(window.tabs.tracks);

    window.tabs.labels.albums.onclick = switchToAlbumsTab;
    setTabLabelActive(window.tabs.labels.albums);
    setTabLabelCurrent(window.tabs.labels.tracks);
}

function setTabLabelActive(tab)
{
    tab.classList.add("active");
    tab.classList.remove("inactive");
}

function setTabLabelCurrent(tab)
{
    let tabs = [window.tabs.labels.artists, window.tabs.labels.albums, window.tabs.labels.tracks];
    for (var singleTab of tabs)
    {
        singleTab.classList.remove("current");
    }
    tab.classList.add("current");
    setTabLabelActive(tab);
}

function setTabLabelInactive(tab)
{
    tab.classList.add("inactive");
    tab.classList.remove("active");
}

function showTab(tab)
{
    tab.classList.remove("hidden");
}

function hideTab(tab)
{
    tab.classList.add("hidden");
}
