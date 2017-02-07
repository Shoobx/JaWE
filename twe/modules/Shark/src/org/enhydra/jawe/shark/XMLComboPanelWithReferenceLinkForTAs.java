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

package org.enhydra.jawe.shark;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.ResourceManager;
import org.enhydra.jawe.Settings;
import org.enhydra.jawe.base.controller.JaWEController;
import org.enhydra.jawe.base.editor.StandardXPDLElementEditor;
import org.enhydra.jawe.base.panel.InlinePanel;
import org.enhydra.jawe.base.panel.PanelContainer;
import org.enhydra.jawe.base.panel.PanelSettings;
import org.enhydra.jawe.base.panel.SpecialChoiceElement;
import org.enhydra.jawe.base.panel.panels.PanelUtilities;
import org.enhydra.jawe.base.panel.panels.TooltipComboRenderer;
import org.enhydra.jawe.base.panel.panels.XMLBasicPanel;
import org.enhydra.jawe.base.panel.panels.XMLComboPanelWithReferenceLink;
import org.enhydra.jawe.base.panel.panels.XMLElementView;
import org.enhydra.jawe.base.panel.panels.XMLPanel;
import org.enhydra.jawe.shark.business.SharkConstants;
import org.enhydra.jxpdl.XMLAttribute;
import org.enhydra.jxpdl.XMLComplexChoice;
import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLEmptyChoiceElement;
import org.enhydra.jxpdl.XMLSimpleElement;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.elements.Application;
import org.enhydra.jxpdl.elements.ExtendedAttribute;
import org.enhydra.jxpdl.elements.ExtendedAttributes;

/**
 * Creates panel with JLabel and JComboBox.
 * 
 * @author Sasa Bojanic
 * @author Zoran Milakovic
 */
public class XMLComboPanelWithReferenceLinkForTAs extends XMLBasicPanel {

   private static Dimension refButDimension = new Dimension(25, 20);

   Dimension textDim = new Dimension(400, 20);

   protected JComboBox jcb;

   protected JButton jb;

   protected JLabel jl;

