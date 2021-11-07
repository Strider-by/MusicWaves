window.onload = function ()
{
    init();
};

function init()
{
    window.constants = {};
    window.constants.regCookieName = "registered";
    window.constants.logCookieName = "logged";
    window.constants.langCookieName = "language";


    window.loginForm = document.getElementById("login_form");
    window.registerForm = document.getElementById("register_form");
    window.loginModeButton = document.getElementById("login_mode_button");
    window.registerModeButton = document.getElementById("register_mode_button");
    window.regModeButtonArea = document.getElementById("reg_selector");
    window.logModeButtonArea = document.getElementById("log_selector");
    window.modeSelectionArea = document.getElementById("mode_selector");
    window.loginPasswordCheckbox = document.getElementById("login_password_checkbox");
    window.registerPasswordCheckbox = document.getElementById("register_password_checkbox");
    window.registerPasswordInput1 = document.getElementById("reg_password_input_1");
    window.registerPasswordInput2 = document.getElementById("reg_password_input_2");
    window.loginPasswordInput = document.getElementById("log_password_input");
    window.languageSelector = document.getElementById("preferred_language");
    window.languageSelectorForm = document.getElementById("preferred_language_form");

    logModeButtonArea.addEventListener("click", () => enableLoginMode());
    regModeButtonArea.addEventListener("click", () => enableRegisterMode());
    loginPasswordCheckbox.addEventListener("change", () => showPassword(loginPasswordCheckbox.checked));
    registerPasswordCheckbox.addEventListener("change", () => showPassword(registerPasswordCheckbox.checked));
    
    document.getElementById("login_form_submit_button").addEventListener("click", () => login());
    document.getElementById("register_form_submit_button").addEventListener("click", () => register());

    setSavedInCookieLanguage();
    languageSelector.addEventListener("change", () => setPreferredLanguage());


    enableEntranceMode();
}

function login()
{
    let login = document.getElementById("log_login_input").value;
    let password = document.getElementById("log_password_input").value;
    let form = document.createElement("form");
    form.className = "hidden";
    form.method = "POST";
    form.action = "../action"; 
    document.body.appendChild(form);
    
    /*form.appendChild(document.getElementById("log_login_input"));
    form.appendChild(document.getElementById("log_password_input"));*/
    
    let actionInput = document.createElement("input");
    actionInput.name = "command";
    actionInput.value = "login";
    form.appendChild(actionInput);
    
    let loginInput = document.createElement("input");
    loginInput.name = "login";
    loginInput.value = login;
    form.appendChild(loginInput);
    
    let passwordInput = document.createElement("input");
    passwordInput.type = "password";
    passwordInput.name = "password";
    passwordInput.value = password;
    form.appendChild(passwordInput);
    
    
    form.submit();
}

function register()
{
    let login = document.getElementById("reg_login_input").value;
    let password1 = document.getElementById("reg_password_input_1").value;
    let password2 = document.getElementById("reg_password_input_2").value;
    let inviteCode = document.getElementById("invite_code").value;

    let form = document.createElement("form");
    form.className = "hidden";
    form.method = "POST";
    form.action = "../action";
    document.body.appendChild(form);

    let actionInput = document.createElement("input");
    actionInput.name = "command";
    actionInput.value = "register";
    form.appendChild(actionInput);

    let loginInput = document.createElement("input");
    loginInput.name = "login";
    loginInput.value = login;
    form.appendChild(loginInput);

    let password1Input = document.createElement("input");
    password1Input.name = "password1";
    password1Input.type = "password";
    password1Input.value = password1;
    form.appendChild(password1Input);

    let password2Input = document.createElement("input");
    password2Input.name = "password2";
    password2Input.type = "password";
    password2Input.value = password2;
    form.appendChild(password2Input);

    let inviteCodeInput = document.createElement("input");
    inviteCodeInput.name = "invite_code";
    inviteCodeInput.value = inviteCode;
    form.appendChild(inviteCodeInput);

    form.submit();
}

function enableEntranceMode()
{
    let modeValue = getMode();
    console.log(modeValue);
    let loginMode = modeValue !== undefined && modeValue !== "register";
    if (loginMode)
    {
        enableLoginMode();
    } 
    else
    {
        enableRegisterMode();
    }
}

function enableLoginMode()
{
    loginForm.classList.remove("hidden");
    registerForm.classList.add("hidden");
    logModeButtonArea.classList.remove("disabled");
    regModeButtonArea.classList.add("disabled");
    modeSelectionArea.classList.add("flex_row_reversed");
    languageSelector.classList.add("login_mode_active");
    languageSelector.classList.remove("register_mode_active");
    setLoginMode();
}

function enableRegisterMode()
{
    loginForm.classList.add("hidden");
    registerForm.classList.remove("hidden");
    logModeButtonArea.classList.add("disabled");
    regModeButtonArea.classList.remove("disabled");
    modeSelectionArea.classList.remove("flex_row_reversed");
    languageSelector.classList.remove("login_mode_active");
    languageSelector.classList.add("register_mode_active");
    setRegisterMode();
}

function showPassword(show)
{
    registerPasswordCheckbox.checked = loginPasswordCheckbox.checked = show;
    loginPasswordInput.type = registerPasswordInput1.type = registerPasswordInput2.type =
            show ? "text" : "password";
}

// these methods are to trace which mode (register | login) we were in last use of it
// to set proper entrance mode

function setRegisterMode()
{
    setCookie("mode", "register");
}

function setLoginMode()
{
    setCookie("mode", "login");
}

function getMode()
{
    return getCookie("mode");
}

// Language processing

function setPreferredLanguage()
{
    let options = {};
    options.path = "/";
    setCookie(constants.langCookieName, languageSelector.value, options);
    languageSelectorForm.submit();
}

function getPreferredLanguage()
{
    let valueFromCookie = getCookie(constants.langCookieName);
    return typeof valueFromCookie === "undefined" ? "" : valueFromCookie;

}

function setSavedInCookieLanguage()
{
    let value = getPreferredLanguage();
    let options = languageSelector.options;

    for (let i = 0; i < options.length; i++)
    {
        if (value === options[i].value)
        {
            // option list does contain stored in cookie value
            languageSelector.value = value;
            break;
        }
    }
}