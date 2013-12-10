package org.enhydra.jawe.components.graph.actions;

import javax.swing.ImageIcon;

import org.enhydra.jawe.components.graph.GraphController;
import org.enhydra.jawe.components.graph.GraphEAConstants;
import org.enhydra.jawe.components.graph.GraphSettings;

/**
 * Inserts the special (virtual) participant in the graph that represents
 * the expression for the performer of each activity being put inside it.
 * There could be any number of such (virtual) participants in the graph,
 * and they will be represented as the part of extended attribute in the
 * XPDL model.
 * @author Sasa Bojanic
 */
public class SetParticipantModeCommonExpression extends SetParticipantMode {

   public SetParticipantModeCommonExpression (GraphController jawecomponent) {
      super(jawecomponent,GraphEAConstants.PARTICIPANT_TYPE_COMMON_EXPRESSION);
   }

   public void enableDisableAction() {
      GraphController gc = (GraphController)jawecomponent;
      
      if (gc.getSelectedGraph() != null && !gc.getDisplayedXPDLObject().isReadOnly())      
         setEnabled(true);
      else      
         setEnabled(false);      
   }
   
   protected ImageIcon getIcon () {
      return ((GraphSettings)jawecomponent.getSettings()).getCommonExpresionParticipantIcon();
   }

}
