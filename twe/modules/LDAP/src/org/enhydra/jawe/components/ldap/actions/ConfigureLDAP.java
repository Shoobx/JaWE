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

package org.enhydra.jawe.components.ldap.actions;

import java.awt.event.ActionEvent;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.editor.StandardXPDLElementEditor;
import org.enhydra.jawe.components.ldap.LDAPConfiguration;
import org.enhydra.jawe.components.ldap.LDAPController;
import org.enhydra.jxpdl.elements.Package;

/**
 * Opens LDAP configuration dialog.
 * 
 * @author Sasa Bojanic
 */
public class ConfigureLDAP extends ActionBase {

   public ConfigureLDAP(JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {
      Package mainPkg=JaWEManager.getInstance().getJaWEController().getMainPackage();
      Package workingPkg=JaWEManager.getInstance().getJaWEController().getSelectionManager().getWorkingPKG();
      if (mainPkg!=null && mainPkg==workingPkg) {
         setEnabled(true); 
      } else {
         setEnabled(false);
      }
   }

   public void actionPerformed(ActionEvent e) {
      LDAPController controller = (LDAPController) jawecomponent;
      LDAPConfiguration lc = controller.getLDAPConfig();
      StandardXPDLElementEditor ed = new StandardXPDLElementEditor();
      ed.setTitle(controller.getSettings().getLanguageDependentString("DialogLDAPConfiguration"));
      ed.editXPDLElement(lc);
   }
}
