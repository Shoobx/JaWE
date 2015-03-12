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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.enhydra.jawe.ResourceManager;
import org.enhydra.jawe.Settings;
import org.enhydra.jawe.base.panel.InlinePanel;
import org.enhydra.jawe.base.panel.PanelContainer;
import org.enhydra.jawe.base.panel.PanelSettings;
import org.enhydra.jxpdl.XMLElement;

/**
 * Creates panel with JLabel, JTextField and JButton.
 * 
 * @author Sasa Bojanic
 */
public class XMLTextPanelWithReferenceLink extends XMLBasicPanel {

   private static Dimension refButDimension = new Dimension(25, 20);

   protected JTextField jtf;

   protected JButton jb;

   protected JLabel jl;

   public XMLTextPanelWithReferenceLink(PanelContainer pc,
                                        XMLElement myOwnerL,
                                        final XMLElement toReference,
                                        String label,
                                        boolean isVertical,
                                        boolean isEnabled,
                                        String tooltip) {

      super(pc, myOwnerL, "", isVertical, false, true, tooltip);

      boolean rightAllignment = false;
      Dimension textDim = new Dimension(375, 20);

      Color bkgCol = new Color(245, 245, 245);

      if (pc != null) {
         Settings settings = pc.getSettings();

         rightAllignment = settings.getSettingBoolean("XMLBasicPanel.RightAllignment");

         textDim = new Dimension(settings.getSettingInt("SimplePanelTextWidth"),
                                 settings.getSettingInt("SimplePanelTextHeight"));

         textDim.setSize(textDim.width - refButDimension.width, textDim.height);
         if (settings instanceof PanelSettings) {
            bkgCol = ((PanelSettings) settings).getBackgroundColor();
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

      jtf = new JTextField();
      jtf.setText(myOwner.toValue());
      jtf.setAlignmentX(Component.LEFT_ALIGNMENT);
      jtf.setAlignmentY(Component.BOTTOM_ALIGNMENT);
      jtf.setMinimumSize(new Dimension(textDim));
      jtf.setMaximumSize(new Dimension(textDim));
      jtf.setPreferredSize(new Dimension(textDim));

      jtf.setEnabled(isEnabled);

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
         public void keyPressed(KeyEvent e) {
            if (getPanelContainer() == null)
               return;
            if (PanelUtilities.isModifyingEvent(e)) {
               getPanelContainer().panelChanged(p, e);
            }
         }
      });

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
      jb.setEnabled(toReference != null);

      jb.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent ae) {
            ((InlinePanel) getPanelContainer()).displayGivenElement(toReference);
         }

      });
      add(jb);
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

   public Object getValue() {
      return getText();
   }

   public void requestFocus() {
      jtf.requestFocus();
   }
}
