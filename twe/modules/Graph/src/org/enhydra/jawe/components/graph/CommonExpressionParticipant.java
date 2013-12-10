package org.enhydra.jawe.components.graph;

import org.enhydra.jawe.ResourceManager;
import org.enhydra.shark.xpdl.XMLCollectionElement;
import org.enhydra.shark.xpdl.elements.Participant;

/**
 *  Represents imaginary participant for entering performer expressions.
 *
 *  @author Sasa Bojanic
 */
public class CommonExpressionParticipant extends Participant {

   protected XMLCollectionElement wpOrAs;
   
   public CommonExpressionParticipant (CommonExpressionParticipants parent,XMLCollectionElement wpOrAs) {
      super(parent);
      this.wpOrAs=wpOrAs;
      setDescription(ResourceManager.getLanguageDependentString("CommonExpressionParticipantDescription"));
      setReadOnly(true);
      this.isReadOnly=false;
      get("Id").setReadOnly(false);
   }

   public XMLCollectionElement getGraphXPDLElement () {
      return wpOrAs;
   }
      
}
