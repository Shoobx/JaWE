/**
 * Together Workflow Editor
 * Copyright (C) 2011 Together Teamsolutions Co., Ltd. 
 * 
 * This program is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version. 
 *
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
 * GNU General Public License for more details. 
 *
 * You should have received a copy of the GNU General Public License 
 * along with this program. If not, see http://www.gnu.org/licenses
 */

package org.enhydra.jawe.components.xpdlview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.ByteArrayOutputStream;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.text.PlainDocument;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEComponentView;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.controller.JaWEController;
import org.enhydra.jxpdl.XMLAttribute;
import org.enhydra.jxpdl.XMLCollection;
import org.enhydra.jxpdl.XMLComplexChoice;
import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLSimpleElement;
import org.enhydra.jxpdl.elements.Package;
import org.jedit.syntax.JEditTextArea;
import org.jedit.syntax.SyntaxDocument;
import org.jedit.syntax.SyntaxStyle;
import org.jedit.syntax.TextAreaPainter;
import org.jedit.syntax.Token;
import org.jedit.syntax.XMLTokenMarker;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Container for displaying menubar, toolbar, process graphs ...
 *
 * @author Sasa Bojanic
 * @author Miroslav Popov
 * @author Zoran Milakovic
 */
public class XPDLViewControllerPanel extends JPanel implements JaWEComponentView {

   protected XPDLViewController controller;

   // protected JScrollPane xpdlPreviewSP;
   protected JEditTextArea xpdlPreviewTA;

   protected JComboBox searchCB;

   protected Component searchPanel;

   protected XMLElement elementToShow;

   public XPDLViewControllerPanel(XPDLViewController controller) {
      this.controller = controller;
   }

   public void configure() {
   }

   public void init() {
      setBorder(BorderFactory.createEtchedBorder());
      setLayout(new BorderLayout());

      add(createCenterComponent(), BorderLayout.CENTER);
      searchPanel = createSearchPanel();
      add(searchPanel, BorderLayout.NORTH);
   }

   public JaWEComponent getJaWEComponent() {
      return controller;
   }

   public JComponent getDisplay() {
      return this;
   }

   public void setSelectedElement(XMLElement pkgOrWpOrAs) {
      this.elementToShow = pkgOrWpOrAs;
      refreshView();
   }

   public XMLElement getSelectedElement() {
      return elementToShow;
   }

   /**
    * Create the center component of this panel.
    */
   protected Component createCenterComponent() {
      // creating text area pane for xpdl preview
      xpdlPreviewTA = new JEditTextArea();
      // xpdlPreviewTA.setLineWrap(false);
      // xpdlPreviewTA.setWrapStyleWord(false);
      xpdlPreviewTA.setEditable(false);
      xpdlPreviewTA.setMinimumSize(new Dimension(100, 100));
      xpdlPreviewTA.setElectricScroll(3);
      xpdlPreviewTA.addMouseWheelListener(new MouseWheelHandler(xpdlPreviewTA));

      Color bkgCol = ((XPDLViewSettings) controller.getSettings()).getBackgroundColor();
      if (controller.getXPDLViewSettings().showHighlight()) {
         SyntaxDocument doc = new SyntaxDocument();
         doc.putProperty(PlainDocument.tabSizeAttribute, new Integer(4));
         xpdlPreviewTA.setDocument(doc);

         xpdlPreviewTA.setTokenMarker(new XMLTokenMarker());

         TextAreaPainter painter = xpdlPreviewTA.getPainter();
         SyntaxStyle[] styles = painter.getStyles();
         styles[Token.KEYWORD1] = new SyntaxStyle(new Color(0, 102, 153), false, true);
         styles[Token.KEYWORD2] = new SyntaxStyle(new Color(0, 153, 102), false, true);
         styles[Token.KEYWORD3] = new SyntaxStyle(new Color(0, 153, 255), false, true);
         styles[Token.LITERAL1] = new SyntaxStyle(new Color(255, 0, 204), false, false);
         styles[Token.LITERAL2] = new SyntaxStyle(new Color(204, 0, 204), false, false);
         painter.setStyles(styles);
         painter.setEOLMarkersPainted(false);
         painter.setBracketHighlightEnabled(true);
         painter.setBackground(bkgCol);
      }
      xpdlPreviewTA.setBackground(bkgCol);
      // xpdlPreviewSP=new JScrollPane();
      // xpdlPreviewSP.setViewportView(xpdlPreviewTA);
      // JViewport port = xpdlPreviewSP.getViewport();
      // port.setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
      // return xpdlPreviewSP;
      return xpdlPreviewTA;
   }

