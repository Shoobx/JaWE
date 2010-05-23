package org.enhydra.jawe.shark;

import org.enhydra.jawe.base.xpdlobjectfactory.XPDLObjectFactory;
import org.enhydra.jawe.base.xpdlobjectfactory.XPDLObjectFactorySettings;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.XMLUtil;
import org.enhydra.shark.xpdl.elements.ExtendedAttribute;
import org.enhydra.shark.xpdl.elements.ExtendedAttributes;
import org.enhydra.shark.xpdl.elements.WorkflowProcess;

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
   
   public void adjustXPDLObject(XMLElement el,String type) {
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
