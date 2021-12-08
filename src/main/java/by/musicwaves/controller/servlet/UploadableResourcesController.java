package by.musicwaves.controller.servlet;

import by.musicwaves.service.util.UploadableResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;


public class UploadableResourcesController extends HttpServlet {

    private final static Logger LOGGER = LogManager.getLogger(UploadableResourcesController.class);

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String uri = request.getRequestURI();

        String[] parts = uri.split("/");
        if(parts.length != 3) {
            // something is wrong
            LOGGER.debug("Request uri does not meet expectations, found " + parts.length + " elements");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String resourceAlias = parts[1];
        UploadableResource resourceType  = UploadableResource.getByAlias(resourceAlias);
        if(resourceType == UploadableResource.UNKNOWN_RESOURCE) {
            // something is wrong
            LOGGER.error("Cannot process variable resource request: uri[" + uri + "] resourceType[" + resourceType + "]");
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
}
