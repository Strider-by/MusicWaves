package by.musicwaves.controller.servlet.tag;

import by.musicwaves.entity.User;
import by.musicwaves.entity.ancillary.Language;
import by.musicwaves.util.BooleanOption;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


public class BooleanOptionsTag extends SimpleTagSupport {

    private final static String BUNDLE_BASENAME = "internationalization.jsp.shared";
    private final static Logger LOGGER = LogManager.getLogger(BooleanOptionsTag.class);
    private final static String SESSION_ATTRIBUTE_NAME_USER = "user";
    private final static List<BooleanOption> BOOLEAN_OPTIONS;

    static {
        BOOLEAN_OPTIONS = Arrays.stream(BooleanOption.values())
                .filter(BooleanOption::isValidTagOption)
                .collect(Collectors.toList());
    }

    public void doTag() throws JspException {

        PageContext pageContext = (PageContext) getJspContext();
        HttpSession session = pageContext.getSession();
        User user = (User) session.getAttribute(SESSION_ATTRIBUTE_NAME_USER);
        Locale locale = Optional.ofNullable(user)
                .map(User::getLanguage)
                .map(Language::getLocale)
                .orElse(Language.UNKNOWN.getLocale());
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_BASENAME, locale);
        StringBuilder sb = new StringBuilder();

        for (BooleanOption option : BOOLEAN_OPTIONS) {
            int id = option.getId();
            String localizedName = bundle.getString(option.getPropertyKey());

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
            LOGGER.error("We have caught an exception during writing to JSP", ex);
            throw new JspException(ex);
        }

    }

}
