function setEqualWidthForButtons(className)
{
    let buttons = document.getElementsByClassName(className);
    var maxWidth = 0;

    for(var button of buttons)
    {
        button.style.width = "auto";
    }

    for(var button of buttons)
    {
        maxWidth = maxWidth < button.offsetWidth ? button.offsetWidth : maxWidth;
    }

    for(var button of buttons)
    {
        button.style.width = maxWidth + 1 + "px";
    }
}

function setEqualWidthForClasses(changeToWidthFirst, ... classNames)
{
    let maxWidth = 0;

    for(var className of classNames)
    {
        let currentClassElements = document.getElementsByClassName(className);
        for(var element of currentClassElements)
        {
            if(changeToWidthFirst != null) element.style.width = changeToWidthFirst;
            maxWidth = maxWidth < element.offsetWidth ? element.offsetWidth : maxWidth;
        }
    }

    for(var className of classNames)
    {
        let currentClassElements = document.getElementsByClassName(className);
        for(var element of currentClassElements)
        {
            // + 1 here is kind of necessary since without it buttons became too shots and do not fit inner text
            element.style.width = maxWidth + 1 + "px";
        }
    }
}

function getObjectProperties(object)
{
    let keys = Object.keys(object);
    let properties = [];

    for (var i = 0; i < keys.length; i++) {
        var val = object[keys[i]];
        properties[i] = val;
    }

    return properties;
}

function clearInputFields(... inputIds)
{
    for(id of inputIds)
    {
        var input = document.getElementById(id);
        input.value = "";
    }
}

function show(... ids)
{
    for(id of ids)
    {
        var element = document.getElementById(id);
        element.classList.remove("hidden");
    }
}

function hide(... ids)
{
    for(id of ids)
    {
        var element = document.getElementById(id);
        element.classList.add("hidden");
    }
}

function showClassNames(... classNames)
{
    for(className of classNames)
    {
        var elements = document.getElementsByClassName(className);
        for(element of elements)
        {
            element.classList.remove("hidden");
        }
    }
}

function hideClassNames(... classNames)
{
    for(className of classNames)
    {
        var elements = document.getElementsByClassName(className);
        for(element of elements)
        {
            element.classList.add("hidden");
        }
    }
}

function createActionCommandForm(command)
{
    let form = document.createElement("form");
    form.className = "hidden";
    form.method = "POST";
    form.action = "../action";
    document.body.appendChild(form);

    let actionInput = createTextInput("command", command);
    form.appendChild(actionInput);

    return form;
}

async function sendAndFetchJson(command, paramsMap)
{
    let formData = new FormData();
    formData.append("command", command);

    for(var pair of paramsMap)
    {
        var key = pair[0];
        var value = pair[1];
        formData.append(key, value);
    }

    let response;
    let respJson;
    try
    {
        response = await fetch("/xhr", {method: "POST", body: formData});
        respJson = await response.json();
    }
    catch(e)
    {
        showRequestFailedMessage();
        return;
    }

    if(respJson.success !== true)
    {
        let errorMessages = respJson.error_messages;
        let globalMessage = "";
        for(message of errorMessages)
        {
            globalMessage += message + "\n";
        }

        showError(globalMessage);
        // we don't give actual response json since there is no point in it
        return;
    }

    if(respJson.messages.length !== 0)
    {
        let messages = respJson.error_messages;
        let globalMessage = "";
        for(message of messages)
        {
            globalMessage += message + "\n";
        }

        showMessage(globalMessage);
    }

    return respJson;
}

function createTextInput(name, value)
{
    let input = document.createElement("input");
    input.name = name;
    input.value = value;

    return input;
}

function createDiv(className, value)
{
    let div = document.createElement("div");
    if(className) div.className = className;
    if(value) div.innerText = value;
    return div;
}

function createButton(className, value, type)
{
    let button = document.createElement("button");
    if(className) button.className = className;
    button.innerText = value;
    if(type) button.type = type;
    return button;
}

function createElement(type, className, innerText)
{
    let element = document.createElement(type);
    if(className) element.className = className;
    if(innerText) element.innerText = innerText;
    return element;
}

function getValueById(id)
{
    return document.getElementById(id).value;
}

function showSystemMessages()
{
    let showRequired = document.getElementById("msg_show_required").innerHTML.trim() === "true";
    console.log("message required to be shown: " + showRequired);

    if(showRequired)
    {
        openMessageBox();
    }
}

function logout()
{
    let form = createActionCommandForm("logout");
    form.submit();
}


function showError(message)
{
    alert(message);
}

function showMessage(message)
{
    alert(message);
}

function showRequestFailedMessage()
{
    let message = document.getElementById("request_failed_message_holder").innerText;
    showError(message);
}

function buildPathToArtistImage(fileName)
{
    return "/artist-images/" + fileName;
}

function buildPathToAlbumImage(fileName)
{
    return "/album-images/" + fileName;
}

function buildPathToAuditTrack(fileName)
{
    return "/music/" + fileName;
}