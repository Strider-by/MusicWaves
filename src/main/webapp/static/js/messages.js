window.MessageType = Object.freeze({message: {}, warning: {}, error: {}});

function showMessage(text, messageType, headingMessage)
{
// setting window decoration depending on message type
    let dialogueWindow = document.getElementById("dialog_window");
    dialogueWindow.className = "";
    switch (messageType)
    {
        case window.MessageType.message:
            dialogueWindow.className = "message";
            break;
        case window.MessageType.warning:
            dialogueWindow.className = "warning";
            break;
        case window.MessageType.error:
            dialogueWindow.className = "error";
            break;
        default:
            dialogueWindow.className = "unknown";
            break;
    }

    if (headingMessage !== undefined && headingMessage !== null)
    {
        dialogueWindow.getElementsByTagName("h3")[0].innerHTML = headingMessage;
    }

    dialogueWindow.getElementsByTagName("p")[0].innerHTML = text;
    dialogueWindow.showModal();
}

function appendMessagePart()
{
    let msg = "<dialog id=\"dialog_window\" class=\"message\">";
    msg += "<div id=\"dialog_window_header\">";
    msg += "<span id=\"close_dialog_window\" class=\"noselect\">&#10005;</span>";
    msg += "</div>";
    msg += "<div id=\"dialog_window_content_container\">";
    msg += "<h3></h3>";
    msg += "<p></p>";
    msg += "</div>";
    msg += "</dialog>";

    //document.getElementsByTagName("body")[0].innerHTML += msg;
    let parser = new DOMParser();
    let doc = parser.parseFromString(msg, 'text/html');
    let dialogPart = doc.getElementsByTagName("dialog")[0];
    document.getElementsByTagName("body")[0].appendChild(dialogPart);

    document.getElementById("close_dialog_window").addEventListener("click",
            function ()
            {
                document.getElementById("dialog_window").close();
            });
}

function getAndShowServiceMessages()
{
    let systemWarnings = document.getElementById("system_warnings").getElementsByTagName("li");
    let systemMessages = document.getElementById("system_messages").getElementsByTagName("li");

    let showRequired = systemMessages.length > 0 || systemWarnings.length > 0;
    if (!showRequired)
    {
        return;
    }

    let messageLevel;
    if (systemWarnings.length > 0)
    {
        messageLevel = window.MessageType.warning;
    }
    else
    {
        messageLevel = window.MessageType.message;
    }

    let sb = ""
    for (var msg of systemWarnings)
    {
        sb += "! &nbsp;&nbsp;" + msg.innerText + "<br>";
    }

    if (systemWarnings.length > 0 && systemMessages.length > 0)
    {
        sb += "<br>";
    }
    for (var msg of systemMessages)
    {
        sb += "&bull; &nbsp;&nbsp;" + msg.innerText + "<br>";
    }

    showMessage(sb, messageLevel);
}


