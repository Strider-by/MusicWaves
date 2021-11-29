function initArtistsPart()
{
    if(!window.maxRows) window.maxRows = {};
    window.maxRows.artists = 10;
    if(!window.pagesTotal) window.pagesTotal = {};
    window.pagesTotal.artists = 1;
    if(!window.filter) window.filter = {};
    if(!window.navigation) window.navigation = {};
    if(!window.newInstance) window.newInstance = {};
    if(!window.editInstance) window.editInstance = {};
    if(!window.blocks) window.blocks = {};
    window.blocks.artists = {};
    window.blocks.artists.editInstance = document.getElementById("edit_artist_block");

    //  ** SEARCH FILTERS ** //
    window.filter.artists = {};
    // value holders
    window.filter.artists.valueHolders = {};
    window.filter.artists.valueHolders.name =  document.getElementById("artist_name_filter");
    window.filter.artists.valueHolders.nameSearchType = document.getElementById("artist_name_search_type");
    window.filter.artists.valueHolders.getVisibility = () => document.querySelector('input[name="artists_search_visible"]:checked').value;
    // buttons
    window.filter.artists.buttons = {};
    window.filter.artists.buttons.search = {};
    window.filter.artists.buttons.search.clearName = document.getElementById("clear_artist_name_filter");
    // actions
    window.filter.artists.buttons.search.clearName.onclick = () => clearArtistFilters();


    // ** NAVIGATION ** //
    window.navigation.artists = {};
    // value holders
    window.navigation.artists.valueHolders = {};
    window.navigation.artists.valueHolders.pagesAndRecords = document.getElementById("artists_search_results_count");
    window.navigation.artists.valueHolders.currentPage = document.getElementById("artists_current_page");
    // buttons
    window.navigation.artists.buttons = {};
    window.navigation.artists.buttons.firstPage = document.getElementById("1st_artist_page");
    window.navigation.artists.buttons.lastPage = document.getElementById("last_artist_page");
    window.navigation.artists.buttons.prevPage = document.getElementById("prev_artist_page");
    window.navigation.artists.buttons.nextPage = document.getElementById("next_artist_page");
    window.navigation.artists.buttons.reload = document.getElementById("reload_artist_page");
    // actions
    window.navigation.artists.buttons.firstPage.onclick = () => goFirstArtistsPage();
    window.navigation.artists.buttons.lastPage.onclick = () => goLastArtistsPage();
    window.navigation.artists.buttons.prevPage.onclick = () => goPrevArtistsPage();
    window.navigation.artists.buttons.nextPage.onclick = () => goNextArtistsPage();
    window.navigation.artists.buttons.reload.onclick = () => getArtistsDataRows();


    // ** NEW INSTANCE ** //
    window.newInstance.artists = {};
    // value holders
    window.newInstance.artists.valueHolders = {};
    window.newInstance.artists.valueHolders.name = document.getElementById("new_artist_name");
    window.newInstance.artists.valueHolders.getVisibility = () => document.querySelector('input[name="new_artist_visibility"]:checked').value;
    // buttons
    window.newInstance.artists.buttons = {};
    window.newInstance.artists.buttons.apply = document.getElementById("applyArtistCreation");
    window.newInstance.artists.buttons.clearName = document.getElementById("clearNewArtistName");
    // actions
    window.newInstance.artists.buttons.apply.onclick = () => createArtist();
    window.newInstance.artists.buttons.clearName.onclick = () => clearNewArtistName();


    // ** EDIT INSTANCE ** //
    window.editInstance.artists = {};
    // value holders
    window.editInstance.artists.valueHolders = {}
    window.editInstance.artists.valueHolders.id = document.getElementById("artist_in_use_id");
    window.editInstance.artists.valueHolders.name = document.getElementById("artist_in_use_name");
    window.editInstance.artists.valueHolders.getVisibility = () => document.querySelector('input[name="current_artist_visibility"]:checked').value;
    window.editInstance.artists.valueHolders.fileUploader = document.getElementById("artist_file_input");
    window.editInstance.artists.valueHolders.image = document.getElementById("artistImage");
    // buttons
    window.editInstance.artists.buttons = {}
    window.editInstance.artists.buttons.use = document.getElementById("useCurrentArtist");
    window.editInstance.artists.buttons.upload = document.getElementById("uploadArtistImage");
    window.editInstance.artists.buttons.close = document.getElementById("closeCurrentArtist");
    window.editInstance.artists.buttons.update = document.getElementById("updateCurrentArtist");
    window.editInstance.artists.buttons.delete = document.getElementById("deleteCurrentArtist");
    // actions
    window.editInstance.artists.buttons.use.onclick = () => useSelectedArtist();
    window.editInstance.artists.buttons.upload.onclick = () => requestArtistFile();
    window.editInstance.artists.valueHolders.fileUploader.onchange = () => uploadArtistFile();
    window.editInstance.artists.buttons.close.onclick = () => hideEditArtistBlock();
    window.editInstance.artists.buttons.update.onclick = () => updateArtist();
    window.editInstance.artists.buttons.delete.onclick = () => deleteArtist(); // todo: change to long press event?


    // ** DATA ROWS ** //
    if(!window.blocks) window.blocks = {};
    window.blocks.artistsBlock = document.getElementById("found_artists_data_rows");

    getArtistsDataRows();
    hideEditArtistBlock();

}

