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

import java.util.HashSet;
import java.util.Set;

import org.enhydra.jxpdl.elements.Activity;
import org.enhydra.jxpdl.elements.Artifact;
import org.jgraph.graph.DefaultGraphCell;

/**
 * Interface for creating JGraph's swimlane object (Pool or Lane).
 */
public abstract class GraphSwimlaneInterface extends DefaultGraphCell implements
                                                                     WorkflowElement {

   /**
    * @return true if this swimlane is a container for any other swimlane.
    */
   public boolean hasAnySwimlane() {
      for (int i = 0; i < getChildCount(); i++) {
         if (getChildAt(i) instanceof GraphSwimlaneInterface) {
            return true;
         }
      }
      return false;
   }

   /**
    * @return the number of swimlanes for which this swimlane is a container.
    */
   public int howManyChildSwimlanes() {
      int cd = 0;
      for (int i = 0; i < getChildCount(); i++) {
         if (getChildAt(i) instanceof GraphSwimlaneInterface) {
            cd++;
         }
      }
      return cd;
   }

   /**
    * @return A set of {@link GraphSwimlaneInterface} instances for which this swimlane is
    *         a container.
    */
   public Set getChildSwimlanes() {
      Set childParticipants = new HashSet();
      for (int i = 0; i < getChildCount(); i++) {
         Object child = getChildAt(i);
         if (child instanceof GraphSwimlaneInterface) {
            childParticipants.add(child);
         }
      }
      return childParticipants;
   }

   /**
    * @return true if swimlane is a container for any activity or artifact.
    */
   public boolean hasAnyActivityOrArtifact() {
      for (int i = 0; i < getChildCount(); i++) {
         if (getChildAt(i) instanceof GraphActivityInterface
             || getChildAt(i) instanceof GraphArtifactInterface) {
            return true;
         }
      }
      return false;
   }

   /**
    * @return The number of swimlane's children activities and artifacts (number of
    *         activities and artifacts for which it is a container).
    */
   public int howManyChildActivitiesOrArtifacts() {
      int ca = 0;
      for (int i = 0; i < getChildCount(); i++) {
         if (getChildAt(i) instanceof GraphActivityInterface
             || getChildAt(i) instanceof GraphArtifactInterface) {
            ca++;
         }
      }
      return ca;
   }

   /**
    * @return A set of {@link Activity} and {@link Artifact} instances which are contained
    *         within this swimlane.
    */
   public Set getChildActivitiesAndArtifacts() {
      Set childActivitiesAndArtifacts = new HashSet();
      for (int i = 0; i < getChildCount(); i++) {
         Object child = getChildAt(i);
         if (child instanceof GraphActivityInterface
             || child instanceof GraphArtifactInterface) {
            childActivitiesAndArtifacts.add(child);
         }
      }
      return childActivitiesAndArtifacts;
   }

}
