package by.musicwaves.controller.tag;

import by.musicwaves.entity.Role;
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
import java.util.stream.Collectors;


public class RoleOptionsTag extends SimpleTagSupport {

    private static final String BUNDLE_BASENAME = "internationalization.jsp.shared";
    private static final Logger LOGGER = LogManager.getLogger(RoleOptionsTag.class);
    private static final String SESSION_ATTRIBUTE_NAME_USER = "user";
    private static final List<Role> roles;

    static {
        roles = Arrays.stream(Role.values())
                .filter(Role::isValidOption)
                .sorted(Comparator.comparing(Role::getDatabaseId))
                .collect(Collectors.toList());
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
        LOGGER.debug("\n\n\n\n used locale: " + locale + "; used bundle: " + bundle + "\n\n\n\n");
        StringBuilder sb = new StringBuilder();

        for (Role role : roles) {
            int roleId = role.getDatabaseId();
            String localizedRoleName = bundle.getString(role.getPropertyKey());

            // opening tag
            sb.append("<option value=\"");
            sb.append(roleId);
            sb.append("\">");

            // inner Html
            sb.append(localizedRoleName);

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
