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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.enhydra.jawe.ResourceManager;
import org.enhydra.jawe.Settings;
import org.enhydra.jawe.base.panel.PanelContainer;
import org.enhydra.jawe.base.panel.PanelSettings;
import org.enhydra.jxpdl.XMLAttribute;
import org.enhydra.jxpdl.XMLCollectionElement;
import org.enhydra.jxpdl.XMLComplexChoice;
import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLEmptyChoiceElement;
import org.enhydra.jxpdl.XMLSimpleElement;
import org.enhydra.jxpdl.XMLUtil;

/**
 * Creates panel with JLabel and JComboBox.
 * 
 * @author Sasa Bojanic
 * @author Zoran Milakovic
 */
public class XMLComboPanel extends XMLBasicPanel {

   Dimension textDim = new Dimension(400, 20);

   protected JComboBox jcb;

   protected JLabel jl;

   public XMLComboPanel(PanelContainer pc,
                        XMLElement myOwner,
                        List choices,
                        boolean ldStrChoices,
                        boolean hasEmptyBorder,
                        boolean isVertical,
                        boolean isEditable,
                        boolean isEnabled) {
      this(pc,
           myOwner,
           null,
           choices,
           ldStrChoices,
           hasEmptyBorder,
           isVertical,
           isEditable,
           isEnabled,
           true,
           true,
           null);
   }

   public XMLComboPanel(PanelContainer pc,
                        XMLElement myOwner,
                        String title,
                        List choices,
                        boolean ldStrChoices,
                        boolean hasEmptyBorder,
                        boolean isVertical,
                        boolean isEditable,
                        boolean isEnabled,
                        boolean performSorting,
                        boolean adjustDimension,
                        String tooltip) {

      super(pc, myOwner, "", isVertical, false, hasEmptyBorder, tooltip);

      boolean rightAllignment = false;
      boolean comboEnabled = true;

      Color bkgCol = new Color(245, 245, 245);
      if (pc != null) {

         Settings settings = pc.getSettings();
         rightAllignment = settings.getSettingBoolean("XMLBasicPanel.RightAllignment");

         // check if there is a property to disable combo
         if (!(myOwner instanceof XMLAttribute)) {
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
         }

         textDim = new Dimension(settings.getSettingInt("SimplePanelTextWidth"),
                                 settings.getSettingInt("SimplePanelTextHeight"));
         if (title == null) {
            jl = new JLabel(pc.getLabelGenerator().getLabel(myOwner) + ": ");
         } else {
            jl = new JLabel(title + ": ");
         }

         if (settings instanceof PanelSettings) {
            bkgCol = ((PanelSettings) settings).getBackgroundColor();
         }

      } else {
         if (title == null) {
            jl = new JLabel(ResourceManager.getLanguageDependentString(myOwner.toName()
                                                                       + "Key")
                            + ": ");
         } else {
            jl = new JLabel(title + ": ");
         }
      }

      jl.setAlignmentX(Component.LEFT_ALIGNMENT);
      jl.setAlignmentY(Component.BOTTOM_ALIGNMENT);
      if (rightAllignment) {
         jl.setHorizontalAlignment(SwingConstants.RIGHT);
      } else {
         jl.setHorizontalAlignment(SwingConstants.LEFT);
      }

      // jl.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
      Dimension toSet = new Dimension(textDim);
      List chs = null;
      Object chsn = null;
      boolean ldStr = true;
      if (choices != null) {
         ldStr = ldStrChoices;
      } else {
         if (myOwner instanceof XMLAttribute) {
            choices = ((XMLAttribute) myOwner).getChoices();
         } else if (myOwner instanceof XMLComplexChoice) {
            choices = ((XMLComplexChoice) myOwner).getChoices();
         } else {
            choices = new ArrayList();
         }
      }
      if (!(myOwner instanceof XMLComplexChoice)) {
         // chs=new ArrayList(((XMLAttribute)myOwner).getChoices());
         chs = PanelUtilities.toXMLElementViewList(pc, choices, ldStr);
         if (choices.size() > 0) {
            Object fc = choices.get(0);
            if (fc instanceof XMLElement) {
               if (fc instanceof XMLCollectionElement) {
                  for (int i = 0; i < choices.size(); i++) {
                     XMLCollectionElement cel = (XMLCollectionElement) choices.get(i);
                     if (cel.getId().equals(myOwner.toValue())) {
                        chsn = new XMLElementView(pc, cel, XMLElementView.TONAME);
                        break;
                     }
                  }
               }
               if (chsn == null) {
                  chsn = new XMLElementView(pc,
                                            (XMLElement) choices.get(0),
                                            XMLElementView.TONAME);
               }
            }
         }
         if (chsn == null) {
            chsn = new XMLElementView(pc, myOwner.toValue(), ldStr);
         }

      } else {
         XMLElement choosen = ((XMLComplexChoice) myOwner).getChoosen();
         chs = PanelUtilities.toXMLElementViewList(pc, choices, ldStr);
         if (choosen instanceof XMLComplexElement) {
            chsn = new XMLElementView(pc, choosen, XMLElementView.TONAME);
         } else {
            chsn = new XMLElementView(pc, choosen, XMLElementView.TOVALUE);
         }
      }
      Vector cv = null;
      if (performSorting) {
         cv = XMLComboPanel.sortComboEntries(chs);
      } else {
         cv = new Vector(chs);
      }
      jcb = new JComboBox(cv);
      jcb.setRenderer(new TooltipComboRenderer());
      // jcb.setMinimumSize(new Dimension(400,50));
      if (chsn != null) {
         jcb.setSelectedItem(chsn);
      }
      if (adjustDimension) {
         toSet = getComboDimension(chs, true);
      }
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
      }

