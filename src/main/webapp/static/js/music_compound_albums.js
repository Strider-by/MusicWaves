function initAlbumsPart()
{
    if(!window.maxRows) window.maxRows = {};
    window.maxRows.albums = 10;
    if(!window.pagesTotal) window.pagesTotal = {};
    window.pagesTotal.albums = 1;
    if(!window.filter) window.filter = {};
    if(!window.navigation) window.navigation = {};
    if(!window.newInstance) window.newInstance = {};
    if(!window.editInstance) window.editInstance = {};
    if(!window.blocks) window.blocks = {};
    window.blocks.albums = {};
    window.blocks.albums.editInstance = document.getElementById("edit_album_block");

    //  ** SEARCH FILTERS ** //
    window.filter.albums = {};
    // value holders
    window.filter.albums.valueHolders = {};
    window.filter.albums.valueHolders.name =  document.getElementById("album_name_filter");
    window.filter.albums.valueHolders.year = document.getElementById("album_year_filter");
    window.filter.albums.valueHolders.getVisibility = () => document.querySelector('input[name="albums_search_visible"]:checked').value;
    // buttons
    window.filter.albums.buttons = {};
    window.filter.albums.buttons.search = {};
    window.filter.albums.buttons.search.clearName = document.getElementById("clear_album_name_filter");
    // actions
    window.filter.albums.buttons.search.clearName.onclick = () => clearAlbumFilters();


    // ** NAVIGATION ** //
    window.navigation.albums = {};
    // value holders
    window.navigation.albums.valueHolders = {};
    window.navigation.albums.valueHolders.pagesAndRecords = document.getElementById("albums_search_results_count");
    window.navigation.albums.valueHolders.currentPage = document.getElementById("albums_current_page");
    // buttons
    window.navigation.albums.buttons = {};
    window.navigation.albums.buttons.firstPage = document.getElementById("1st_album_page");
    window.navigation.albums.buttons.lastPage = document.getElementById("last_album_page");
    window.navigation.albums.buttons.prevPage = document.getElementById("prev_album_page");
    window.navigation.albums.buttons.nextPage = document.getElementById("next_album_page");
    window.navigation.albums.buttons.reload = document.getElementById("reload_album_page");
    // actions
    window.navigation.albums.buttons.firstPage.onclick = () => goFirstAlbumsPage();
    window.navigation.albums.buttons.lastPage.onclick = () => goLastAlbumsPage();
    window.navigation.albums.buttons.prevPage.onclick = () => goPrevAlbumsPage();
    window.navigation.albums.buttons.nextPage.onclick = () => goNextAlbumsPage();
    window.navigation.albums.buttons.reload.onclick = () => getAlbumsDataRows();


    // ** NEW INSTANCE ** //
    window.newInstance.albums = {};
    // value holders
    window.newInstance.albums.valueHolders = {};
    window.newInstance.albums.valueHolders.name = document.getElementById("new_album_name");
    window.newInstance.albums.valueHolders.year = document.getElementById("new_album_year");
    window.newInstance.albums.valueHolders.getVisibility = () => document.querySelector('input[name="new_album_visibility"]:checked').value;
    // buttons
    window.newInstance.albums.buttons = {};
    window.newInstance.albums.buttons.apply = document.getElementById("applyAlbumCreation");
    window.newInstance.albums.buttons.clear = document.getElementById("clearNewAlbumInputs");
    // actions
    window.newInstance.albums.buttons.apply.onclick = () => createAlbum();
    window.newInstance.albums.buttons.clear.onclick = () => clearNewAlbumInputs();


    // ** EDIT INSTANCE ** //
    window.editInstance.albums = {};
    // value holders
    window.editInstance.albums.valueHolders = {}
    window.editInstance.albums.valueHolders.id = document.getElementById("album_in_use_id");
    window.editInstance.albums.valueHolders.name = document.getElementById("album_in_use_name");
    window.editInstance.albums.valueHolders.year = document.getElementById("album_in_use_year");
    window.editInstance.albums.valueHolders.getVisibility = () => document.querySelector('input[name="current_album_visibility"]:checked').value;
    window.editInstance.albums.valueHolders.fileUploader = document.getElementById("album_file_input");
    window.editInstance.albums.valueHolders.image = document.getElementById("albumImage");
    // buttons
    window.editInstance.albums.buttons = {}
    window.editInstance.albums.buttons.use = document.getElementById("useCurrentAlbum");
    window.editInstance.albums.buttons.upload = document.getElementById("uploadAlbumImage");
    window.editInstance.albums.buttons.close = document.getElementById("closeCurrentAlbum");
    window.editInstance.albums.buttons.update = document.getElementById("updateCurrentAlbum");
    window.editInstance.albums.buttons.delete = document.getElementById("deleteCurrentAlbum");
    // actions
    window.editInstance.albums.buttons.use.onclick = () => useSelectedAlbum();
    window.editInstance.albums.buttons.upload.onclick = () => requestAlbumFile();
    window.editInstance.albums.valueHolders.fileUploader.onchange = () => uploadAlbumFile();
    window.editInstance.albums.buttons.close.onclick = () => hideEditAlbumBlock();
    window.editInstance.albums.buttons.update.onclick = () => updateAlbum();
    window.editInstance.albums.buttons.delete.onclick = () => deleteAlbum(); // todo: change to long press event?


    // ** DATA ROWS ** //
    if(!window.blocks) window.blocks = {};
    window.blocks.albumsBlock = document.getElementById("found_albums_data_rows");
    hideEditAlbumBlock();

}

