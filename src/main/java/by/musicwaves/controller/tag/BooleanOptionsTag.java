package by.musicwaves.controller.tag;

import by.musicwaves.entity.User;
import by.musicwaves.entity.ancillary.Language;
import by.musicwaves.util.BooleanOption;

import javax.servlet.jsp.PageContext;
import java.util.*;
import java.util.stream.Collectors;

public class BooleanOptionsTag extends AbstractTag {

    private final static String BUNDLE_BASENAME = "internationalization.jsp.shared";
    private final static List<BooleanOption> BOOLEAN_OPTIONS = Arrays.stream(BooleanOption.values())
            .filter(BooleanOption::isValidTagOption)
            .collect(Collectors.toList());

    @Override
    protected StringBuilder prepareCustomHtmlElement(PageContext pageContext) {
        Locale locale = Optional.ofNullable(getUser(pageContext))
                .map(User::getLanguage)
                .map(Language::getLocale)
                .orElse(Language.DEFAULT.getLocale());
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
        return sb;
    }
}
