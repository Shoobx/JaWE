package org.enhydra.jawe.components.graph;

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
      GraphParticipantInterface p1 = (GraphParticipantInterface) o1;
      GraphParticipantInterface p2 = (GraphParticipantInterface) o2;
      int pos1;
      int pos2;
      if (!graphManager.isGraphRotated()) {
         pos1 = graphManager.getBounds(p1, null).y;
         pos2 = graphManager.getBounds(p2, null).y;
      } else {
         pos1 = graphManager.getBounds(p1, null).x;
         pos2 = graphManager.getBounds(p2, null).x;         
      }

      return (pos1 < pos2 ? -1 : (pos1 == pos2 ? 0 : 1)); //sort that highest is the first
   }

}

