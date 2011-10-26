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

import java.awt.Point;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.tooltip.TooltipGenerator;
import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.XPDLConstants;
import org.enhydra.jxpdl.elements.Activity;
import org.enhydra.jxpdl.elements.Association;
import org.enhydra.jxpdl.elements.Transition;

/**
 * Used to define Activity object in graph and to modify it's properties.
 */
public class DefaultGraphActivity extends GraphActivityInterface {

   /**
    * Creates activity graph object for the given XPDL activity.
    */
   public DefaultGraphActivity(Activity act) {
      setUserObject(act);
      addAPort();
   }

   /** Adds a port for this object. */
   protected void addAPort() {
      // Create Port
      // Floating Center Port (Child 0 is Default)
      GraphPortInterface port = GraphUtilities.getGraphController()
         .getGraphObjectFactory()
         .createPort("Center", GraphEAConstants.PORT_TYPE_DEFAULT);
      add(port);
   }

   public String getType() {
      return JaWEManager.getInstance()
         .getJaWEController()
         .getTypeResolver()
         .getJaWEType((Activity) userObject)
         .getTypeId();
   }

   public GraphPortInterface getPort() {
      for (Enumeration e = children(); e.hasMoreElements();) {
         Object child = e.nextElement();
         if (child instanceof GraphPortInterface) {
            return (GraphPortInterface) child;
         }
      }
      return null;
   }

   /**
    * Returns <code>true</code> if Activity is a valid source for transition. This depends
    * of activitie's type property, e.g it rejects to be a source for transition if it is
    * an End event activity.
    */
   public boolean acceptsSource() {
      int actType = ((Activity) userObject).getActivityType();
      if (actType == XPDLConstants.ACTIVITY_TYPE_EVENT_START) {
         // if (getReferencedActivities().size() < 1) {
         return true;
         // }
         // return false;
      } else if (actType == XPDLConstants.ACTIVITY_TYPE_EVENT_END) {
         return false;
      }
      return true;
   }

   /**
    * Returns <code>true</code> if Activity is a valid target for transition. This depends
    * of activitie's type property, e.g. it rejects to be a target for for transition if
    * it is a Start event activity.
    */
   public boolean acceptsTarget() {
      int actType = ((Activity) userObject).getActivityType();
      if (actType == XPDLConstants.ACTIVITY_TYPE_EVENT_START) {
         return false;
      } else if (actType == XPDLConstants.ACTIVITY_TYPE_EVENT_END) {
         // if (getReferencingActivities().size() < 1) {
         return true;
         // }
         // return false;
      }

      return true;
   }

   public XMLComplexElement getPropertyObject() {
      if (userObject instanceof XMLComplexElement) {// Harald Meister: fixes a rare bug
         return (XMLComplexElement) userObject;
      }
      return null;// Harald Meister
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
      if (userObject != null) {
         name = getPropertyObject().get("Name").toValue();
         if (name.equals("")) {
            name = getPropertyObject().get("Id").toValue();
         }
      }
      return name;
   }

   /**
    * Create a clone of the cell. The cloning of the user object is deferred to the
    * cloneUserObject() method. NOTE: this original method of DefaultGraphCell is modified
    * to retain synchronization of userObject and value attribute from attribute map when
    * model is attribute store
    * 
    * @return Object a clone of this object.
    */
   public Object clone() {
      DefaultGraphActivity c = (DefaultGraphActivity) super.clone();
      c.setUserObject(c.userObject);
      return c;
   }

   /**
    * Create a clone of the Activity XPDL object.
    * 
    * @return Object a clone of this activity XPDL object.
    */
   protected Object cloneUserObject() {
      return ((Activity) userObject).clone();
   }

   public Set getReferencingActivities() {
      Set referencingActivities = new HashSet();
      for (Iterator i = getPort().edges(); i.hasNext();) {
         Object t = i.next();
         if (t instanceof GraphTransitionInterface) {
            GraphTransitionInterface l = (GraphTransitionInterface) t;
            GraphCommonInterface target = ((GraphPortInterface) l.getTarget()).getActivityOrArtifact();
            if (this == target && l.getPropertyObject() instanceof Transition) {
               referencingActivities.add(((GraphPortInterface) l.getSource()).getActivityOrArtifact());
            }
         }
      }
      return referencingActivities;
   }

   public Set getReferencedActivities() {
      Set referencedActivities = new HashSet();
      for (Iterator i = getPort().edges(); i.hasNext();) {
         Object t = i.next();
         if (t instanceof GraphTransitionInterface) {
            GraphTransitionInterface l = (GraphTransitionInterface) t;
            GraphCommonInterface source = ((GraphPortInterface) l.getSource()).getActivityOrArtifact();
            if (this == source && l.getPropertyObject() instanceof Transition) {
               referencedActivities.add(((GraphPortInterface) l.getTarget()).getActivityOrArtifact());
            }
         }
      }
      return referencedActivities;
   }

   public Set getReferencingArtifacts() {
      Set referencingArtifacts = new HashSet();
      for (Iterator i = getPort().edges(); i.hasNext();) {
         Object t = i.next();
         if (t instanceof GraphTransitionInterface) {
            GraphTransitionInterface l = (GraphTransitionInterface) t;
            GraphCommonInterface target = ((GraphPortInterface) l.getTarget()).getActivityOrArtifact();
            if (this == target && l.getPropertyObject() instanceof Association) {
               referencingArtifacts.add(((GraphPortInterface) l.getSource()).getActivityOrArtifact());
            }
         }
      }
      return referencingArtifacts;
   }

   public Set getReferencedArtifacts() {
      Set referencedArtifacts = new HashSet();
      for (Iterator i = getPort().edges(); i.hasNext();) {
         Object t = i.next();
         if (t instanceof GraphTransitionInterface) {
            GraphTransitionInterface l = (GraphTransitionInterface) t;
            GraphCommonInterface source = ((GraphPortInterface) l.getSource()).getActivityOrArtifact();
            if (this == source && l.getPropertyObject() instanceof Association) {
               referencedArtifacts.add(((GraphPortInterface) l.getTarget()).getActivityOrArtifact());
            }
         }
      }
      return referencedArtifacts;
   }

   public Point getOffset() {
      return GraphUtilities.getOffsetPoint((Activity) userObject);
   }

}
