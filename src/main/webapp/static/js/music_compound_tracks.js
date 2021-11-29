function initTracksPart()
{
    if(!window.newInstance) window.newInstance = {};
    if(!window.editInstance) window.editInstance = {};
    if(!window.blocks) window.blocks = {};
    window.blocks.tracks = {};
    window.blocks.tracks.editInstance = document.getElementById("edit_track_block");

    // ** NEW INSTANCE ** //
    window.newInstance.tracks = {};
    // value holders
    window.newInstance.tracks.valueHolders = {};
    window.newInstance.tracks.valueHolders.name = document.getElementById("new_track_name");
    window.newInstance.tracks.valueHolders.getVisibility = () => document.querySelector('input[name="new_track_visibility"]:checked').value;
    // buttons
    window.newInstance.tracks.buttons = {};
    window.newInstance.tracks.buttons.apply = document.getElementById("applyTrackCreation");
    window.newInstance.tracks.buttons.clear = document.getElementById("clearNewTrackName");
    // actions
    window.newInstance.tracks.buttons.apply.onclick = () => createTrack();
    window.newInstance.tracks.buttons.clear.onclick = () => clearNewTrackInputs();


    // ** EDIT INSTANCE ** //
    window.editInstance.tracks = {};
    // value holders
    window.editInstance.tracks.valueHolders = {}
    window.editInstance.tracks.valueHolders.id = document.getElementById("track_in_use_id");
    window.editInstance.tracks.valueHolders.name = document.getElementById("track_in_use_name");
    window.editInstance.tracks.valueHolders.number = document.getElementById("current_track_number");
    window.editInstance.tracks.valueHolders.file = document.getElementById("current_track_file");
    window.editInstance.tracks.valueHolders.fileUploader = document.getElementById("track_file_input");
    window.editInstance.tracks.valueHolders.getVisibility = () => document.querySelector('input[name="current_track_visibility"]:checked').value;
    // buttons
    window.editInstance.tracks.buttons = {}
    window.editInstance.tracks.buttons.upload = document.getElementById("uploadTrackFile");
    window.editInstance.tracks.buttons.close = document.getElementById("closeCurrentTrack");
    window.editInstance.tracks.buttons.update = document.getElementById("updateCurrentTrack");
    window.editInstance.tracks.buttons.delete = document.getElementById("deleteCurrentTrack");
    window.editInstance.tracks.buttons.shiftUp = document.getElementById("shift_track_up_button");
    window.editInstance.tracks.buttons.shiftDown = document.getElementById("shift_track_down_button");
    // actions
    window.editInstance.tracks.buttons.upload.onclick = () => requestTrackFile();
    window.editInstance.tracks.valueHolders.fileUploader.onchange = () => uploadTrackFile();
    window.editInstance.tracks.buttons.close.onclick = () => hideEditTrackBlock();
    window.editInstance.tracks.buttons.update.onclick = () => updateTrack();
    window.editInstance.tracks.buttons.delete.onclick = () => deleteTrack(); // todo: change to long press event?
    window.editInstance.tracks.buttons.shiftUp.onclick = () => shiftTrackUp();
    window.editInstance.tracks.buttons.shiftDown.onclick = () => shiftTrackDown();


    // ** DATA ROWS ** //
    if(!window.blocks) window.blocks = {};
    window.blocks.tracksBlock = document.getElementById("found_tracks_data_rows");
    hideEditTrackBlock();

}

async function getTracksDataRows()
{
    let params = new Map();

    // actual values
    params.set("album", window.editInstance.albums.valueHolders.id.value);

    let json = await sendAndFetchJson("find_album_tracks", params);
    if(json)
    {
        let tracks = json.data.tracks;

        clearTrackDataRows();
        for (track of tracks)
        {
            appendTrackDataRow(track);
        }
    }
}

function appendTrackDataRow(track)
{
    window.blocks.tracksBlock.appendChild( createTrackDataRow(track) );
}

function clearTrackDataRows()
{
    var rows;

    do
    {
        rows = window.blocks.tracksBlock.getElementsByClassName("single_track_block");
        for(row of rows)
        {
            window.blocks.tracksBlock.removeChild(row);
        }
    }
    while(rows.length > 0)
}

