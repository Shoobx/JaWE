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

import org.enhydra.jawe.base.xpdlobjectfactory.XPDLObjectFactory;
import org.enhydra.jawe.base.xpdlobjectfactory.XPDLObjectFactorySettings;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.elements.ExtendedAttribute;
import org.enhydra.jxpdl.elements.ExtendedAttributes;
import org.enhydra.jxpdl.elements.WorkflowProcess;

/**
 * @author Sasa Bojanic
 */
public class SharkXPDLObjectFactory extends XPDLObjectFactory {

   public SharkXPDLObjectFactory(XPDLObjectFactorySettings settings) throws Exception {
      super(settings);
   }

   public ExtendedAttribute createXPDLObject(ExtendedAttributesWrapper eas,
                                             String type,
                                             boolean addToCollection) {
      ExtendedAttribute ea = (ExtendedAttribute) eas.generateNewElement();
      if (addToCollection) {
         eas.add(ea);
      }
      return ea;
   }

   public WfVariable createXPDLObject(WfVariables sps,
                                      String type,
                                      boolean addToCollection) {
      WfVariable sp = (WfVariable) sps.generateNewElement();

      adjustXPDLObject(sp, type);

      if (addToCollection) {
         sps.add(sp);
      }
      return sp;
   }

   public void adjustXPDLObject(XMLElement el, String type) {
      super.adjustType(el, type);
      if (el instanceof WorkflowProcess) {
         ExtendedAttributes peas = XMLUtil.getPackage(el).getExtendedAttributes();
         ExtendedAttributes eas = ((WorkflowProcess) el).getExtendedAttributes();
         ExtendedAttribute pea = peas.getFirstExtendedAttributeForName(SharkConstants.EA_ALLOW_UNDEFINED_VARIABLES);
         ExtendedAttribute ea = null;
         if (pea != null) {
            ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_ALLOW_UNDEFINED_VARIABLES);
            if (ea != null) {
               ea.setVValue(pea.getVValue());
            }
         }
         pea = peas.getFirstExtendedAttributeForName(SharkConstants.EA_DYNAMIC_VARIABLE_HANDLING);
         if (pea != null) {
            ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_DYNAMIC_VARIABLE_HANDLING);
            if (ea != null) {
               ea.setVValue(pea.getVValue());
            }
         }
         pea = peas.getFirstExtendedAttributeForName(SharkConstants.EA_CHOOSE_NEXT_PERFORMER);
         if (pea != null) {
            ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_CHOOSE_NEXT_PERFORMER);
            if (ea != null) {
               ea.setVValue(pea.getVValue());
            }
         }
         pea = peas.getFirstExtendedAttributeForName(SharkConstants.EA_MAX_ASSIGNMENTS);
         if (pea != null) {
            ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_MAX_ASSIGNMENTS);
            if (ea != null) {
               ea.setVValue(pea.getVValue());
            }
         }
         pea = peas.getFirstExtendedAttributeForName(SharkConstants.EA_WORKLOAD_FACTOR);
         if (pea != null) {
            ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_WORKLOAD_FACTOR);
            if (ea != null) {
               ea.setVValue(pea.getVValue());
            }
         }
         pea = peas.getFirstExtendedAttributeForName(SharkConstants.EA_USE_PROCESS_CONTEXT_ONLY);
         if (pea != null) {
            ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_USE_PROCESS_CONTEXT_ONLY);
            if (ea != null) {
               ea.setVValue(pea.getVValue());
            }
         }
         pea = peas.getFirstExtendedAttributeForName(SharkConstants.EA_CREATE_ASSIGNMENTS);
         if (pea != null) {
            ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_CREATE_ASSIGNMENTS);
            if (ea != null) {
               ea.setVValue(pea.getVValue());
            }
         }
         pea = peas.getFirstExtendedAttributeForName(SharkConstants.EA_TRANSIENT);
         if (pea != null) {
            ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_TRANSIENT);
            if (ea != null) {
               ea.setVValue(pea.getVValue());
            }
         }
         pea = peas.getFirstExtendedAttributeForName(SharkConstants.EA_DELETE_FINISHED);
         if (pea != null) {
            ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_DELETE_FINISHED);
            if (ea != null) {
               ea.setVValue(pea.getVValue());
            }
         }
         pea = peas.getFirstExtendedAttributeForName(SharkConstants.EA_CHECK_FOR_FIRST_ACTIVITY);
         if (pea != null) {
            ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_CHECK_FOR_FIRST_ACTIVITY);
            if (ea != null) {
               ea.setVValue(pea.getVValue());
            }
         }
         pea = peas.getFirstExtendedAttributeForName(SharkConstants.EA_CHECK_FOR_CONTINUATION);
         if (pea != null) {
            ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_CHECK_FOR_CONTINUATION);
            if (ea != null) {
               ea.setVValue(pea.getVValue());
            }
         }
         pea = peas.getFirstExtendedAttributeForName(SharkConstants.EA_REDIRECT_AFTER_PROCESS_END);
         if (pea != null) {
            ea = eas.getFirstExtendedAttributeForName(SharkConstants.EA_REDIRECT_AFTER_PROCESS_END);
            if (ea != null) {
               ea.setVValue(pea.getVValue());
            }
         }

      }
   }

}
