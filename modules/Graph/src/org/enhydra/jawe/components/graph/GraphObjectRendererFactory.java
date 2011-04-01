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

import org.enhydra.jxpdl.XMLCollectionElement;
import org.enhydra.jxpdl.XPDLConstants;
import org.enhydra.jxpdl.elements.Activity;
import org.enhydra.jxpdl.elements.Artifact;

/**
 * Factory for rendering graph objects.
 * 
 * @author Sasa Bojanic
 */
public class GraphObjectRendererFactory {

   protected Properties properties;

   public void configure(Properties props) throws Exception {
      this.properties = props;
   }

   public GraphActivityRendererInterface createActivityRenderer(Activity act) {
      // System.out.println("ACT "+act.getId()+","+act.getName()+", \ntype="+act.getActivityType());
      int actType = act.getActivityType();
      if (actType == XPDLConstants.ACTIVITY_TYPE_EVENT_START
          || actType == XPDLConstants.ACTIVITY_TYPE_EVENT_END) {
         return new DefaultGraphEventActivityRenderer();
      }
      return new DefaultGraphActivityRenderer();
   }

   public GraphArtifactRendererInterface createArtifactRenderer(Artifact art) {
      return new DefaultGraphArtifactRenderer();
   }

   public GraphTransitionRendererInterface createTransitionRenderer(XMLCollectionElement tra) {
      return new DefaultGraphTransitionRenderer();
   }

   public GraphSwimlaneRendererInterface createParticipantRenderer(Object par) {
      return new DefaultGraphSwimlaneRenderer();
   }

   public GraphPortRendererInterface createPortRenderer(String type) {
      return new DefaultGraphPortRenderer();
   }
}
