package by.musicwaves.controller.servlet.tag;

import by.musicwaves.dao.util.SortOrder;
import by.musicwaves.entity.User;
import by.musicwaves.entity.ancillary.Language;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;


public class SortOrderOptionsTag extends SimpleTagSupport {

    private final static Logger LOGGER = LogManager.getLogger(SortOrderOptionsTag.class);
    private final static String BUNDLE_BASENAME = "internationalization.jsp.shared";
    private final static String SESSION_ATTRIBUTE_NAME_USER = "user";
    private final static SortOrder[] SORT_ORDERS;

    static {
        SORT_ORDERS = SortOrder.values();
    }

    public void doTag() throws JspException {

        PageContext pageContext = (PageContext) getJspContext();
        HttpSession session = pageContext.getSession();
        User user = (User) session.getAttribute(SESSION_ATTRIBUTE_NAME_USER);
        Locale locale = Optional.ofNullable(user)
                .map(User::getLanguage)
                .map(Language::getLocale)
                .orElse(Language.DEFAULT.getLocale());

        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_BASENAME, locale);
        StringBuilder sb = new StringBuilder();

        for (SortOrder sortOrder : SORT_ORDERS) {
            int id = sortOrder.getId();
            String localizedName = bundle.getString(sortOrder.getPropertyKey());

            // opening tag
            sb.append("<option value=\"");
            sb.append(id);
            sb.append("\">");

            // inner Html
            sb.append(localizedName);

            // closing tag
            sb.append("</option>");
        }

        try {
            JspWriter out = pageContext.getOut();
            out.println(sb);
        } catch (IOException ex) {
            LOGGER.error("We have caught an exception during writing to JSP");
            throw new JspException(ex);
        }
    }
}
