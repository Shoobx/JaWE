package org.enhydra.jawe.base.controller;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Post-processor for XPDL documents to handle line ending normalization
 * and Python code snippet formatting consistency.
 *
 * This class addresses two specific issues:
 * 1. Converts CRLF line endings to Unix-style LF endings, relevant on Windows.
 * 2. Restores trailing newlines for multi-line Python snippets that
 *    should have them preserved (matching xpdl.py blackenAll behavior)
 */
public class XPDLPostProcessor {

    private static final Logger LOG = Logger.getLogger(XPDLPostProcessor.class.getName());

    // XPath expressions for elements that contain Python code
    // Using name() function to match full tag names including prefixes
    // because the DOM is made of non-namespace-aware elements
    private static final String[] PYTHON_ELEMENT_XPATHS = {
        "//*[name()='xpdl:Script']",
        "//*[name()='xpdl:Condition']",
        "//*[name()='xpdl:TaskApplication']//*[name()='xpdl:ActualParameter']"
    };

    // Pattern for ExtendedAttribute names that contain Python code
    private static final Pattern PYTHON_EXTENDED_ATTR_PATTERN =
        Pattern.compile(":(params|setup|finish|verify)$");

    /**
     * Post-processes an XPDL document to normalize line endings and
     * restore trailing newlines for Python code snippets.
     *
     * @param document The DOM document to process
     */
    public static void postProcess(Document document) {
        try {
            normalizeLineEndings(document);
            restorePythonTrailingNewlines(document);
        } catch (Exception e) {
            // Log the error but don't fail the save operation
            LOG.warning("XPDL post-processing failed: " + e.getMessage());
            LOG.fine(() -> {
                // optionally log stack trace at a finer level
                StringBuilder sb = new StringBuilder();
                for (StackTraceElement el : e.getStackTrace()) {
                    sb.append("\n\t").append(el.toString());
                }
                return sb.toString();
            });
        }
    }

    /**
     * Recursively traverses the document and converts CRLF to LF in all text nodes.
     */
    private static void normalizeLineEndings(Node node) {
        if (node.getNodeType() == Node.TEXT_NODE) {
            Text textNode = (Text) node;
            String content = textNode.getTextContent();
            if (content != null && content.contains("\r\n")) {
                textNode.setTextContent(content.replace("\r\n", "\n"));
            }
        }

        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            normalizeLineEndings(children.item(i));
        }
    }

    /**
     * Restores trailing newlines for multi-line Python code snippets in XPDL elements.
     * This matches the behavior of the blackenAll() method in xpdl.py.
     */
    private static void restorePythonTrailingNewlines(Document document) throws Exception {
        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xpath = xPathFactory.newXPath();


        // Process Script, Condition, and ActualParameter elements
        for (String xpathExpr : PYTHON_ELEMENT_XPATHS) {
            XPathExpression expr = xpath.compile(xpathExpr);
            NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);

            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);
                processTextContent(element);
                String newText = element.getTextContent();
            }
        }

        // Process ExtendedAttribute elements with Python code
        XPathExpression extAttrExpr = xpath.compile("//*[name()='xpdl:ExtendedAttribute']");
        NodeList extAttrNodes = (NodeList) extAttrExpr.evaluate(document, XPathConstants.NODESET);

        for (int i = 0; i < extAttrNodes.getLength(); i++) {
            Element element = (Element) extAttrNodes.item(i);
            String name = element.getAttribute("Name");

            if (name != null && PYTHON_EXTENDED_ATTR_PATTERN.matcher(name).find()) {
                // Process both text content and Value attribute
                processTextContent(element);

                String value = element.getAttribute("Value");
                String processedValue = addTrailingNewlineIfNeeded(value);
                if (!value.equals(processedValue)) {
                    element.setAttribute("Value", processedValue);
                }
            }
        }
    }

    /**
     * Processes the text content of an element to add trailing
     * newlines to multiline values.
     */
    private static void processTextContent(Element element) {
        String textContent = element.getTextContent();
        if (textContent != null &&
            textContent.contains("\n") &&
            !textContent.endsWith("\n")) {
            element.setTextContent(textContent + "\n");
        }
    }

    /**
     * Ensures multiline strings have trailing newlines.
     */
    private static String addTrailingNewlineIfNeeded(String content) {
        if (content != null && content.contains("\n") && !content.endsWith("\n")) {
            return content + "\n";
        }

        return content;
    }
}
