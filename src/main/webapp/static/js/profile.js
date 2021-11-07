window.onload = () => init();

function init()
{
    setEqualWidthForButtons("button_2");
    document.getElementById("close_message_box_button").addEventListener("click", () => closeMessageBox());

    window.mainMenu = {};
    window.mainMenu.logout = document.getElementById("logout_button");
    window.mainMenu.logout.addEventListener("click", () => logout());

    window.modeButtons = {}
    window.modeButtons.deleteAccountButton = document.getElementById("delete_account_mode_button");
    window.modeButtons.changeLoginButton = document.getElementById("change_login_mode_button");
    window.modeButtons.changePasswordButton = document.getElementById("change_password_mode_button");
    window.modeButtons.changeLanguageButton = document.getElementById("change_language_mode_button");

    window.modeControlButtons = {};
    window.modeControlButtons.languageChange = {}
    window.modeControlButtons.languageChange.apply = document.getElementById("apply_language_change_button");
    window.modeControlButtons.languageChange.cancel = document.getElementById("cancel_language_change_button");
    window.modeControlButtons.loginChange = {}
    window.modeControlButtons.loginChange.check = document.getElementById("check_new_login_button");
    window.modeControlButtons.loginChange.apply = document.getElementById("apply_login_change_button");
    window.modeControlButtons.loginChange.cancel = document.getElementById("cancel_login_change_button");
    window.modeControlButtons.passwordChange = {}
    window.modeControlButtons.passwordChange.apply = document.getElementById("apply_password_change_button");
    window.modeControlButtons.passwordChange.cancel = document.getElementById("cancel_password_change_button");
    window.modeControlButtons.deleteAccount = {}
    window.modeControlButtons.deleteAccount.apply = document.getElementById("apply_account_deletion_button");
    window.modeControlButtons.deleteAccount.cancel = document.getElementById("cancel_account_deletion_button");

    window.modeButtons.changeLanguageButton.addEventListener("click", () => enableLanguageChangeMode(true));
    window.modeButtons.changeLoginButton.addEventListener("click", () => enableLoginChangeMode(true));
    window.modeButtons.changePasswordButton.addEventListener("click", () => enablePasswordChangeMode(true));
    window.modeButtons.deleteAccountButton.addEventListener("click", () => enableDeleteAccountMode(true));

    window.modeControlButtons.languageChange.cancel.addEventListener("click", () => enableLanguageChangeMode(false));
    window.modeControlButtons.loginChange.cancel.addEventListener("click", () => enableLoginChangeMode(false));
    window.modeControlButtons.passwordChange.cancel.addEventListener("click", () => enablePasswordChangeMode(false));
    window.modeControlButtons.deleteAccount.cancel.addEventListener("click", () => enableDeleteAccountMode(false));

    window.modeControlButtons.languageChange.apply.addEventListener("click", () => changeLanguage());
    window.modeControlButtons.passwordChange.apply.addEventListener("click", () => changePassword());
    window.modeControlButtons.loginChange.apply.addEventListener("click", () => changeLogin());

    // show system messages if they are present
    showSystemMessages();
}

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

function openMessageBox()
{
    document.getElementById("message_box_container").classList.remove("hidden");
    document.getElementById("main_window").classList.add("hidden");
}

function closeMessageBox()
{
    document.getElementById("message_box_container").classList.add("hidden");
    document.getElementById("main_window").classList.remove("hidden");
}

function setMessageTitle(title)
{
    if (title === undefined || title === "")
    {
        document.getElementById("msg_title_container").classList.add("hidden");
        return;
    }

    document.getElementById("msg_title_container").classList.remove("hidden");
    document.getElementById("msg_title_text").innerHtml = title;
}

function setMessageBody(...lines)
{
    document.getElementById("msg_body_container").innerHTML = "";
    for(let line of lines)
    {
        document.getElementById("msg_body_container").innerHTML += line + "<br/>";
    }
}