function createTrackDataRow(data)
{
    let outerDiv = createDiv("single_track_block");
    outerDiv.appendChild( createDiv("track_id hidden", data.id) );
    outerDiv.appendChild( createDiv("track_number", data.number) );

    outerDiv.appendChild( createDiv("track_name", data.name) );
    outerDiv.appendChild( createDiv("track_file_is_present", data.file !== null && data.file !== "" ? "♪" : "🔇") );
    outerDiv.appendChild( createDiv("track_is_visible", data.visible ? "👁" : "✕") );

    let controls = createDiv("track_controls");

    //let selectTrackButton = createButton("select_track", "&#128269;");
    let selectTrackButton = createButton("select_track", "≡");
    controls.appendChild(selectTrackButton);

    selectTrackButton.addEventListener("click", () => selectTrack(data.id, data.number, data.name, data.visible, data.file));

    outerDiv.appendChild(controls);

    return outerDiv;
}

function selectTrack(id, number, name, visible, file)
{
    window.editInstance.tracks.valueHolders.id.value = id;
    window.editInstance.tracks.valueHolders.number.value = number;
    window.editInstance.tracks.valueHolders.name.value = name;
    window.editInstance.tracks.valueHolders.file.value = file !== null && file !== "" ? file : "---"
    // setting visibility
    let editInstanceVisibilityOptions = document.querySelectorAll('input[name="current_track_visibility"]');
    // 1 is true, 2 is false // todo do smth with that?
    let visibilityId = visible ? 1 : 2;
    for(var option of editInstanceVisibilityOptions)
    {
        if(option.value - 0 === visibilityId)
        {
            option.checked = true;
        }
    }
    showEditTrackBlock();
}

async function createTrack()
{
    let params = new Map();

    // actual values
    params.set("album_id", window.editInstance.albums.valueHolders.id.value);
    params.set("name", window.newInstance.tracks.valueHolders.name.value);
    params.set("visible", window.newInstance.tracks.valueHolders.getVisibility());
    let json = await sendAndFetchJson("create_track", params);
    if(json)
    {
        let track = json.data.track;
        await getTracksDataRows();
        selectLastlyCreatedTrack(track.id);
    }
}

function showEditTrackBlock()
{
    show(window.blocks.tracks.editInstance.id);
}

function hideEditTrackBlock()
{
    hide(window.blocks.tracks.editInstance.id);
}

function clearNewTrackInputs()
{
    window.newInstance.tracks.valueHolders.name.value = "";
}

function useSelectedTrack()
{
    switchToTracksTab();
    getTracksDataRows();
}

function requestTrackFile()
{
    window.editInstance.tracks.valueHolders.fileUploader.click();
}

async function updateTrack()
{
    let params = new Map();
    params.set("id", window.editInstance.tracks.valueHolders.id.value);
    params.set("name", window.editInstance.tracks.valueHolders.name.value);
    params.set("visible", window.editInstance.tracks.valueHolders.getVisibility());
    let json = await sendAndFetchJson("update_track_name_and_visibility", params);
    if(json)
    {
        getTracksDataRows();
    }
}

async function shiftTrackUp()
{
    let params = new Map();
    params.set("id", window.editInstance.tracks.valueHolders.id.value);
    let json = await sendAndFetchJson("shift_track_number_up", params);
    if(json)
    {
        getTracksDataRows();
        selectLastlyCreatedTrack(window.editInstance.tracks.valueHolders.id.value - 0);
    }
}

async function shiftTrackDown()
{
    let params = new Map();
    params.set("id", window.editInstance.tracks.valueHolders.id.value);
    let json = await sendAndFetchJson("shift_track_number_down", params);
    if(json)
    {
        getTracksDataRows();
    }
}

async function deleteTrack()
{
    let params = new Map();
    params.set("id", window.editInstance.tracks.valueHolders.id.value);
    let json = await sendAndFetchJson("delete_track", params);
    if(json)
    {
        hideEditTrackBlock();
        getTracksDataRows();
    }
}


function selectLastlyCreatedTrack(trackId)
{
     let idCells = document.getElementsByClassName("track_id");
     let buttons = document.getElementsByClassName("select_track");

     for(var i = 0; i < idCells.length; i++)
     {
        var id = idCells[i].innerText - 0;
        if(id === trackId)
        {
            buttons[i].click();
            return;
        }
     }
}

async function uploadTrackFile()
{
    let input = window.editInstance.tracks.valueHolders.fileUploader;
    let file = input.files[0];
    if(!file)
    {
        return;
    }

    let params = new Map();
    params.set("id", window.editInstance.tracks.valueHolders.id.value);
    params.set("file", file);
    let json = await sendAndFetchJson("upload_track", params);
    window.editInstance.tracks.valueHolders.fileUploader.value = "";
    if(json)
    {
        await getTracksDataRows();
        selectLastlyCreatedTrack(window.editInstance.tracks.valueHolders.id.value - 0);
    }
}