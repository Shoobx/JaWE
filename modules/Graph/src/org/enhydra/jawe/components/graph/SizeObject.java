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

package org.enhydra.jawe.components.graph;

import org.enhydra.jxpdl.XMLAttribute;
import org.enhydra.jxpdl.XMLComplexElement;

/**
 * Represents fake XML object for defining sizes.
 * 
 * @author Sasa Bojanic
 */
public class SizeObject extends XMLComplexElement {

   public SizeObject() {
      super(null, true);
   }

   protected void fillStructure() {
      XMLAttribute attrHeight = new XMLAttribute(this, "Height", false);
      XMLAttribute attrWidth = new XMLAttribute(this, "Width", false);
      add(attrHeight);
      add(attrWidth);
   }

   public int getHeight() {
      String h = get("Height").toValue();
      if (!"".equals(h)) {
         return (int) Double.parseDouble(h);
      }
      return 0;
   }

   public void setHeight(int height) {
      set("Height", String.valueOf(height));
   }

   public int getWidth() {
      String w = get("Width").toValue();
      if (!"".equals(w)) {
         return (int) Double.parseDouble(w);
      }
      return 0;
   }

   public void setWidth(int width) {
      set("Width", String.valueOf(width));
   }
}
