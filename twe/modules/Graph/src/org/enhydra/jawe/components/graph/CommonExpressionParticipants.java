/**
 * Together Workflow Editor
 * Copyright (C) 2010 Together Teamsolutions Co., Ltd. 
 * 
 * This program is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version. 
 *
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
 * GNU General Public License for more details. 
 *
 * You should have received a copy of the GNU General Public License 
 * along with this program. If not, see http://www.gnu.org/licenses
 */

package org.enhydra.jawe.components.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.enhydra.jawe.ResourceManager;
import org.enhydra.jxpdl.XMLCollectionElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.elements.ActivitySet;
import org.enhydra.jxpdl.elements.Participants;
import org.enhydra.jxpdl.elements.WorkflowProcess;

/**
 * Represents the collection of helper participants for entering common expressions for
 * activity performers.
 */
public class CommonExpressionParticipants extends Participants {

   /** The singleton instance of this class. */
   private static CommonExpressionParticipants instance;

   /**
    * Map that holds information about all WorkflowProcess/ActivitySet common expression
    * participants.
    */
   private Map wpOrAsToCEPs = new HashMap();

   /**
    * @return The singleton instance of this clas.
    */
   public static CommonExpressionParticipants getInstance() {
      if (instance == null) {
         instance = new CommonExpressionParticipants();
      }
      return instance;
   }

   /**
    * Default (private) constructor.
    */
   private CommonExpressionParticipants() {
      super((WorkflowProcess) null);
   }

   /**
    * This method should not be called - it throws runtime exception. Use method
    * generateCommonExpressionParticipant() instead.
    */
   public XMLElement generateNewElement() {
      throw new RuntimeException("Please use method generateCommonExpressionParticipant (XMLCollectionElement wpOrAs)");
   }

   /**
    * Creates new instance of {@link CommonExpressionParticipant}.
    * 
    * @param wpOrAs {@link WorkflowProcess} or {@link ActivitySet} instance.
    * @return {@link CommonExpressionParticipant} instance.
    */
   public CommonExpressionParticipant generateCommonExpressionParticipant(XMLCollectionElement wpOrAs) {
      CommonExpressionParticipant cep = new CommonExpressionParticipant(this, wpOrAs);
      int i = 1;
      String prefix = ResourceManager.getLanguageDependentString("CommonExpressionParticipantPrefix")
                      + " ";
      String id = prefix + String.valueOf(i);
      while (getCommonExpressionParticipant(wpOrAs, id) != null) {
         i++;
         id = prefix + String.valueOf(i);
      }
      cep.setId(id);
      Set ceps = (Set) wpOrAsToCEPs.get(wpOrAs);
      if (ceps == null) {
         ceps = new HashSet();
      }
      ceps.add(cep);
      wpOrAsToCEPs.put(wpOrAs, ceps);
      return cep;
   }

   /**
    * Retrieves a set of {@link CommonExpressionParticipant} instances for the given
    * {@link WorkflowProcess}/{@link ActivitySet}.
    * 
    * @param wpOrAs {@link WorkflowProcess} or {@link ActivitySet} instance.
    * @return A set of {@link CommonExpressionParticipant} instances for the given
    *         {@link WorkflowProcess}/{@link ActivitySet}.
    */
   public Set getCommonExpressionParticipants(XMLCollectionElement wpOrAs) {
      Set ceps = (Set) wpOrAsToCEPs.get(wpOrAs);
      if (ceps == null) {
         ceps = new HashSet();
      }
      return ceps;
   }

   /**
    * Searches for {@link CommonExpressionParticipant} with given Id for the given
    * {@link WorkflowProcess}/{@link ActivitySet}.
    * 
    * @param wpOrAs {@link WorkflowProcess} or {@link ActivitySet} instance.
    * @param id The Id of {@link CommonExpressionParticipant}.
    * @return The {@link CommonExpressionParticipant} instance.
    */
   public CommonExpressionParticipant getCommonExpressionParticipant(XMLCollectionElement wpOrAs,
                                                                     String id) {
      Set ceps = (Set) wpOrAsToCEPs.get(wpOrAs);
      CommonExpressionParticipant cep = null;
      if (ceps != null) {
         Iterator it = ceps.iterator();
         while (it.hasNext()) {
            CommonExpressionParticipant p = (CommonExpressionParticipant) it.next();
            if (p.getId().equals(id)) {
               cep = p;
               break;
            }
         }
      }
      if (cep != null) {
         // System.err.println("GETCEP -> Found cep "+id);
      } else {
         // System.err.println("GETCEP -> Can't find cep "+id);
      }
      return cep;
   }

   /**
    * Creates the value for extended attribute based on
    * {@link CommonExpressionParticipant} Id. It adds prefix and suffix to the Id.
    * 
    * @param pId Id of {@link CommonExpressionParticipant}.
    * @return The value for extended attribute based on
    *         {@link CommonExpressionParticipant} Id. It adds prefix and suffix to the Id.
    */
   public String getIdForVisualOrderEA(String pId) {
      return GraphEAConstants.COMMON_EXPRESSION_LANE_PREFIX
             + pId + GraphEAConstants.COMMON_EXPRESSION_LANE_SUFIX;
   }

   /**
    * Extracts Id of {@link CommonExpressionParticipant} from extended attribute value
    * which includes prefix and suffix.
    * 
    * @param pId {@link CommonExpressionParticipant} Id with prefix and sufix.
    * @return The Id of {@link CommonExpressionParticipant} without prefix and suffix.
    */
   public String getIdFromVisualOrderEA(String pId) {
      return pId.substring(GraphEAConstants.COMMON_EXPRESSION_LANE_PREFIX.length(),
                           pId.length()
                                 - GraphEAConstants.COMMON_EXPRESSION_LANE_SUFIX.length());
   }

   /**
    * Checks if given Id belongs to a {@link CommonExpressionParticipant}.
    * 
    * @param pId Participant Id.
    * @return true if given Id belongs to a {@link CommonExpressionParticipant}.
    */
   public boolean isCommonExpressionParticipantId(String pId) {
      return pId.startsWith(GraphEAConstants.COMMON_EXPRESSION_LANE_PREFIX)
             && pId.endsWith(GraphEAConstants.COMMON_EXPRESSION_LANE_SUFIX);
   }

}
