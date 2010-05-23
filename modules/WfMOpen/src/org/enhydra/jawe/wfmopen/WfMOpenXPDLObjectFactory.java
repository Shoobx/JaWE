package org.enhydra.jawe.wfmopen;

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
public class WfMOpenXPDLObjectFactory extends XPDLObjectFactory {

   public WfMOpenXPDLObjectFactory(XPDLObjectFactorySettings settings) throws Exception {
      super(settings);
   }

   public Property createXPDLObject(PropertyList props,
                                    String type,
                                    boolean addToCollection) {
      Property prop = (Property) props.generateNewElement();

      adjustXPDLObject(prop, type);

      if (addToCollection) {
         props.add(prop);
      }
      return prop;
   }

   public ExceptionMapping createXPDLObject(ExceptionMappings ems,
                                            String type,
                                            boolean addToCollection) {
      ExceptionMapping em = (ExceptionMapping) ems.generateNewElement();

      adjustXPDLObject(em, type);

      if (addToCollection) {
         ems.add(em);
      }
      return em;
   }

   public void adjustXPDLObject(XMLElement el, String type) {
      super.adjustXPDLObject(el, type);
      if (el instanceof WorkflowProcess) {
         ExtendedAttributes peas = XMLUtil.getPackage(el).getExtendedAttributes();
         ExtendedAttributes eas = ((WorkflowProcess) el).getExtendedAttributes();
         ExtendedAttribute pea = peas.getFirstExtendedAttributeForName(WfMOpenConstants.EA_REMOVE_CLOSED_PROCESS);
         ExtendedAttribute ea = null;
         if (pea != null) {
            ea = eas.getFirstExtendedAttributeForName(WfMOpenConstants.EA_REMOVE_CLOSED_PROCESS);
            if (ea != null) {
               ea.setVValue(pea.getVValue());
            }
         }
         pea = peas.getFirstExtendedAttributeForName(WfMOpenConstants.EA_STORE_AUDIT_EVENTS);
         if (pea != null) {
            ea = eas.getFirstExtendedAttributeForName(WfMOpenConstants.EA_STORE_AUDIT_EVENTS);
            if (ea != null) {
               ea.setVValue(pea.getVValue());
            }
         }
         pea = peas.getFirstExtendedAttributeForName(WfMOpenConstants.EA_AUDIT_EVENT_SELECTION);
         if (pea != null) {
            ea = eas.getFirstExtendedAttributeForName(WfMOpenConstants.EA_AUDIT_EVENT_SELECTION);
            if (ea != null) {
               ea.setVValue(pea.getVValue());
            }
         }
      }
   }

}
