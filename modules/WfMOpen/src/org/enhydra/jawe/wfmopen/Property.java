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

package org.enhydra.jawe.wfmopen;

import org.enhydra.shark.xpdl.XMLAttribute;
import org.enhydra.shark.xpdl.XMLComplexElement;
import org.enhydra.shark.xpdl.XMLElement;

/**
 *  Represents special element for WfMOpen ext. attrib of Application definition.
 *
 *  @author Sasa Bojanic
 */
public class Property extends XMLComplexElement {
   
   public Property (PropertyList parent) {
      super(parent, false);
      notifyMainListeners = false;
      notifyListeners = false;
   }

   protected void fillStructure () {
      XMLAttribute attrName=new XMLAttribute(this,"Name", true);
      add(attrName);
   }

   public void makeAs (XMLElement el) {
      super.makeAs(el);
      setValue(el.toValue());
   }

   public void setValue(String v) {
      if (isReadOnly) {
         throw new RuntimeException("Can't set the value of read only element!");
      }
      if (v == null) {
         if (((PropertyList)parent).contains(this)) {
            getParent().setValue(v);
         }
      } else {
         this.value = v;
      }
   }
   
   public String getName() {
      return get("Name").toValue();
   }
   public void setName(String name) {
      set("Name",name);
   }

}
