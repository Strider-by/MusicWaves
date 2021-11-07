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

function createTextInput(name, value)
{
    let input = document.createElement("input");
    input.name = name;
    input.value = value;

    return input;
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