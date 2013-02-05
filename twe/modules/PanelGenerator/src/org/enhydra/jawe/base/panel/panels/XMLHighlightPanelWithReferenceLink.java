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

package org.enhydra.jawe.base.panel.panels;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.Settings;
import org.enhydra.jawe.base.panel.InlinePanel;
import org.enhydra.jawe.base.panel.PanelContainer;
import org.enhydra.jawe.base.panel.PanelSettings;
import org.enhydra.jxpdl.XMLAttribute;
import org.enhydra.jxpdl.XMLCollectionElement;
import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLEmptyChoiceElement;
import org.enhydra.jxpdl.XMLSimpleElement;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.elements.Participant;
import org.enhydra.jxpdl.elements.Performer;
import org.enhydra.jxpdl.elements.WorkflowProcess;
import org.enhydra.jxpdl.utilities.SequencedHashMap;
import org.jedit.syntax.JEditTextArea;

/**
 * Creates panel with JLabel and JEditPanel.
 * 
 * @author Sinisa Tutus
 */
public class XMLHighlightPanelWithReferenceLink extends XMLBasicPanel implements
                                                                     XMLAppendChoiceInterface {

   private static Dimension refButDimension = new Dimension(25, 20);

   Dimension textDim = new Dimension(400, 20);

   protected XMLMultiLineHighlightPanelWithChoiceButton panel;

   protected JButton jb;

   protected ArrayList participants;

   public XMLHighlightPanelWithReferenceLink(PanelContainer pc,
                                             Performer myOwner,
                                             List choices,
                                             boolean hasEmptyBorder,
                                             boolean isVertical,
                                             boolean isEditable,
                                             boolean isEnabled,
                                             String tooltip) {

      super(pc, myOwner, "", isVertical, false, hasEmptyBorder, tooltip);

      boolean panelEnabled = true;
      if (pc != null) {

         Settings settings = pc.getSettings();

         // check if there is a property to disable panel
         String discbo = settings.getSettingString("XMLComboPanel.DisablePanel");
         String[] hstra = XMLUtil.tokenize(discbo, " ");
         if (hstra != null) {
            for (int i = 0; i < hstra.length; i++) {
               if (hstra[i].equals(myOwner.toName())) {
                  panelEnabled = false;
                  break;
               }
            }
         }
      }
      SequencedHashMap ch = new SequencedHashMap();
      WorkflowProcess wp = XMLUtil.getWorkflowProcess(this.myOwner);
      if (wp == null) {
         String wpOrAsId = XMLUtil.getPool(this.myOwner).getProcess();
         wp = XMLUtil.getPackage(this.myOwner).getWorkflowProcess(wpOrAsId);
         if (wp == null) {
            XMLElement as = XMLUtil.getPackage(this.myOwner).getActivitySet(wpOrAsId);
            if (as != null) {
               wp = XMLUtil.getWorkflowProcess(as);
            }
         }
      }
      if (wp != null) {
         ch = XMLUtil.getPossibleParticipants(wp, JaWEManager.getInstance()
            .getXPDLHandler());
      }
      participants = new ArrayList(ch.values());

      XMLChoiceButtonWithPopup variableList = null;
      List choice = null;

      if (wp != null) {

         choice = new ArrayList(XMLUtil.getPossibleVariables(wp).values());

         variableList = new XMLChoiceButtonWithPopup(this,
                                                     choice,
                                                     ((PanelSettings) pc.getSettings()).getInsertVariableDefaultIcon(),
                                                     ((PanelSettings) pc.getSettings()).getInsertVariablePressedIcon());
      }

      Object chsn = null;
      XMLElement choosen = myOwner;
      if (choosen instanceof XMLComplexElement) {
         chsn = new XMLElementView(pc, choosen, XMLElementView.TONAME);
      } else {
         chsn = new XMLElementView(pc, choosen, XMLElementView.TOVALUE);
      }
      String initText = null;
      if (chsn != null) {
         XMLElement element = ((XMLElementView) chsn).getElement();
         if (element instanceof XMLCollectionElement)
            initText = ((((XMLCollectionElement) element).getId()));
         else
            initText = element.toValue();
      }
      List<List> mc = new ArrayList<List>();
      mc.add(participants);
      panel = new XMLMultiLineHighlightPanelWithChoiceButton(getPanelContainer(),
                                                             myOwner,
                                                             myOwner.toName(),
                                                             false,
                                                             true,
                                                             XMLMultiLineHighlightPanelWithChoiceButton.SIZE_MEDIUM,
                                                             false,
                                                             mc,
                                                             isEnabled,
                                                             initText,
                                                             null);

      panel.setAlignmentX(Component.LEFT_ALIGNMENT);
      panel.setAlignmentY(Component.TOP_ALIGNMENT);
      panel.setEnabled(isEnabled && panelEnabled);
      Iterator it = participants.iterator();
      while (it.hasNext()) {
         XMLElement el = ((XMLElement) it.next());
         if (!(el instanceof XMLEmptyChoiceElement)) {
            String scriptType = XMLUtil.getPackage(el).getScript().getType();
            panel.setHighlightScript(scriptType);
            break;
         }
      }

      if (isEditable) {
         panel.jta.addCaretListener(new CaretListener() {
            public void caretUpdate(CaretEvent e) {

               if (!(getInsertedText() instanceof XMLElement)) {
                  jb.setEnabled(false);
               } else {
                  jb.setEnabled(true);
               }
            }
         });
      }

      add(panel);

      if (pc != null) {
         jb = new JButton(((PanelSettings) pc.getSettings()).getArrowRightImageIcon());
      } else {
         jb = new JButton("->");
      }
      jb.setBorderPainted(false);
      jb.setAlignmentX(Component.LEFT_ALIGNMENT);
      jb.setAlignmentY(Component.TOP_ALIGNMENT);
      jb.setMinimumSize(new Dimension(refButDimension));
      jb.setMaximumSize(new Dimension(refButDimension));
      jb.setPreferredSize(new Dimension(refButDimension));
      jb.setRolloverEnabled(true);
      jb.setContentAreaFilled(false);
      jb.setEnabled((getInsertedText() instanceof XMLElement));
      jb.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent ae) {
            Object toShow = getInsertedText();
            if (toShow instanceof XMLElement) {
               ((InlinePanel) getPanelContainer()).displayGivenElement((XMLElement) toShow);
            }
         }

      });

      if (variableList != null) {
         variableList.setBorderPainted(false);
         variableList.setContentAreaFilled(false);
         variableList.setAlignmentX(Component.LEFT_ALIGNMENT);
         variableList.setAlignmentY(Component.TOP_ALIGNMENT);
         if (!isEnabled || choice.size() == 0)
            variableList.setEnabled(false);

         panel.jspAndOpt.add(variableList, 0);
      }
      panel.jspAndOpt.add(jb);
   }

   public boolean validateEntry() {
      String siv = "";
      Object selItem = getInsertedText();
      System.err.println("Validating entry for " + selItem);
      if (selItem != null) {
         // System.err.println("SI="+selItem+", class="+selItem.getClass().getName());
         if (selItem instanceof XMLElement) {
            if (selItem instanceof XMLSimpleElement || selItem instanceof XMLAttribute /*|| selItem instanceof XMLEmptyChoiceElement*/) {
               siv = ((XMLElement) selItem).toValue();
            } else {
               siv = ((XMLElement) selItem).toName();
            }
         } else {
            siv = selItem.toString();
         }
      }
      // System.err.println("SIV="+siv);
      // System.err.println("IREQ="+getOwner().isRequired()+", iro="+getOwner().isReadOnly());
      // if ((selItem==null || siv.trim().equals("")) &&
      // getOwner().isRequired() && !getOwner().isReadOnly()) {
      // int cs=((SpecialChoiceElement)myOwner).getChoices().size();
      // Object ec=null;
      // if (cs==1) {
      // ec=((SpecialChoiceElement)myOwner).getChoices().get(0);
      // }
      // System.err.println("CCCCSSSS="+cs+", ec="+ec);
      // if (!(cs==0 || cs==1)) {
      // XMLBasicPanel.defaultErrorMessage(this.getWindow(),panel.jl.getText());
      // panel.jta.requestFocus();
      // return false;
      // }
      // }
      return true;
   }

   public void setElements() {
      if (!getOwner().isReadOnly()) {
         Object sel = getInsertedText();
         if (sel instanceof Participant) {
            myOwner.setValue(((Participant) sel).getId());
         } else if (sel instanceof String) {
            myOwner.setValue(sel.toString());
         }
      }
   }

   public JEditTextArea getEditTextArea() {
      return panel.jta;
   }

   public Object getInsertedText() {
      try {
         Object el = null;
         String compareString = null;
         el = panel.getText().trim();

         Iterator it = participants.iterator();
         while (it.hasNext()) {

            XMLElement xmlEl = (XMLElement) it.next();
            if (xmlEl instanceof XMLCollectionElement)
               compareString = (((XMLCollectionElement) xmlEl).getId());
            else
               compareString = xmlEl.toName();

            if (el.equals(compareString))
               return xmlEl;
         }
         if (el != null) {
            return el.toString();
         }
         return "";
      } catch (Exception ex) {
         ex.printStackTrace();
         return "";
      }
   }

   public Object getValue() {
      return getInsertedText();
   }

   public void cleanup() {
   }

   public String getText() {
      return panel.getText();
   }

   public void appendText(String txt) {
      panel.appendText(txt);
      panel.jta.requestFocus();
   }
}
