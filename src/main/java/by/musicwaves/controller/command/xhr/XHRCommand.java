package by.musicwaves.controller.command.xhr;

import by.musicwaves.controller.command.Command;

/**
 * Represent type of commands that user requests asynchronously, without changing page, and gets json as a response.
 * This interface provides no additional methods and exists only to separate asynchronously sent
 * {@link XHRCommand} commands from "direct" {@link by.musicwaves.controller.command.action.ActionCommand} commands.
 */
public interface XHRCommand extends Command {

}
