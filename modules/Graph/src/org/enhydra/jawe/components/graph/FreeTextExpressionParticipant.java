package org.enhydra.jawe.components.graph;

import org.enhydra.jawe.ResourceManager;
import org.enhydra.shark.xpdl.elements.Participant;

/**
 *  Represents imaginary participant for entering performer expressions.
 *
 *  @author Sasa Bojanic
 */
public class FreeTextExpressionParticipant extends Participant {

   private static FreeTextExpressionParticipant instance;

   public static FreeTextExpressionParticipant getInstance () {
      if (instance==null) {
         instance=new FreeTextExpressionParticipant();
      }
      return instance;
   }

   private FreeTextExpressionParticipant () {
      super(null);
      setId("FreeTextExpressionParticipant");
      setName(ResourceManager.getLanguageDependentString("FreeTextExpressionParticipantKey"));
      setDescription(ResourceManager.getLanguageDependentString("FreeTextExpressionParticipantDescription"));      
      setReadOnly(true);
   }

}