async function getArtistsDataRows()
{
    let params = new Map();

    // actual values
    params.set("records_per_page", window.maxRows.artists);
    params.set("page_number", window.navigation.artists.valueHolders.currentPage.value);
    params.set("name", window.filter.artists.valueHolders.name.value);
    params.set("name_search_type_id", window.filter.artists.valueHolders.nameSearchType.value);
    params.set("visible", window.filter.artists.valueHolders.getVisibility());

    let json = await sendAndFetchJson("find_artists", params);
    if(json)
    {
        let artists = json.data.artists;
        let foundArtistsTotalQuantity = json.data.overall_quantity;
        let pages = Math.ceil(foundArtistsTotalQuantity / window.maxRows.artists);
        if(pages === 0) pages = 1;
        window.pagesTotal.artists = pages;
        setArtistsSearchResultsQuantity(pages, foundArtistsTotalQuantity);

        window.navigation.artists.valueHolders.currentPage.max = pages;

        clearArtistDataRows();
        for (artist of artists)
        {
            appendArtistDataRow(artist);
        }
    }
}

function appendArtistDataRow(artist)
{
    window.blocks.artistsBlock.appendChild( createArtistDataRow(artist) );
}

function clearArtistDataRows()
{
    var rows;

    do
    {
        rows = window.blocks.artistsBlock.getElementsByClassName("single_artist_block");
        for(row of rows)
        {
            window.blocks.artistsBlock.removeChild(row);
        }
    }
    while(rows.length > 0)
}

function createArtistDataRow(data)
{
    let outerDiv = createDiv("single_artist_block");
    outerDiv.appendChild( createDiv("artist_id hidden", data.id) );
    let imageContainer = createDiv("small_image_container");
    let image = document.createElement("img");
    image.src = buildPathToArtistImage(data.image);
    imageContainer.appendChild(image);
    outerDiv.appendChild(imageContainer);
    outerDiv.appendChild( createDiv("artist_name", data.name) );
    outerDiv.appendChild( createDiv("artist_is_visible", data.visible ? "👁" : "✕") );

    let controls = createDiv("artist_controls");

    //let selectArtistButton = createButton("select_artist", "&#128269;");
    let selectArtistButton = createButton("select_artist", unescape("↬"));
    controls.appendChild(selectArtistButton);

    selectArtistButton.addEventListener("click", () => selectArtist(data.id, data.name, data.visible, data.image));

    outerDiv.appendChild(controls);

    return outerDiv;
}

function setArtistsSearchResultsQuantity(pages, foundArtistsTotalQuantity)
{
    window.navigation.artists.valueHolders.pagesAndRecords.value = pages + "  |  " + foundArtistsTotalQuantity;
}

