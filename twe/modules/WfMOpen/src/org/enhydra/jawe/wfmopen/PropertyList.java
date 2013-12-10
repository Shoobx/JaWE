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

import java.util.List;

import org.enhydra.shark.xpdl.XMLCollection;
import org.enhydra.shark.xpdl.XMLElement;

public class PropertyList extends XMLCollection {

   public PropertyList(ImplementationExtendedAttribute iea) {
      super(iea, false);
      notifyMainListeners = false;
      notifyListeners = false;
   }

   public void createStructure (List props) {
      for (int i=0; i<props.size(); i++) {
         elements.add(props.get(i));
      }
   }
   
   public void setValue(String v) {
      if (isReadOnly) {
         throw new RuntimeException("Can't set the value of read only element!");
      }
      if (v == null) {
         getParent().setValue(v);
      } else {
         this.value = v;
      }
   }

   public void add (XMLElement el) {
      super.add(el);
      setValue(null);
   }   
   
   public boolean add (int no,XMLElement el) {
      boolean ret=super.add(no,el);
      setValue(null);
      return ret;
   }   

   protected XMLElement removeElement (int no) {
      XMLElement el=super.removeElement(no);
      setValue(null);
      return el;
   }   

   public boolean reposition (XMLElement el,int newPos) {
      boolean ret=super.reposition(el, newPos);
      setValue(null);
      return ret;
   }
   
   public XMLElement generateNewElement() {
      return new Property(this);
   }

}
