package by.musicwaves.controller.command.action;

import by.musicwaves.controller.command.Command;

/**
 * Represent type of commands that user requests with standard request, when page is being changed and actual response is
 * either redirection to a page, or a page itself.
 * This interface provides no additional methods and exists only to separate "direct" {@link ActionCommand} commands
 * from asynchronously sent {@link by.musicwaves.controller.command.xhr.XHRCommand} commands.
 */
public interface ActionCommand extends Command {

}
