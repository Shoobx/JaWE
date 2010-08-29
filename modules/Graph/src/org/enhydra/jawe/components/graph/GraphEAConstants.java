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

import java.util.Arrays;
import java.util.List;


/**
* Used to hold various static variables to be easily acessed
* from anywhere.
*/
public class GraphEAConstants {

   // ****** WORKFLOW PROCESSS
   public static final String EA_JAWE_GRAPH_WORKFLOW_PARTICIPANT_ORIENTATION = "JaWE_GRAPH_WORKFLOW_PARTICIPANT_ORIENTATION";
   public static final String EA_JAWE_GRAPH_BLOCK_PARTICIPANT_ORIENTATION = "JaWE_GRAPH_BLOCK_PARTICIPANT_ORIENTATION";
   public static final String EA_JAWE_GRAPH_PARTICIPANT_ORIENTATION_VALUE_HORIZONTAL = "HORIZONTAL";
   public static final String EA_JAWE_GRAPH_PARTICIPANT_ORIENTATION_VALUE_VERTICAL = "VERTICAL";

   public static final String EA_JAWE_GRAPH_WORKFLOW_PARTICIPANT_ORDER = "JaWE_GRAPH_WORKFLOW_PARTICIPANT_ORDER";
   public static final String EA_JAWE_GRAPH_BLOCK_PARTICIPANT_ORDER = "JaWE_GRAPH_BLOCK_PARTICIPANT_ORDER";
   
   public static final String EA_JAWE_GRAPH_START_OF_WORKFLOW = "JaWE_GRAPH_START_OF_WORKFLOW";
   public static final String EA_JAWE_GRAPH_END_OF_WORKFLOW = "JaWE_GRAPH_END_OF_WORKFLOW";
   public static final String EA_JAWE_GRAPH_START_OF_BLOCK = "JaWE_GRAPH_START_OF_BLOCK";
   public static final String EA_JAWE_GRAPH_END_OF_BLOCK = "JaWE_GRAPH_END_OF_BLOCK";

   public static final String EA_JAWE_GRAPH_WORKFLOW_PARTICIPANT_ORDER_OLD = "ParticipantVisualOrder";
   public static final String EA_JAWE_GRAPH_BLOCK_PARTICIPANT_ORDER_OLD = "ParticipantVisualOrder";
   
   public static final String EA_JAWE_GRAPH_START_OF_WORKFLOW_OLD = "StartOfWorkflow";
   public static final String EA_JAWE_GRAPH_END_OF_WORKFLOW_OLD = "EndOfWorkflow";
   public static final String EA_JAWE_GRAPH_START_OF_BLOCK_OLD = "StartOfBlock";
   public static final String EA_JAWE_GRAPH_END_OF_BLOCK_OLD = "EndOfBlock";
   
   // ****** PARTICIPANT   
   public static final String PARTICIPANT_TYPE_FREE_TEXT_EXPRESSION = "PARTICIPANT_FREE_TEXT_EXPRESSION";
   public static final String PARTICIPANT_TYPE_COMMON_EXPRESSION = "PARTICIPANT_COMMON_EXPRESSION";
   public static final String COMMON_EXPRESSION_PARTICIPANT_PREFIX="### CEP -> ";
   public static final String COMMON_EXPRESSION_PARTICIPANT_SUFIX=" <- CEP ###";

   // ****** ACTIVITY
   public static final String EA_JAWE_GRAPH_PARTICIPANT_ID = "JaWE_GRAPH_PARTICIPANT_ID";
   public static final String EA_JAWE_GRAPH_OFFSET = "JaWE_GRAPH_OFFSET";

   public static final String EA_JAWE_GRAPH_PARTICIPANT_ID_OLD = "ParticipantID";
   public static final String EA_JAWE_GRAPH_OFFSET_OLD_X = "XOffset";
   public static final String EA_JAWE_GRAPH_OFFSET_OLD_Y = "YOffset";
   
   // ****** TRANSITION
   public static final String EA_JAWE_GRAPH_BREAK_POINTS = "JaWE_GRAPH_BREAK_POINTS";
   public static final String EA_JAWE_GRAPH_TRANSITION_STYLE = "JaWE_GRAPH_TRANSITION_STYLE";
   public static final String EA_JAWE_GRAPH_TRANSITION_STYLE_VALUE_NO_ROUTING_BEZIER = "NO_ROUTING_BEZIER";
   public static final String EA_JAWE_GRAPH_TRANSITION_STYLE_VALUE_NO_ROUTING_ORTHOGONAL = "NO_ROUTING_ORTHOGONAL";
   public static final String EA_JAWE_GRAPH_TRANSITION_STYLE_VALUE_NO_ROUTING_SPLINE = "NO_ROUTING_SPLINE";
   public static final String EA_JAWE_GRAPH_TRANSITION_STYLE_VALUE_SIMPLE_ROUTING_BEZIER = "SIMPLE_ROUTING_BEZIER";
   public static final String EA_JAWE_GRAPH_TRANSITION_STYLE_VALUE_SIMPLE_ROUTING_ORTHOGONAL = "SIMPLE_ROUTING_ORTHOGONAL";
   public static final String EA_JAWE_GRAPH_TRANSITION_STYLE_VALUE_SIMPLE_ROUTING_SPLINE = "SIMPLE_ROUTING_SPLINE";
   
   public static final String EA_JAWE_GRAPH_TRANSITION_STYLE_OLD = "RoutingType";
   public static final String EA_JAWE_GRAPH_BREAK_POINTS_OLD = "BreakPoint";
   
   // ****** EXTENDED ATTRIBUTE PARTS
   public static final String EA_PART_ACTIVITY_SET_ID = "ACTIVITY_SET_ID";
   public static final String EA_PART_CONNECTING_ACTIVITY_ID = "CONNECTING_ACTIVITY_ID";
   public static final String EA_PART_GRAPH_OFFSET_X = "X_OFFSET";
   public static final String EA_PART_GRAPH_OFFSET_Y = "Y_OFFSET";
   public static final String EA_PART_TYPE = "TYPE";

   // ******** BUBBLES
   public static final String START_TYPE = "START";
   public static final String START_TYPE_DEFAULT = "START_DEFAULT";

   public static final String END_TYPE = "END";
   public static final String END_TYPE_DEFAULT = "END_DEFAULT";
   
   // ******** SELECT
   public static final String SELECT_TYPE = "SELECT";
   public static final String SELECT_TYPE_DEFAULT = "SELECT_DEFAULT";
 
   
   // ******* PORT
   public static final String PORT_TYPE_DEFAULT="DEFAULT";

   public static List transitionStyles = Arrays.asList(new Object[] {
         GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE_VALUE_NO_ROUTING_BEZIER,
         GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE_VALUE_NO_ROUTING_ORTHOGONAL,
         GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE_VALUE_NO_ROUTING_SPLINE,
         GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE_VALUE_SIMPLE_ROUTING_BEZIER,
         GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE_VALUE_SIMPLE_ROUTING_ORTHOGONAL,
         GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE_VALUE_SIMPLE_ROUTING_SPLINE });

   
}
