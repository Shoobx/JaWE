package org.enhydra.jawe.base.xpdlvalidator;

import java.util.List;
import java.util.Properties;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.shark.xpdl.StandardPackageValidator;
import org.enhydra.shark.xpdl.XMLValidationError;
import org.enhydra.shark.xpdl.XPDLValidationErrorIds;
import org.enhydra.shark.xpdl.elements.Activity;

/**
 *  Used to validate XPDL model.
 *
 *  @author Sasa Bojanic
 */
public class TogWEXPDLValidator extends StandardPackageValidator {

   public TogWEXPDLValidator () {
   }
   
   public TogWEXPDLValidator (Properties settings) {
      super(settings);
   }
   
   protected boolean checkActivityConnection(Activity act, List existingErrors, boolean fullCheck) {
      super.checkActivityConnection(act, existingErrors, fullCheck);
      int cc=JaWEManager.getInstance().getTransitionHandler().isProperlyConnected(act);
      if (cc>0) {
         XMLValidationError verr=new XMLValidationError(
               XMLValidationError.TYPE_ERROR,
               XMLValidationError.SUB_TYPE_CONNECTION,
               XPDLValidationErrorIds.ERROR_IMPROPERLY_CONNECTED_ACTIVITY_MULTIPLE_OUTGOING_TRANSITIONS,
               "",
               act
         );
         existingErrors.add(verr);                     
      }
      if (cc>1) {
         XMLValidationError verr=new XMLValidationError(
               XMLValidationError.TYPE_ERROR,
               XMLValidationError.SUB_TYPE_CONNECTION,
               XPDLValidationErrorIds.ERROR_IMPROPERLY_CONNECTED_ACTIVITY_MULTIPLE_INCOMING_TRANSITIONS,
               "",
               act
         );
         existingErrors.add(verr);                              
      }
      return (cc==0);
   }
   
   protected StandardPackageValidator createValidatorInstance () {
      return new TogWEXPDLValidator();
   }

}
