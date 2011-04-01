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

import java.awt.Rectangle;
import java.util.Comparator;

/**
 * Used to properly sort visual objects that represents Participant's contained within graph.
 */
public class GraphParticipantComparator implements Comparator {

   GraphManager graphManager;

   public GraphParticipantComparator(GraphManager graphManager) {
      this.graphManager = graphManager;
   }

   public int compare(Object o1, Object o2) {
      GraphSwimlaneInterface p1 = (GraphSwimlaneInterface) o1;
      GraphSwimlaneInterface p2 = (GraphSwimlaneInterface) o2;
      int pos1;
      int pos2;
      Rectangle b1 = graphManager.getCBounds(p1, null);
      Rectangle b2 = graphManager.getCBounds(p2, null);
      if (!graphManager.isGraphRotated()) {
         pos1 = b1.y;
         pos2 = b2.y;
         if (pos1==pos2) {
            if (b1.x>b2.x) {
               pos1++;
            } else {
               pos2++;
            }
         }
      } else {
         pos1 = b1.x;
         pos2 = b2.x;
         if (pos1==pos2) {
            if (b1.y>b2.y) {
               pos1++;
            } else {
               pos2++;
            }
         }         
      }

      return (pos1 < pos2 ? -1 : (pos1 == pos2 ? 0 : 1)); //sort that highest is the first
   }

}

