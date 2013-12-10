package org.enhydra.jawe.base.transitionhandler;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.shark.utilities.SequencedHashMap;
import org.enhydra.shark.xpdl.XMLCollectionElement;
import org.enhydra.shark.xpdl.XMLUtil;
import org.enhydra.shark.xpdl.elements.Activities;
import org.enhydra.shark.xpdl.elements.Activity;
import org.enhydra.shark.xpdl.elements.Transition;
import org.enhydra.shark.xpdl.elements.Transitions;

/**
 * Checks if it is allowed to add activity as a source or a target for a
 * transition, and if it is allowed to connect two activities.
 * 
 * @author Sasa Bojanic
 */
public class TransitionHandler {

   protected TransitionHandlerSettings settings;

   public TransitionHandler() {
      settings = new TransitionHandlerSettings();
      settings.init((JaWEComponent) null);
   }

   public TransitionHandler(TransitionHandlerSettings settings) {
      this.settings = settings;
      this.settings.init((JaWEComponent) null);
   }

   public boolean acceptsSource(Activity act, boolean isExcTrans) {
      boolean retVal = true;
      String type = JaWEManager.getInstance().getJaWEController().getTypeResolver().getJaWEType(act).getTypeId();
      boolean moreOutgoing = settings.canHaveMoreOutgoingTransition(type);

      if (!moreOutgoing) {
         Set nonExcOutgTras = XMLUtil.getNonExceptionalOutgoingTransitions(act);
         if (nonExcOutgTras.size() > 0 && !moreOutgoing && !isExcTrans) {
            retVal = false;
         }
      }

      return retVal;
   }

   protected boolean acceptsSource(Activity act, Transitions tras, boolean isExcTrans) {
      boolean retVal = true;
      String type = JaWEManager.getInstance().getJaWEController().getTypeResolver().getJaWEType(act).getTypeId();
      boolean moreOutgoing = settings.canHaveMoreOutgoingTransition(type);

      if (!moreOutgoing) {
         Set nonExcOutgTras = XMLUtil.getNonExceptionalOutgoingTransitions(act, tras);

         if (nonExcOutgTras.size() > 0 && !moreOutgoing && !isExcTrans) {
            retVal = false;
         }
      }

      return retVal;
   }

   public boolean acceptsTarget(Activity act) {
      boolean retVal = true;
      String type = JaWEManager.getInstance().getJaWEController().getTypeResolver().getJaWEType(act).getTypeId();
      boolean moreIncoming = settings.canHaveMoreIncomingTransition(type);

      if (!moreIncoming) {
         Set incTras = XMLUtil.getIncomingTransitions(act);

         if (incTras.size() > 0 && !moreIncoming) {
            retVal = false;
         }
      }

      return retVal;
   }

   protected boolean acceptsTarget(Activity act, Transitions tras) {
      boolean retVal = true;
      String type = JaWEManager.getInstance().getJaWEController().getTypeResolver().getJaWEType(act).getTypeId();
      boolean moreIncoming = settings.canHaveMoreIncomingTransition(type);

      if (!moreIncoming) {
         Set incTras = XMLUtil.getIncomingTransitions(act);

         if (incTras.size() > 0 && !moreIncoming) {
            retVal = false;
         }
      }

      return retVal;
   }

   /**
    * 
    * @param act1
    *           First activity
    * @param act2
    *           Last activity
    * @param status
    *           List with first parameter as Integer with status. 0 - everything
    *           is OK, 1 - source activity can't have more outgoing transitions,
    *           2 - target activity can't have more incoming transitions. 3 -
    *           can't connect two activities twice.
    * @return true if can connect these two activities.
    */
   public boolean allowsConnection(Activity act1, Activity act2, Transition t, boolean isExcTra, List status) {
      if (status != null)
         status.add(new Integer(0));
      String type1 = JaWEManager.getInstance().getJaWEController().getTypeResolver().getJaWEType(act1).getTypeId();
      String type2 = JaWEManager.getInstance().getJaWEController().getTypeResolver().getJaWEType(act2).getTypeId();

      boolean srcMoreOutgoing = settings.canHaveMoreOutgoingTransition(type1);
      boolean targetMoreIncoming = settings.canHaveMoreIncomingTransition(type2);

      // boolean isExceptionalTransition= (t==null ||
      // XMLUtil.isExceptionalTransition(t) || isExcTra);
      Set nonExcOutgTras1 = XMLUtil.getNonExceptionalOutgoingTransitions(act1);
      if (t != null)
         nonExcOutgTras1.remove(t);
      if (nonExcOutgTras1.size() > 0 && !srcMoreOutgoing && !isExcTra) {
         if (status != null)
            status.set(0, new Integer(1));
         return false;
      }

      Set incTras2 = XMLUtil.getIncomingTransitions(act2);
      if (t != null)
         incTras2.remove(t);
      if (incTras2.size() > 0 && !targetMoreIncoming) {
         if (status != null)
            status.set(0, new Integer(2));
         return false;
      }

      Set outgTras = XMLUtil.getOutgoingTransitions(act1);
      if (t != null)
         outgTras.remove(t);
      Iterator it = outgTras.iterator();
      while (it.hasNext()) {
         Transition tr = (Transition) it.next();
         if (tr.getTo().equals(act2.getId())) {
            if (status != null)
               status.set(0, new Integer(3));
            return false;
         }
      }

      return true;
   }

