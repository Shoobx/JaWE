/**
 * Together Workflow Editor
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

package org.enhydra.jawe.components.graph.jped;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.controller.JaWEType;
import org.enhydra.jawe.components.graph.Graph;
import org.enhydra.jawe.components.graph.GraphEAConstants;
import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.elements.Activities;
import org.enhydra.jxpdl.elements.Activity;
import org.enhydra.jxpdl.elements.DataField;
import org.enhydra.jxpdl.elements.DataFields;
import org.enhydra.jxpdl.elements.ExtendedAttribute;
import org.enhydra.jxpdl.elements.ExtendedAttributes;
import org.enhydra.jxpdl.elements.Transition;
import org.enhydra.jxpdl.elements.Transitions;
import org.enhydra.jxpdl.elements.WorkflowProcess;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.ChapterAutoNumber;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class PDFPlugin {
   private static Logger log = Logger.getLogger(PDFPlugin.class.getName());

   private static Font CHAPTER_FONT = FontFactory.getFont(FontFactory.HELVETICA,
                                                          24,
                                                          Font.BOLD,
                                                          BaseColor.BLACK);

   private static Font SECTION_FONT = FontFactory.getFont(FontFactory.HELVETICA,
                                                          16,
                                                          Font.BOLD,
                                                          BaseColor.BLACK);

   private static Font NORMAL_FONT = FontFactory.getFont(FontFactory.HELVETICA,
                                                         10,
                                                         Font.NORMAL,
                                                         BaseColor.BLACK);

   private static Font PARAMETER_FONT = FontFactory.getFont(FontFactory.HELVETICA,
                                                            10,
                                                            Font.ITALIC,
                                                            BaseColor.BLACK);

   private static Font TRANSITION_CONDITION_FONT = FontFactory.getFont(FontFactory.HELVETICA,
                                                                       8,
                                                                       Font.NORMAL,
                                                                       BaseColor.BLACK);

   private Graph graph;

   private Image graphImage;

   private Image getGraphImage() {
      // picture caching disabled
      // if (graphImage!=null)
      // return graphImage;
      BufferedImage img = null;
      Object[] cells = graph.getRoots();
      if (cells.length > 0) {
         double originalScale = graph.getScale();
         // graph.setScale(1.5);
         java.awt.Rectangle bounds = graph.getCellBounds(cells).getBounds();// HM,
                                                                            // JGraph3.4.1
         graph.toScreen(bounds);

         // Create a Buffered Image
         Dimension d = bounds.getSize();
         img = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
         Graphics2D graphics = img.createGraphics();
         graph.paint(graphics);
         graph.setScale(originalScale);
      } else {
         return null;
      }
      try {
         graphImage = Image.getInstance(img, null);
      } catch (BadElementException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
      return graphImage;

   }

   private void insertGraphImage(Document document, WorkflowProcess process) {
      com.itextpdf.text.Rectangle originalSize = document.getPageSize();
      try {
         Image graphImage = getGraphImage();
         if (graphImage != null) {
            float width = (originalSize.getWidth() - originalSize.getBorderWidthLeft() - originalSize.getBorderWidthRight()) * 0.8f;
            float height = (originalSize.getHeight() - originalSize.getBorderWidthTop() - originalSize.getBorderWidthBottom()) * 0.8f;

            graphImage.setAlignment(Image.MIDDLE);
            float pageRatio = width / height;
            float ratio = graphImage.getScaledWidth() / graphImage.getScaledHeight();
            if (((pageRatio > 1.0f) && (ratio < 1.0f))
                || ((pageRatio < 1.0f) && (ratio > 1.0f))) {
               graphImage.setRotationDegrees(-90.0f);
               ratio = 1 / ratio;
            }
            float scale;
            if (ratio > pageRatio)
               scale = width / graphImage.getScaledWidth();
            else
               scale = height / graphImage.getScaledHeight();
            graphImage.scaleToFit(graphImage.getScaledWidth() * scale,
                                  graphImage.getScaledHeight() * scale);
            Paragraph p = new Paragraph();
            p.setAlignment(Element.ALIGN_CENTER);
            p.add(new Phrase(process.getId(), SECTION_FONT));
            document.add(p);
            document.add(Chunk.NEWLINE);
            Paragraph description = new Paragraph();
            description.add(new Phrase(process.getProcessHeader().getDescription(),
                                       NORMAL_FONT));
            document.add(description);
            document.newPage();
            document.add(graphImage);
            document.newPage();
         }
      } catch (DocumentException e) {
         e.printStackTrace();
      } finally {
         document.setPageSize(originalSize);
      }
   }

   private boolean addParameterRow(PdfPTable table, XMLElement elt, PDFChain chain)
      throws DocumentException {
      if (log.isLoggable(Level.FINEST))
         log.finest("creating row for element " + elt + "(name=" + elt.toName() + ")");
      Element visual = chain.startProcessElement(elt);
      if (visual != null) {
         table.addCell(makeBorderLessCell(new Phrase(JaWEManager.getInstance()
            .getLabelGenerator()
            .getLabel(elt), PARAMETER_FONT)));
         table.addCell(makeBorderLessCell(visual));
         return true;
      }
      return false;
   }

   private boolean addParameterRowNoId(PdfPTable table, XMLElement elt, PDFChain chain)
      throws DocumentException {
      if (log.isLoggable(Level.FINEST))
         log.finest("creating row for element " + elt + "(name=" + elt.toName() + ")");
      Element visual = chain.startProcessElement(elt);
      if (visual != null) {
         PdfPCell cell = makeBorderLessCell(visual);
         cell.setColspan(2);
         table.addCell(cell);
         return true;
      }
      return false;
   }

   private boolean addAttributeRow(PdfPTable table, ExtendedAttribute elt, PDFChain chain)
      throws DocumentException {
      Element visual = chain.startProcessElement(elt);
      if (visual != null) {
         table.addCell(makeBorderLessCell(new Phrase(elt.getName(), PARAMETER_FONT)));
         table.addCell(makeBorderLessCell(visual));
         return true;
      }
      return false;
   }

   private PdfPTable makeDataFieldReport(DataField field, PDFChain chain)
      throws DocumentException {
      PdfPTable table = new PdfPTable(2);
      addParameterRow(table, field.get("Id"), chain);
      addParameterRow(table, field.get("Name"), chain);
      addParameterRow(table, field.get("InitialValue"), chain);
      addParameterRow(table, field.get("Description"), chain);
      addParameterRow(table, field.get("ExtendedAttributes"), chain);
      return table;
   }

   private PdfPCell makeBorderLessCell(Element elt) {
      PdfPCell cell;
      if (elt instanceof Phrase)
         cell = new PdfPCell((Phrase) elt);
      else if (elt instanceof PdfPTable)
         cell = new PdfPCell((PdfPTable) elt);
      else if (elt instanceof Image)
         cell = new PdfPCell((Image) elt);
      else {
         cell = new PdfPCell();
         cell.addElement(elt);
      }
      cell.setBorderWidth(0.0f);
      return cell;
   }

   private Map transitionPictures = new HashMap();

   private Image getTransitionPicture(JaWEType type, boolean incoming, String condition)
      throws Exception {
      if (!transitionPictures.containsKey(type))
         transitionPictures.put(type, new Image[4]);
      Image[] list = (Image[]) transitionPictures.get(type);
      int index = condition.length() == 0 ? 0 : 1;
      if (incoming)
         index += 2;
      if (list[index] == null) {
         BufferedImage buf = new BufferedImage(90, 37, BufferedImage.TYPE_INT_ARGB);
         Graphics g = buf.getGraphics();
         g.setColor(Color.WHITE);
         g.fillRect(0, 1, 90, 37);
         g.setColor(type.getColor());
         int lineWidth = condition.length() == 0 ? 3 : 9;
         Graphics2D g2 = (Graphics2D) g;
         if (incoming)
            g2.setTransform(new AffineTransform(-1, 0, 0, 1, 90, 0));
         g2.setStroke(new BasicStroke(lineWidth,
                                      BasicStroke.CAP_ROUND,
                                      BasicStroke.JOIN_BEVEL));
         g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_ON);
         g2.drawLine(3, 17, 84 - lineWidth, 17);
         g2.drawLine(85, 17, 61, 5);
         g2.drawLine(85, 17, 61, 29);
         Image image = Image.getInstance(buf, null);
         image.scaleToFit(30.0f, 19.0f);
         list[index] = image;
      }
      return list[index];
   }

   private Image getTransitionPicture(Transition t, boolean incoming) throws Exception {
      JaWEType type = JaWEManager.getInstance()
         .getJaWEController()
         .getTypeResolver()
         .getJaWEType(t);
      return getTransitionPicture(type, incoming, t.getCondition().toValue());
   }

   private List getActivityOutgoingTransitions(Activity activity) {
      WorkflowProcess process = (WorkflowProcess) activity.getParent().getParent();
      Transitions transitions = process.getTransitions();
      List childs = transitions.toElements();
      for (Iterator i = childs.iterator(); i.hasNext();) {
         Object o = i.next();
         if ((!(o instanceof Transition))
             || (!((Transition) o).getFrom().equals(activity.getId())))
            i.remove();
      }
      return childs;
   }

   private List getActivityIngoingTransitions(Activity activity) {
      WorkflowProcess process = (WorkflowProcess) activity.getParent().getParent();
      Transitions transitions = process.getTransitions();
      List childs = transitions.toElements();
      for (Iterator i = childs.iterator(); i.hasNext();) {
         Object o = i.next();
         if ((!(o instanceof Transition))
             || (!((Transition) o).getTo().equals(activity.getId())))
            i.remove();
      }
      return childs;
   }

   private PdfPTable makeActivityTable(Activity activity, PDFSectionChain chain)
      throws DocumentException {
      PdfPTable table = new PdfPTable(2);
      table.setWidthPercentage(100);
      table.setKeepTogether(true);
      Rectangle border = new Rectangle(0.0f, 0.0f);
      border.setBorderWidth(0.0f);
      PdfPTable detailsTable = new PdfPTable(new float[] {
            0.3f, 0.7f
      });
      addParameterRow(detailsTable, activity.get("Id"), chain);
      addParameterRow(detailsTable, activity.get("Name"), chain);
      addParameterRow(detailsTable, activity.getFirstPerformerObj(), chain);
      addParameterRow(detailsTable, activity.get("Deadlines"), chain);
      PdfPCell cell = makeBorderLessCell(detailsTable);
      table.addCell(cell);
      List transitions;
      detailsTable = new PdfPTable(new float[] {
            0.2f, 0.3f, 0.5f
      });

      transitions = getActivityIngoingTransitions(activity);
      if (!transitions.isEmpty()) {
         cell = new PdfPCell(new Phrase("Incoming transitions"));
         cell.setColspan(3);
         detailsTable.addCell(cell);
         for (Iterator i = transitions.iterator(); i.hasNext();) {
            Transition t = (Transition) i.next();
            String condition = t.getCondition().toValue();
            try {
               detailsTable.addCell(makeBorderLessCell(getTransitionPicture(t, true)));
               detailsTable.addCell(makeBorderLessCell(new Phrase(t.getFrom(),
                                                                  TRANSITION_CONDITION_FONT)));
               detailsTable.addCell(makeBorderLessCell(new Phrase(condition,
                                                                  TRANSITION_CONDITION_FONT)));
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
      }
      transitions = getActivityOutgoingTransitions(activity);
      if (!transitions.isEmpty()) {
         cell = new PdfPCell(new Phrase("Outgoing transitions"));
         cell.setColspan(3);
         detailsTable.addCell(cell);
         for (Iterator i = transitions.iterator(); i.hasNext();) {
            Transition t = (Transition) i.next();
            String condition = t.getCondition().toValue();
            try {
               detailsTable.addCell(makeBorderLessCell(getTransitionPicture(t, false)));
               detailsTable.addCell(makeBorderLessCell(new Phrase(t.getTo(),
                                                                  TRANSITION_CONDITION_FONT)));
               detailsTable.addCell(makeBorderLessCell(new Phrase(condition,
                                                                  TRANSITION_CONDITION_FONT)));
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
      }

      cell = makeBorderLessCell(detailsTable);
      table.addCell(cell);

      /* now add rest of parameters (description, extended attributes) bottom */
      detailsTable = new PdfPTable(new float[] {
            0.3f, 0.7f
      });
      addParameterRowNoId(detailsTable, activity.get("Description"), chain);
      addParameterRow(detailsTable, activity.get("ExtendedAttributes"), chain);
      cell = makeBorderLessCell(detailsTable);
      cell.setColspan(2);
      table.addCell(cell);
      return table;
   }

   public Document processDocument(PDFDocumentChain chain) throws DocumentException {
      this.graph = chain.getGraph();
      Document document = null;
      if (chain.getElement() instanceof WorkflowProcess) {
         document = new Document(PageSize.A4);
         PdfWriter.getInstance(document, chain.getOutputStream());
         document.open();
         WorkflowProcess process = (WorkflowProcess) chain.getElement();
         insertGraphImage(document, process);
         document.add(chain.startProcessSection(process.getDataFields()));
         document.add(chain.startProcessSection(process.getActivities()));
      }
      this.graph = null;
      return document;
   }

   public static List excludedAttributes = new ArrayList();
   static {
      excludedAttributes.add(GraphEAConstants.EA_JAWE_GRAPH_PARTICIPANT_ID);
      excludedAttributes.add(GraphEAConstants.EA_JAWE_GRAPH_OFFSET);
   }

   public Element processElement(PDFElementChain chain) throws DocumentException {
      if (log.isLoggable(Level.FINER)) {
         if (chain.getElement() == null)
            log.finer("received a null element to processElement");
         else
            log.finer("processing element of type "
                      + chain.getElement().getClass().getName() + "(value="
                      + chain.getElement().toValue() + ")");
      }
      if (chain.getElement() instanceof ExtendedAttributes) {
         List l = ((ExtendedAttributes) chain.getElement()).toElements();
         if (log.isLoggable(Level.FINEST))
            log.finest("There are " + l.size() + " extended attributes to process");
         boolean hasContent = false;
         PdfPTable table = new PdfPTable(2);
         table.setWidthPercentage(100);
         table.setKeepTogether(true);
         for (Iterator i = l.iterator(); i.hasNext();) {
            ExtendedAttribute ea = (ExtendedAttribute) i.next();
            if (excludedAttributes.contains(ea.getName()))
               continue;
            hasContent |= addAttributeRow(table, ea, chain);
         }
         if (hasContent)
            return table;
         else
            return null;
      }
      String s = null;
      XMLElement el = chain.getElement();
      if (el != null) {
         s = chain.getElement().toValue();
         if (chain.getElement() instanceof XMLComplexElement) {
            XMLComplexElement cplx = (XMLComplexElement) chain.getElement();
            if (cplx.containsName("Value"))
               s = cplx.get("Value").toValue();
         }
      }
      if (s == null || s.length() == 0)
         return null;
      return new Paragraph(s, NORMAL_FONT);
   }

   public Section processSection(PDFSectionChain chain) throws DocumentException {
      if (chain.getElement() instanceof DataFields) {
         Chunk chapterTitle;
         Paragraph chapterTitleParagraph;
         Chapter chapter;
         chapterTitle = new Chunk("Variables", CHAPTER_FONT);
         chapterTitleParagraph = new Paragraph("", CHAPTER_FONT);
         chapterTitleParagraph.add(chapterTitle);
         chapter = new ChapterAutoNumber(chapterTitleParagraph);
         List dataFields = ((DataFields) chain.getElement()).toElements();
         Collections.sort(dataFields, new Comparator() {

            public int compare(Object o1, Object o2) {
               DataField left = (DataField) o1;
               DataField right = (DataField) o2;
               return left.getId().compareTo(right.getId());
            }

         });
         Section enclosing = chain.getEnclosingSection();
         chain.setEnclosingSection(chapter);
         for (Iterator i = dataFields.iterator(); i.hasNext();) {
            DataField field = (DataField) i.next();
            chain.startProcessSection(field);
         }
         chain.setEnclosingSection(enclosing);
         return chapter;
      } else if (chain.getElement() instanceof DataField) {
         DataField field = (DataField) chain.getElement();
         String name = field.getId();
         Paragraph sectionTitle = new Paragraph(name, SECTION_FONT);
         Section section = chain.getEnclosingSection().addSection(sectionTitle);
         section.add(Chunk.NEWLINE);
         section.add(makeDataFieldReport(field, chain));
         return section;
      } else if (chain.getElement() instanceof Activities) {
         Chunk chapterTitle;
         Paragraph chapterTitleParagraph;
         Chapter chapter;
         chapterTitle = new Chunk("Activities", CHAPTER_FONT);
         chapterTitleParagraph = new Paragraph("", CHAPTER_FONT);
         chapterTitleParagraph.add(chapterTitle);
         chapter = new ChapterAutoNumber(chapterTitleParagraph);
         List activities = ((Activities) chain.getElement()).toElements();
         Collections.sort(activities, new Comparator() {

            public int compare(Object o1, Object o2) {
               Activity left = (Activity) o1;
               Activity right = (Activity) o2;
               return left.getId().compareTo(right.getId());
            }

         });
         Section enclosing = chain.getEnclosingSection();
         chain.setEnclosingSection(chapter);
         for (Iterator i = activities.iterator(); i.hasNext();) {
            Activity activity = (Activity) i.next();
            chain.startProcessSection(activity);
         }
         chain.setEnclosingSection(enclosing);
         return chapter;
      } else if (chain.getElement() instanceof Activity) {
         Activity activity = (Activity) chain.getElement();
         String name = activity.getName();
         if ((name == null) || (name.length() == 0))
            name = activity.getId();
         Paragraph sectionTitle = new Paragraph(name, SECTION_FONT);
         Section section = chain.getEnclosingSection().addSection(sectionTitle);
         section.add(Chunk.NEWLINE);
         PdfPTable table = makeActivityTable(activity, chain);
         section.add(table);
         return section;
      }
      log.warning("Could not handle section for "
                  + chain.getElement() + " in code pdf plugin");
      return null;
   }

}
