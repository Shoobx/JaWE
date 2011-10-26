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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jgraph.graph.ParentMap;

/**
* Describes relations between childs and parents.
*
*/
public class JaWEParentMap extends ParentMap {

   /**
   * Returns the list of parents that will be empty when
   * this parent map is applied.
   */
   public ArrayList emptyParentList() {
      ArrayList list = new ArrayList();
      Iterator it = childCount.entrySet().iterator();
      while (it.hasNext()) {
         Map.Entry entry = (Map.Entry) it.next();
         if (entry.getValue() instanceof Integer) {
            if (((Integer) entry.getValue()).intValue()==0)
               list.add(entry.getKey());
         }
      }
      return list;
   }

   /**
   * Returns true if Participant that is in changed nodes set has other Participants.
   * NOTE: because this method is not used when Participants are added to each other,
   *       method doesn't check for added participants but only for removed.
   */
   public boolean hasAnyParticipant (Object p) {
      if ((p==null) || !(p instanceof DefaultGraphSwimlane)) {
         return false;
      }
      // getting current state in model
      Set childParticipants=new HashSet(((DefaultGraphSwimlane)p).getChildSwimlanes());
      // removing from it all changedNodes - this means (considering a NOTE)
      // that these participants will be deleted after parent map is applied
      childParticipants.removeAll(changedNodes);

      return (childParticipants.size()>0);

   }


   /**
   * Returns all children which will belong to given parent
   * when this parent map is applied.
   */
   public ArrayList getNewChildren (Object parent) {
      ArrayList list = new ArrayList();
      if (parent!= null) {
         Iterator it = entries.iterator();
         while (it.hasNext()) {
            Entry entry = (Entry)it.next();
            Object parentFromEntry=entry.getParent();
            if (parentFromEntry != null  && parentFromEntry.equals(parent)) {
               list.add(entry.getChild());
            }
         }
      }
      return list;
   }

   /**
   * Returns parent of given child. The child will belong to returned
   * parent when this parent map is applied.
   */
   public Object getNewParent (Object child) {
      Object parent=null;
      if (child !=null) {
         Iterator it = entries.iterator();
         while (it.hasNext()) {
            Entry entry = (Entry)it.next();
            Object childFromEntry=entry.getChild();
            if (childFromEntry!=null && childFromEntry.equals(child)) {
               parent=entry.getParent();
               break;
            }
         }
      }
      return parent;
   }

   /**
   * Returns nodes that will be removed after this map is applied (These
   * are entries which parent part is null).
   */
   public ArrayList getRemovedNodes () {
      ArrayList list = new ArrayList();
      Iterator it = entries.iterator();
      while (it.hasNext()) {
         Entry entry = (Entry)it.next();
         Object parentFromEntry=entry.getParent();
         if (parentFromEntry == null) {
            list.add(entry.getChild());
         }
      }
      return list;
   }

   /**
   * Returns the number of entries in parent map.
   */
   public int entryCount () {
      return entries.size();
   }

}

/* End of PEParentMap.java */
