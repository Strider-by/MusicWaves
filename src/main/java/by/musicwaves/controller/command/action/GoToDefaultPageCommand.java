package by.musicwaves.controller.command.action;

import by.musicwaves.controller.resources.ApplicationPage;
import by.musicwaves.controller.resources.TransitType;

public class GoToDefaultPageCommand extends ActionCommand
{
    public GoToDefaultPageCommand() {
        super(ApplicationPage.INDEX, TransitType.REDIRECT);
    }
    
}
