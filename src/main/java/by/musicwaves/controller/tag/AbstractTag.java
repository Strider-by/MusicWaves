package by.musicwaves.controller.tag;

import by.musicwaves.entity.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

import static by.musicwaves.controller.util.Constant.SESSION_ATTRIBUTE_NAME_USER;

public abstract class AbstractTag extends SimpleTagSupport {
    public final static Logger LOGGER = LogManager.getLogger(AbstractTag.class);

    protected abstract StringBuilder prepareCustomHtmlElement(PageContext pageContext);

    @Override
    public void doTag() throws JspException {
        PageContext pageContext = (PageContext) getJspContext();

        // if user doesn't fits by his rights, there will be no buttons shown
        if (isValidCondition(pageContext)) {
            StringBuilder sb = prepareCustomHtmlElement(pageContext);
            printPreparedElementOnJsp(pageContext, sb);
        }
    }

    protected boolean isValidCondition(PageContext pageContext) {
        return true;
    }

    protected void printPreparedElementOnJsp(PageContext pageContext, StringBuilder sb) throws JspException {
        try {
            JspWriter out = pageContext.getOut();
            out.println(sb);
        } catch (IOException ex) {
            LOGGER.error("We have caught an exception during writing to JSP", ex);
            throw new JspException(ex);
        }
    }

    protected User getUser(PageContext pageContext) {
        User user = (User) pageContext.getSession().getAttribute(SESSION_ATTRIBUTE_NAME_USER);
        return user;
    }
}
