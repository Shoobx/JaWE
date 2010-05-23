package org.enhydra.jawe.components.graph.actions;

import javax.swing.ImageIcon;

import org.enhydra.jawe.components.graph.Graph;
import org.enhydra.jawe.components.graph.GraphController;
import org.enhydra.jawe.components.graph.GraphEAConstants;
import org.enhydra.jawe.components.graph.GraphSettings;

/**
/**
 * Inserts the special (virtual) participant in the graph that represents
 * the container for entering expressions for evaluating performer of each
 * activity being put inside it. Each activity contained inside this participant
 * can have its own performer expression.
 * There can be only one such (virtual) participant in the graph,
 * and it will be represented as the part of extended attribute in the
 * XPDL model.
 * @author Sasa Bojanic
 */
public class SetParticipantModeFreeTextExpression extends SetParticipantMode {

   public SetParticipantModeFreeTextExpression (GraphController jawecomponent) {
      super(jawecomponent,GraphEAConstants.PARTICIPANT_TYPE_FREE_TEXT_EXPRESSION);
   }

   public void enableDisableAction() {
      GraphController gc = (GraphController)jawecomponent;
      boolean en=false;
      Graph sg=gc.getSelectedGraph();
      if (sg!=null && !sg.getXPDLObject().isReadOnly()) {
         if (!sg.getGraphManager().isFreeTextExpressionParticipantShown()) {
            en=true;
         }
      }
      setEnabled(en);
   }

   protected ImageIcon getIcon () {
      return ((GraphSettings)jawecomponent.getSettings()).getFreeTextParticipantIcon();
   }

}