   /**
    * @return true if transition is properly connected.
    */
   public boolean isProperlyConnected(Transition t) {

      String tFrom = t.getFrom();
      Activity act1 = ((Activities) ((XMLCollectionElement) t.getParent().getParent()).get("Activities"))
            .getActivity(tFrom);

      String tTo = t.getTo();
      Activity act2 = ((Activities) ((XMLCollectionElement) t.getParent().getParent()).get("Activities"))
            .getActivity(tTo);

      if (act1 == null || act2 == null)
         return false;

      String type1 = JaWEManager.getInstance().getJaWEController().getTypeResolver().getJaWEType(act1).getTypeId();
      String type2 = JaWEManager.getInstance().getJaWEController().getTypeResolver().getJaWEType(act2).getTypeId();

      boolean srcMoreOutgoing = settings.canHaveMoreOutgoingTransition(type1);
      boolean targetMoreIncoming = settings.canHaveMoreIncomingTransition(type2);

      Set nonExcOutgTras1 = XMLUtil.getNonExceptionalOutgoingTransitions(act1, (Transitions) t.getParent());
      if (nonExcOutgTras1.size() > 1 && !srcMoreOutgoing && !XMLUtil.isExceptionalTransition(t)) {
         return false;
      }

      Set incTras2 = XMLUtil.getIncomingTransitions(act2, (Transitions) t.getParent());
      if (incTras2.size() > 1 && !targetMoreIncoming) {
         return false;
      }

      Iterator it = XMLUtil.getOutgoingTransitions(act1, (Transitions) t.getParent()).iterator();
      int count = 0;
      while (it.hasNext()) {
         t = (Transition) it.next();
         if (t.getTo().equals(act2.getId())) {
            count++;
            if (count > 1)
               return false;
         }
      }

      return true;
   }

   /**
    * 0 - everything is OK, 
    * 1 - activity can't have more outgoing transitions, 
    * 2 - activity can't have more incoming transitions, 
    * 3 - both, 1 and 2
    */
   public int isProperlyConnected(Activity act) {
      int ret = 0;

      String type = JaWEManager.getInstance().getJaWEController().getTypeResolver().getJaWEType(act).getTypeId();

      boolean moreOutgoing = settings.canHaveMoreOutgoingTransition(type);
      boolean moreIncoming = settings.canHaveMoreIncomingTransition(type);

      Set nonExcOutgTras1 = XMLUtil.getNonExceptionalOutgoingTransitions(act);
      if (nonExcOutgTras1.size() > 1 && !moreOutgoing) {
         ret=1;
      }

      Set incTras = XMLUtil.getIncomingTransitions(act);
      if (incTras.size() > 1 && !moreIncoming) {
         ret=ret+2;
      }

      return ret;
   }

   public SequencedHashMap getPossibleSourceActivities(Transition tra) {
      SequencedHashMap toRet = new SequencedHashMap();
      List acts = ((Activities) ((XMLCollectionElement) tra.getParent().getParent()).get("Activities")).toElements();
      Iterator it = acts.iterator();
      while (it.hasNext()) {
         Activity act = (Activity) it.next();
         if (acceptsSource(act, (Transitions) tra.getParent(), XMLUtil.isExceptionalTransition(tra))) {
            toRet.put(act.getId(), act);
         }
      }
      return toRet;
   }

   public SequencedHashMap getPossibleTargetActivities(Transition tra) {
      SequencedHashMap toRet = new SequencedHashMap();
      List acts = ((Activities) ((XMLCollectionElement) tra.getParent().getParent()).get("Activities")).toElements();
      Iterator it = acts.iterator();
      while (it.hasNext()) {
         Activity act = (Activity) it.next();
         if (acceptsTarget(act, (Transitions) tra.getParent())) {
            toRet.put(act.getId(), act);
         }
      }
      return toRet;
   }

}
