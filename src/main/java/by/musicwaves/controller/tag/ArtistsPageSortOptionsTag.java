package by.musicwaves.controller.tag;

import by.musicwaves.dao.impl.ArtistDaoImpl.Field;
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
import java.util.*;


public class ArtistsPageSortOptionsTag extends SimpleTagSupport {

    private static final String BUNDLE_BASENAME = "internationalization.jsp.artists";
    private static final Logger LOGGER = LogManager.getLogger(ArtistsPageSortOptionsTag.class);
    private static final String SESSION_ATTRIBUTE_NAME_USER = "user";
    private static final List<Field> fields;

    static {
        fields = Arrays.asList(
                Field.ID,
                Field.NAME,
                Field.VISIBILITY);
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

        for (Field field : fields) {
            int fieldId = field.getId();
            String localizedFieldName = bundle.getString(field.getPropertyKey());

            // opening tag
            sb.append("<option value=\"");
            sb.append(fieldId);
            sb.append("\">");

            // inner Html
            sb.append(localizedFieldName);

            // closing tag
            sb.append("</option>");
        }

        try {
            JspWriter out = pageContext.getOut();
            out.println(sb);
        } catch (IOException ex) {
            LOGGER.error("We have caught an exception during writing to JSP", ex);
            throw new JspException(ex);
        }
    }
}
