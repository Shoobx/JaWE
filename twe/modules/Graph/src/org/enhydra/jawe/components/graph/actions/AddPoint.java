package org.enhydra.jawe.components.graph.actions;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.controller.JaWEController;
import org.enhydra.jawe.components.graph.Graph;
import org.enhydra.jawe.components.graph.GraphController;
import org.enhydra.jawe.components.graph.GraphMarqueeHandler;
import org.enhydra.jawe.components.graph.GraphTransitionInterface;
import org.enhydra.jawe.components.graph.GraphUtilities;
import org.enhydra.shark.xpdl.elements.Transition;

/**
* Class that realizes <B>AddPoint</B> action.
* "Breaking point" is added on transition at the popup position.
* @author Sasa Bojanic
*/
public class AddPoint extends ActionBase {

   public AddPoint (JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {
      JaWEController jc = JaWEManager.getInstance().getJaWEController();
      GraphController gc=(GraphController)jawecomponent;
      Graph g=gc.getSelectedGraph();
      if (getPackage() == jc.getMainPackage() && g!=null) {
         Object[] sc=g.getSelectionCells();
         if (sc!=null && sc.length==1 && sc[0] instanceof GraphTransitionInterface) {
            setEnabled(true);
         }
      } else {      
         setEnabled(false);
      }
   }
   
   public void actionPerformed(ActionEvent e) {
      GraphController gc=(GraphController)jawecomponent;
      GraphMarqueeHandler mh = gc.getGraphMarqueeHandler();
      Graph graph=gc.getSelectedGraph();
      Point addAt=mh.getPopupPoint();
//      double scale = gc.getSelectedGraph().getScale();
//      addAt.setLocation(addAt.getX()/scale, addAt.getY()/scale);      
      Object cell=graph.getSelectionCell();
      if (cell instanceof GraphTransitionInterface) {
         GraphTransitionInterface gtra=(GraphTransitionInterface)cell;
         JaWEController jc=JaWEManager.getInstance().getJaWEController();      
         gc.setUpdateInProgress(true);
         jc.startUndouableChange();
         List pnts=graph.getGraphManager().addOrRemoveBreakPoint(gtra, addAt, true);
         Transition tra=(Transition)gtra.getPropertyObject();
         GraphUtilities.setBreakpoints(tra, pnts);
         List toSelect=new ArrayList();
         toSelect.add(tra);      
         jc.endUndouableChange(toSelect);
         gc.setUpdateInProgress(false);
      }
   }
}
