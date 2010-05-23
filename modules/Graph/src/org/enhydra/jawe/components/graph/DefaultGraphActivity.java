package org.enhydra.jawe.components.graph;

import java.awt.Point;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.tooltip.TooltipGenerator;
import org.enhydra.shark.xpdl.XMLComplexElement;
import org.enhydra.shark.xpdl.elements.Activity;

/**
 * Used to define Activity object in graph and to modify it's
 * properties.
 *
 * @author Sasa Bojanic
 */
public class DefaultGraphActivity extends GraphActivityInterface {

   /**
    * Creates activity with given userObject. Also creates default port
    * for holding activity transitions.
    */
   public DefaultGraphActivity(Activity act) {
      setUserObject(act);
      addPort();
   }
   
   protected void addPort () {
      // Create Port
      // Floating Center Port (Child 0 is Default)
      GraphPortInterface port=GraphUtilities.getGraphController().getGraphObjectFactory().createPort("Center",GraphEAConstants.PORT_TYPE_DEFAULT);
      add(port);      
   }

   public String getType () {
      return JaWEManager.getInstance().getJaWEController().getTypeResolver().getJaWEType((Activity)userObject).getTypeId();
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

   /**
    * Returns <code>true</code> if Activity is a valid source for transition.
    * This depends of activitie's type property, it can accept to be a
    * source for transition if it is a normal or starting.
    */
   public boolean acceptsSource() {
      return true;
   }

   /**
    * Returns <code>true</code> if Activity is a valid target for transition.
    * This depends of activitie's type property, it can accept to be a
    * target for for transition if it is a normal or ending.
    */
   public boolean acceptsTarget() {
      return true;
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
      TooltipGenerator ttg=JaWEManager.getInstance().getTooltipGenerator();
      if (userObject!=null && ttg!=null) {
         return ttg.getTooltip(getPropertyObject());
      }
      return "";
   }

   /**
    * Gets an activity "display name" property.
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
      DefaultGraphActivity c = (DefaultGraphActivity)super.clone();
      c.setUserObject(c.userObject);
      return c;
   }
   /**
    * Create a clone of the ActivityProperties object.
    * @return Object a clone of this activity property object.
    */
   protected Object cloneUserObject() {
      return ((Activity)userObject).clone();
   }

   /**
    * Gets all activities that reference this one.
    */
   public Set getReferencingActivities () {
      Set referencingActivities=new HashSet();
      for( Iterator i = getPort().edges(); i.hasNext();){
         GraphTransitionInterface l = (GraphTransitionInterface)i.next();
         GraphActivityInterface target = ((GraphPortInterface)l.getTarget()).getActivity();
         if(this==target) {
            referencingActivities.add(((GraphPortInterface)l.getSource()).getActivity());
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
         GraphActivityInterface source = ((GraphPortInterface)l.getSource()).getActivity();
         if(this==source) {
            referencedActivities.add(((GraphPortInterface)l.getTarget()).getActivity());
         }
      }
      return referencedActivities;
   }

   public Point getOffset () {
      return GraphUtilities.getOffsetPoint((Activity)userObject);
   }
   
}
