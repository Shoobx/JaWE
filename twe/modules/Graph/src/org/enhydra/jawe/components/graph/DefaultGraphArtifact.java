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

import java.awt.Point;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.tooltip.TooltipGenerator;
import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.XPDLConstants;
import org.enhydra.jxpdl.elements.Artifact;

/**
 * Used to define Artifact object in graph and to modify it's properties.
 * 
 * @author Sasa Bojanic
 */
public class DefaultGraphArtifact extends GraphArtifactInterface {

   /**
    * Creates artifact with given userObject. Also creates default port for holding
    * artifact's associations.
    */
   public DefaultGraphArtifact(Artifact act) {
      setUserObject(act);
      addAPort();
   }

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
         .getJaWEType((Artifact) userObject)
         .getTypeId();
   }

   /**
    * Gets the port associate with this artifact.
    */
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
    * Returns <code>true</code> if Artifact is a valid source for transition.
    */
   public boolean acceptsSource() {
      return true;
   }

   /**
    * Returns <code>true</code> if Artifact is a valid target for transition.
    */
   public boolean acceptsTarget() {
      return true;
   }

   public XMLComplexElement getPropertyObject() {
      if (userObject instanceof XMLComplexElement) {// Harald Meister: fixes a rare bug
         return (XMLComplexElement) userObject;
      }
      return null;// Harald Meister
   }

   /**
    * Gets a tooltip text for activity.
    */
   public String getTooltip() {
      TooltipGenerator ttg = JaWEManager.getInstance().getTooltipGenerator();
      if (userObject != null && ttg != null) {
         return ttg.getTooltip(getPropertyObject());
      }
      return "";
   }

   /**
    * Gets an activity "display name" property.
    */
   public String toString() {
      String name = null;
      if (userObject != null) {
         Artifact art = (Artifact) userObject;
         if (art.getArtifactType().equals(XPDLConstants.ARTIFACT_TYPE_ANNOTATION)) {
            name = art.getTextAnnotation();
         } else {
            name = art.getDataObject().getName();
         }
         if (name==null || "".equals(name)) {
            name = art.getName();
         }
         if (name.equals("")) {
            name = art.getId();
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
      DefaultGraphArtifact c = (DefaultGraphArtifact) super.clone();
      c.setUserObject(c.userObject);
      return c;
   }

   /**
    * Create a clone of the ActivityProperties object.
    * 
    * @return Object a clone of this activity property object.
    */
   protected Object cloneUserObject() {
      return ((Artifact) userObject).clone();
   }

   /**
    * Gets all activities that reference this one.
    */
   public Set getReferencingActivities() {
      Set referencingActivities = new HashSet();
      for (Iterator i = getPort().edges(); i.hasNext();) {
         GraphTransitionInterface l = (GraphTransitionInterface) i.next();
         GraphCommonInterface target = ((GraphPortInterface) l.getTarget()).getActivityOrArtifact();
         if (this == target) {
            referencingActivities.add(((GraphPortInterface) l.getSource()).getActivityOrArtifact());
         }
      }
      return referencingActivities;
   }

   /**
    * Gets all activities that this activity references.
    */
   public Set getReferencedActivities() {
      Set referencedActivities = new HashSet();
      for (Iterator i = getPort().edges(); i.hasNext();) {
         GraphTransitionInterface l = (GraphTransitionInterface) i.next();
         GraphCommonInterface source = ((GraphPortInterface) l.getSource()).getActivityOrArtifact();
         if (this == source) {
            referencedActivities.add(((GraphPortInterface) l.getTarget()).getActivityOrArtifact());
         }
      }
      return referencedActivities;
   }

   public Point getOffset() {
      return GraphUtilities.getOffsetPoint((Artifact) userObject);
   }

}
