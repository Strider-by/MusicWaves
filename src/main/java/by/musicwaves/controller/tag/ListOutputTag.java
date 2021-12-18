package by.musicwaves.controller.tag;

import javax.servlet.jsp.PageContext;
import java.util.List;

public class ListOutputTag extends AbstractTag {

    private String htmlElementTag;
    private String className;
    private List list;
    private String elementsSeparator;

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

    @Override
    protected boolean isValidCondition(PageContext pageContext) {
        return list != null && !list.isEmpty();
    }

    @Override
    protected StringBuilder prepareCustomHtmlElement(PageContext pageContext) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {

            openElementTag(sb);
            // append element to be shown
            sb.append(list.get(i));
            closeElementTag(sb);

            // add elements separator if this not the last element of the list
            if (i < list.size() - 1) {
                addElementSeparator(sb);
            }
        }

        LOGGER.debug(sb);
        return sb;
    }

    private void openElementTag(StringBuilder sb) {
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

    private void closeElementTag(StringBuilder sb) {
        if (htmlElementTag != null) {
            sb.append("</");
            sb.append(htmlElementTag);
            sb.append(">");
        }
    }

    private void addElementSeparator(StringBuilder sb) {
        // add elements separator if the value was provided and this not the last element of the list
        if (elementsSeparator != null) {
            sb.append(elementsSeparator);
        }
    }

}
