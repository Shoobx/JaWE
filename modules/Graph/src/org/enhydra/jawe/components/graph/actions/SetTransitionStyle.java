package org.enhydra.jawe.components.graph.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.controller.JaWEController;
import org.enhydra.jawe.components.graph.Graph;
import org.enhydra.jawe.components.graph.GraphController;
import org.enhydra.jawe.components.graph.GraphTransitionInterface;
import org.enhydra.jawe.components.graph.GraphUtilities;
import org.enhydra.shark.xpdl.elements.Transition;

/**
* Class that realizes <B>SetTransitionType</B> action.
* @author Sasa Bojanic
*/
public abstract class SetTransitionStyle extends ActionBase {

   protected String style;
   public SetTransitionStyle (JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public SetTransitionStyle (JaWEComponent jawecomponent,String style) {
      super(jawecomponent);
      this.style=style;
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
      Graph graph=gc.getSelectedGraph();
      Object cell=graph.getSelectionCell();
      if (cell instanceof GraphTransitionInterface) {
         JaWEController jc=JaWEManager.getInstance().getJaWEController();      
         gc.setUpdateInProgress(true);
         jc.startUndouableChange();
         GraphTransitionInterface gtra=(GraphTransitionInterface)cell;
         Transition tra=(Transition)gtra.getUserObject();
         GraphUtilities.setStyle(tra, style);
         graph.getGraphManager().updateStyle(gtra);
         List toSelect=new ArrayList();
         toSelect.add(tra);      
         JaWEManager.getInstance().getJaWEController().endUndouableChange(toSelect);
         gc.setUpdateInProgress(false);
      }
   }
}
