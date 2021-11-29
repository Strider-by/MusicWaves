window.onload = () =>
{
    init();
    getDataRows();
}

function init()
{
    window.maxRows = 10;

    window.blocks = {};
    window.blocks.artistsBlock = document.getElementById("artists_block");

    window.filter = {};
    window.filter.buttons = {};
    window.filter.buttons.clearIdFilter = document.getElementById("clear_id_filter");
    window.filter.buttons.clearNameFilter = document.getElementById("clear_name_filter");
    window.filter.buttons.clearVisibilityStateFilter = document.getElementById("clear_visibility_state_filter");
    window.filter.buttons.clearAllFilters = document.getElementById("clear_filters");

    window.filter.buttons.clearIdFilter.addEventListener("click", () => clearArtistIdFilter());
    window.filter.buttons.clearNameFilter.addEventListener("click", () => clearArtistNameFilter());
    window.filter.buttons.clearVisibilityStateFilter.addEventListener("click", () => clearArtistVisibilityStateFilter());
    window.filter.buttons.clearAllFilters.addEventListener("click", () => clearAllFilters());

    window.filter.valueHolders = {};
    window.filter.valueHolders.id = document.getElementById("id_filter");
    window.filter.valueHolders.name = document.getElementById("name_filter");
    window.filter.valueHolders.visible = document.getElementById("visibility_state_filter");

    window.filter.valueHolders.id.addEventListener("input", () => applyFilters());
    window.filter.valueHolders.name.addEventListener("input", () => applyFilters());
    window.filter.valueHolders.visible.addEventListener("input", () => applyFilters());

    window.filter.searchRules = {};
    window.filter.searchRules.name = document.getElementById("name_filter_rule");
    window.filter.searchRules.name.addEventListener("input", () => applyFilters());

    window.filter.sort = {};
    window.filter.sort.by = document.getElementById("sort_by");
    window.filter.sort.order = document.getElementById("order_of_sorting");
    window.filter.sort.by.addEventListener("input", () => applyFilters());
    window.filter.sort.order.addEventListener("input", () => applyFilters());

    window.navigation = {};
    window.navigation.pageNumber = document.getElementById("current_page");
    window.navigation.pagesMax = document.getElementById("pages_total");
    window.navigation.artistFound = document.getElementById("total_elements");
    window.navigation.buttons = {};
    window.navigation.buttons.reload = document.getElementById("reload_page_button");
    window.navigation.buttons.prevPage = document.getElementById("prev_page_button");
    window.navigation.buttons.nextPage = document.getElementById("next_page_button");
    window.navigation.buttons.firstPage = document.getElementById("first_page_button");
    window.navigation.buttons.lastPage = document.getElementById("last_page_button");

    window.navigation.buttons.reload.addEventListener("click", () => reloadPage());
    window.navigation.buttons.prevPage.addEventListener("click", () => goToPreviousPage());
    window.navigation.buttons.nextPage.addEventListener("click", () => goToNextPage());
    window.navigation.buttons.firstPage.addEventListener("click", () => goToFirstPage());
    window.navigation.buttons.lastPage.addEventListener("click", () => goToLastPage());

}

async function getDataRows()
{
    let params = new Map();

    // actual values
    params.set("records_per_page", window.maxRows);
    params.set("page_number", window.navigation.pageNumber.value);
    params.set("id", window.filter.valueHolders.id.value);
    params.set("name", window.filter.valueHolders.name.value);
    params.set("name_search_type_id", window.filter.searchRules.name.value);
    params.set("visible", window.filter.valueHolders.visible.value);

    // sort
    params.set("sort_order_id", window.filter.sort.order.value);
    params.set("sort_by", window.filter.sort.by.value);

    let json = await sendAndFetchJson("find_artists", params);
    let artists = json.data.artists;
    let foundArtistsTotalQuantity = json.data.overall_quantity;
    let pages = Math.ceil(foundArtistsTotalQuantity / window.maxRows);
    setPagesQuantity(pages);
    setFoundArtistsQuantity(foundArtistsTotalQuantity);
    clearArtistDataRows();
    for (artist of artists)
    {
        appendArtistDataRow(artist);
    }

    evenRowButtonsWidth();
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
    let delButtonText = document.getElementById("delete_button_value").value;
    let changeRoleButtonText = document.getElementById("rename_artist_button_value").value;
    let cancelButtonText = document.getElementById("cancel_button_value").value;
    let applyButtonText = document.getElementById("apply_button_value").value;

    let outerDiv = createDiv("single_artist_block");
    outerDiv.appendChild( createDiv("artist_id", data.id) );
    outerDiv.appendChild( createDiv("image", data.image) );
    outerDiv.appendChild( createDiv("artist_name", data.name) );
    outerDiv.appendChild( createDiv("artist_is_visible", data.visible) );

    let controls = createDiv("artist_controls");

    let deleteArtistButton = createButton("delete_artist equal_width_b", delButtonText);
    controls.appendChild(deleteArtistButton);
    let renameArtistButton = createButton("rename_artist equal_width_b", changeRoleButtonText);
    controls.appendChild(renameArtistButton);

    renameArtistButton.addEventListener("click", () => enterDeleteArtistMode(outerDiv, data.id));
    renameArtistButton.addEventListener("click", () => enterRenameArtistMode(outerDiv, data.id));

    outerDiv.appendChild(controls);

    return outerDiv;
}

