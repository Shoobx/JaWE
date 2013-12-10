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

import org.enhydra.jawe.ResourceManager;
import org.enhydra.shark.xpdl.XMLComplexElement;
import org.enhydra.shark.xpdl.elements.ExtendedAttribute;

/**
 * Used to define End object.
 * @author Sasa Bojanic
 */
public class DefaultGraphBubbleActivity extends GraphBubbleActivityInterface {

   protected boolean isStart;
   protected String type;
   
   /**
    * Creates End with given userObject.
    */
   public DefaultGraphBubbleActivity (ExtendedAttribute ea) {
      setUserObject(ea);
      StartEndDescription sed=getStartEndDescription();
      isStart=sed.isStart();
      type=sed.getType();
      addAPort();
   }
   
   protected void addAPort () {
      // Create Port
      // Floating Center Port (Child 0 is Default)
      GraphPortInterface port=GraphUtilities.getGraphController().getGraphObjectFactory().createPort("Center",GraphEAConstants.PORT_TYPE_DEFAULT);
      add(port);
   }
   
   public String getType () {
      return type;
   }
   
   public boolean isStart () {
      return isStart;
   }

   public StartEndDescription getStartEndDescription () {
      return new StartEndDescription((ExtendedAttribute)userObject);
   }
   
   /**
    * Gets the port associate with this activity.
    */
   public GraphPortInterface getPort () {
      for (Enumeration e=children();e.hasMoreElements();) {
         Object child=e.nextElement();
         if (child instanceof GraphPortInterface) {
            return (GraphPortInterface)child;
         }
      }
      return null;
   }
      
   public XMLComplexElement getPropertyObject () {
      if (userObject instanceof XMLComplexElement){//Harald Meister: fixes a rare bug
         return (XMLComplexElement)userObject;
      }
      return null;//Harald Meister
   }

   /**
    * Gets a tooltip text for activity.
    */
   public String getTooltip () {
      return "";
   }

   
   /**
    * Returns <code>true</code> if End is a valid target for transition.
    * This depends if there is any transition already connected to it.
    */
   public boolean acceptsTarget() {
      if (isStart()) {
         return false;
      } 
      if (getReferencingActivities().size()<1) {
         return true;
      }
      return false;
   }

   public boolean acceptsSource() {
      if (isStart()) {
         if (getReferencedActivities().size()<1) {
            return true;
         }
         return false;
      } 
      return false;
   }
   
   /**
    *
    */
   public String toString () {
      String toRet=ResourceManager.getLanguageDependentString("EndKey");
      if (isStart()) {
         toRet=ResourceManager.getLanguageDependentString("StartKey");
      }
      if (toRet!=null) {
         return toRet;
      } 
      return "";      
   }

   /**
    * Create a clone of the cell. The cloning of the
    * user object is deferred to the cloneUserObject()
    * method.
    * NOTE: this original method of DefaultGraphCell is
    *       modified to retain synchronization of userObject and
    *       value attribute from attribute map when model
    *       is attribute store
    *
    * @return Object  a clone of this object.
    */
   public Object clone() {
      DefaultGraphBubbleActivity c = (DefaultGraphBubbleActivity)super.clone();
      c.setUserObject(c.userObject);
      return c;
   }
   /**
    * Create a clone of the ActivityProperties object.
    * @return Object a clone of this activity property object.
    */
   protected Object cloneUserObject() {
      return ((ExtendedAttribute)userObject).clone();
   }


   /**
    * Gets all activities that reference this one.
    */
   public Set getReferencingActivities () {
      Set referencingActivities=new HashSet();
      for( Iterator i = getPort().edges(); i.hasNext();){
         GraphTransitionInterface l = (GraphTransitionInterface)i.next();
         Object target = ((GraphPortInterface)(l.getTarget())).getParent();
         if(this==target) {
            referencingActivities.add(((GraphPortInterface)(l.getSource())).getParent());
         }
      }
      return referencingActivities;
   }

   /**
    * Gets all activities that this activity references.
    */
   public Set getReferencedActivities () {
      Set referencedActivities=new HashSet();
      for( Iterator i = getPort().edges(); i.hasNext();){
         GraphTransitionInterface l = (GraphTransitionInterface)i.next();
         Object source = ((GraphPortInterface)(l.getSource())).getParent();
         if(this==source) {
            referencedActivities.add(((GraphPortInterface)(l.getTarget())).getParent());
         }
      }
      return referencedActivities;
   }

   public Point getOffset () {
      return getStartEndDescription().getOffset();
   }

}
