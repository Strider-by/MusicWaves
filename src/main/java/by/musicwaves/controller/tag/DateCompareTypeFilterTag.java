package by.musicwaves.controller.tag;

import by.musicwaves.dao.util.DateCompareType;
import by.musicwaves.entity.User;
import by.musicwaves.entity.ancillary.Language;

import javax.servlet.jsp.PageContext;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class DateCompareTypeFilterTag extends AbstractTag {

    private final static String BUNDLE_BASENAME = "internationalization.jsp.shared";

    @Override
    protected StringBuilder prepareCustomHtmlElement(PageContext pageContext) {
        Locale locale = Optional.ofNullable(getUser(pageContext))
                .map(User::getLanguage)
                .map(Language::getLocale)
                .orElse(Language.DEFAULT.getLocale());
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_BASENAME, locale);
        StringBuilder sb = new StringBuilder();

        for (DateCompareType type : DateCompareType.values()) {
            int id = type.getId();
            String localizedName = bundle.getString(type.getPropertyKey());

            // opening tag
            sb.append("<option value=\"");
            sb.append(id);
            sb.append("\">");

            // inner Html
            sb.append(localizedName);

            // closing tag
            sb.append("</option>");
        }
        return sb;
    }
}
