package org.enhydra.jawe.components.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.enhydra.jawe.ResourceManager;
import org.enhydra.shark.xpdl.XMLCollectionElement;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.elements.Participants;
import org.enhydra.shark.xpdl.elements.WorkflowProcess;

/**
 * Represents collection of imaginary participants for entering 
 * common expressions for activity performers.
 *
 *  @author Sasa Bojanic
 */
public class CommonExpressionParticipants extends Participants {

   private static CommonExpressionParticipants instance;

   private Map wpOrAsToCEPs=new HashMap();
   
   public static CommonExpressionParticipants getInstance () {
      if (instance==null) {
         instance=new CommonExpressionParticipants();
      }
      return instance;
   }

   private CommonExpressionParticipants () {
      super((WorkflowProcess)null);
   }

   public XMLElement generateNewElement() {
      throw new RuntimeException("Please use method generateCommonExpressionParticipant (XMLCollectionElement wpOrAs)");
   }

   public CommonExpressionParticipant generateCommonExpressionParticipant (XMLCollectionElement wpOrAs) {
      CommonExpressionParticipant cep=new CommonExpressionParticipant(this,wpOrAs);
      int i=1;
      String prefix = ResourceManager.getLanguageDependentString("CommonExpressionParticipantPrefix") + " ";
      String id=prefix+String.valueOf(i);
      while (getCommonExpressionParticipant(wpOrAs, id)!=null) {         
         i++;
         id=prefix+String.valueOf(i);
      }
      cep.setId(id);
      Set ceps=(Set)wpOrAsToCEPs.get(wpOrAs);
      if (ceps==null) {
         ceps=new HashSet();
      }
      ceps.add(cep);
      wpOrAsToCEPs.put(wpOrAs,ceps);
      return cep;
   }

   public Set getCommonExpressionParticipants (XMLCollectionElement wpOrAs) {
      Set ceps=(Set)wpOrAsToCEPs.get(wpOrAs);
      if (ceps==null) {
         ceps=new HashSet();
      }
      return ceps;
   }

   public CommonExpressionParticipant getUpdatedCommonExpressionParticipant (List vo,XMLCollectionElement wpOrAs) {
      Set ceps=(Set)wpOrAsToCEPs.get(wpOrAs);
      CommonExpressionParticipant cep=null;
      if (ceps!=null) {
         Iterator it=ceps.iterator();
         while (it.hasNext()) {
            CommonExpressionParticipant p=(CommonExpressionParticipant)it.next();
            String idToSearch=
               GraphEAConstants.COMMON_EXPRESSION_PARTICIPANT_PREFIX+
               p.getId()+
               GraphEAConstants.COMMON_EXPRESSION_PARTICIPANT_SUFIX;
            if (!vo.contains(idToSearch)) {
               cep=p;
               break;
            }
         }
      }
      if (cep==null) {
//         System.err.println("CAN'T FIND UPDATEDCEP !!!");
      } else {
//         System.err.println("GETUPDATEDCEP -> "+cep.getId());         
      }
      return cep;
   }
   
   public void removeUnusedCommonExpressionParticipants (List vo,XMLCollectionElement wpOrAs) {
      List cepsToRemove=new ArrayList();
      Set s=(Set)wpOrAsToCEPs.get(wpOrAs);
      if (s!=null) {
         Iterator it=s.iterator();               
         while (it.hasNext()) {
            CommonExpressionParticipant cep=(CommonExpressionParticipant)it.next();
            String idToSearch=
               GraphEAConstants.COMMON_EXPRESSION_PARTICIPANT_PREFIX+
               cep.getId()+
               GraphEAConstants.COMMON_EXPRESSION_PARTICIPANT_SUFIX;
            if (!vo.contains(idToSearch)) {
               cepsToRemove.add(cep);
            }
         }
         it=cepsToRemove.iterator();
         while (it.hasNext()) {
            CommonExpressionParticipant cepToRemove=(CommonExpressionParticipant)it.next();
            s.remove(cepToRemove);
//            System.err.println("REMUNUSEDCEPS -> removing cep "+cepToRemove.getId());
         }
      }            
   }
   
   public CommonExpressionParticipant getCommonExpressionParticipant (XMLCollectionElement wpOrAs,String id) {
      Set ceps=(Set)wpOrAsToCEPs.get(wpOrAs);
      CommonExpressionParticipant cep=null;
      if (ceps!=null) {
         Iterator it=ceps.iterator();
         while (it.hasNext()) {
            CommonExpressionParticipant p=(CommonExpressionParticipant)it.next();
            if (p.getId().equals(id)) {
               cep=p;
               break;
            }
         }
      }
      if (cep!=null) {
//         System.err.println("GETCEP -> Found cep "+id);
      } else {
//         System.err.println("GETCEP -> Can't find cep "+id);
      }
      return cep;
   }
   
   public void printList (XMLCollectionElement wpOrAs) {
      Set ceps=getCommonExpressionParticipants(wpOrAs);
      Iterator it=ceps.iterator();
      int i=1;
      while (it.hasNext()) {
         System.err.println("cep "+(i++)+" is "+((CommonExpressionParticipant)it.next()).getId());
      }
   }
   
   public String getIdForVisualOrderEA (String pId) {
      return GraphEAConstants.COMMON_EXPRESSION_PARTICIPANT_PREFIX+pId+GraphEAConstants.COMMON_EXPRESSION_PARTICIPANT_SUFIX;
   }
   
   public String getIdFromVisualOrderEA (String pId) {
      return pId.substring(
            GraphEAConstants.COMMON_EXPRESSION_PARTICIPANT_PREFIX.length(),
            pId.length()-GraphEAConstants.COMMON_EXPRESSION_PARTICIPANT_SUFIX.length());      
   }
   
   public boolean isCommonExpressionParticipantId (String pId) {
      return pId.startsWith(GraphEAConstants.COMMON_EXPRESSION_PARTICIPANT_PREFIX) &&
            pId.endsWith(GraphEAConstants.COMMON_EXPRESSION_PARTICIPANT_SUFIX);      
   }
   
}
