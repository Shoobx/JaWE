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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.Settings;
import org.enhydra.jawe.base.panel.PanelContainer;
import org.enhydra.jawe.base.panel.PanelSettings;
import org.enhydra.jawe.base.panel.panels.PanelUtilities;
import org.enhydra.jawe.base.panel.panels.TooltipComboRenderer;
import org.enhydra.jawe.base.panel.panels.XMLBasicPanel;
import org.enhydra.jawe.base.panel.panels.XMLComboPanel;
import org.enhydra.jawe.base.panel.panels.XMLElementView;
import org.enhydra.jawe.base.panel.panels.XMLPanel;
import org.enhydra.jawe.shark.business.SharkConstants;
import org.enhydra.jxpdl.XMLCollectionElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.elements.BasicType;
import org.enhydra.jxpdl.elements.DataField;
import org.enhydra.jxpdl.elements.ExtendedAttribute;
import org.enhydra.jxpdl.elements.FormalParameter;
import org.enhydra.jxpdl.elements.SchemaType;
import org.enhydra.jxpdl.elements.WorkflowProcess;

/**
 * Creates a panel for ext. attribs with a checkbox which represents the name of extended
 * attribute.
 * 
 * @author Sasa Bojanic
 */
public class ExtAttribPanel extends XMLBasicPanel {

   protected JCheckBox jchkb;

   Dimension textDim = new Dimension(400, 20);

   protected XMLPanel prevPanel = null;

   protected JScrollPane jsp;

   protected JComboBox jcb;

   protected JComboBox jcbch;

   protected JLabel jlch;

   protected JLabel jl;

   protected List allVars;

   protected Color bkgCol;

