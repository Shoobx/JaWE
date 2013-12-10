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

/**
 * Miroslav Popov, Sep 1, 2005
 */
package org.enhydra.jawe.base.controller.actions.defaultactions;

import java.awt.event.ActionEvent;
import java.util.Collection;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.controller.JaWEController;
import org.enhydra.jawe.base.editor.XPDLElementEditor;
import org.enhydra.shark.xpdl.XMLElement;

/**
 * @author Miroslav Popov
 *
 */
public class EditProperties extends ActionBase {
    
    public EditProperties (JaWEComponent jawecomponent) {
        super(jawecomponent);
    }
    
    public void enableDisableAction() {  
        JaWEController jc = (JaWEController)jawecomponent;
        setEnabled(jc.getSelectionManager().canEditProperties() && JaWEManager.getInstance().getXPDLElementEditor()!=null && !JaWEManager.getInstance().getXPDLElementEditor().isVisible());
    }
    
    public void actionPerformed(ActionEvent e) {
        JaWEController jc = (JaWEController)jawecomponent;
        
        Collection c =  jc.getSelectionManager().getSelectedElements();
        if (c.size() != 1)
            return;
        
        setEnabled(false);
        
        XPDLElementEditor ed = JaWEManager.getInstance().getXPDLElementEditor(); 
        ed.editXPDLElement((XMLElement)c.iterator().next());
        
    }
}
