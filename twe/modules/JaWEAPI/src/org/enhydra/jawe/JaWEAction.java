/**
 * Together Workflow Editor
 * Copyright (C) 2010 Together Teamsolutions Co., Ltd. 
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

package org.enhydra.jawe;

import javax.swing.ImageIcon;

/**
 * Represents the structure that describes a JaWE related action. Includes a real action
 * to be done, the language dependent (i18n) name for the action and an icon for the
 * action to graphically represent it in tool-bar, menu-bar, etc.
 */
public class JaWEAction {

   /** The actual action to be done. */
   private ActionBase action;

   /** The language dependent (i18n) name for the action. */
   private String langDepName = null;

   /** The action icon. */
   private ImageIcon icon = null;

   /**
    * Default constructor.
    */
   public JaWEAction() {
   }

   /**
    * Constructor with a given action to perform.
    * 
    * @param action Action to perform.
    */
   public JaWEAction(ActionBase action) {
      init(action, null, null);
   }

   /**
    * Constructor with a given action to perform and an icon.
    * 
    * @param action Action to perform.
    * @param icon Icon to present graphically.
    */
   public JaWEAction(ActionBase action, ImageIcon icon) {
      init(action, icon, null);
   }

   /**
    * Constructor with a given action to perform and language dependent name for the
    * action.
    * 
    * @param action Action to perform.
    * @param langName Language dependent name for the action.
    */
   public JaWEAction(ActionBase action, String langName) {
      init(action, null, langName);
   }

   /**
    * Constructor with a given action to perform, an icon and language dependent name for
    * the action.
    * 
    * @param action Action to perform.
    * @param icon Icon to present graphically.
    * @param langName Language dependent name for the action.
    */
   public JaWEAction(ActionBase action, ImageIcon icon, String langName) {
      init(action, icon, langName);
   }

   /**
    * This method is used to initialize the object when default constructor is used for
    * its creation.
    * 
    * @param pAction Action to perform.
    * @param pIcon Icon to present graphically.
    * @param pLangName Language dependent name for the action.
    */
   private void init(ActionBase pAction, ImageIcon pIcon, String pLangName) {
      this.action = pAction;
      this.icon = pIcon;
      this.langDepName = pLangName;
   }

   /**
    * @return Action to perform.
    */
   public ActionBase getAction() {
      return action;
   }

   /**
    * @return Icon to present graphically.
    */
   public ImageIcon getIcon() {
      return icon;
   }

   /**
    * @return Language dependent name for the action.
    */
   public String getLangDepName() {
      return langDepName;
   }

   /**
    * Sets the action to perform.
    * 
    * @param action Action to perform.
    */
   public void setAction(ActionBase action) {
      this.action = action;
   }

   /**
    * Sets the icon to present graphically.
    * 
    * @param icon Icon to present graphically.
    */
   public void setIcon(ImageIcon icon) {
      this.icon = icon;
   }

   /**
    * Sets the language dependent name for the action. *
    * 
    * @param langDepName Language dependent name.
    */
   public void setLangDepName(String langDepName) {
      this.langDepName = langDepName;
   }

}
