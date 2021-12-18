package by.musicwaves.controller.tag;

import by.musicwaves.controller.util.AccessLevelEnum;
import by.musicwaves.controller.util.ApplicationPageEnum;
import by.musicwaves.entity.User;
import by.musicwaves.entity.ancillary.Language;

import javax.servlet.jsp.PageContext;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class CuratorAndHigherButtonsTag extends AbstractTag {
    private final static String BUNDLE_BASENAME = "internationalization.jsp.buttons";

    private final static String BUTTONS_GROUP_BLOCK_PATTERN = "<div class=\"%s\">";
    private final static String MENU_BUTTONS_GROUP_CLASS = "heading_menu_button_section";
    private final static String BUTTON_PATTERN
            = "<button class=\"%s\" onclick=\"location.href = '%s'\" title=\"%s\">"
            + "<img src=\"/static/img/%s\"/></button>";
    private final static String BUTTON_CLASS = "heading_menu_button";

    @Override
    protected boolean isValidCondition(PageContext pageContext) {
        return AccessLevelEnum.MUSIC_CURATOR_PLUS.isAccessGranted(getUser(pageContext));
    }

    @Override
    protected StringBuilder prepareCustomHtmlElement(PageContext pageContext) {
        Locale locale = Optional.ofNullable(getUser(pageContext))
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
        return sb;
    }

    private enum Button {

        MUSIC_COMPOUND(ApplicationPageEnum.MUSIC_COMPOUND.getAlias(), "music_compound", "music-compound-menu-button.svg");

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
