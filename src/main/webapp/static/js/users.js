window.onload = () =>
{
    init();
    getDataRows();
}

function init()
{
    window.blocks = {};
    window.maxRows = 10;
    window.maxPages; // will be used for pagination
    window.blocks.usersBlock = document.getElementById("users_block");

    window.filter = {};
    window.filter.buttons = {};
    window.filter.buttons.clearIdFilter = document.getElementById("clear_id_filter");
    window.filter.buttons.clearLoginFilter = document.getElementById("clear_login_filter");
    window.filter.buttons.clearRoleFilter = document.getElementById("clear_role_filter");
    window.filter.buttons.clearRegDateFilter = document.getElementById("clear_reg_date_filter");
    window.filter.buttons.clearAllFilters = document.getElementById("clear_filters");
    window.filter.buttons.applyFilters = document.getElementById("apply_filters");

    window.filter.valueHolders = {};
    window.filter.valueHolders.id = document.getElementById("id_filter");
    window.filter.valueHolders.login = document.getElementById("login_filter");
    window.filter.valueHolders.role = document.getElementById("role_filter");
    window.filter.valueHolders.regDate = document.getElementById("reg_date_filter");

    window.filter.valueHolders.id.addEventListener("input", () => applyFilters());
    window.filter.valueHolders.login.addEventListener("input", () => applyFilters());
    window.filter.valueHolders.role.addEventListener("input", () => applyFilters());
    window.filter.valueHolders.regDate.addEventListener("input", () => applyFilters());

    window.filter.searchRules = {};
    window.filter.searchRules.login = document.getElementById("login_filter_rule");
    window.filter.searchRules.regDate = document.getElementById("reg_date_filter_rule");
    window.filter.searchRules.login.addEventListener("input", () => applyFilters());
    window.filter.searchRules.regDate.addEventListener("input", () => applyFilters());

    window.filter.sort = {};
    window.filter.sort.by = document.getElementById("sort_by");
    window.filter.sort.order = document.getElementById("order_of_sorting");
    window.filter.sort.by.addEventListener("input", () => applyFilters());
    window.filter.sort.order.addEventListener("input", () => applyFilters());


    window.filter.valueHolders.regDate.addEventListener("change", () => defineRegDateInputColour());

    window.filter.buttons.clearIdFilter.addEventListener("click", () => clearUserIdFilter());
    window.filter.buttons.clearLoginFilter.addEventListener("click", () => clearUserLoginFilter());
    window.filter.buttons.clearRoleFilter.addEventListener("click", () => clearUserRoleFilter());
    window.filter.buttons.clearRegDateFilter.addEventListener("click", () => clearUserRegDateFilter());
    window.filter.buttons.clearAllFilters.addEventListener("click", () => clearAllFilters());
    window.filter.buttons.applyFilters.addEventListener("click", () => applyFilters());


    window.navigation = {};
    window.navigation.pageNumber = document.getElementById("current_page");
    window.navigation.pagesMax = document.getElementById("pages_total");
    window.navigation.usersFound = document.getElementById("total_elements");
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

function appendUserDataRow(user)
{
    window.blocks.usersBlock.appendChild( createUserDataRow(user) );
}

function clearUserDataRows()
{
    var rows;

    do
    {
        rows = window.blocks.usersBlock.getElementsByClassName("single_user_block");
        for(row of rows)
        {
            window.blocks.usersBlock.removeChild(row);
        }
    }
    while(rows.length > 0)
}

function createUserDataRow(data)
{
    let delButtonText = document.getElementById("delete_button_value").value;
    let changeRoleButtonText = document.getElementById("change_user_role_button_value").value;
    let cancelButtonText = document.getElementById("cancel_button_value").value;
    let applyButtonText = document.getElementById("apply_button_value").value;

    let outerDiv = createDiv("single_user_block");
    outerDiv.appendChild( createDiv("user_id", data.id) );
    outerDiv.appendChild( createDiv("user_login", data.login) );
    outerDiv.appendChild( createDiv("user_role", roleIdToRoleName(data.role)) );
    outerDiv.appendChild( createDiv("registered", data.registered) );

    let controls = createDiv("user_controls");

    let deleteUserButton = createButton("delete_user equal_width_b", delButtonText);
    controls.appendChild(deleteUserButton);
    let changeRoleButton = createButton("promote_user equal_width_b", changeRoleButtonText);
    controls.appendChild(changeRoleButton);

    deleteUserButton.addEventListener("click", () => enterDeleteUserMode(outerDiv, data.id));
    changeRoleButton.addEventListener("click", () => enterChangeUserRoleMode(outerDiv, data.id));

    outerDiv.appendChild(controls);

    return outerDiv;
}

function enterDeleteUserMode(parentContainer, userId)
{
    parentContainer.appendChild( createDeleteUserPanel(userId) );
    hideClassNames("delete_user");
    hideClassNames("promote_user");
}

function enterChangeUserRoleMode(parentContainer, userId)
{
    parentContainer.appendChild( createChangeUserRolePanel(userId) );
    hideClassNames("delete_user");
    hideClassNames("promote_user");
}

function createDeleteUserPanel(userId)
{
    let outerDiv = createDiv("delete_user_mode_controls");
    let modeDescriptionText = document.getElementById("delete_button_value").value;
    let modeDescriptionSpan = createElement("span", "mode_description", modeDescriptionText);
    outerDiv.appendChild(modeDescriptionSpan);
    let deleteButton = createButton("apply_delete_user equal_width_b", "V");
    deleteButton.addEventListener("click", () => deleteUser(userId));
    outerDiv.appendChild(deleteButton);
    let cancelButton = createButton("cancel_delete_user equal_width_b", "X");
    cancelButton.addEventListener("click", () => exitDeleteUserMode(outerDiv));
    outerDiv.appendChild(cancelButton);
    return outerDiv;
}

function exitDeleteUserMode(panelToRemove)
{
    panelToRemove.parentNode.removeChild(panelToRemove);
    showClassNames("delete_user");
    showClassNames("promote_user");
}

function createChangeUserRolePanel(userId)
{
    let outerDiv = createDiv("change_user_role_mode_controls");
    let modeDescriptionText = document.getElementById("change_user_role_button_value").value;
    let modeDescriptionSpan = createElement("span", "mode_description", modeDescriptionText);
    outerDiv.appendChild(modeDescriptionSpan);
    let selector = document.getElementById("role_selector_example").cloneNode(true);
    outerDiv.appendChild(selector);
    let changeButton = createButton("apply_change_user_role", "V");
    changeButton.addEventListener("click", () => updateUserRole(userId, selector.value));
    outerDiv.appendChild(changeButton);
    let cancelButton = createButton("cancel_change_user_role", "X");
    cancelButton.addEventListener("click", () => exitChangeUserRoleMode(outerDiv));
    outerDiv.appendChild(cancelButton);
    return outerDiv;
}

function exitChangeUserRoleMode(panelToRemove)
{
    panelToRemove.parentNode.removeChild(panelToRemove);
    showClassNames("delete_user");
    showClassNames("promote_user");
}

function evenRowButtonsWidth()
{
    setEqualWidthForClasses("", "equal_width_b");
}

function evenIdColumnWidth()
{
    setEqualWidthForClasses("", "user_id");
}

function evenRoleColumnWidth()
{
    setEqualWidthForClasses("", "user_role");
}

function evenLoginColumnWidth()
{
    setEqualWidthForClasses("", "user_login");
}

async function deleteUser(userId)
{
    let params = new Map();
    params.set("user_id", userId);

    let json = await sendAndFetchJson("delete_user_by_admin", params);
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

async function updateUserRole(userId, roleId)
{
    let params = new Map();
    params.set("user_id", userId);
    params.set("role_id", roleId);

    console.log(params);

    let json = await sendAndFetchJson("change_user_role", params);
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

function clearUserIdFilter()
{
    window.filter.valueHolders.id.value = "";
    applyFilters();
}

function clearUserLoginFilter()
{
    window.filter.valueHolders.login.value = "";
    applyFilters();
}

function clearUserRoleFilter()
{
    window.filter.valueHolders.role.value = "";
    applyFilters();
}

function clearUserRegDateFilter()
{
    window.filter.valueHolders.regDate.value = "";
    defineRegDateInputColour();
    applyFilters();
}

function clearAllFilters()
{
    window.filter.valueHolders.id.value = "";
    window.filter.valueHolders.login.value = "";
    window.filter.valueHolders.role.value = "";
    window.filter.valueHolders.regDate.value = "";
    defineRegDateInputColour();
    applyFilters();
}

function defineRegDateInputColour()
{
    let elem = window.filter.valueHolders.regDate;
    let className = "blue_colour";

    if(elem.value === "")
    {
        elem.classList.remove(className);
    }
    else
    {
        elem.classList.add(className);
    }
}

async function getDataRows()
{
    let params = new Map();

    // actual values
    params.set("records_per_page", window.maxRows);
    params.set("page_number", window.navigation.pageNumber.value);
    params.set("id", window.filter.valueHolders.id.value);
    params.set("login", window.filter.valueHolders.login.value);
    params.set("login_search_type_id", window.filter.searchRules.login.value);
    params.set("role_id", window.filter.valueHolders.role.value);

    // search rules
    params.set("register_date", window.filter.valueHolders.regDate.value);
    params.set("register_date_compare_type_id", window.filter.searchRules.regDate.value)

    // sort
    params.set("sort_order_id", window.filter.sort.order.value);
    params.set("sort_by", window.filter.sort.by.value);

    let json = await sendAndFetchJson("find_users", params);
    let users = json.data.users;
    let foundUsersTotalQuantity = json.data.overall_quantity;
    let pages = Math.ceil(foundUsersTotalQuantity / window.maxRows);
    setPagesQuantity(pages);
    setFoundUsersQuantity(foundUsersTotalQuantity);
    clearUserDataRows();
    for (user of users)
    {
        appendUserDataRow(user);
    }

    evenRowButtonsWidth();
}

function roleIdToRoleName(id)
{
    let roleSelector = document.getElementById("role_selector_example");
    let map = new Map();
    let length = roleSelector.length;
    for(var i = 0; i < length; i++)
    {
        map.set(roleSelector[i].value + "", roleSelector[i].innerText);
    }

    return map.has(id + "") ? map.get(id + "") : id;
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

function setFoundUsersQuantity(value)
{
    window.navigation.usersFound.value = value;
}