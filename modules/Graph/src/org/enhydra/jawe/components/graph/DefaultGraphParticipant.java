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
