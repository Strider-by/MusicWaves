package by.musicwaves.controller.command.action;

import by.musicwaves.controller.command.CommandException;
import by.musicwaves.controller.command.Validator;
import by.musicwaves.controller.resources.ApplicationPage;
import by.musicwaves.controller.resources.TransitType;
import by.musicwaves.entity.User;
import by.musicwaves.service.ServiceException;
import by.musicwaves.service.ServiceResponse;
import by.musicwaves.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


public class DeleteAccountByUserCommand extends ActionCommand {

    private final static UserService service = UserService.getInstance();
    private static final Logger LOGGER = LogManager.getLogger(DeleteAccountByUserCommand.class);
    private final static String PARAM_NAME_PASSWORD = "password";
    private final static String SESSION_ATTRIBUTE_NAME_USER = "user";


    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws CommandException {
        this.transitType = TransitType.REDIRECT;

        char[] password = Validator.assertNonNull(request.getParameter(PARAM_NAME_PASSWORD)).toCharArray();
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(SESSION_ATTRIBUTE_NAME_USER);

        // user is not logged in
        if (user == null) {
            throw new CommandException("This command requires user to be logged in");
        }

        try {
            ServiceResponse<Boolean> serviceResponse = service.deleteUserAccount(user, password);

            // if everything went well, we can now forget about user via invalidating session
            if (serviceResponse.isSuccess()) {
                session.invalidate();
                this.targetPage = ApplicationPage.ENTRANCE;
            }
            // if something went wrong (most likely - the password was incorrect)
            else {
                this.targetPage = ApplicationPage.PROFILE;
            }

            // set messages and error codes to be shown for user or to be processed by front-end
            attachServiceResponse(request, serviceResponse);
            // go to proper page
            transfer(request, response);
            
        } catch(ServletException | IOException | ServiceException ex) {
            throw new CommandException("Failed to execute command", ex);
        }
    }
    
}
