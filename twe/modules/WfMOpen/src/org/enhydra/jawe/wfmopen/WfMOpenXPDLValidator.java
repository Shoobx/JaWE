package org.enhydra.jawe.wfmopen;

import java.util.List;
import java.util.Properties;

import org.enhydra.jawe.base.xpdlvalidator.TogWEXPDLValidator;
import org.enhydra.shark.xpdl.StandardPackageValidator;
import org.enhydra.shark.xpdl.XMLAttribute;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.XMLValidationError;
import org.enhydra.shark.xpdl.elements.Application;
import org.enhydra.shark.xpdl.elements.DeclaredType;
import org.enhydra.shark.xpdl.elements.ExtendedAttribute;
import org.enhydra.shark.xpdl.elements.Priority;
import org.enhydra.shark.xpdl.elements.Script;

/**
 * Special WfMOpen validation - to determine if the package is 'WfMOpen' valid. It extends
 * the JaWE's package validator to add some additional restrictions.
 * 
 * @author Sasa Bojanic
 */
public class WfMOpenXPDLValidator extends TogWEXPDLValidator {

   public WfMOpenXPDLValidator() {
   }

   public WfMOpenXPDLValidator(Properties settings) throws Exception {
      super(settings);
   }
   
   public void validateElement(XMLAttribute el, List existingErrors, boolean fullCheck) {
      super.validateElement(el, existingErrors, fullCheck);

      XMLElement parent=el.getParent();
      if (el.toName().equals("Name")
          && parent instanceof ExtendedAttribute
          && parent.getParent().getParent() instanceof Application) {
         if (el.toValue().equals(WfMOpenConstants.EA_IMPLEMENTATION)) {
            ExtendedAttribute ea = (ExtendedAttribute) parent;
            ImplementationExtendedAttribute iea=new ImplementationExtendedAttribute(ea);            
            if (iea.getClassValue().equals("")) {
               XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_WARNING,
                                                                XMLValidationError.SUB_TYPE_LOGIC,
                                                                WfMOpenValidationErrorIds.WARNING_TOOL_AGENT_CLASS_NOT_DEFINED,
                                                                "",
                                                                ea);
               existingErrors.add(verr);
            }
            List props=iea.getPropertyList().toElements();
            for (int i=0; i<props.size(); i++) {
               Property prop=(Property)props.get(i);
               if (prop.getName().equals("")) {
                  XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                                   XMLValidationError.SUB_TYPE_LOGIC,
                                                                   WfMOpenValidationErrorIds.ERROR_TOOL_AGENT_PROPERTY_NAME_NOT_DEFINED,
                                                                   prop.toValue(),
                                                                   ea);
                  existingErrors.add(verr);                  
               }
            }
         }
      }

   }

   public void validateElement(Application el, List existingErrors, boolean fullCheck) {
      super.validateElement(el, existingErrors, fullCheck);

      if (el.getExtendedAttributes()
         .getFirstExtendedAttributeForName(WfMOpenConstants.EA_IMPLEMENTATION) == null) {
         XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_WARNING,
                                                          XMLValidationError.SUB_TYPE_LOGIC,
                                                          WfMOpenValidationErrorIds.WARNING_TOOL_AGENT_CLASS_NOT_DEFINED,
                                                          "",
                                                          el);
         existingErrors.add(verr);
      }
   }

   public void validateElement(DeclaredType el, List existingErrors, boolean fullCheck) {
      XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                       XMLValidationError.SUB_TYPE_LOGIC,
                                                       WfMOpenValidationErrorIds.ERROR_UNSUPPORTED_DATA_TYPE,
                                                       el.toName(),
                                                       el);
      existingErrors.add(verr);
   }

   public void validateElement(org.enhydra.shark.xpdl.elements.Package el,
                               List existingErrors,
                               boolean fullCheck) {
      super.validateElement(el, existingErrors, fullCheck);
      if (existingErrors.size() == 0 || fullCheck) {
         validateScript(el.getScript(), existingErrors, fullCheck);
      }
   }

   public void validateElement(Priority el, List existingErrors, boolean fullCheck) {
      super.validateElement(el, existingErrors, fullCheck);
      int pri = 0;
      boolean notInt = false;
      try {
         pri = Integer.parseInt(el.toValue());
      } catch (Exception ex) {
         notInt = true;
      }
      if (!notInt && (pri < 0 || pri > 5)) {
         XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                          XMLValidationError.SUB_TYPE_LOGIC,
                                                          WfMOpenValidationErrorIds.ERROR_PRIORITY_OUT_OF_RANGE,
                                                          el.toValue(),
                                                          el);
         existingErrors.add(verr);
      }
   }

   /** Introduces restrictions on script type. */
   public void validateScript(Script el, List existingErrors, boolean fullCheck) {
      String sType = el.getType();

      if (!(sType.equals("") || sType.equals(WfMOpenConstants.SCRIPT_VALUE_JAVASCRIPT) || sType.equals(WfMOpenConstants.SCRIPT_VALUE_ECMASCRIPT))) {
         XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                          XMLValidationError.SUB_TYPE_LOGIC,
                                                          WfMOpenValidationErrorIds.ERROR_UNSUPPORTED_SCRIPT,
                                                          sType,
                                                          el);
         existingErrors.add(verr);
      }

      String sVer = el.getVersion();
      if (!sVer.equals("")) {
         if (sType.equals(WfMOpenConstants.SCRIPT_VALUE_JAVASCRIPT)) {
            if (!sVer.equals(WfMOpenConstants.JAVASCRIPT_VERSION_1_5_VALUE)) {
               XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                                XMLValidationError.SUB_TYPE_LOGIC,
                                                                WfMOpenValidationErrorIds.ERROR_UNSUPPORTED_SCRIPT_VERSION,
                                                                sVer,
                                                                el);
               existingErrors.add(verr);
            }
         } else if (sType.equals(WfMOpenConstants.SCRIPT_VALUE_ECMASCRIPT)) {
            if (!sVer.equals(WfMOpenConstants.ECMASCRIPT_VERSION_3rd_Edition_VALUE)) {
               XMLValidationError verr = new XMLValidationError(XMLValidationError.TYPE_ERROR,
                                                                XMLValidationError.SUB_TYPE_LOGIC,
                                                                WfMOpenValidationErrorIds.ERROR_UNSUPPORTED_SCRIPT_VERSION,
                                                                sVer,
                                                                el);
               existingErrors.add(verr);
            }
         }
      }
   }

   protected StandardPackageValidator createValidatorInstance () {
      return new WfMOpenXPDLValidator();
   }
   
}
