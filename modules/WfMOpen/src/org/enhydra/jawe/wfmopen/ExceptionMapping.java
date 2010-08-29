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
public class ExceptionMapping extends XMLComplexElement {
   
   public ExceptionMapping (ExceptionMappings parent) {
      super(parent, false);
      notifyMainListeners = false;
      notifyListeners = false;
  }

   protected void fillStructure () {
      XMLAttribute attrJavaExc=new XMLAttribute(this,"JavaException", true);
      XMLAttribute attrProcessException=new XMLAttribute(this,"ProcessException", false);
      add(attrJavaExc);
      add(attrProcessException);
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
         if (((ExceptionMappings)parent).contains(this)) {
            getParent().setValue(v);
         }
      } else {
         this.value = v;
      }
   }
   
   public String getJavaException() {
      return get("JavaException").toValue();
   }
   public void setJavaException(String javaExc) {
      set("JavaException",javaExc);
   }
   public String getProcessException() {
      return get("ProcessException").toValue();
   }
   public void setProcessException(String procExc) {
      set("ProcessException",procExc);
   }

}
