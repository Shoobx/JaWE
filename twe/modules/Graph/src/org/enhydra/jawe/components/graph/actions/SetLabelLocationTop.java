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

package org.enhydra.jawe.components.graph.actions;

import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.components.graph.GraphEAConstants;

/**
 * Sets the Artifact or Start/End event or XOR/AND Gateway label location to north.
 * 
 * @author Sasa Bojanic
 */
public class SetLabelLocationTop extends SetLabelLocation {

   public SetLabelLocationTop(JaWEComponent jawecomponent) {
      super(jawecomponent, GraphEAConstants.LABEL_POSITION_TOP);
   }

}
