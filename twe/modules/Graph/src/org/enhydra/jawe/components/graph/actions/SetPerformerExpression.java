package org.enhydra.jawe.components.graph.actions;

import java.awt.event.ActionEvent;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.components.graph.CommonExpressionParticipant;
import org.enhydra.jawe.components.graph.ExpressionParticipantEditor;
import org.enhydra.jawe.components.graph.Graph;
import org.enhydra.jawe.components.graph.GraphController;
import org.enhydra.jawe.components.graph.WorkflowElement;

/**
 * Sets the performer expression for all the activities contained inside
 * selected CommonExpressionParticipant.
 * @author Sasa Bojanic
 */
public class SetPerformerExpression extends ActionBase {

   public SetPerformerExpression (JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {
      GraphController gc = (GraphController)jawecomponent;
      
      boolean en=false;
      if (gc.getSelectedGraph() != null) {
         Graph g=gc.getSelectedGraph();
         Object[] scells=g.getSelectionCells();
         if (!g.getXPDLObject().isReadOnly() && scells!=null && scells.length==1 && ((WorkflowElement)scells[0]).getPropertyObject() instanceof CommonExpressionParticipant) {
            en=true;
         }
      }
      setEnabled(en);
   }
   
   public void actionPerformed(ActionEvent e) {
      GraphController gc = (GraphController)jawecomponent;
      if (gc.getSelectedGraph() != null) {
         Graph g=gc.getSelectedGraph();
         CommonExpressionParticipant cep=(CommonExpressionParticipant)((WorkflowElement)g.getSelectionCell()).getPropertyObject();
         ExpressionParticipantEditor ed=new ExpressionParticipantEditor(cep);
         ed.editXPDLElement();
      }
   }   
}
