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
 * Interface for classes which will be used to listen to the changes of GraphControllerChoiceButton.
 */
import java.util.List;

import org.enhydra.jawe.base.controller.JaWEType;
import org.enhydra.jxpdl.XMLElement;

public interface ChoiceButtonListener {

   /**
    * It is called when choice changes.
    * 
    * @param cbutton The {@link ChoiceButton} instance.
    * @param change Represents the object of change (e.g. instance of {@link XMLElement}
    *           or {@link JaWEType} class.
    */
   public void selectionChanged(ChoiceButton cbutton, Object change);

   /**
    * Depending on implementation, it can return the "working" object for this button.
    * 
    * @param cbutton The {@link ChoiceButton} instance.
    * @return Selected choice object.
    */
   public Object getSelectedObject(ChoiceButton cbutton);

   /**
    * Returns the possible choices for the given button. E.g. it could be
    * {@link XMLElement} or {@link JaWEType} instances.
    * 
    * @param cbutton The {@link ChoiceButton} instance.
    * @return The list of possible choices.
    */
   public List getChoices(ChoiceButton cbutton);

}
