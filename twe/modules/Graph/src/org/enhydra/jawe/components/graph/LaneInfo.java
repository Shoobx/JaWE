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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jxpdl.XMLCollectionElement;
import org.enhydra.jxpdl.elements.Lane;

/**
 * Info structure for Lanes.
 * 
 * @author Sasa Bojanic
 */
public final class LaneInfo {

   private Lane lane;

   private List activitiesAndArtifacts = new ArrayList();

   public LaneInfo(Lane p) {
      this.lane = p;
   }

   public Lane getLane() {
      return this.lane;
   }

   public void setLane(Lane lane) {
      this.lane = lane;
   }

   public void addActivityOrArtifact(XMLCollectionElement actOrArtif) {
      activitiesAndArtifacts.add(actOrArtif);
   }

   public void removeActivityOrArtifact(XMLCollectionElement actOrArtif) {
      activitiesAndArtifacts.remove(actOrArtif);
   }

   public List getActivitiesAndArtifacts() {
      return activitiesAndArtifacts;
   }

   public void setActivitiesAndArtifacts(List activities) {
      this.activitiesAndArtifacts = activities;
   }

   public String toString() {
      String name = lane.getName();
      if (name.equals("")) {
         name = lane.getId();
      }
      String ret = "p=" + name + ", acts=";
      Iterator it = activitiesAndArtifacts.iterator();
      while (it.hasNext()) {
         XMLCollectionElement actOrArtif = (XMLCollectionElement) it.next();
         name = actOrArtif.get("Name").toValue();
         if (name.equals("")) {
            name = actOrArtif.getId();
         }
         ret += name
                + "-pId="
                + JaWEManager.getInstance().getXPDLUtils().getLaneId(actOrArtif) + ";";
      }
      ret += "\n";
      return ret;
   }
}
