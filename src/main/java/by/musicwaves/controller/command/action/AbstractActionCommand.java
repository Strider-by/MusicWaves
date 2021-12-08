package by.musicwaves.controller.command.action;

import by.musicwaves.controller.command.exception.CommandException;
import by.musicwaves.controller.resource.ApplicationPage;
import by.musicwaves.controller.resource.TransitType;
import by.musicwaves.dto.ServiceResponse;
import by.musicwaves.entity.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public abstract class AbstractActionCommand implements ActionCommand {

    private final static String SESSION_ATTRIBUTE_NAME_USER = "user";

    public AbstractActionCommand() {
    }

    public static void attachServiceResponse(HttpServletRequest request, ServiceResponse serviceResponse) {
        HttpSession session = request.getSession(true);
        session.setAttribute("serviceResponse", serviceResponse);
    }

    protected User getUser(HttpServletRequest request) {
        return (User) request.getSession().getAttribute(SESSION_ATTRIBUTE_NAME_USER);
    }

    @Override
    public abstract void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException;

    public void transfer(HttpServletRequest request, HttpServletResponse response,
                         ApplicationPage targetPage, TransitType transitType) throws ServletException, IOException {
        switch (transitType) {
            case FORWARD:
                RequestDispatcher dispatcher = request.getServletContext().getRequestDispatcher(targetPage.getPathToPage());
                dispatcher.forward(request, response);
                break;

            case REDIRECT:
                response.sendRedirect(targetPage.getAlias());
                break;
        }
    }

}
