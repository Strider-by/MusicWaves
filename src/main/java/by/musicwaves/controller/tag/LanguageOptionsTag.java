package by.musicwaves.controller.tag;

import by.musicwaves.entity.User;
import by.musicwaves.entity.ancillary.Language;

import javax.servlet.jsp.PageContext;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LanguageOptionsTag extends AbstractTag {

    private final static List<Language> languages = Arrays.stream(Language.values())
            .filter(Language::isValidOption)
            .sorted(Comparator.comparing(Enum::name))
            .collect(Collectors.toList());

    @Override
    protected StringBuilder prepareCustomHtmlElement(PageContext pageContext) {
        int userLanguageId = Optional.ofNullable(getUser(pageContext))
                .map(User::getLanguage)
                .map(Language::getDatabaseId)
                .orElse(-1);

        StringBuilder sb = new StringBuilder();
        for (Language language : languages) {
            int currentLanguageId = language.getDatabaseId();

            // opening tag
            sb.append("<option value=\"");
            sb.append(currentLanguageId);
            sb.append("\"");
            if (userLanguageId == currentLanguageId) {
                sb.append("selected");
            }
            sb.append(">");

            // inner Html
            sb.append("[");
            sb.append(language.name().toLowerCase());
            sb.append("] :: ");
            sb.append(language.getNativeName().toLowerCase());

            // closing tag
            sb.append("</option>");
        }
        return sb;
    }

}
