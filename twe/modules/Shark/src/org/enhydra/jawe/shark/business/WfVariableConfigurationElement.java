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

import org.enhydra.jxpdl.XMLAttribute;
import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.elements.ExtendedAttribute;
import org.enhydra.jxpdl.elements.ExtendedAttributes;

public class WfVariableConfigurationElement extends XMLComplexElement {

   protected ExtendedAttributes eas;

   protected boolean isPersisted = false;

   public WfVariableConfigurationElement(ExtendedAttributes eas) {
      super(eas.getParent(), "WfConfiguration", true);
      this.eas = eas;
      notifyMainListeners = false;
      notifyListeners = false;
      handleStructure();
      setReadOnly(eas.isReadOnly());
   }

   public void setValue(String v) {
      if (v == null) {
         SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_TRANSIENT, null, null, false, false);
         SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_DYNAMICSCRIPT, null, null, false, false);
         SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_IS_ACTIVITY_SCOPE_ONLY, null, null, false, false);
         SharkUtils.updateSingleExtendedAttribute(this, eas, SharkConstants.EA_EVALUATE_DYNAMICSCRIPT_VARIABLE_FOR_TOOL_AGENT_ACTIVITIES, null, null, false, false);
      } else {
         if (isReadOnly) {
            throw new RuntimeException("Can't set the value of read only element!");
         }
         this.value = v;
      }
   }

   public XMLAttribute getTransientAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_TRANSIENT);
   }

   public XMLAttribute getDynamicScriptAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_DYNAMICSCRIPT);
   }

   public XMLAttribute getIsActivityScopeOnlyAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_IS_ACTIVITY_SCOPE_ONLY);
   }

   public XMLAttribute getUseInToolAgentActivitiesAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_EVALUATE_DYNAMICSCRIPT_VARIABLE_FOR_TOOL_AGENT_ACTIVITIES);
   }

   protected void fillStructure() {
      XMLAttribute attrTransient = new XMLAttribute(this, SharkConstants.EA_TRANSIENT, false, new String[] {
            "true", "false"
      }, 1);
      XMLAttribute attrDynamicScript = new XMLAttribute(this, SharkConstants.EA_DYNAMICSCRIPT, false, new String[] {
            "true", "false"
      }, 1);
      XMLAttribute attrIsActivityScopeOnly = new XMLAttribute(this, SharkConstants.EA_IS_ACTIVITY_SCOPE_ONLY, false, new String[] {
            "true", "false"
      }, 1);
      XMLAttribute attrUseInToolAgentActivities = new XMLAttribute(this, SharkConstants.EA_EVALUATE_DYNAMICSCRIPT_VARIABLE_FOR_TOOL_AGENT_ACTIVITIES, false, new String[] {
            "true", "false"
      }, 0);

      add(attrTransient);
      add(attrDynamicScript);
      add(attrIsActivityScopeOnly);
      add(attrUseInToolAgentActivities);
   }

   protected void handleStructure() {
      int pc = 0;
      Iterator it = eas.toElements().iterator();
      while (it.hasNext()) {
         ExtendedAttribute ea = (ExtendedAttribute) it.next();
         String eaname = ea.getName();
         XMLElement attr = get(eaname);
         if (attr != null) {
            String eaval = ea.getVValue();
            attr.setValue(eaval);
            pc++;
         }
      }
      isPersisted = pc == 4;
   }

   public boolean isPersisted() {
      return isPersisted;
   }

   public void setPersisted(boolean isPersisted) {
      this.isPersisted = isPersisted;
   }

   public boolean isForModalDialog() {
      return true;
   }

}