async function getAlbumsDataRows()
{
    let params = new Map();

    // actual values
    params.set("artist", window.editInstance.artists.valueHolders.id.value);
    params.set("records_per_page", window.maxRows.albums);
    params.set("page_number", window.navigation.albums.valueHolders.currentPage.value);
    params.set("name", window.filter.albums.valueHolders.name.value);
    params.set("year", window.filter.albums.valueHolders.year.value);
    params.set("visible", window.filter.albums.valueHolders.getVisibility());

    let json = await sendAndFetchJson("find_albums", params);
    if(json)
    {
        let albums = json.data.albums;
        let foundAlbumsTotalQuantity = json.data.overall_quantity;
        let pages = Math.ceil(foundAlbumsTotalQuantity / window.maxRows.albums);
        if(pages === 0) pages = 1;
        window.pagesTotal.albums = pages;
        setAlbumsSearchResultsQuantity(pages, foundAlbumsTotalQuantity);

        window.navigation.albums.valueHolders.currentPage.max = pages;

        clearAlbumDataRows();
        for (album of albums)
        {
            appendAlbumDataRow(album);
        }
    }
}

function appendAlbumDataRow(album)
{
    window.blocks.albumsBlock.appendChild( createAlbumDataRow(album) );
}

function clearAlbumDataRows()
{
    var rows;

    do
    {
        rows = window.blocks.albumsBlock.getElementsByClassName("single_album_block");
        for(row of rows)
        {
            window.blocks.albumsBlock.removeChild(row);
        }
    }
    while(rows.length > 0)
}

function createAlbumDataRow(data)
{
    let outerDiv = createDiv("single_album_block");
    outerDiv.appendChild( createDiv("album_id hidden", data.id) );
    let imageContainer = createDiv("small_image_container");
    let image = document.createElement("img");
    image.src = data.image !== null ?  buildPathToAlbumImage(data.image) : "";
    imageContainer.appendChild(image);
    outerDiv.appendChild(imageContainer);
    outerDiv.appendChild( createDiv("album_name", data.name) );
    outerDiv.appendChild( createDiv("album_year", data.year !== null ? data.year : "") );
    outerDiv.appendChild( createDiv("album_is_visible", data.visible ? "👁" : "✕") );

    let controls = createDiv("album_controls");

    //let selectAlbumButton = createButton("select_album", "&#128269;");
    let selectAlbumButton = createButton("select_album", unescape("↬"));
    controls.appendChild(selectAlbumButton);

    selectAlbumButton.addEventListener("click", () => selectAlbum(data.id, data.name, data.year, data.visible, data.image));

    outerDiv.appendChild(controls);

    return outerDiv;
}

function setAlbumsSearchResultsQuantity(pages, foundAlbumsTotalQuantity)
{
    window.navigation.albums.valueHolders.pagesAndRecords.value = pages + "  |  " + foundAlbumsTotalQuantity;
}