   public ExtAttribPanel(PanelContainer pc,
                         ExtendedAttribute myOwner,
                         List choices,
                         List allVars,
                         boolean hasEmptyBorder,
                         boolean isVertical,
                         boolean isEnabled,
                         boolean isChoiceEnabled,
                         String tooltip) {

      super(pc, myOwner, pc.getLanguageDependentString("VariableKey"), isVertical, false, hasEmptyBorder, tooltip);

      bkgCol = new Color(245, 245, 245);
      if (pc != null && pc.getSettings() instanceof PanelSettings) {
         bkgCol = ((PanelSettings) pc.getSettings()).getBackgroundColor();
      }
      UIManager.put("ComboBox.background", new javax.swing.plaf.ColorUIResource(bkgCol));

      this.allVars = allVars;
      boolean rightAllignment = false;
      if (pc != null) {

         Settings settings = pc.getSettings();
         rightAllignment = settings.getSettingBoolean("XMLBasicPanel.RightAllignment");

         textDim = new Dimension(settings.getSettingInt("SimplePanelTextWidth"), settings.getSettingInt("SimplePanelTextHeight"));
      }

      boolean isFetch = myOwner.getName().equals(SharkConstants.EA_VTP_FETCH);
      if (isChoiceEnabled) {
         Dimension toSet;
         List chs = null;
         Object chsn = null;
         String vid = myOwner.getVValue();
         if (isFetch) {
            vid = vid.substring(0, vid.indexOf(";"));
         }
         XMLCollectionElement che = null;
         for (int i = 0; i < choices.size(); i++) {
            Object el = choices.get(i);
            if (el instanceof XMLCollectionElement) {
               XMLCollectionElement cel = (XMLCollectionElement) choices.get(i);
               if (cel.getId().equals(vid)) {
                  chsn = new XMLElementView(pc, cel, XMLElementView.TONAME);
                  che = cel;
               }
            } else {
               chsn = new XMLElementView(pc, vid, false);
            }
         }
         chs = PanelUtilities.toXMLElementViewList(pc, choices, true);

         jcb = new JComboBox(XMLComboPanel.sortComboEntries(chs));
         jcb.setRenderer(new TooltipComboRenderer());

         jcb.getEditor().getEditorComponent().setBackground(bkgCol);
         ((JTextField) jcb.getEditor().getEditorComponent()).setOpaque(true);

         if (chsn != null) {
            jcb.setSelectedItem(chsn);
         }
         toSet = getComboDimension(chs, true);
         jcb.setEditable(true);

         jcb.setAlignmentX(Component.LEFT_ALIGNMENT);
         jcb.setAlignmentY(Component.BOTTOM_ALIGNMENT);

         jcb.setMinimumSize(new Dimension(toSet));
         jcb.setMaximumSize(new Dimension(toSet));
         jcb.setPreferredSize(new Dimension(toSet));

         jcb.setEnabled(isEnabled);

         jsp = new JScrollPane();
         jsp.setAlignmentX(Component.LEFT_ALIGNMENT);
         jsp.setAlignmentY(Component.TOP_ALIGNMENT);
         jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
         jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

         setFetchChoicesCB(che, true, isEnabled);

         jcb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
               // if (prevPanel!=null) {
               // prevPanel.setElements();
               // }
               XMLCollectionElement chosen = null;
               Object el = null;
               if (jcb.isEditable()) {
                  el = jcb.getEditor().getItem();
               } else {
                  el = jcb.getSelectedItem();
               }
               if (el instanceof XMLElementView) {
                  XMLElementView ev = (XMLElementView) getComboBox().getSelectedItem();
                  if (ev != null) {
                     if (ev.getElement() instanceof XMLCollectionElement) {
                        chosen = (XMLCollectionElement) ev.getElement();
                     }
                  }

               }
               setFetchChoicesCB(chosen, false, true);
               jsp.setViewportView(jcbch);
            }
         });

         final XMLPanel cp = this;
         jcb.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
               if (getPanelContainer() == null)
                  return;
               getPanelContainer().panelChanged(cp, e);
            }

         });

         jcb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               if (getPanelContainer() == null)
                  return;
               getPanelContainer().panelChanged(cp, e);
            }
         });

         jcb.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
               if (getPanelContainer() == null)
                  return;
               if (PanelUtilities.isModifyingEvent(e)) {
                  getPanelContainer().panelChanged(cp, e);
               }
            }
         });

      } else {
         WorkflowProcess wp = XMLUtil.getWorkflowProcess(myOwner);
         String vid = myOwner.getVValue();
         String fvid = null;
         if (isFetch) {
            fvid = vid.substring(vid.indexOf(";") + 1);
            vid = vid.substring(0, vid.indexOf(";"));
         }
         XMLCollectionElement cel = wp.getDataField(vid);
         if (cel == null) {
            cel = wp.getFormalParameter(vid);
         }
         XMLCollectionElement celf = null;
         if (isFetch) {
            celf = wp.getDataField(fvid);
            if (celf == null) {
               celf = wp.getFormalParameter(fvid);
            }
         }

         String dn = "";
         if (cel != null) {
            dn = JaWEManager.getInstance().getDisplayNameGenerator().getDisplayName(cel);
         }
         jl = new JLabel(dn);
         jl.setAlignmentX(Component.LEFT_ALIGNMENT);
         jl.setAlignmentY(Component.BOTTOM_ALIGNMENT);
         if (rightAllignment) {
            jl.setHorizontalAlignment(SwingConstants.RIGHT);
         } else {
            jl.setHorizontalAlignment(SwingConstants.LEFT);
         }
         if (isFetch) {
            dn = "";
            if (celf != null) {
               dn = JaWEManager.getInstance().getDisplayNameGenerator().getDisplayName(celf);
            }
            jlch = new JLabel(dn);
            jlch.setAlignmentX(Component.LEFT_ALIGNMENT);
            jlch.setAlignmentY(Component.BOTTOM_ALIGNMENT);
            if (rightAllignment) {
               jlch.setHorizontalAlignment(SwingConstants.RIGHT);
            } else {
               jlch.setHorizontalAlignment(SwingConstants.LEFT);
            }
         }

      }
      UIManager.put("CheckBox.interiorBackground", bkgCol);
      jchkb = new JCheckBox(pc.getLanguageDependentString("ReadOnlyKey") + ":");
      jchkb.setBorder(BorderFactory.createEmptyBorder());
      jchkb.setSelected(new Boolean(myOwner.getName().equals(SharkConstants.EA_VTP_VIEW)).booleanValue());
      jchkb.setAlignmentX(Component.LEFT_ALIGNMENT);
      jchkb.setAlignmentY(Component.BOTTOM_ALIGNMENT);
      jchkb.setHorizontalTextPosition(SwingConstants.LEFT);
      jchkb.setVerticalTextPosition(SwingConstants.BOTTOM);
      // jchkb.setMinimumSize(new Dimension(textDim));
      // jchkb.setMaximumSize(new Dimension(textDim));
      // jchkb.setPreferredSize(new Dimension(textDim));

      jchkb.setEnabled(isEnabled);

      final XMLPanel p = this;
      jchkb.addItemListener(new ItemListener() {

         public void itemStateChanged(ItemEvent e) {
            if (getPanelContainer() == null)
               return;
            getPanelContainer().panelChanged(p, e);
         }

      });

      if (rightAllignment) {
         add(Box.createHorizontalGlue());
      }

      JLabel jldn = new JLabel("Name: ");
      jldn.setAlignmentX(Component.LEFT_ALIGNMENT);
      jldn.setAlignmentY(Component.BOTTOM_ALIGNMENT);
      if (rightAllignment) {
         jldn.setHorizontalAlignment(SwingConstants.RIGHT);
      } else {
         jldn.setHorizontalAlignment(SwingConstants.LEFT);
      }
      JLabel jldf = new JLabel("Fetch from: ");
      jldf.setAlignmentX(Component.LEFT_ALIGNMENT);
      jldf.setAlignmentY(Component.BOTTOM_ALIGNMENT);
      if (rightAllignment) {
         jldf.setHorizontalAlignment(SwingConstants.RIGHT);
      } else {
         jldf.setHorizontalAlignment(SwingConstants.LEFT);
      }

      if (jcb != null) {
         add(jldn);
         add(jcb);
         add(Box.createHorizontalStrut(15));
         add(jldf);
         add(jsp);
         add(Box.createHorizontalStrut(15));
      } else {
         add(jldn);
         add(jl);
         add(Box.createHorizontalStrut(15));
         add(jldf);
         add(jlch);
         add(Box.createHorizontalStrut(15));
      }
      if (!rightAllignment) {
         add(Box.createHorizontalGlue());
      }
      add(jchkb);
      add(Box.createHorizontalStrut(15));

   }

   public boolean validateEntry() {
      if (jcb != null) {
         String siv = "";
         Object selItem = getSelectedItem();
         if (selItem != null) {
            // System.err.println("SI="+selItem+", class="+selItem.getClass().getName());
            if (selItem instanceof XMLCollectionElement) {
               siv = ((XMLCollectionElement) selItem).getId();
            } else {
               siv = selItem.toString();
            }
         }
         if ((selItem == null || siv.trim().equals("")) && !getOwner().isReadOnly()) {

            XMLBasicPanel.defaultErrorMessage(this.getWindow(), "");
            jcb.requestFocus();
            return false;
         }
      }
      return true;
   }

   public void setElements() {
      if (!getOwner().isReadOnly()) {
         ExtendedAttribute ea = (ExtendedAttribute) myOwner;
         ea.setName((String) getCHKBValue());
         if (jcb != null) {
            Object sel = getSelectedItem();
            String val = null;
            if (sel instanceof XMLCollectionElement) {
               val = ((XMLCollectionElement) sel).getId();
            } else {
               val = ((String) sel).trim();
            }
            if (ea.getName().equals(SharkConstants.EA_VTP_FETCH)) {
               val += ";";
               sel = getSelectedFetchChoiceItem();
               if (sel instanceof XMLCollectionElement) {
                  val += ((XMLCollectionElement) sel).getId();
               } else {
                  val += ((String) sel).trim();
               }
            }
            ea.setVValue(val);
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

   public Object getSelectedFetchChoiceItem() {
      try {
         Object el = null;
         if (jcbch.isEditable()) {
            el = jcbch.getEditor().getItem();
         } else {
            el = jcbch.getSelectedItem();
         }
         if (el instanceof XMLElementView) {
            XMLElementView ev = (XMLElementView) jcbch.getSelectedItem();
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
      if (jcb != null) {
         return getSelectedItem();
      }
      return jl.getText();
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

   public boolean getCheckboxStatus() {
      return (jchkb.isSelected());
   }

   public Object getCHKBValue() {
      if (jcbch != null && !jcbch.getSelectedItem().toString().trim().equals(""))
         return SharkConstants.EA_VTP_FETCH;
      if (jchkb.isSelected()) {
         return SharkConstants.EA_VTP_VIEW;
      }
      return SharkConstants.EA_VTP_UPDATE;
   }

   private void setFetchChoicesCB(XMLCollectionElement selel, boolean initialization, boolean isEnabled) {
      List chs = null;
      Object chsn = null;
      XMLElement dt = null;
      boolean isArray = false;
      List fc = new ArrayList();
      if (selel != null) {
         if (selel instanceof DataField) {
            dt = ((DataField) selel).getDataType().getDataTypes().getChoosen();
            isArray = ((DataField) selel).getIsArray();
         } else if (selel instanceof FormalParameter) {
            dt = ((FormalParameter) selel).getDataType().getDataTypes().getChoosen();
            isArray = ((FormalParameter) selel).getIsArray();
         }
         fc = getFilteredChoices(allVars, dt, isArray);
         if (initialization) {
            String eav = ((ExtendedAttribute) myOwner).getVValue();
            if (eav.indexOf(";") != -1) {
               String fvid = eav.substring(eav.indexOf(";") + 1);
               for (int i = 0; i < fc.size(); i++) {
                  Object el = fc.get(i);
                  if (el instanceof XMLCollectionElement) {
                     XMLCollectionElement cel = (XMLCollectionElement) fc.get(i);
                     if (cel.getId().equals(fvid)) {
                        chsn = new XMLElementView(pc, cel, XMLElementView.TONAME);
                     }
                  } else {
                     chsn = new XMLElementView(pc, selel.getId(), false);
                  }
               }
            }
         }
      }
      DataField df = new DataField(null);
      df.setId(" ");
      df.getDataType().getDataTypes().setBasicType();
      df.getDataType().getDataTypes().getBasicType().setTypeSTRING();
      fc.add(0, df);

      chs = PanelUtilities.toXMLElementViewList(pc, fc, true);

      jcbch = new JComboBox(XMLComboPanel.sortComboEntries(chs));
      jcbch.setRenderer(new TooltipComboRenderer());
      if (chsn != null) {
         jcbch.setSelectedItem(chsn);
      }
      Dimension toSet = getComboDimension(chs, true);

      jcbch.setEditable(true);

      // jcbch.setAlignmentX(Component.LEFT_ALIGNMENT);
      // jcbch.setAlignmentY(Component.BOTTOM_ALIGNMENT);
      // jcbch.setMinimumSize(new Dimension(toSet));
      // jcbch.setMaximumSize(new Dimension(toSet));
      // jcbch.setPreferredSize(new Dimension(toSet));

      jcbch.setEnabled(isEnabled);
      jcbch.getEditor().getEditorComponent().setBackground(bkgCol);
      ((JTextField) jcbch.getEditor().getEditorComponent()).setOpaque(true);

      jsp.setAlignmentX(Component.LEFT_ALIGNMENT);
      jsp.setAlignmentY(Component.BOTTOM_ALIGNMENT);
      jsp.setMinimumSize(new Dimension(toSet));
      jsp.setMaximumSize(new Dimension(toSet));
      jsp.setPreferredSize(new Dimension(toSet));
      jsp.setViewportView(jcbch);

      final XMLPanel cp = this;
      jcbch.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent e) {
            if (getPanelContainer() == null)
               return;
            getPanelContainer().panelChanged(cp, e);
         }

      });

      jcbch.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (getPanelContainer() == null)
               return;
            getPanelContainer().panelChanged(cp, e);
         }
      });

      jcbch.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
         public void keyPressed(KeyEvent e) {
            if (getPanelContainer() == null)
               return;
            if (PanelUtilities.isModifyingEvent(e)) {
               getPanelContainer().panelChanged(cp, e);
            }
         }
      });

   }

   protected List getFilteredChoices(List vars, XMLElement dt, boolean isArray) {
      List filter = null;
      if (!isArray) {
         if (dt instanceof BasicType) {
            filter = Arrays.asList(new String[] {
               ((BasicType) dt).getType() + "[]"
            });

         } else if (dt instanceof SchemaType) {
            filter = Arrays.asList(new String[] {
               XMLUtil.getShortClassName(SchemaType.class.getName()) + "[]"
            });
         }
      }
      if (filter != null) {
         return PanelUtilities.getPossibleVariableChoices(vars, filter, 2, false);
      }
      return new ArrayList();
   }

}
