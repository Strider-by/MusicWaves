package by.musicwaves.controller.servlet.tag;

import by.musicwaves.entity.Role;
import by.musicwaves.entity.User;
import by.musicwaves.entity.ancillary.Language;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.taglibs.standard.tag.common.fmt.BundleSupport;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import static javax.servlet.jsp.jstl.fmt.LocaleSupport.getLocalizedMessage;


public class RoleOptionsTag extends SimpleTagSupport {

    private final static String BUNDLE_BASENAME = "internationalization.jsp.shared";
    private final static Logger LOGGER = LogManager.getLogger(RoleOptionsTag.class);
    private final static String SESSION_ATTRIBUTE_NAME_USER = "user";
    private final static List<Role> roles;

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
                .orElse(Language.UNKNOWN.getLocale());
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_BASENAME, locale);
        StringBuilder sb = new StringBuilder();

        for (Role role : roles) {
            int roleId = role.getDatabaseId();
            String localizedRoleName = bundle.getString(role.getPropertyKey());
            //LocalizationContext localizationContext = BundleSupport.getLocalizationContext(pageContext, BUNDLE_BASENAME);
            //LOGGER.debug("Used locale is: " + localizationContext.getLocale());

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
