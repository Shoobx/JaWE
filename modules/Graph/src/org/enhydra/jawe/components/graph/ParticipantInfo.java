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
