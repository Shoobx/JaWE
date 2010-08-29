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

/**
 * Miroslav Popov, Dec 1, 2005
 * miroslav.popov@gmail.com
 */
package org.enhydra.jawe;

import javax.swing.ImageIcon;

/**
 * @author Miroslav Popov
 *
 */
public class JaWEAction {

   private ActionBase action;
   private String langDepName = null;
   private ImageIcon icon = null;
   
   public JaWEAction() {      
   }
   
   public JaWEAction(ActionBase action) {
      init(action, null, null);
   }
   
   public JaWEAction(ActionBase action, ImageIcon icon) {
      init(action, icon, null);
   }
   
   public JaWEAction(ActionBase action, String langName) {
      init(action, null, langName);
   }
   
   public JaWEAction(ActionBase action, ImageIcon icon, String langName) {
      init(action, icon, langName);
   }
   
   private void init(ActionBase pAction, ImageIcon pIcon, String pLangName) {
      this.action = pAction;
      this.icon = pIcon;
      this.langDepName = pLangName;
   }
   
   public ActionBase getAction() {
      return action;
   }
   
   public ImageIcon getIcon() {
      return icon;
   }
   
   public String getLangDepName() {
      return langDepName;
   }
   
   public void setAction(ActionBase action) {
      this.action = action;
   }
   
   public void setIcon(ImageIcon icon) {
      this.icon = icon;
   }
   
   public void setLangDepName(String langDepName) {
      this.langDepName = langDepName;
   }
   
   
   
}
