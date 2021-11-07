package by.musicwaves.controller.command.action;

import by.musicwaves.controller.command.CommandException;
import by.musicwaves.controller.resources.ApplicationPage;
import by.musicwaves.controller.resources.TransitType;
import by.musicwaves.service.ServiceResponse;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public abstract class ActionCommand
{
    ApplicationPage targetPage;
    TransitType transitType;
    
    ActionCommand(ApplicationPage targetPage, TransitType transitType)
    {
        this.targetPage = targetPage;
        this.transitType = transitType;
    }

    public ActionCommand() {

    }

    public ApplicationPage getTargetPage()
    {
        return targetPage;
    }

    public TransitType getTransitType()
    {
        return transitType;
    }

    /**
     * By default this command processor execute method just redirect or forward
     * to target page without doing anything else.
     * More specific behaviour should be defined by overriding this method.
     */
    public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException {
        try {
            transfer(request, response);
        } catch(ServletException | IOException ex) {
            throw new CommandException("Failed to execute command", ex);
        }
    }

    public void transfer(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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

    public static void attachServiceResponse(HttpServletRequest request, ServiceResponse serviceResponse) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.setAttribute("serviceResponse", serviceResponse);
        }
    }


    static class Validator {

        public static void isValidInteger(String value) throws CommandException {
            try {
                Integer.parseInt(value);
            } catch(NullPointerException | NumberFormatException ex) {
                throw new CommandException("Provided value is either empty or not a valid integer value", ex);
            }
        }

        public static void isAllowedRequestMethod(String method, List<String> allowedMethods) throws CommandException {
            if (!allowedMethods.contains(method)) {
                throw new CommandException("This request method is not allowed");
            }
        }

        public static <T> T nonNull(T object) throws CommandException {
            if (object == null) {
                throw new CommandException("Provided value can not be null");
            }

            return object;
        }
    }


}
