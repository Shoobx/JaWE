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