function selectAlbum(id, name, year, visible, image)
{
    window.editInstance.albums.valueHolders.id.value = id;
    window.editInstance.albums.valueHolders.name.value = name;
    window.editInstance.albums.valueHolders.year.value = year;
    window.editInstance.albums.valueHolders.image.src = (image !== null && image !== "") ? buildPathToAlbumImage(image) : "";
    // setting visibility
    let editInstanceVisibilityOptions = document.querySelectorAll('input[name="current_album_visibility"]');
    // 1 is true, 2 is false // todo do smth with that?
    let visibilityId = visible ? 1 : 2;
    for(var option of editInstanceVisibilityOptions)
    {
        if(option.value - 0 === visibilityId)
        {
            option.checked = true;
        }
    }
    showEditAlbumBlock();
}

async function createAlbum()
{
    let params = new Map();

    // actual values
    params.set("artist", window.editInstance.artists.valueHolders.id.value);
    params.set("name", window.newInstance.albums.valueHolders.name.value);
    params.set("year", window.newInstance.albums.valueHolders.year.value);
    params.set("visible", window.newInstance.albums.valueHolders.getVisibility());
    let json = await sendAndFetchJson("create_album", params);
    if(json)
    {
        let album = json.data.album;
        selectAlbum(album.id, album.name, album.year, album.visible, album.image);
        getAlbumsDataRows();
    }
}

function showEditAlbumBlock()
{
    show(window.blocks.albums.editInstance.id);
}

function hideEditAlbumBlock()
{
    hide(window.blocks.albums.editInstance.id);
}

function goNextAlbumsPage()
{
    let currentPage = window.navigation.albums.valueHolders.currentPage.value;
    let maxPage = window.pagesTotal.albums;
    let targetPage = currentPage + 1 <= maxPage ? currentPage + 1 : maxPage;
    window.navigation.albums.valueHolders.currentPage.value = targetPage;
    getAlbumsDataRows();
}

function goPrevAlbumsPage()
{
    let currentPage = window.navigation.albums.valueHolders.currentPage.value;
    let minPage = 1;
    let targetPage = currentPage - 1 > 0 ? currentPage - 1 : minPage;
    window.navigation.albums.valueHolders.currentPage.value = targetPage;
    getAlbumsDataRows();
}

function goFirstAlbumsPage()
{
    window.navigation.albums.valueHolders.currentPage.value = 1;
    getAlbumsDataRows();
}

function goLastAlbumsPage()
{
    window.navigation.albums.valueHolders.currentPage.value = window.pagesTotal.albums;
    getAlbumsDataRows();
}

function clearAlbumFilters()
{
    window.filter.albums.valueHolders.name.value = "";
    window.filter.albums.valueHolders.year.value = "";
    window.navigation.albums.valueHolders.currentPage.value = 1;
    document.querySelector('input[name="albums_search_visible"][value="0"]').checked = true;
    getAlbumsDataRows();
}

function clearNewAlbumInputs()
{
    window.newInstance.albums.valueHolders.name.value = "";
    window.newInstance.albums.valueHolders.year.value = "";
}

function useSelectedAlbum() // todo: make sure we clear filter on upper levels
{
    switchToTracksTab();
    getTracksDataRows();
}

function uploadAlbumFile() // fixme
{
    alert("upload album");
}

async function updateAlbum()
{
    let params = new Map();
    params.set("id", window.editInstance.albums.valueHolders.id.value);
    params.set("name", window.editInstance.albums.valueHolders.name.value);
    params.set("year", window.editInstance.albums.valueHolders.year.value);
    params.set("visible", window.editInstance.albums.valueHolders.getVisibility());
    let json = await sendAndFetchJson("update_album", params);
    if(json)
    {
        getAlbumsDataRows();
    }
}

async function deleteAlbum()
{
    let params = new Map();
    params.set("id", window.editInstance.albums.valueHolders.id.value);
    let json = await sendAndFetchJson("delete_album", params);
    if(json)
    {
        hideEditAlbumBlock();
        getAlbumsDataRows();
    }
}

function requestAlbumFile()
{
    window.editInstance.albums.valueHolders.fileUploader.click();
}

async function uploadAlbumFile()
{
    let input = window.editInstance.albums.valueHolders.fileUploader;
    let file = input.files[0];
    if(!file)
    {
        return;
    }

    let params = new Map();
    params.set("id", window.editInstance.albums.valueHolders.id.value);
    params.set("file", file);
    let json = await sendAndFetchJson("upload_album_image", params);
    window.editInstance.albums.valueHolders.fileUploader.value = "";
    if(json)
    {
        await getAlbumsDataRows();
        window.editInstance.albums.valueHolders.image.src = buildPathToAlbumImage(json.file);
    }
}