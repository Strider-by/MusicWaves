package by.musicwaves.controller.tag;

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
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class LanguageOptionsTag extends SimpleTagSupport {

    private static final Logger LOGGER = LogManager.getLogger(LanguageOptionsTag.class);
    private static final List<Language> languages;

    static {
        languages = Arrays.stream(Language.values())
                .filter(Language::isValidOption)
                .sorted(Comparator.comparing(Enum::name))
                .collect(Collectors.toList());
    }

    public void doTag() throws JspException {

        PageContext pageContext = (PageContext) getJspContext();
        HttpSession session = pageContext.getSession();
        User user = (User) session.getAttribute("user");
        int userLanguageId = Optional.ofNullable(user)
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

        try {
            JspWriter out = pageContext.getOut();
            out.println(sb);
        } catch (IOException ex) {
            LOGGER.error("We have caught an exception during writing to JSP");
            throw new JspException(ex);
        }
    }
}
