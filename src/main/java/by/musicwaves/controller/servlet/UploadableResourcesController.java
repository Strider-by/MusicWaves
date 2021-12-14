package by.musicwaves.controller.servlet;

import by.musicwaves.entity.User;
import by.musicwaves.service.util.UploadableResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;


public class UploadableResourcesController extends HttpServlet {

    private final static Logger LOGGER = LogManager.getLogger(UploadableResourcesController.class);
    private final static String SESSION_ATTRIBUTE_NAME_USER = "user";
    private static final int EXPECTED_URI_PARTS_QUANTITY = 3;
    private static final int ALIAS_URI_PART_NUMBER = 1;

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // checking if user is logged in and has rights to request resource
        User user = getUser(request);
        // if user isn't logged in
        if (user == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // checking if request is valid
        String uri = request.getRequestURI();
        String[] parts = uri.split("/");
        if(parts.length != EXPECTED_URI_PARTS_QUANTITY) {
            // something is wrong
            LOGGER.warn("Request uri does not meet expectations, found " + parts.length + " elements; " + uri);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String resourceAlias = parts[ALIAS_URI_PART_NUMBER];
        UploadableResource resourceType  = UploadableResource.getByAlias(resourceAlias);
        if(resourceType == UploadableResource.UNKNOWN_RESOURCE) {
            // something is wrong
            LOGGER.warn("Cannot process variable resource request: uri[" + uri + "] resourceType[" + resourceType + "]");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        File resource = new File(resourceType.getPathToResourceDirectory() + File.separator + parts[2]);
        LOGGER.debug("Resource path is: " + resource.getAbsolutePath());
        LOGGER.debug("Resource is present: " + (resource.exists() && resource.isFile()));
        if (!resource.exists() || resource.isDirectory()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        try (InputStream is = new FileInputStream(resource); OutputStream os = response.getOutputStream()) {
            int read;
            byte bytes[] = new byte[1024];

            while ((read = is.read(bytes)) != -1) {
                os.write(bytes, 0, read);
            }
            os.flush();
        }
    }

    private User getUser(ServletRequest servletRequest) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(SESSION_ATTRIBUTE_NAME_USER);
        return user;
    }
}
