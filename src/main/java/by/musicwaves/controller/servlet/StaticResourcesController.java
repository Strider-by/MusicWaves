package by.musicwaves.controller.servlet;

        import java.io.IOException;
        import javax.servlet.ServletException;
        import javax.servlet.http.HttpServlet;
        import javax.servlet.http.HttpServletRequest;
        import javax.servlet.http.HttpServletResponse;

        import org.apache.logging.log4j.Logger;
        import org.apache.logging.log4j.LogManager;


public class StaticResourcesController extends HttpServlet {

    private final static Logger LOGGER = LogManager.getLogger();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        LOGGER.debug("We have reached STATIC RESOURCES controller");
        LOGGER.debug("URL: " + request.getRequestURL().toString());
        LOGGER.debug("URI: " + uri);

        request.getRequestDispatcher(convertUriToLocalResourcePath(uri)).forward(request, response);
    }

    private static String convertUriToLocalResourcePath(String uri) {
        return uri.replaceFirst("/resources", "/static");
    }
}
