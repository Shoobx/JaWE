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
import java.awt.Container;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.ResourceManager;
import org.enhydra.jawe.base.editor.XPDLElementEditor;
import org.enhydra.jawe.base.panel.PanelContainer;
import org.enhydra.jxpdl.XMLElement;

/**
 * Basic implementation of XMLPanel interface.
 * 
 * @author Sasa Bojanic
 */
public class XMLBasicPanel extends XMLPanel {

   protected XMLElement myOwner;

   // protected XMLElement myOwnerOrginal;
   protected String title;

   protected XPDLElementEditor editor;

   protected PanelContainer pc;

   public XMLBasicPanel() {
      super();
   }

   public XPDLElementEditor getEditor() {
      return editor;
   }

   public XMLBasicPanel(PanelContainer pc,
                        XMLElement myOwnerL,
                        String title,
                        boolean isVertical,
                        boolean hasBorder,
                        boolean hasEmptyBorder,
                        String tooltip) {

      super();
      this.pc = pc;
      this.myOwner = myOwnerL;

      setBorder(title, hasBorder, hasEmptyBorder);

      setLayout(new BoxLayout(this, ((isVertical) ? BoxLayout.Y_AXIS : BoxLayout.X_AXIS)));
      setAlignmentX(Component.LEFT_ALIGNMENT);
      setAlignmentY(Component.TOP_ALIGNMENT);

      if (myOwner != null) {
         setEnabled(JaWEManager.getInstance()
            .getJaWEController()
            .canModifyElement(myOwner));
      }

      if (tooltip != null) {
         setToolTipText(tooltip);
      }
   }

   public void setBorder(String title, boolean hasBorder, boolean hasEmptyBorder) {
      this.title = title;

      // int emptyBorderHSize=4;
      // int emptyBorderVSize=4;
      //
      // Border emptyb=BorderFactory.createEmptyBorder(emptyBorderVSize,emptyBorderHSize,
      // emptyBorderVSize,emptyBorderHSize);
      // Border inb=BorderFactory.createEmptyBorder(0,0,0,0);
      // if (hasBorder) {
      // inb=BorderFactory.createMatteBorder(1,1,1,1,Color.darkGray);
      // inb=BorderFactory.createTitledBorder(inb,title);
      // }
      // else {
      // emptyb=BorderFactory.createTitledBorder(emptyb,title);
      // }
      // setBorder(BorderFactory.createCompoundBorder(emptyb,inb));

      int emptyBT = 0;
      int emptyBL = 3;
      int emptyBB = 4;
      int emptyBR = 3;
      try {
         emptyBT = pc.getSettings().getSettingInt("EmptyBorder.TOP");
         emptyBL = pc.getSettings().getSettingInt("EmptyBorder.LEFT");
         emptyBB = pc.getSettings().getSettingInt("EmptyBorder.BOTTOM");
         emptyBR = pc.getSettings().getSettingInt("EmptyBorder.RIGHT");
      } catch (Exception e) {
      }

      Border border = null;
      if (hasBorder) {
         // BorderFactory.cre
         border = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
         // border=BorderFactory.createMatteBorder(1,0,0,0);
         border = BorderFactory.createTitledBorder(border, title);
         // border=BorderFactory.createCompoundBorder(etched,border);
      } else {
         if (hasEmptyBorder) {
            border = BorderFactory.createEmptyBorder(emptyBT, emptyBL, emptyBB, emptyBR);
         }
      }
      if (border != null) {
         setBorder(border);
      }

   }

   public static void defaultErrorMessage(Window w, String elementTitle) {
      String message = ResourceManager.getLanguageDependentString("ErrorValueMustBeDefined");
      String dialogTitle = ResourceManager.getLanguageDependentString("DialogValueIsNotDefined");
      XMLBasicPanel.errorMessage(w, dialogTitle, elementTitle, message);
   }

   public static void errorMessage(Window w,
                                   String dialogTitle,
                                   String elementTitle,
                                   String message) {
      JOptionPane.showMessageDialog(w,
                                    elementTitle + message,
                                    dialogTitle,
                                    JOptionPane.ERROR_MESSAGE);
   }

   public void setOwner(XMLElement owner) {
      this.myOwner = owner;
   }

   public XMLElement getOwner() {
      return myOwner;
   }

   // public XMLElement getOrginalOwner () {
   // return myOwnerOrginal;
   // }

   public String getTitle() {
      return title;
   }

   /**
    * Checks if the element that owns panel
    */
   public boolean validateEntry() {
      return true;
   }

   // Always returns true, this is set because of panels that are never empty
   // but this method is used when checking emptiness of group panel,
   // and panels that do not override this method should not be ever considered
   public boolean isEmpty() {
      return true;
   }

   public void setElements() {
      return;
   }

   public Object getValue() {
      return null;
   }

   public void updateView() {

   }

   // public JComponent getMainComponent () {
   // return this;
   // }
   /**
    * Find the hosting window.
    */
   public Window getWindow() {
      for (Container p = getParent(); p != null; p = p.getParent()) {
         if (p instanceof Window) {
            return (Window) p;
         }
      }
      return null;
   }

   public void cleanup() {
   }

   public PanelContainer getPanelContainer() {
      return this.pc;
   }

   public void canceled() {
   }

}
