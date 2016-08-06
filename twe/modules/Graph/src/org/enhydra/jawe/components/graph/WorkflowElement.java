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

package org.enhydra.jawe.components.graph;

import org.enhydra.jxpdl.XMLComplexElement;

/**
 * Interface that should be implemented by any graph object that holds a reference to
 * corresponding XPDL object.
 */
public interface WorkflowElement {

   /**
    * @return {@link XMLComplexElement} instance bound to this object.
    */
   public XMLComplexElement getPropertyObject();

   /**
    * @return Tooltip text for element.
    */
   public String getTooltip();

   /**
    * @return The type of element.
    */
   public String getType();
}
