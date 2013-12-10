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
import org.enhydra.jxpdl.elements.Artifact;

/**
 * Used to define Artifact object in graph and to modify it's properties.
 */
public class DefaultGraphArtifact extends GraphArtifactInterface {

   /**
    * Creates graph artifact object for the given XPDL object. Also creates default port
    * for holding artifact's associations.
    */
   public DefaultGraphArtifact(Artifact act) {
      setUserObject(act);
      addAPort();
   }

   /**
    * Adds a port to an artifact.
    */
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
    * Always returns <code>true</code>.
    */
   public boolean acceptsSource() {
      return true;
   }

   /**
    * Always returns <code>true</code>.
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
         Artifact art = (Artifact) userObject;
         if (art.getArtifactType().equals(XPDLConstants.ARTIFACT_TYPE_ANNOTATION)) {
            name = art.getTextAnnotation();
         } else {
            name = art.getDataObject().getName();
         }
         if (name == null || "".equals(name)) {
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
    * Create a clone of the XPDL Artifact object.
    * 
    * @return Object a clone of this artifact's XPDL object.
    */
   protected Object cloneUserObject() {
      return ((Artifact) userObject).clone();
   }

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
