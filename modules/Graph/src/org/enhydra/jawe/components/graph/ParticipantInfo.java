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

import org.enhydra.shark.xpdl.elements.Activity;
import org.enhydra.shark.xpdl.elements.Participant;


/**
 * Info structure for Participants.
 *
 * @author Sasa Bojanic
 */
public final class ParticipantInfo {
   
   private Participant participant;
   private List activities=new ArrayList();
   
   public ParticipantInfo (Participant p) {
      this.participant=p;
   }
   public Participant getParticipant() {
      return this.participant;
   }
   public void setParticipant(Participant participant) {
      this.participant = participant;
   }
   public void addActivity(Activity act) {
      activities.add(act);
   }
   public void removeActivity(Activity act) {
      activities.remove(act);
   }
   public List getActivities () {
      return activities;
   }
   public void setActivities (List activities) {
      this.activities=activities;
   }
 
   public String toString () {
      String name=participant.getName();
      if (name.equals("")){
         name=participant.getId();
      }
      String ret="p="+name+", acts=";
      Iterator it=activities.iterator();
      while (it.hasNext()) {
         Activity act=(Activity)it.next();
         name=act.getName();
         if (name.equals("")) {
            name=act.getId();
         }
         ret+=name+"-pId="+GraphUtilities.getParticipantId(act)+";";
      }
      ret+="\n";
      return ret;
   }
}
