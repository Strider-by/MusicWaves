package by.musicwaves.controller.tag;

import by.musicwaves.dao.impl.UserDaoImpl.Field;
import by.musicwaves.entity.User;
import by.musicwaves.entity.ancillary.Language;

import javax.servlet.jsp.PageContext;
import java.util.*;

public class UsersPageSortOptionsTag extends AbstractTag {

    private final static String BUNDLE_BASENAME = "internationalization.jsp.users";
    private final static List<Field> fields = Arrays.asList(
            Field.ID,
            Field.LOGIN,
            Field.ROLE,
            Field.REGISTER_DATE);

    @Override
    protected StringBuilder prepareCustomHtmlElement(PageContext pageContext) {
        Locale locale = Optional.ofNullable(getUser(pageContext))
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
        return sb;
    }
}
