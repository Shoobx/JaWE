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
