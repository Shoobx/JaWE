package org.enhydra.jawe;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JMenuItem;

/**
 * Class used to change enable state of buttons.
 */
public class ButtonPropertyChangedListener implements PropertyChangeListener {

     AbstractButton button;

     public ButtonPropertyChangedListener ( AbstractButton b ) {
        super();
        button = b;
     }

     public void propertyChange( PropertyChangeEvent e ) {
        String propertyName = e.getPropertyName();
        if ( e.getPropertyName().equals( Action.NAME ) && button instanceof JMenuItem ) {
           String text = ( String ) e.getNewValue();
           button.setText( text );
        }
        else {
           if ( propertyName.equals( "enabled" ) ) {
              Boolean enabledState = ( Boolean ) e.getNewValue();
              button.setEnabled( enabledState.booleanValue() );
           }
        }
     }
}
