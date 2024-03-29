package by.musicwaves.controller.tag;

import by.musicwaves.controller.resource.AccessLevel;
import by.musicwaves.controller.resource.ApplicationPage;
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
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;


public class CuratorAndHigherButtonsTag extends SimpleTagSupport {

    private static final Logger LOGGER = LogManager.getLogger(CuratorAndHigherButtonsTag.class);
    private static final String BUNDLE_BASENAME = "internationalization.jsp.buttons";
    private static final String SESSION_ATTRIBUTE_NAME_USER = "user";

    private static final String BUTTONS_GROUP_BLOCK_PATTERN = "<div class=\"%s\">";
    private static final String MENU_BUTTONS_GROUP_CLASS = "heading_menu_button_section";
    private static final String BUTTON_PATTERN
            = "<button class=\"%s\" onclick=\"location.href = '%s'\" title=\"%s\">"
            + "<img src=\"/static/img/%s\"/></button>";
    private static final String BUTTON_CLASS = "heading_menu_button";

    public void doTag() throws JspException {

        PageContext pageContext = (PageContext) getJspContext();
        HttpSession session = pageContext.getSession();
        User user = (User) session.getAttribute(SESSION_ATTRIBUTE_NAME_USER);

        // if user doesn't fits by his rights, there will be no buttons shown
        if (!AccessLevel.MUSIC_CURATOR_PLUS.isAccessGranted(user)) {
            return;
        }

        Locale locale = Optional.ofNullable(user)
                .map(User::getLanguage)
                .map(Language::getLocale)
                .orElse(Language.DEFAULT.getLocale());
        ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_BASENAME, locale);
        StringBuilder sb = new StringBuilder();

        // opening group tag
        sb.append(String.format(BUTTONS_GROUP_BLOCK_PATTERN, MENU_BUTTONS_GROUP_CLASS));

        for (Button button : Button.values()) {
            String title = bundle.getString(button.titlePropertyKey);
            // creating button tag
            sb.append(String.format(
                    BUTTON_PATTERN,
                    BUTTON_CLASS,
                    button.link,
                    title,
                    button.buttonImageName));
        }

        // closing group tag
        sb.append("</div>");

        try {
            JspWriter out = pageContext.getOut();
            out.println(sb);
        } catch (IOException ex) {
            LOGGER.error("We have caught an exception during writing to JSP", ex);
            throw new JspException(ex);
        }
    }

    private enum Button {

        MUSIC_COMPOUND(ApplicationPage.MUSIC_COMPOUND.getAlias(), "music_compound", "music-compound-menu-button.svg");

        private final String link;
        private final String titlePropertyKey;
        private final String buttonImageName;

        Button(String link, String titlePropertyKey, String buttonImageName) {
            this.link = link;
            this.titlePropertyKey = titlePropertyKey;
            this.buttonImageName = buttonImageName;
        }
    }
}