function selectArtist(id, name, visible, image)
{
    window.editInstance.artists.valueHolders.id.value = id;
    window.editInstance.artists.valueHolders.name.value = name;
    window.editInstance.artists.valueHolders.image.src = (image !== null && image !== "") ? buildPathToArtistImage(image) : ""; // todo fix to set proper image path?
    // setting visibility
    let editInstanceVisibilityOptions = document.querySelectorAll('input[name="current_artist_visibility"]');
    // 1 is true, 2 is false // todo do smth with that?
    let visibilityId = visible ? 1 : 2;
    for(var option of editInstanceVisibilityOptions)
    {
        if(option.value - 0 === visibilityId)
        {
            option.checked = true;
        }
    }
    showEditArtistBlock();
}

async function createArtist()
{
    let params = new Map();

    // actual values
    params.set("name", window.newInstance.artists.valueHolders.name.value);
    params.set("visible", window.newInstance.artists.valueHolders.getVisibility());
    let json = await sendAndFetchJson("create_artist", params);
    if(json)
    {
        let artist = json.data.artist;
        selectArtist(artist.id, artist.name, artist.visible, artist.image);
        getArtistsDataRows();
    }
}

function showEditArtistBlock()
{
    show(window.blocks.artists.editInstance.id);
}

function hideEditArtistBlock()
{
    hide(window.blocks.artists.editInstance.id);
}

function goNextArtistsPage()
{
    let currentPage = window.navigation.artists.valueHolders.currentPage.value;
    let maxPage = window.pagesTotal.artists;
    let targetPage = currentPage + 1 <= maxPage ? currentPage + 1 : maxPage;
    window.navigation.artists.valueHolders.currentPage.value = targetPage;
    getArtistsDataRows();
}

function goPrevArtistsPage()
{
    let currentPage = window.navigation.artists.valueHolders.currentPage.value;
    let minPage = 1;
    let targetPage = currentPage - 1 > 0 ? currentPage - 1 : minPage;
    window.navigation.artists.valueHolders.currentPage.value = targetPage;
    getArtistsDataRows();
}

function goFirstArtistsPage()
{
    window.navigation.artists.valueHolders.currentPage.value = 1;
    getArtistsDataRows();
}

function goLastArtistsPage()
{
    window.navigation.artists.valueHolders.currentPage.value = window.pagesTotal.artists;
    getArtistsDataRows();
}

function clearArtistFilters()
{
    window.filter.artists.valueHolders.name.value = "";
    window.navigation.artists.valueHolders.currentPage.value = 1;
    document.querySelector('input[name="artists_search_visible"][value="0"]').checked = true;
    getArtistsDataRows();
}

function clearNewArtistName()
{
    window.newInstance.artists.valueHolders.name.value = "";
}

function useSelectedArtist() // todo: make sure we clear filter on upper levels
{
    switchToAlbumsTab();
    getAlbumsDataRows();
}

function uploadArtistFile() // fixme
{
    alert("upload artist");
}

async function updateArtist()
{
    let params = new Map();
    params.set("id", window.editInstance.artists.valueHolders.id.value);
    params.set("name", window.editInstance.artists.valueHolders.name.value);
    params.set("visible", window.editInstance.artists.valueHolders.getVisibility());
    let json = await sendAndFetchJson("update_artists_name_and_visibility", params);
    if(json)
    {
        getArtistsDataRows();
    }
}

async function deleteArtist()
{
    let params = new Map();
    params.set("id", window.editInstance.artists.valueHolders.id.value);
    let json = await sendAndFetchJson("delete_artist", params);
    if(json)
    {
        hideEditArtistBlock();
        getArtistsDataRows();
    }
}

function requestArtistFile()
{
    window.editInstance.artists.valueHolders.fileUploader.click();
}

async function uploadArtistFile()
{
    let input = window.editInstance.artists.valueHolders.fileUploader;
    let file = input.files[0];
    if(!file)
    {
        return;
    }

    let params = new Map();
    params.set("id", window.editInstance.artists.valueHolders.id.value);
    params.set("file", file);
    let json = await sendAndFetchJson("upload_artist_image", params);
    window.editInstance.artists.valueHolders.fileUploader.value = "";
    if(json)
    {
        await getArtistsDataRows();
        window.editInstance.artists.valueHolders.image.src = buildPathToArtistImage(json.file);
    }
}