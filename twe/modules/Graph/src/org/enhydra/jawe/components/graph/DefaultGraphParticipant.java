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

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.tooltip.TooltipGenerator;
import org.enhydra.shark.xpdl.XMLComplexElement;
import org.enhydra.shark.xpdl.elements.Participant;

/**
 * Used to define Participant object in process. Participant is a container
 * for activities, subflows and other departmnet objects. Activities and
 * subflow can be moved from one to the other Participant.
 *
 * @author Sasa Bojanic
 */
public class DefaultGraphParticipant extends GraphParticipantInterface {

   /**
   * Creates Participant with given userObject.
   */
   public DefaultGraphParticipant(Participant par) {
      super();
      setUserObject(par);
   }


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

   public XMLComplexElement getPropertyObject () {
      if (userObject instanceof Participant) {
         return (XMLComplexElement)userObject;
      } 
      return null;      
   }


   /**
   * Gets a tooltip text for participant.
   */
   public String getTooltip () {
      TooltipGenerator ttg=JaWEManager.getInstance().getTooltipGenerator();
      if (userObject!=null && ttg!=null) {
         return ttg.getTooltip(getPropertyObject());
      }
      return "";
   }
   /**
   * Gets an participant name property.
   */
   public String toString () {
      String name=null;
      if (userObject!=null) {
         name=getPropertyObject().get("Name").toValue();
         if (name.equals("")) {
            name=getPropertyObject().get("Id").toValue();
         }
      }
      return name;
   }

   /**
   * Create a clone of the user object.
   * @return Object a clone of this participant property object.
   */
   protected Object cloneUserObject() {
      return null;
   }

   public String getType () {
      return JaWEManager.getInstance().getJaWEController().getTypeResolver().getJaWEType((Participant) userObject).getTypeId();     
   }
}
