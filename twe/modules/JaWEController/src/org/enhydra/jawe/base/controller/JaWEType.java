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
 * Miroslav Popov, Oct 3, 2005
 * miroslav.popov@gmail.com
 */
package org.enhydra.jawe.base.controller;

import java.awt.Color;

import javax.swing.ImageIcon;

/**
 * @author Miroslav Popov
 *
 */
public class JaWEType {

   protected Class type;
   protected String typeId;   
   protected String displayName;
   protected ImageIcon typeIcon;
   protected Color color;
   
   public JaWEType(Class classType, String typeId) {
      this.type = classType;
      this.typeId = typeId;
   }
   
   public JaWEType(Class classType, String typeId, String displayName, ImageIcon icon, Color color) {
      this.type = classType;
      this.typeId = typeId;
      this.displayName = displayName;
      this.typeIcon = icon;
      this.color = color;
   }

   public JaWEType(JaWEType old) {
      this.type = old.type;
      this.typeId = old.typeId;
      this.displayName = old.displayName;
      this.typeIcon = old.typeIcon;
      this.color = old.color;
   }
   
   public Class getClassType() {
      return type;
   }
   
   public String getTypeId() {
      return typeId;
   }
   
   public String getDisplayName() {
      return displayName;
   }
   
   public ImageIcon getIcon() {
      return typeIcon;
   }
   
   public Color getColor() {
      return new Color(color.getRed(),color.getGreen(),color.getBlue());
   }

   public void setColor(Color color) {
      this.color = color;
   }

   public void setDisplayName(String displayName) {
      this.displayName = displayName;
   }

   public void setIcon(ImageIcon icon) {
      this.typeIcon = icon;
   }
   
}
