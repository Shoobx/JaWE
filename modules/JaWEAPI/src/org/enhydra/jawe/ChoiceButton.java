package org.enhydra.jawe;

/**
 * Interface for classes which will be used as a choice button.
 *
 * @author Sasa Bojanic
 */
import javax.swing.JButton;

public abstract class ChoiceButton extends JButton {
   
   public abstract Class getChoiceType ();
   
}
