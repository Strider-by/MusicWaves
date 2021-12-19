package by.musicwaves.controller.tag;

import by.musicwaves.entity.Role;
import by.musicwaves.entity.User;
import by.musicwaves.entity.ancillary.Language;

import javax.servlet.jsp.PageContext;
import java.util.*;
import java.util.stream.Collectors;

public class RoleOptionsTag extends AbstractTag {

    private final static String BUNDLE_BASENAME = "internationalization.jsp.shared";
    private final static List<Role> roles = Arrays.stream(Role.values())
            .filter(Role::isValidOption)
            .sorted(Comparator.comparing(Role::getDatabaseId))
            .collect(Collectors.toList());

    @Override
    protected StringBuilder prepareCustomHtmlElement(PageContext pageContext) {
        Locale locale = Optional.ofNullable(getUser(pageContext))
                .map(User::getLanguage)
                .map(Language::getLocale)
                .orElse(Language.DEFAULT.getLocale());
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_BASENAME, locale);
        LOGGER.debug("\n\n\n\n used locale: " + locale + "; used bundle: " + bundle + "\n\n\n\n");
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
        return sb;
    }
}
