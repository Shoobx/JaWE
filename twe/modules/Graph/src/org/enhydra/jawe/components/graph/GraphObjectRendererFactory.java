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

package org.enhydra.jawe.components.graph;

import java.util.Properties;

import org.enhydra.shark.xpdl.elements.Activity;
import org.enhydra.shark.xpdl.elements.Participant;
import org.enhydra.shark.xpdl.elements.Transition;

/**
 * Factory for rendering graph objects.
 * @author Sasa Bojanic
 */
public class GraphObjectRendererFactory {

   protected Properties properties;

   public void configure (Properties props) throws Exception {
      this.properties=props;
   }

   public GraphActivityRendererInterface createActivityRenderer (Activity act) {
      return new DefaultGraphActivityRenderer();
   }

   public GraphTransitionRendererInterface createTransitionRenderer (Transition tra) {        
      return new DefaultGraphTransitionRenderer();
   }
   
   public GraphParticipantRendererInterface createParticipantRenderer (Participant par) {      
      return new DefaultGraphParticipantRenderer();      
   }
   
   public GraphActivityRendererInterface createBubbleRenderer (String type) {
      return new DefaultGraphBubbleActivityRenderer();      
   }

   public GraphPortRendererInterface createPortRenderer (String type) {
      return new DefaultGraphPortRenderer();
   }   
}
