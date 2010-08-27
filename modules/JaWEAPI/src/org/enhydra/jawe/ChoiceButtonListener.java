/**
 * Miroslav Popov, Jul 19, 2005
 */
package org.enhydra.jawe;

/**
 * Interface for classes which will be used to listen changes on GraphContorllerChoiceButton
 *
 * @author Miroslav Popov
 * @author Sasa Bojanic
 */
import java.util.List;

public interface ChoiceButtonListener {
   
   public void selectionChanged(ChoiceButton cbutton,Object change);
   
   public Object getSelectedObject (ChoiceButton cbutton);
   
   public List getChoices (ChoiceButton cbutton);
   
}
