package by.musicwaves.controller.command.factory;

import by.musicwaves.controller.command.action.*;

import java.util.Arrays;

public enum ActionCommandEnum {

    REGISTER("register", new RegisterUserCommand()),
    LOGIN("login", new LoginCommand()),
    LOGOUT("logout", new LogoutCommand()),
    GO_TO_DEFAULT_PAGE("go_to_default_page", new GoToDefaultPageCommand()),
    CHANGE_LANGUAGE("change_language", new ChangeLanguageCommand()),
    CHANGE_PASSWORD("change_password", new ChangePasswordCommand()),
    CHANGE_LOGIN("change_login", new ChangeLoginCommand()),
    DELETE_ACCOUNT_BY_USER("delete_account", new DeleteAccountByUserCommand());

    private final String alias;
    private final AbstractActionCommand command;

    ActionCommandEnum(String alias, AbstractActionCommand command) {
        this.alias = alias;
        this.command = command;
    }

    public static AbstractActionCommand getCommandByAlias(String alias) {
        return Arrays.stream(values())
                .filter(command -> command.alias.equalsIgnoreCase(alias))
                .findAny()
                .map(ActionCommandEnum::getCommand)
                .orElse(GO_TO_DEFAULT_PAGE.command);
    }

    public String getAlias() {
        return alias;
    }

    public AbstractActionCommand getCommand() {
        return command;
    }
}