   protected Component createSearchPanel() {
      JPanel sp = new JPanel();
      sp.setLayout(new BoxLayout(sp, BoxLayout.X_AXIS));

      Border emptyb = BorderFactory.createEmptyBorder(5, 5, 5, 5);
      sp.setBorder(emptyb);

      JLabel jl = new JLabel(controller.getSettings().getLanguageDependentString("SearchForKey") + ": ");
      jl.setAlignmentX(Component.LEFT_ALIGNMENT);
      jl.setAlignmentY(Component.BOTTOM_ALIGNMENT);
      jl.setHorizontalAlignment(SwingConstants.RIGHT);

      Color bkgCol = ((XPDLViewSettings) controller.getSettings()).getBackgroundColor();
      UIManager.put("ComboBox.background", new javax.swing.plaf.ColorUIResource(bkgCol));
      searchCB = new JComboBox();
      searchCB.setEditable(true);
      searchCB.setAlignmentX(Component.LEFT_ALIGNMENT);
      searchCB.setAlignmentY(Component.BOTTOM_ALIGNMENT);
      Dimension comboBoxDimension = new Dimension(200, 20);
      searchCB.setMinimumSize(new Dimension(comboBoxDimension));
      searchCB.setMaximumSize(new Dimension(comboBoxDimension));
      searchCB.setPreferredSize(new Dimension(comboBoxDimension));
      searchCB.setBackground(bkgCol);
      searchCB.getEditor().getEditorComponent().setBackground(bkgCol);
      ((JTextField) searchCB.getEditor().getEditorComponent()).setOpaque(true);

      JButton jb = new JButton("");

      jb.setIcon(controller.getXPDLViewSettings().getFindNextIcon());

      jb.setToolTipText(controller.getSettings().getLanguageDependentString("FindNextStringOccuranceKey"));
      jb.setVerticalTextPosition(SwingConstants.CENTER);
      jb.setAlignmentX(Component.LEFT_ALIGNMENT);
      jb.setAlignmentY(Component.BOTTOM_ALIGNMENT);
      jb.setPreferredSize(new Dimension(20, 20));

      sp.add(jl);
      sp.add(searchCB);
      sp.add(Box.createHorizontalStrut(5));
      sp.add(jb);

      jb.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent ae) {
            findString();
         }
      });

      return sp;
   }

   public void refreshView() {
      try {
         String toDisp = "";
         xpdlPreviewTA.setText(toDisp);

         Document document = null;

         DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
         DocumentBuilder dbuilder = dbf.newDocumentBuilder();
         document = dbuilder.newDocument();
         ByteArrayOutputStream baos = new ByteArrayOutputStream();

         // Here we get all document elements
         if (elementToShow != null) {

            if (!(elementToShow instanceof XMLAttribute)) {
               if (elementToShow instanceof Package) {
                  JaWEManager.getInstance().getXPDLHandler().getXPDLRepositoryHandler().toXML(document, (Package) elementToShow);
               } else {
                  Node node = document.createElement(elementToShow.toName());
                  if (elementToShow instanceof XMLComplexElement) {
                     JaWEManager.getInstance().getXPDLHandler().getXPDLRepositoryHandler().toXML(node, (XMLComplexElement) elementToShow);
                  } else if (elementToShow instanceof XMLCollection) {
                     JaWEManager.getInstance().getXPDLHandler().getXPDLRepositoryHandler().toXML(node, (XMLCollection) elementToShow);
                  } else if (elementToShow instanceof XMLComplexChoice) {
                     JaWEManager.getInstance().getXPDLHandler().getXPDLRepositoryHandler().toXML(node, (XMLComplexChoice) elementToShow);
                  } else if (elementToShow instanceof XMLSimpleElement) {
                     JaWEManager.getInstance().getXPDLHandler().getXPDLRepositoryHandler().toXML(node, (XMLSimpleElement) elementToShow);
                  }
                  Node toAppend = node.getFirstChild();
                  if (toAppend == null) {
                     toAppend = node;
                  }
                  document.appendChild(toAppend);
               }
               // Use a Transformer for output
               TransformerFactory tFactory = new org.apache.xalan.processor.TransformerFactoryImpl();
               Transformer transformer = tFactory.newTransformer();
               transformer.setOutputProperty("indent", "yes");
               transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
               transformer.setOutputProperty("encoding", JaWEManager.getInstance().getJaWEController().getControllerSettings().getEncoding());
               if (!(elementToShow instanceof Package)) {
                  transformer.setOutputProperty("omit-xml-declaration", "yes");
               } else {
                  transformer.setOutputProperty(OutputKeys.STANDALONE, "no");
               }
               DOMSource source = new DOMSource(document);
               StreamResult result = new StreamResult(baos);
               transformer.transform(source, result);

               toDisp = baos.toString(JaWEManager.getInstance().getJaWEController().getControllerSettings().getEncoding());
               baos.close();

            } else {
               toDisp = elementToShow.toName() + "=" + "\"" + elementToShow.toValue() + "\"";
            }

         }

         xpdlPreviewTA.setText(toDisp);
         xpdlPreviewTA.setCaretPosition(0);
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   /**
    * Searches the XPDL context containd within text area to find wanted string. Search starts from the current cursor location. When some new text is entered
    * into search box, it can be latter retrieved from the box.
    */
   protected void findString() {
      if (searchCB.getSelectedItem() == null)
         return;
      String searchingTxt = searchCB.getSelectedItem().toString();
      addItemToCombo(searchingTxt);
      String txt = xpdlPreviewTA.getText();
      try {
         int foundPos = xpdlPreviewTA.getCaretPosition();
         for (int i = foundPos; i <= (txt.length() - searchingTxt.length()); i++) {
            if (txt.substring(i, i + searchingTxt.length()).equalsIgnoreCase(searchingTxt)) {
               xpdlPreviewTA.setCaretPosition(i);
               // xpdlPreviewTA.getCaret().setSelectionVisible(true);
               xpdlPreviewTA.select(i, i + searchingTxt.length());
               foundPos = i + searchingTxt.length();
               return;
            }
         }
      } catch (Exception ex) {
      }
      JaWEController jc = JaWEManager.getInstance().getJaWEController();
      JOptionPane.showMessageDialog(jc.getJaWEFrame(),
                                    controller.getSettings().getLanguageDependentString("MessageJaWEHasFinishedSearchingXPDL"),
                                    jc.getAppTitle(),
                                    JOptionPane.INFORMATION_MESSAGE);
      xpdlPreviewTA.setCaretPosition(0);
      return;
   }

   /**
    * Adds a text into combo box if it hasn't been there previously.
    */
   private void addItemToCombo(String searchingTxt) {
      if (searchingTxt == null || searchingTxt.trim().length() == 0)
         return;
      for (int i = 0; i < searchCB.getItemCount(); i++) {
         if (searchCB.getItemAt(i).toString().equals(searchingTxt)) {
            return;
         }
      }
      searchCB.addItem(searchingTxt);
   }

   public void changeLanguage() {
      JLabel l = (JLabel) ((JPanel) searchPanel).getComponent(0);
      JButton b = (JButton) ((JPanel) searchPanel).getComponent(3);
      l.setText(controller.getSettings().getLanguageDependentString("SearchForKey") + ": ");
      b.setToolTipText(controller.getSettings().getLanguageDependentString("FindNextStringOccuranceKey"));
   }

}

class MouseWheelHandler implements MouseWheelListener {
   JEditTextArea area;

   public MouseWheelHandler(JEditTextArea area) {
      this.area = area;
   }

   public void mouseWheelMoved(MouseWheelEvent e) {
      if (e.isControlDown()) {
         if ((area.getFirstLine() + e.getWheelRotation()) < 0 || ((area.getFirstLine() + area.getVisibleLines() + e.getWheelRotation()) > area.getLineCount()))
            return;
         area.setFirstLine(area.getFirstLine() + e.getWheelRotation());
      } else {
         if ((area.getFirstLine() + e.getUnitsToScroll()) < 0 || ((area.getFirstLine() + area.getVisibleLines() + e.getUnitsToScroll()) > area.getLineCount()))
            return;
         area.setFirstLine(area.getFirstLine() + e.getUnitsToScroll());
      }
   }
}
