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
import java.util.List;
import java.util.Set;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.tooltip.TooltipGenerator;
import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.elements.Lane;
import org.enhydra.jxpdl.elements.Performer;
import org.enhydra.jxpdl.elements.Pool;

/**
 * Used to define swimlane object in graph and to modify it's properties. Swimlane is a
 * container for other swimlanes (Pools and Lanes) and also activities, artifacts and
 * their connecting objects (transitions and associations).
 */
public class DefaultGraphSwimlane extends GraphSwimlaneInterface {

   /**
    * Creates swimlane graph object for the given XPDL Pool.
    * 
    * @param pool {@link Pool} instance.
    */
   public DefaultGraphSwimlane(Pool pool) {
      super();
      setUserObject(pool);
   }

   /**
    * Creates swimlane graph object for the given XPDL Lane.
    * 
    * @param lane {@link Lane} instance.
    */
   public DefaultGraphSwimlane(Lane lane) {
      super();
      setUserObject(lane);
   }

   public boolean hasAnySwimlane() {
      for (int i = 0; i < getChildCount(); i++) {
         if (getChildAt(i) instanceof GraphSwimlaneInterface) {
            return true;
         }
      }
      return false;
   }

   public int howManyChildSwimlanes() {
      int cd = 0;
      for (int i = 0; i < getChildCount(); i++) {
         if (getChildAt(i) instanceof GraphSwimlaneInterface) {
            cd++;
         }
      }
      return cd;
   }

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

   public XMLComplexElement getPropertyObject() {
      if (userObject instanceof Pool || userObject instanceof Lane) {
         return (XMLComplexElement) userObject;
      }
      return null;
   }

   public String getTooltip() {
      TooltipGenerator ttg = JaWEManager.getInstance().getTooltipGenerator();
      if (userObject != null && ttg != null) {
         return ttg.getTooltip(getPropertyObject());
      }
      return "";
   }

   public String toString() {
      String name = null;
      String alias = null;
      if (userObject != null) {
         name = getPropertyObject().get("Name").toValue();
         if (name.equals("")) {
            name = getPropertyObject().get("Id").toValue();
         }
         XMLComplexElement aliasObject = null;
         if (userObject instanceof Pool) {
            aliasObject = JaWEManager.getInstance()
               .getXPDLUtils()
               .getProcessForPool((Pool) userObject);
         } else {
            aliasObject = GraphUtilities.getParticipantForLane((Lane) userObject, null);
         }
         if (aliasObject == null) {
            alias = name;
            if (userObject instanceof Lane) {
               List perfs = ((Lane) userObject).getPerformers().toElements();
               if (perfs.size() > 0) {
                  alias = ((Performer) perfs.get(0)).toValue();
               }
            }
         } else {
            alias = aliasObject.get("Name").toValue();
            if (alias.equals("")) {
               alias = aliasObject.get("Id").toValue();
            }
         }
      }
      return alias;
   }

   /**
    * @return null.
    */
   protected Object cloneUserObject() {
      return null;
   }

   public String getType() {
      if (userObject instanceof XMLElement) {
         return JaWEManager.getInstance()
            .getJaWEController()
            .getTypeResolver()
            .getJaWEType((XMLElement) userObject)
            .getTypeId();
      }
      return "";
   }
}
