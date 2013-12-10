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

package org.enhydra.jawe.samples.loopactivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.controller.JaWEType;
import org.enhydra.jawe.base.panel.StandardPanelGenerator;
import org.enhydra.jawe.base.panel.panels.XMLCheckboxPanel;
import org.enhydra.jawe.base.panel.panels.XMLComboPanel;
import org.enhydra.jawe.base.panel.panels.XMLGroupPanel;
import org.enhydra.jawe.base.panel.panels.XMLMultiLineTextPanel;
import org.enhydra.jawe.base.panel.panels.XMLMultiLineTextPanelWithChoiceButton;
import org.enhydra.jawe.base.panel.panels.XMLPanel;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.elements.Activity;
import org.enhydra.jxpdl.elements.ExtendedAttribute;
import org.enhydra.jxpdl.elements.ExtendedAttributes;

/**
 * @author Miroslav Popov
 * @author Sasa Bojanic
 */
public class TWELSamplePanelGenerator extends StandardPanelGenerator {

   public TWELSamplePanelGenerator() throws Exception {
      super();
   }

   protected XMLPanel getPanel(Activity el, int no, Set hidden) {     
      JaWEType type=JaWEManager.getInstance().getJaWEController().getTypeResolver().getJaWEType(el);
      if (!type.getTypeId().equals(TWELSampleConstants.ACTIVITY_TYPE_LOOP)) {
         return super.getPanel(el, no, hidden);
      }
      
      XMLPanel p=null;
      ExtendedAttributes eas = el.getExtendedAttributes();
      ExtendedAttribute ea=null;
      List panelElements = new ArrayList();
      switch (no) {
      case 1:
         panelElements.add(el.get("Name"));
         ea = eas.getFirstExtendedAttributeForName(TWELSampleConstants.EA_BACK_TO_POOL);         
         XMLCheckboxPanel cbp = new XMLCheckboxPanel(getPanelContainer(),ea.get("Value"), "Back to pool", false, JaWEManager.getInstance().getJaWEController().canModifyElement(ea.get("Value")),false);
         panelElements.add(cbp);
         
         ea = eas.getFirstExtendedAttributeForName(TWELSampleConstants.EA_SET_TEMPORARY);         
         cbp = new XMLCheckboxPanel(getPanelContainer(),ea.get("Value"), "Set temporary", false,JaWEManager.getInstance().getJaWEController().canModifyElement(ea.get("Value")),false);
         panelElements.add(cbp);
         
         panelElements.add(el.getFirstPerformerObj());
         panelElements.add(el.get("Description"));
         panelElements.add(el.getDeadlines());
         p = new XMLGroupPanel(getPanelContainer(), el, panelElements, getPanelContainer().getLanguageDependentString("GeneralKey"), true, false, true);

         break;

      case 2:
         p=super.getPanel(el, no, hidden);
         break;
         
      case 3:
         p=super.getPanel(el, no, hidden);
         break;
         
      case 4:
         ea = eas.getFirstExtendedAttributeForName(TWELSampleConstants.EA_LOOP_TYPE);         
         List chs=new ArrayList();
         chs.add(TWELSampleConstants.EA_LOOP_TYPE_VALUE_DO);
         chs.add(TWELSampleConstants.EA_LOOP_TYPE_VALUE_WHILE);
         XMLPanel pnl=new XMLComboPanel(getPanelContainer(),ea.get("Value"),chs,false,false,false,false,JaWEManager.getInstance().getJaWEController().canModifyElement(ea.get("Value")));
         panelElements.add(pnl);
         
         ea = eas.getFirstExtendedAttributeForName(TWELSampleConstants.EA_LOOP_CONDITION);         
         pnl=new XMLMultiLineTextPanelWithChoiceButton(getPanelContainer(),ea,"Condition",false,true,XMLMultiLineTextPanel.SIZE_MEDIUM,true,new ArrayList(XMLUtil.getPossibleVariables(XMLUtil.getWorkflowProcess(el)).values()),JaWEManager.getInstance().getJaWEController().canModifyElement(ea));                           
         panelElements.add(pnl);
         
         p = new XMLGroupPanel(getPanelContainer(), el, panelElements, "Loop characteristic", true, false, true);

         break;
         
      default:
         throw new RuntimeException();

      }
      return p;
   }
}
