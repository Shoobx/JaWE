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

import org.enhydra.jawe.base.controller.ControllerSettings;
import org.enhydra.jawe.base.controller.JaWEController;
import org.enhydra.jawe.shark.business.I18nVariable;
import org.enhydra.jawe.shark.business.XPDLStringVariable;
import org.enhydra.jxpdl.XMLAttribute;
import org.enhydra.jxpdl.XMLCollection;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.elements.Activity;
import org.enhydra.jxpdl.elements.ExtendedAttribute;
import org.enhydra.jxpdl.elements.WorkflowProcess;

public class SharkJaWEController extends JaWEController {

   public SharkJaWEController(ControllerSettings settings) {
      super(settings);
   }

   public boolean canDuplicateElement(XMLCollection col, XMLElement el, boolean checkReadOnly) {
      if (el instanceof XPDLStringVariable || el instanceof I18nVariable) {
         return false;
      }
      return super.canDuplicateElement(col, el, checkReadOnly);
   }

   protected boolean isNonValidatingXMLAttribute(XMLElement el) {
      boolean ret = super.isNonValidatingXMLAttribute(el);
      if (ret) {
         if (el instanceof XMLAttribute) {
            String elName = el.toName();
            if (elName.equals("ScriptType")) {
               ret = false;
            } else if (el.getParent() instanceof ExtendedAttribute && elName.equals("Value")) {
               ret = false;
            } else if ((el.getParent() instanceof WorkflowProcess || el.getParent() instanceof Activity) && elName.equals("Name")) {
               ret = false;
            }
         }
      }
      return ret;
   }

}
