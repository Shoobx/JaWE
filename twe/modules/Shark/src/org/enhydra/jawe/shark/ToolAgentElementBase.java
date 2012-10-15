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

package org.enhydra.jawe.shark;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jxpdl.XMLAttribute;
import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.elements.Application;

public abstract class ToolAgentElementBase extends XMLComplexElement {

   public ToolAgentElementBase(Application app, String name) {
      super(app, name, true);
      notifyMainListeners = false;
      notifyListeners = false;
      handleStructure();
      setReadOnly(app.isReadOnly());
      getToolAgentClass().setReadOnly(true);
   }

   public XMLAttribute getToolAgentClass() {
      return (XMLAttribute) get(SharkConstants.EA_TOOL_AGENT_CLASS);
   }

   protected abstract void handleStructure();

   protected void fillStructure() {
      XMLAttribute taname = new XMLAttribute(this,
                                             SharkConstants.EA_TOOL_AGENT_CLASS,
                                             true);
      String cn = JaWEManager.getInstance()
         .getJaWEController()
         .getSettings()
         .getLanguageDependentString(toName() + "Key");
      if (cn == null) {
         cn = toName();
      }
      taname.setValue(cn);
      taname.setReadOnly(true);
      add(taname);
   }

   public String toValue() {
      return toName();
   }
}
