package by.musicwaves.controller.servlet.tag;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.util.List;


public class ListOutputTag extends SimpleTagSupport {

    private final static Logger LOGGER = LogManager.getLogger(ListOutputTag.class);
    private String htmlElementTag;
    private String className;
    private List list;
    private String elementsSeparator;
    private final StringBuilder sb = new StringBuilder();

    public String getHtmlElementTag() {
        return htmlElementTag;
    }

    public void setHtmlElementTag(String htmlElementTag) {
        this.htmlElementTag = htmlElementTag;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }

    public String getElementsSeparator() {
        return elementsSeparator;
    }

    public void setElementsSeparator(String elementsSeparator) {
        this.elementsSeparator = elementsSeparator;
    }

    public void doTag() throws JspException {

        // if the list is empty, there is nothing to output
        if (list == null || list.isEmpty()) {
            return;
        }

        PageContext pageContext = (PageContext) getJspContext();
        for (int i = 0; i < list.size(); i++) {

            openElementTag();
            // append element to be shown
            sb.append(list.get(i));
            closeElementTag();

            // add elements separator if this not the last element of the list
            if (i < list.size() - 1) {
                addElementSeparator();
            }
        }

        LOGGER.debug(this.sb);

        try {
            JspWriter out = pageContext.getOut();
            out.println(sb);
        } catch (IOException ex) {
            LOGGER.error("We have caught an exception during writing to JSP");
            throw new JspException(ex);
        }

    }

    private void openElementTag() {
        if (htmlElementTag != null) {
            sb.append("<");
            sb.append(htmlElementTag);

            if (className != null) {
                sb.append(" class=\"");
                sb.append(className);
                sb.append("\"");
            }
            sb.append(">");
        }
    }

    private void closeElementTag() {
        if (htmlElementTag != null) {
            sb.append("</");
            sb.append(htmlElementTag);
            sb.append(">");
        }
    }

    private void addElementSeparator() {
        // add elements separator if the value was provided and this not the last element of the list
        if (elementsSeparator != null) {
            sb.append(elementsSeparator);
        }
    }

}
