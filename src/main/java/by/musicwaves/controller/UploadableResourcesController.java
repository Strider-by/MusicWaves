package by.musicwaves.controller;

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

import static by.musicwaves.controller.util.Constant.SESSION_ATTRIBUTE_NAME_USER;
import static java.util.Objects.nonNull;

/**
 * This servlet provides access to the uploadable resources ({@link UploadableResource}) files and additionally prevents
 * access to them if the user, that requested specific resource, in not logged in.
 */
public class UploadableResourcesController extends HttpServlet {

    private static final Logger LOGGER = LogManager.getLogger(UploadableResourcesController.class);
    private static final int ALIAS_URI_PART_NUMBER = 1;
    private static final int FILE_NAME_URI_PART_NUMBER = 2;
    private static final int EXPECTED_URI_PARTS_QUANTITY = 3;

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        final StateHolder stateHolder = new StateHolder();

        isAuthorized(stateHolder, request, response);
        isValidUri(stateHolder, request, response);
        isValidResourceType(stateHolder, response);
        isExistingResource(stateHolder, response);

        addResourceToResponse(stateHolder, response);
    }

    private void isAuthorized(StateHolder stateHolder, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // checking if user is logged in and has rights to request resource
        User user = getUser(request);
        stateHolder.setAuthorized(nonNull(user));
        // if user isn't logged in
        if (!stateHolder.isAuthorized()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    private void isValidUri(StateHolder stateHolder, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (stateHolder.isAuthorized()) {
            // checking if request is valid
            stateHolder.setUri(request.getRequestURI());
            stateHolder.setParts(stateHolder.getUri().split("/"));
            stateHolder.setValidUri(stateHolder.getParts().length != EXPECTED_URI_PARTS_QUANTITY);
            if (!stateHolder.isValidUri()) {
                // something is wrong
                LOGGER.warn("Request uri does not meet expectations, found " + stateHolder.getParts().length +
                        " elements; " + stateHolder.getUri());
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }

    private void isValidResourceType(StateHolder stateHolder, HttpServletResponse response) throws IOException {
        if (stateHolder.isValidUri()) {
            String resourceAlias = stateHolder.getParts()[ALIAS_URI_PART_NUMBER];
            stateHolder.setResourceType(UploadableResource.getByAlias(resourceAlias));
            if (stateHolder.getResourceType() == UploadableResource.UNKNOWN_RESOURCE) {
                // something is wrong
                LOGGER.warn("Cannot process variable resource request: uri[" + stateHolder.getUri() + "] resourceType["
                        + stateHolder.getResourceType() + "]");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }

    private void isExistingResource(StateHolder stateHolder, HttpServletResponse response) throws IOException {
        if (stateHolder.isValidResourceType()) {
            stateHolder.setResource(new File(stateHolder.getResourceType().getPathToResourceDirectory() +
                    File.separator + stateHolder.getParts()[FILE_NAME_URI_PART_NUMBER]));
            LOGGER.debug("Resource path is: " + stateHolder.getResource().getAbsolutePath());
            LOGGER.debug("Resource is present: " + (stateHolder.getResource().exists() && stateHolder.getResource().isFile()));
            stateHolder.setExistingResource(stateHolder.getResource().exists() && stateHolder.getResource().isFile());
            if (!stateHolder.isExistingResource()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        }
    }

    private void addResourceToResponse(StateHolder stateHolder, HttpServletResponse response) throws IOException {
        if (stateHolder.isExistingResource()) {
            try (InputStream is = new FileInputStream(stateHolder.getResource()); OutputStream os = response.getOutputStream()) {
                int read;
                byte[] bytes = new byte[1024];

                while ((read = is.read(bytes)) != -1) {
                    os.write(bytes, 0, read);
                }
                os.flush();
            }
        }
    }

    private User getUser(ServletRequest servletRequest) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(SESSION_ATTRIBUTE_NAME_USER);
        return user;
    }

    private class StateHolder {
        private boolean isAuthorized;
        private boolean isValidUri;
        private boolean isValidResourceType;
        private boolean isExistingResource;
        private String uri = null;
        private String[] parts = null;
        private UploadableResource resourceType = null;
        private File resource = null;

        private boolean isAuthorized() {
            return isAuthorized;
        }

        private void setAuthorized(boolean authorized) {
            isAuthorized = authorized;
        }

        private boolean isValidUri() {
            return isValidUri;
        }

        private void setValidUri(boolean validUri) {
            isValidUri = validUri;
        }

        private boolean isValidResourceType() {
            return isValidResourceType;
        }

        private void setValidResourceType(boolean validResourceType) {
            isValidResourceType = validResourceType;
        }

        private boolean isExistingResource() {
            return isExistingResource;
        }

        private void setExistingResource(boolean existingResource) {
            isExistingResource = existingResource;
        }

        private String getUri() {
            return uri;
        }

        private void setUri(String uri) {
            this.uri = uri;
        }

        private String[] getParts() {
            return parts;
        }

        private void setParts(String[] parts) {
            this.parts = parts;
        }

        private UploadableResource getResourceType() {
            return resourceType;
        }

        private void setResourceType(UploadableResource resourceType) {
            this.resourceType = resourceType;
        }

        private File getResource() {
            return resource;
        }

        private void setResource(File resource) {
            this.resource = resource;
        }
    }
}
