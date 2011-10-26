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

package org.enhydra.jawe;

/**
 * Interface for classes which will be used as a choice button.
 *
 * @author Sasa Bojanic
 */
import javax.swing.JButton;

public abstract class ChoiceButton extends JButton {

   /**
    * Returns the Class representing the type of choice.
    * 
    * @return The Class representing the type of choice.
    */
   public abstract Class getChoiceType();

}
