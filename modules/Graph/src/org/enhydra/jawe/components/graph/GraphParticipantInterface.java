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

import java.util.HashSet;
import java.util.Set;

import org.jgraph.graph.DefaultGraphCell;

/**
 * Interface for creating JGraph's Participant object.
 *
 * @author Sasa Bojanic
 */
public abstract class GraphParticipantInterface extends DefaultGraphCell implements WorkflowElement {

   /**
    * Returns true if participant is a container for any other participant.
    */
    public boolean hasAnyParticipant () {
       for (int i=0; i<getChildCount(); i++) {
          if (getChildAt(i) instanceof GraphParticipantInterface) {
             return true;
          }
       }
       return false;
    }

    /**
    * Returns the number of participant's children participants (number of
    * participants for which it is a container).
    */
    public int howManyChildParticipants () {
       int cd=0;
       for (int i=0; i<getChildCount(); i++) {
          if (getChildAt(i) instanceof GraphParticipantInterface) {
             cd++;
          }
       }
       return cd;
    }

    /**
    * Returns participant's children participants (participants for which
    * it is a container).
    */
    public Set getChildParticipants () {
       Set childParticipants=new HashSet();
       for (int i=0; i<getChildCount(); i++) {
          Object child=getChildAt(i);
          if (child instanceof GraphParticipantInterface) {
             childParticipants.add(child);
          }
       }
       return childParticipants;
    }

	/**
   * Returns true if participant is a container for any activity.
   * <BR>NOTE: subflow is a kind of activity.
   */
   public boolean hasAnyActivityOrArtifact () {
      for (int i=0; i<getChildCount(); i++) {
         if (getChildAt(i) instanceof GraphActivityInterface || getChildAt(i) instanceof GraphArtifactInterface) {
            return true;
         }
      }
      return false;
   }

   /**
   * Returns the number of participant's children activities (number of
   * activities for which it is a container).
   * <BR>NOTE: subflow is a kind of activity.
   */
   public int howManyChildActivitiesOrArtifacts () {
      int ca=0;
      for (int i=0; i<getChildCount(); i++) {
         if (getChildAt(i) instanceof GraphActivityInterface || getChildAt(i) instanceof GraphArtifactInterface) {
            ca++;
         }
      }
      return ca;
   }

   /**
   * Returns participant's children activities (activities
   * for which it is a container).
   * <BR>NOTE: subflow is a kind of activity.
   */
   public Set getChildActivitiesAndArtifacts () {
      Set childActivitiesAndArtifacts=new HashSet();
      for (int i=0; i<getChildCount(); i++) {
         Object child=getChildAt(i);
         if (child instanceof GraphActivityInterface || child instanceof GraphArtifactInterface) {
            childActivitiesAndArtifacts.add(child);
         }
      }
      return childActivitiesAndArtifacts;
   }


}