function showModeButtons(visible)
{
    let modeButtons = getObjectProperties(window.modeButtons);
    for(var button of modeButtons)
    {
        if(visible)
        {
            button.classList.remove("hidden");
        }
        else
        {
            button.classList.add("hidden");
        }
    }
}

function showPropertyFields(visible)
{
    let func = visible ? show : hide;
    func(
    "register_date_property_block",
    "role_property_block",
    "login_property_block",
    "password_property_block",
    "language_property_block");
}

function enableLanguageChangeMode(enable)
{
    showModeButtons(!enable);
    controlsClassList = document.getElementById("language_mode_controls").classList;
    languageDisplayFieldClassList = document.getElementById("language").classList;
    languageSelectorClassList = document.getElementById("language_selector").classList;


    if(enable)
    {
        controlsClassList.remove("hidden");
        languageDisplayFieldClassList.add("hidden");
        languageSelectorClassList.remove("hidden");
    }
    else
    {
        controlsClassList.add("hidden");
        languageDisplayFieldClassList.remove("hidden");
        languageSelectorClassList.add("hidden");
    }
}

/**function enableLoginChangeMode(enable)
{
    showModeButtons(!enable);
    controlsClassList = document.getElementById("language_mode_controls").classList;
    languageDisplayFieldClassList = document.getElementById("language").classList;
    languageSelectorClassList = document.getElementById("language_selector").classList;
    clearInputFields("password_for_login_change", "new_login");
    showPropertyFields(enable);

    if(enable)
    {
        controlsClassList.remove("hidden");
        languageDisplayFieldClassList.add("hidden");
        languageSelectorClassList.remove("hidden");
    }
    else
    {
        controlsClassList.add("hidden");
        languageDisplayFieldClassList.remove("hidden");
        languageSelectorClassList.add("hidden");
    }
}*/

function enableLoginChangeMode(enable)
{
    showModeButtons(!enable);
    clearInputFields("password_for_login_change", "new_login");
    let blockId = "login_change_block";
    let tipsId = "login_change_info";

    showPropertyFields(!enable);

    func = enable ? show : hide;
    func(tipsId);
    func(blockId);
}

function enablePasswordChangeMode(enable)
{
    showModeButtons(!enable);
    clearInputFields("old_password_for_password_change", "new_password_1", "new_password_2");
    let blockId = "password_change_block";
    let tipsId = "password_change_info";
    showPropertyFields(!enable);

    if(enable)
    {
        show(tipsId)
        show(blockId);
    }
    else
    {
        hide(tipsId)
        hide(blockId);
    }
}

function enableDeleteAccountMode(enable)
{
    clearInputFields("account_delete_password");
    showModeButtons(!enable);
    let tipsId = "account_delete_info";
    let blockId = "delete_account_block";
    showPropertyFields(!enable);

    if(enable)
    {
        show(tipsId)
        show(blockId);
    }
    else
    {
        hide(tipsId)
        hide(blockId);
    }
}

function changeLanguage()
{
    let newLanguageId = document.getElementById("language_selector").value;
    let form = createActionCommandForm("change_language");

    let languageIdInput = createTextInput("language_id", newLanguageId);
    form.appendChild(languageIdInput);

    form.submit();
}

function changePassword()
{
    let oldPassword = document.getElementById("old_password_for_password_change").value;
    let newPassword1 = document.getElementById("new_password_1").value;
    let newPassword2 = document.getElementById("new_password_2").value;
    let form = createActionCommandForm("change_password");

    let oldPasswordInput = createTextInput("old_password", oldPassword);
    form.appendChild(oldPasswordInput);
    let newPassword1Input = createTextInput("new_password_1", newPassword1);
    form.appendChild(newPassword1Input);
    let newPassword2Input = createTextInput("new_password_2", newPassword2);
    form.appendChild(newPassword2Input);

    form.submit();
}

function changeLogin()
{
    let password = document.getElementById("password_for_login_change").value;
    let login = document.getElementById("new_login").value;
    let form = createActionCommandForm("change_login");

    form.appendChild( createTextInput("password", password) );
    form.appendChild( createTextInput("login", login) );

    form.submit();
}