   public XMLComboPanelWithReferenceLinkForTAs(PanelContainer pc,
                                               SpecialChoiceElement myOwner,
                                               List choices,
                                               boolean hasEmptyBorder,
                                               boolean isVertical,
                                               boolean isEditable,
                                               boolean isEnabled,
                                               String tooltip) {

      super(pc, myOwner, "", isVertical, false, hasEmptyBorder, tooltip);

      boolean rightAllignment = false;
      boolean comboEnabled = true;

      Color bkgCol = new Color(245, 245, 245);

      if (pc != null) {

         Settings settings = pc.getSettings();
         rightAllignment = settings.getSettingBoolean("XMLBasicPanel.RightAllignment");

         // check if there is a property to disable combo
         String discbo = settings.getSettingString("XMLComboPanel.DisableCombo");
         String[] hstra = XMLUtil.tokenize(discbo, " ");
         if (hstra != null) {
            for (int i = 0; i < hstra.length; i++) {
               if (hstra[i].equals(myOwner.toName())) {
                  comboEnabled = false;
                  break;
               }
            }
         }

         textDim = new Dimension(settings.getSettingInt("SimplePanelTextWidth"), settings.getSettingInt("SimplePanelTextHeight"));
         refButDimension = new Dimension(refButDimension.width, textDim.height);
         textDim.width = textDim.width - refButDimension.width;

         if (settings instanceof PanelSettings) {
            bkgCol = ((PanelSettings) settings).getBackgroundColor();
         }

         jl = new JLabel(pc.getLabelGenerator().getLabel(myOwner) + ": ");
      } else {
         jl = new JLabel(ResourceManager.getLanguageDependentString(myOwner.toName() + "Key") + ": ");
      }
      UIManager.put("ComboBox.background", new javax.swing.plaf.ColorUIResource(bkgCol));
      jl.setAlignmentX(Component.LEFT_ALIGNMENT);
      jl.setAlignmentY(Component.BOTTOM_ALIGNMENT);
      if (rightAllignment) {
         jl.setHorizontalAlignment(SwingConstants.RIGHT);
      } else {
         jl.setHorizontalAlignment(SwingConstants.LEFT);
      }

      // jl.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
      Dimension toSet;
      List chs = null;
      Object chsn = null;
      XMLElement choosen = myOwner.getChoosen();
      chs = PanelUtilities.toXMLElementViewList(pc, myOwner.getChoices(), true);
      if (choosen instanceof XMLComplexElement) {
         chsn = new XMLElementView(pc, choosen, XMLElementView.TONAME);
      } else {
         chsn = new XMLElementView(pc, choosen, XMLElementView.TOVALUE);
      }

      jcb = new JComboBox(XMLComboPanelWithReferenceLink.sortComboEntries(chs));
      jcb.setRenderer(new TooltipComboRenderer());
      // jcb.setMinimumSize(new Dimension(400,50));
      if (chsn != null) {
         jcb.setSelectedItem(chsn);
      }
      toSet = getComboDimension(chs, true);
      jcb.setEditable(isEditable);

      jcb.setAlignmentX(Component.LEFT_ALIGNMENT);
      jcb.setAlignmentY(Component.BOTTOM_ALIGNMENT);
      jcb.setMinimumSize(new Dimension(toSet));
      jcb.setMaximumSize(new Dimension(toSet));
      jcb.setPreferredSize(new Dimension(toSet));

      jcb.setEnabled(isEnabled && comboEnabled);
      jcb.setBackground(bkgCol);
      if (isEditable) {
         jcb.getEditor().getEditorComponent().setBackground(bkgCol);
         ((JTextField) jcb.getEditor().getEditorComponent()).setOpaque(true);
      }

      final XMLPanel p = this;

      jcb.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent ae) {
            Object sel = getSelectedItem();
            if (!(sel instanceof ToolAgentElementBase) || (((ToolAgentElementBase) sel).size() == 0 && !(sel instanceof LDAPOrUserGroupToolAgentElement))) {
               jb.setEnabled(false);
            } else {
               jb.setEnabled(true);
            }
            updateApplication(((XMLElement) sel).toName());
         }
      });
      if (isEditable) {
         jcb.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
               Object sel = getSelectedItem();
               if (!(sel instanceof ToolAgentElementBase) || (((ToolAgentElementBase) sel).size() == 0 && !(sel instanceof LDAPOrUserGroupToolAgentElement))) {
                  jb.setEnabled(false);
               } else {
                  jb.setEnabled(true);
               }
               if (getPanelContainer() == null)
                  return;
               if (PanelUtilities.isModifyingEvent(e)) {
                  getPanelContainer().panelChanged(p, e);
               }
            }
         });
      }

      if (rightAllignment) {
         add(Box.createHorizontalGlue());
      }
      add(jl);
      if (!rightAllignment) {
         add(Box.createHorizontalGlue());

      }
      add(jcb);

      if (pc != null) {
         jb = new JButton(((PanelSettings) pc.getSettings()).getArrowRightImageIcon());
      } else {
         jb = new JButton("->");
      }
      jb.setBorderPainted(false);
      jb.setAlignmentX(Component.LEFT_ALIGNMENT);
      jb.setAlignmentY(Component.BOTTOM_ALIGNMENT);
      jb.setMinimumSize(new Dimension(refButDimension));
      jb.setMaximumSize(new Dimension(refButDimension));
      jb.setPreferredSize(new Dimension(refButDimension));
      jb.setRolloverEnabled(true);
      jb.setContentAreaFilled(false);
      jb.setEnabled(!(choosen instanceof XMLEmptyChoiceElement)
                    && (((ToolAgentElementBase) choosen).size() > 0 || choosen instanceof LDAPOrUserGroupToolAgentElement));

      jb.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent ae) {
            String toShow = ((XMLElement) getSelectedItem()).toName();
            XMLElement taElement = ((SharkPanelGenerator) ((InlinePanel) getPanelContainer()).getPanelGenerator()).getToolAgentElement(XMLUtil.getApplication(((SpecialChoiceElement) getOwner()).getControlledElement()),
                                                                                                                                       toShow);
            ((InlinePanel) getPanelContainer()).getJaWEComponent().setUpdateInProgress(true);
            StandardXPDLElementEditor ed = new StandardXPDLElementEditor(true);
            ed.editXPDLElement(taElement);
            ((InlinePanel) getPanelContainer()).getJaWEComponent().setUpdateInProgress(false);

         }

      });
      add(jb);

   }

   protected void updateApplication(String taElement) {
      String toShow = ((XMLElement) getSelectedItem()).toName();
      Application app = (Application) JaWEManager.getInstance().getJaWEController().getJaWETypes().getTemplateElement(toShow);
      if (app != null) {
         List<ExtendedAttribute> ealist = app.getExtendedAttributes().toElements();
         Application orig = XMLUtil.getApplication(((SpecialChoiceElement) getOwner()).getControlledElement());
         ExtendedAttributes eas = orig.getExtendedAttributes();
         Iterator it = eas.toElements().iterator();
         JaWEController jc = JaWEManager.getInstance().getJaWEController();
         jc.startUndouableChange();
         ((InlinePanel) getPanelContainer()).getJaWEComponent().setUpdateInProgress(true);
         List<String> toRemoveNames = new ArrayList<String>();
         while (it.hasNext()) {
            ExtendedAttribute ea = (ExtendedAttribute) it.next();
            if (!(ea.getName().equals(SharkConstants.EA_TOOL_AGENT_CLASS) || ea.getName().equals(SharkConstants.EA_TOOL_AGENT_CLASS_PROXY))) {
               boolean toRemove = true;
               for (int i = 0; i < ealist.size(); i++) {
                  ExtendedAttribute eapr = ealist.get(i);
                  if (eapr.getName().equals(ea.getName())) {
                     ea.setVValue(eapr.getVValue());
                     toRemove = false;
                     break;
                  }
               }
               if (toRemove) {
                  toRemoveNames.add(ea.getName());
               }
            } else if (ea.getName().equals(SharkConstants.EA_TOOL_AGENT_CLASS_PROXY)) {
               ea.setVValue(toShow);
            }
         }
         for (int i = 0; i < toRemoveNames.size(); i++) {
            String ean = toRemoveNames.get(i);
            XMLElement toRem = null;
            while ((toRem = eas.getFirstExtendedAttributeForName(ean)) != null) {
               eas.remove(toRem);
            }
         }
         for (int i = 0; i < ealist.size(); i++) {
            ExtendedAttribute eapr = ealist.get(i);
            if (eas.getFirstExtendedAttributeForName(eapr.getName()) == null) {
               ExtendedAttribute ea = (ExtendedAttribute) eas.generateNewElement();
               ea.setName(eapr.getName());
               ea.setVValue(eapr.getVValue());
               eas.add(ea);
            }
         }

         orig.getApplicationTypes().getFormalParameters().makeAs(app.getApplicationTypes().getFormalParameters());
         List toSelect = new ArrayList();
         toSelect.add(orig);
         jc.endUndouableChange(toSelect);
         ((InlinePanel) getPanelContainer()).getJaWEComponent().setUpdateInProgress(false);
      }
   }

   public boolean validateEntry() {
      String siv = "";
      Object selItem = getSelectedItem();
      if (selItem != null) {
         // System.err.println("SI="+selItem+", class="+selItem.getClass().getName());
         if (selItem instanceof XMLElement) {
            if (selItem instanceof XMLSimpleElement || selItem instanceof XMLAttribute || selItem instanceof XMLEmptyChoiceElement) {
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
      if ((selItem == null || siv.trim().equals("")) && getOwner().isRequired() && !getOwner().isReadOnly()) {
         int cs = ((SpecialChoiceElement) myOwner).getChoices().size();
         Object ec = null;
         if (cs == 1) {
            ec = ((SpecialChoiceElement) myOwner).getChoices().get(0);
         }
         // System.err.println("CCCCSSSS="+cs+", ec="+ec);
         if (!(cs == 0 || (cs == 1 && ec instanceof XMLEmptyChoiceElement))) {
            XMLBasicPanel.defaultErrorMessage(this.getWindow(), jl.getText());
            jcb.requestFocus();
            return false;
         }
      }
      return true;
   }

   public void setElements() {
      if (!getOwner().isReadOnly()) {
         Object sel = getSelectedItem();
         if (myOwner instanceof XMLComplexChoice && sel instanceof XMLElement) {
            ((XMLComplexChoice) getOwner()).setChoosen((XMLElement) sel);
         } else {
            myOwner.setValue(((String) sel).trim());
         }
      }
   }

   public JComboBox getComboBox() {
      return jcb;
   }

   public Object getSelectedItem() {
      try {
         Object el = null;
         if (jcb.isEditable()) {
            el = jcb.getEditor().getItem();
         } else {
            el = jcb.getSelectedItem();
         }
         if (el instanceof XMLElementView) {
            XMLElementView ev = (XMLElementView) getComboBox().getSelectedItem();
            if (ev != null) {
               if (ev.getElement() != null) {
                  return ev.getElement();
               }
               return ev.getElementString();

            }

         } else {
            if (el != null) {
               return el.toString();
            }
         }
         return "";
      } catch (Exception ex) {
         ex.printStackTrace();
         return "";
      }
   }

   public Object getValue() {
      return getSelectedItem();
   }

   public void cleanup() {
   }

   public Dimension getComboDimension(List choices, boolean useDefaultDimension) {
      double w = 0;
      if (!useDefaultDimension && choices != null) {
         double longest = 0;
         for (int i = 0; i < choices.size(); i++) {
            try {
               w = getFontMetrics(getFont()).stringWidth(choices.get(i).toString());
               if (w > longest)
                  longest = w;
            } catch (Exception ex) {
            }
         }

         w = longest + 25;
      }
      if (w < textDim.width)
         w = textDim.width;
      return new Dimension((int) w, textDim.height);
   }

   public static Vector sortComboEntries(List ces) {
      Collections.sort(ces, new ComboEntryComparator());
      return new Vector(ces);
   }

   private static class ComboEntryComparator implements Comparator {
      public int compare(Object o1, Object o2) {
         String p1 = o1.toString();
         String p2 = o2.toString();

         return p1.compareTo(p2);
      }
   }

   public void requestFocus() {
      jcb.requestFocus();
   }

}