      final XMLPanel p = this;
      jcb.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent e) {
            if (getPanelContainer() == null)
               return;
            getPanelContainer().panelChanged(p, e);
         }

      });
      if (isEditable) {
         jcb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               if (getPanelContainer() == null)
                  return;
               getPanelContainer().panelChanged(p, ae);
            }
         });
         jcb.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
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
   }

   public boolean validateEntry() {
      String siv = "";
      Object selItem = getSelectedItem();
      if (selItem != null) {
         // System.err.println("SI="+selItem+", class="+selItem.getClass().getName());
         if (selItem instanceof XMLElement) {
            if (selItem instanceof XMLSimpleElement
                || selItem instanceof XMLAttribute
                || selItem instanceof XMLEmptyChoiceElement) {
               siv = ((XMLElement) selItem).toValue();
            } else {
               siv = ((XMLElement) selItem).toName();
            }
         } else {
            siv = selItem.toString();
         }
      }
      // System.err.println("SIV="+siv);
      // System.err.println("IREQ="+getOwner().isRequired()+",
      // iro="+getOwner().isReadOnly());
      if ((selItem == null || siv.trim().equals("") || (selItem instanceof XMLCollectionElement && ((XMLCollectionElement) selItem).getId()
         .trim()
         .equals("")))
          && getOwner().isRequired() && !getOwner().isReadOnly()) {

         XMLBasicPanel.defaultErrorMessage(this.getWindow(), jl.getText());
         jcb.requestFocus();
         return false;
      }
      return true;
   }

   public void setElements() {
      if (!getOwner().isReadOnly()) {
         Object sel = getSelectedItem();
         if (myOwner instanceof XMLComplexChoice && sel instanceof XMLElement) {
            ((XMLComplexChoice) getOwner()).setChoosen((XMLElement) sel);
         } else {
            if (sel instanceof XMLElement) {
               if (sel instanceof XMLCollectionElement) {
                  myOwner.setValue(((XMLCollectionElement) sel).getId());
               } else {
                  myOwner.setValue(((XMLElement) sel).toName());
               }
            } else {
               myOwner.setValue(((String) sel).trim());
            }
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

   public boolean isEmpty() {
      Object o = getSelectedItem();
      if (o == null) {
         return true;
      }
      if (o instanceof String) {
         return ((String) o).trim().equals("");
      }
      if (o instanceof XMLCollectionElement) {
         return ((XMLCollectionElement) o).getId().trim().equals("");
      }
      return false;
   }

}
