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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.Box;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.Utils;
import org.enhydra.jawe.base.panel.PanelContainer;
import org.enhydra.jawe.base.panel.PanelGenerator;
import org.enhydra.jawe.base.panel.panels.XMLBasicPanel;
import org.enhydra.jawe.base.panel.panels.XMLComboPanel;
import org.enhydra.jawe.base.panel.panels.XMLElementView;
import org.enhydra.jawe.base.panel.panels.XMLMultiLineTextPanelWithOptionalChoiceButtons;
import org.enhydra.jawe.base.panel.panels.XMLPanel;
import org.enhydra.jawe.shark.business.SharkConstants;
import org.enhydra.jxpdl.XMLAttribute;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.elements.DeadlineDuration;

/**
 * Creates a panel for special shark's deadline duration.
 * 
 * @author Sasa Bojanic
 */
public class DeadlineDurationPanel extends XMLBasicPanel {

   protected XMLPanel prevPanel = null;

   protected XMLPanel emptyPanel;

   protected XMLComboPanel pCombo;

   public DeadlineDurationPanel(final PanelContainer pc,
                                final PanelGenerator pg,
                                final DeadlineDuration myOwner,
                                String title,
                                final boolean isEnabled,
                                String tooltip) {

      super(pc, myOwner, title, true, true, true, tooltip);

      XMLAttribute st = (XMLAttribute) myOwner.get("ScriptType");
      List<String> choices = ((SharkPanelGenerator) pg).getScriptChoices(st);
      pCombo = new XMLComboPanel(pc, st, choices, false, false, false, false, isEnabled);
      add(pCombo);

      emptyPanel = new XMLBasicPanel(pc, myOwner, "", true, false, false, tooltip);
      add(emptyPanel);

      pCombo.getComboBox().addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent ae) {
            if (prevPanel != null) {
               emptyPanel.remove(prevPanel);
            }
            String choosen = (String) pCombo.getSelectedItem();
            if (choosen == null || !choosen.equals(SharkConstants.SCRIPT_VALUE_SHARKWF_DEADLINES)) {
               int noOfLines = 8;
               try {
                  noOfLines = getPanelContainer().getSettings().getSettingInt("PreferredNumberOfLinesForExpression");
               } catch (Exception ex) {
                  System.err.println("Wrong value for parameter XMLActualParametersPanel.preferredNumberOfLinesForExpression! Using default: "
                                     + String.valueOf(noOfLines));
               }
               String ext = "txt";

               String scriptType = myOwner.getScriptType();
               if (scriptType.equals("")) {
                  scriptType = XMLUtil.getPackage(myOwner).getScript().getType();
               }
               if (!scriptType.equals("")) {
                  ext = Utils.getFileExtension(scriptType);
               }
               SharkPanelGenerator spg = (SharkPanelGenerator) getPanelContainer().getPanelGenerator();
               prevPanel = new XMLMultiLineTextPanelWithOptionalChoiceButtons(getPanelContainer(),
                                                                              myOwner,
                                                                              "Expression",
                                                                              false,
                                                                              true,
                                                                              noOfLines,
                                                                              false,
                                                                              null,
                                                                              spg.prepareExpressionChoices(myOwner),
                                                                              spg.prepareExpressionChoicesTooltips(myOwner),
                                                                              spg.prepareExpressionChoicesImages(myOwner),
                                                                              JaWEManager.getInstance().getJaWEController().canModifyElement(myOwner),
                                                                              null,
                                                                              null,
                                                                              ext);
            } else {
               prevPanel = new DeadlineExpressionPanel(pc, myOwner, "Expression", isEnabled, null);
            }
            emptyPanel.add(prevPanel);
            paintAll(getGraphics());
         }
      });
      String stv = myOwner.getScriptType();
      if (stv.equals("")) {
         stv = SharkConstants.SCRIPT_VALUE_JAVA;
      }
      pCombo.getComboBox().setSelectedItem(new XMLElementView(pc, stv, true));

      add(Box.createVerticalGlue());

      if (myOwner.getScriptType().equals("")) {
         getPanelContainer().panelChanged(this, null);
      }
   }

   public boolean validateEntry() {
      boolean isOK = pCombo.validateEntry();
      for (int i = 0; i < emptyPanel.getComponentCount(); i++) {
         XMLPanel p = (XMLPanel) emptyPanel.getComponent(i);
         isOK = isOK && p.validateEntry();
      }

      return isOK;
   }

   public void setElements() {
      if (!getOwner().isReadOnly()) {
         pCombo.setElements();
         for (int i = 0; i < emptyPanel.getComponentCount(); i++) {
            XMLPanel p = (XMLPanel) emptyPanel.getComponent(i);
            p.setElements();
         }
      }
   }

   public Object getValue() {
      return pCombo.getValue();
   }

   public void cleanup() {
      pCombo.cleanup();
      for (int i = 0; i < emptyPanel.getComponentCount(); i++) {
         XMLPanel p = (XMLPanel) emptyPanel.getComponent(i);
         p.cleanup();
      }
   }

}
