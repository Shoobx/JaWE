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
