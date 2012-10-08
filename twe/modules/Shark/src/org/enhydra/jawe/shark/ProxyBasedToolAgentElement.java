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

import org.enhydra.jxpdl.XMLAttribute;
import org.enhydra.jxpdl.elements.Application;
import org.enhydra.jxpdl.elements.ExtendedAttribute;

public class ProxyBasedToolAgentElement extends ToolAgentElementBase {

   public ProxyBasedToolAgentElement(Application app, String name) {
      super(app, name);
   }

   public void setValue(String v) {
      if (isReadOnly) {
         throw new RuntimeException("Can't set the value of read only element!");
      }
      if (v == null) {
         SharkUtils.updateSingleExtendedAttribute(this,
                                                  ((Application) this.getParent()).getExtendedAttributes(),
                                                  SharkConstants.EA_TOOL_AGENT_CLASS_PROXY,
                                                  null, true);
      } else {
         this.value = v;
      }
   }

   public XMLAttribute getToolAgentClassProxyAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_TOOL_AGENT_CLASS_PROXY);
   }

   protected void fillStructure() {
      XMLAttribute taproxy = new XMLAttribute(this, SharkConstants.EA_TOOL_AGENT_CLASS_PROXY, true);
      add(taproxy);
   }

   protected void handleStructure() {
      ExtendedAttribute ea = ((Application) this.getParent()).getExtendedAttributes()
         .getFirstExtendedAttributeForName(SharkConstants.EA_TOOL_AGENT_CLASS_PROXY);
      if (ea != null) {
         getToolAgentClassProxyAttribute().setValue(ea.getVValue());
      }
   }

}
