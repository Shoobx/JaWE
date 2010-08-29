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

package org.enhydra.jawe.shark;

import java.util.Iterator;

import org.enhydra.shark.xpdl.XMLCollection;
import org.enhydra.shark.xpdl.XMLComplexElement;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.XMLElementChangeInfo;
import org.enhydra.shark.xpdl.XMLElementChangeListener;
import org.enhydra.shark.xpdl.elements.ExtendedAttribute;
import org.enhydra.shark.xpdl.elements.ExtendedAttributes;

public class ExtendedAttributesWrapper extends XMLCollection implements XMLElementChangeListener {

   protected ExtendedAttributes eas;
   
   public ExtendedAttributesWrapper (ExtendedAttributes eas) {
      super((XMLComplexElement)eas.getParent(), "Variables", false);
      this.eas=eas;
      notifyMainListeners=false;
      notifyListeners=false;
      for (int i=0; i<eas.size(); i++) {
         ExtendedAttribute ea=(ExtendedAttribute)eas.get(i);
         if (ea.getName().equals(SharkConstants.VTP_VIEW) || ea.getName().equals(SharkConstants.VTP_UPDATE)) {
            super.elements.add(ea);
         }
      }
      notifyListeners=true;
      this.eas.addListener(this);
   }

   public void add (XMLElement el) {
      this.eas.removeListener(this);
      super.add(el);
      eas.add(el);
      this.eas.addListener(this);
   }
   
   public int remove (XMLElement el) {
      this.eas.removeListener(this);
      super.remove(el);
      int ret=eas.remove(el);
      this.eas.addListener(this);
      return ret;
   }

   public XMLElement remove (int no) {
      this.eas.removeListener(this);
      super.remove(no);
      XMLElement ret=eas.remove(no);
      this.eas.addListener(this);
      return ret;
   }
   
   public boolean reposition(XMLElement el,int newPos) {
      this.eas.removeListener(this);
      int realNewPos=newPos;
      XMLElement se=(XMLElement)super.elements.get(newPos);
      realNewPos=eas.indexOf(se);
      super.reposition(el, newPos);
      boolean ret=eas.reposition(el, realNewPos);
      this.eas.addListener(this);
      return ret;
   }
   
   public XMLElement generateNewElement() {
      ExtendedAttribute ea=(ExtendedAttribute)eas.generateNewElement();
      ea.setName(SharkConstants.VTP_VIEW);
      ea.setVValue("");
      return ea;
   }

   public void xmlElementChanged(XMLElementChangeInfo info) {
      if (info.getAction() == XMLElementChangeInfo.REMOVED) {
         Iterator it = info.getChangedSubElements().iterator();
         while (it.hasNext()) {
            XMLElement el = (XMLElement) it.next();
            super.remove(el);
         }
      }
   }

   public void unregister () {
      this.eas.removeListener(this);
   }
   
}
