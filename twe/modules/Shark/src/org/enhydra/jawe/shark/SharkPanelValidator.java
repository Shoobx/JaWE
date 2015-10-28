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

import org.enhydra.jawe.ResourceManager;
import org.enhydra.jawe.base.panel.StandardPanelValidator;
import org.enhydra.jawe.base.panel.panels.XMLBasicPanel;
import org.enhydra.jawe.base.panel.panels.XMLPanel;
import org.enhydra.jawe.shark.business.I18nVariable;
import org.enhydra.jawe.shark.business.SharkUtils;
import org.enhydra.jawe.shark.business.WfVariable;
import org.enhydra.jawe.shark.business.XPDLStringVariable;
import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.XPDLValidationErrorIds;
import org.enhydra.jxpdl.elements.DataField;
import org.enhydra.jxpdl.elements.FormalParameter;
import org.enhydra.jxpdl.elements.WorkflowProcess;

/**
 * Class used to validate panels for all XPDL entities.
 * 
 * @author Sasa Bojanic
 */
public class SharkPanelValidator extends StandardPanelValidator {

   public boolean standardPanelValidation(XMLElement el, XMLPanel panel) {
      boolean ok = super.standardPanelValidation(el, panel);
      if (ok
          && (el instanceof XPDLStringVariable || el instanceof I18nVariable || el instanceof DataField || (el instanceof FormalParameter && el.getParent()
             .getParent() instanceof WorkflowProcess))) {
         
         int vartype = el instanceof XPDLStringVariable ? 0 : (el instanceof I18nVariable ? 1 : 2);
         XMLPanel idPanel = findPanel(panel, ((XMLComplexElement) el).get(vartype < 2 ? "Name" : "Id"));
         String newId = ((String) idPanel.getValue()).trim();
         boolean isValid = XMLUtil.isIdValid(newId);
         if (!isValid) {
            XMLBasicPanel.errorMessage(panel.getWindow(),
                                       ResourceManager.getLanguageDependentString("ErrorMessageKey"),
                                       "",
                                       ResourceManager.getLanguageDependentString(XPDLValidationErrorIds.ERROR_INVALID_ID));
            idPanel.requestFocus();
            return false;
         }         

         if (SharkUtils.doesVariableExist(el, newId, vartype) || SharkPanelGenerator.getConfigStringChoices().contains(newId)) {
            XMLBasicPanel.errorMessage(panel.getWindow(),
                                       ResourceManager.getLanguageDependentString("ErrorMessageKey"),
                                       "",
                                       ResourceManager.getLanguageDependentString(XPDLValidationErrorIds.ERROR_NON_UNIQUE_ID));
            idPanel.requestFocus();
            return false;
         }
      }
      return ok;
   }

   protected boolean validateId(XMLPanel pnl, XMLElement el) {
      if (el instanceof WfVariable) {
         return true;
      }
      return super.validateId(pnl, el);
   }

}
