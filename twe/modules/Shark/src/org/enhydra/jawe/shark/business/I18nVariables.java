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

package org.enhydra.jawe.shark.business;

import java.util.Iterator;

import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLElementChangeInfo;
import org.enhydra.jxpdl.XMLElementChangeListener;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.elements.DataFields;
import org.enhydra.jxpdl.elements.ExtendedAttribute;
import org.enhydra.jxpdl.elements.ExtendedAttributes;
import org.enhydra.jxpdl.elements.WorkflowProcess;

public class I18nVariables extends ExtendedAttributes implements XMLElementChangeListener {

   protected ExtendedAttributes eas;

   public I18nVariables(ExtendedAttributes eas) {
      super((XMLComplexElement) eas.getParent());
      this.eas = eas;
      notifyMainListeners = false;
      notifyListeners = false;
      for (int i = 0; i < eas.size(); i++) {
         ExtendedAttribute ea = (ExtendedAttribute) eas.get(i);
         if (ea.getName().startsWith(SharkConstants.EA_I18N_VARIABLE_PREFIX)) {
            super.elements.add(new I18nVariable(this, ea));
         }
      }
      notifyListeners = true;
      this.eas.addListener(this);
   }

   public void add(XMLElement el) {
      this.eas.removeListener(this);
      super.add(el);
      eas.add(((I18nVariable) el).getExtendedAttribute());
      this.eas.addListener(this);
   }

   public int remove(XMLElement el) {
      this.eas.removeListener(this);
      super.remove(el);
      int ret = eas.remove(((I18nVariable) el).getExtendedAttribute());
      this.eas.addListener(this);
      return ret;
   }

   public XMLElement remove(int no) {
      this.eas.removeListener(this);
      super.remove(no);
      XMLElement ret = eas.remove(no);
      this.eas.addListener(this);
      return ret;
   }

   public boolean reposition(XMLElement el, int newPos) {
      this.eas.removeListener(this);
      int realNewPos = newPos;
      XMLElement se = (XMLElement) super.elements.get(newPos);
      realNewPos = eas.indexOf(se);
      super.reposition(((I18nVariable) el).getExtendedAttribute(), newPos);
      boolean ret = eas.reposition(el, realNewPos);
      this.eas.addListener(this);
      return ret;
   }

   public XMLElement generateNewElement() {
      ExtendedAttribute ea = (ExtendedAttribute) eas.generateNewElement();
      WorkflowProcess wp = XMLUtil.getWorkflowProcess(eas);
      DataFields dfs = null;
      if (wp != null) {
         dfs = wp.getDataFields();
      } else {
         dfs = XMLUtil.getPackage(eas).getDataFields();
      }
      ea.setName(SharkConstants.EA_I18N_VARIABLE_PREFIX);
      ea.setVValue("");
      return new I18nVariable(this, ea);
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

   public void unregister() {
      this.eas.removeListener(this);
   }

}