function enterDeleteArtistMode(parentContainer, artistId)
{

}

function enterRenameArtistMode(parentContainer, artistId)
{

}

function createDeleteArtistPanel(artistId)
{
    let outerDiv = createDiv("delete_artist_mode_controls");
    let modeDescriptionText = document.getElementById("delete_button_value").value;
    let modeDescriptionSpan = createElement("span", "mode_description", modeDescriptionText);
    outerDiv.appendChild(modeDescriptionSpan);
    let deleteButton = createButton("apply_delete_artist equal_width_b", "V");
    deleteButton.addEventListener("click", () => deleteArtist(artistId));
    outerDiv.appendChild(deleteButton);
    let cancelButton = createButton("cancel_delete_artist equal_width_b", "X");
    cancelButton.addEventListener("click", () => exitDeleteArtistMode(outerDiv));
    outerDiv.appendChild(cancelButton);
    return outerDiv;
}

function exitDeleteArtistMode(panelToRemove)
{
    panelToRemove.parentNode.removeChild(panelToRemove);
    showClassNames("delete_artist");
    showClassNames("rename_artist");
}

function exitRenameArtistMode(panelToRemove)
{
    panelToRemove.parentNode.removeChild(panelToRemove);
    showClassNames("delete_artist");
    showClassNames("rename_artist");
}

function evenRowButtonsWidth()
{
    setEqualWidthForClasses("", "equal_width_b");
}

function evenIdColumnWidth()
{
    setEqualWidthForClasses("", "artist_id");
}

function evenNameColumnWidth()
{
    setEqualWidthForClasses("", "artist_name");
}

function evenVisibilityColumnWidth()
{
    setEqualWidthForClasses("", "artist_is_visible");
}

async function deleteArtist(artistId)
{
    let params = new Map();
    params.set("artist_id", artistId);

    let json = await sendAndFetchJson("delete_artist", params);
    let success = json.success;

    if(success)
    {
        reloadPage();
    }
    else
    {
        alert("something went wrong");
    }
}

async function renameArtist(artistId, artistName)
{
    let params = new Map();
    params.set("artist_id", artistId);
    params.set("artist_name", artistName);

    console.log(params);

    let json = await sendAndFetchJson("rename_artist", params);
    let success = json.success;

    if(success)
    {
        reloadPage();
    }
    else
    {
        alert("something went wrong");
    }
}

function clearArtistIdFilter()
{
    window.filter.valueHolders.id.value = "";
    applyFilters();
}

function clearArtistNameFilter()
{
    window.filter.valueHolders.name.value = "";
    applyFilters();
}

function clearArtistVisibilityStateFilter()
{
    window.filter.valueHolders.visible.value = "";
    applyFilters();
}


function clearAllFilters()
{
    window.filter.valueHolders.id.value = "";
    window.filter.valueHolders.name.value = "";
    window.filter.valueHolders.visible.value = "";
    applyFilters();
}

function applyFilters()
{
    window.navigation.pageNumber.value = 1;
    getDataRows();
}

function reloadPage()
{
    getDataRows();
}

function goToNextPage()
{
    window.navigation.pageNumber.value =
        window.navigation.pageNumber.value < window.navigation.pageNumber.max - 0
            ? window.navigation.pageNumber.value - 0 + 1
            : window.navigation.pageNumber.value;
    getDataRows();
}

function goToPreviousPage()
{
    window.navigation.pageNumber.value = window.navigation.pageNumber.value == 1 ? 1 : window.navigation.pageNumber.value - 1;
    getDataRows();
}

function goToFirstPage()
{
    window.navigation.pageNumber.value = 1;
    getDataRows();
}

function goToLastPage()
{
    window.navigation.pageNumber.value = window.navigation.pageNumber.max;
    getDataRows();
}

function setPagesQuantity(value)
{
    window.navigation.pagesMax.value = value;
    window.navigation.pageNumber.max = value != 0 ? value : 1;
}

function setFoundArtistsQuantity(value)
{
    window.navigation.artistsFound.value = value;
}