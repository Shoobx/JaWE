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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.enhydra.jawe.ResourceManager;
import org.enhydra.jawe.Settings;
import org.enhydra.jawe.base.panel.PanelContainer;
import org.enhydra.jawe.base.panel.PanelSettings;
import org.enhydra.jxpdl.XMLElement;

/**
 * Creates panel with JLabel and JTextField.
 * 
 * @author Sasa Bojanic
 */
public class XMLTextPanel extends XMLBasicPanel {

   protected JLabel jl;

   protected JTextField jtf;

   public XMLTextPanel(PanelContainer pc, XMLElement myOwnerL, boolean isVertical, boolean isPasswordField, boolean isEnabled) {

      this(pc, myOwnerL, null, isVertical, isPasswordField, false, isEnabled, null, null);
   }

   public XMLTextPanel(PanelContainer pc,
                       XMLElement myOwnerL,
                       String label,
                       boolean isVertical,
                       boolean isPasswordField,
                       final boolean isNumberField,
                       boolean isEnabled,
                       Dimension textDim,
                       String tooltip) {

      super(pc, myOwnerL, "", isVertical, false, true, tooltip);

      boolean rightAllignment = false;

      Color bkgCol = new Color(245, 245, 245);

      if (pc != null) {
         Settings settings = pc.getSettings();

         rightAllignment = settings.getSettingBoolean("XMLBasicPanel.RightAllignment");

         if (textDim == null) {
            textDim = new Dimension(settings.getSettingInt("SimplePanelTextWidth"), settings.getSettingInt("SimplePanelTextHeight"));
         }

         if (settings instanceof PanelSettings) {
            bkgCol = ((PanelSettings) settings).getBackgroundColor();
         }
      } else {
         if (textDim == null) {
            textDim = new Dimension(400, 20);
         }
      }
      if (label == null) {
         if (pc != null) {
            label = pc.getLabelGenerator().getLabel(myOwner);
         } else {
            label = ResourceManager.getLanguageDependentString(myOwner.toName() + "Key");
         }
      }
      jl = new JLabel(label + ": ");
      jl.setAlignmentX(Component.LEFT_ALIGNMENT);
      jl.setAlignmentY(Component.BOTTOM_ALIGNMENT);
      if (rightAllignment) {
         jl.setHorizontalAlignment(SwingConstants.RIGHT);
      } else {
         jl.setHorizontalAlignment(SwingConstants.LEFT);
      }

      if (!isPasswordField) {
         jtf = new JTextField();
      } else {
         jtf = new JPasswordField();
      }
      jtf.setText(myOwner.toValue());
      jtf.setAlignmentX(Component.LEFT_ALIGNMENT);
      jtf.setAlignmentY(Component.BOTTOM_ALIGNMENT);
      jtf.setMinimumSize(new Dimension(textDim));
      jtf.setMaximumSize(new Dimension(textDim));
      jtf.setPreferredSize(new Dimension(textDim));

      if (!isEnabled) {
         jtf.setEditable(false);
         jtf.setForeground(jtf.getDisabledTextColor());
      }

      jtf.setBackground(bkgCol);

      if (rightAllignment && !isVertical) {
         add(Box.createHorizontalGlue());
      }
      add(jl);
      if (!rightAllignment && !isVertical) {
         // add(Box.createRigidArea(new Dimension(200-jl.getPreferredSize().width,1)));
         add(Box.createHorizontalGlue());
      }
      add(jtf);

      final XMLPanel p = this;
      // add key listener
      jtf.addKeyListener(new KeyAdapter() {
         public void keyTyped(KeyEvent e) {
            if (isNumberField) {
               if (!(Character.isDigit(e.getKeyChar())
                     || e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT
                     || e.getKeyCode() == KeyEvent.VK_ESCAPE || e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_END
                     || e.getKeyCode() == KeyEvent.VK_HOME || e.getKeyCode() == KeyEvent.VK_DELETE)) {
                  e.consume();
               }
            }
            super.keyTyped(e);
         }

         public void keyPressed(KeyEvent e) {
            if (isNumberField) {
               if (!(Character.isDigit(e.getKeyChar())
                     || e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT
                     || e.getKeyCode() == KeyEvent.VK_ESCAPE || e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_END
                     || e.getKeyCode() == KeyEvent.VK_HOME || e.getKeyCode() == KeyEvent.VK_DELETE)) {
                  e.consume();
                  return;
               }
            }
            if (getPanelContainer() == null)
               return;
            if (PanelUtilities.isModifyingEvent(e)) {
               getPanelContainer().panelChanged(p, e);
            }
         }
      });
   }

   public boolean validateEntry() {
      if (isEmpty() && getOwner().isRequired() && !getOwner().isReadOnly()) {

         XMLBasicPanel.defaultErrorMessage(this.getWindow(), jl.getText());
         jtf.requestFocus();
         return false;
      }
      return true;
   }

   public boolean isEmpty() {
      return getText().trim().equals("");
   }

   public void setElements() {
      if (!getOwner().isReadOnly()) {
         getOwner().setValue(getText().trim());
      }
   }

   public String getText() {
      return jtf.getText();
   }

   public void setText(String text) {
      jtf.setText(text);
   }

   public Object getValue() {
      return getText();
   }

   public void requestFocus() {
      jtf.requestFocus();
   }

}
