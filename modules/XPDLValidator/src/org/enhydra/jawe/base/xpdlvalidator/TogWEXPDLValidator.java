/**
* Together Workflow Editor
* Copyright (C) 2010 Together Teamsolutions Co., Ltd. 
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

package org.enhydra.jawe.base.xpdlvalidator;

import java.util.List;
import java.util.Properties;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jxpdl.StandardPackageValidator;
import org.enhydra.jxpdl.XMLValidationError;
import org.enhydra.jxpdl.XPDLValidationErrorIds;
import org.enhydra.jxpdl.elements.Activity;

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
