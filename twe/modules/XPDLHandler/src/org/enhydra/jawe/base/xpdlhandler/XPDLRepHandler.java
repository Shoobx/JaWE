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

/**
 * Miroslav Popov, Apr 6, 2006 miroslav.popov@gmail.com
 */
package org.enhydra.jawe.base.xpdlhandler;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.enhydra.jawe.JaWEEAHandler;
import org.enhydra.jawe.components.graph.GraphEAConstants;
import org.enhydra.shark.xpdl.XMLCollection;
import org.enhydra.shark.xpdl.XMLComplexElement;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.XMLSimpleElement;
import org.enhydra.shark.xpdl.XMLUtil;
import org.enhydra.shark.xpdl.XPDLRepositoryHandler;
import org.enhydra.shark.xpdl.elements.ExtendedAttribute;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Miroslav Popov
 */
public class XPDLRepHandler extends XPDLRepositoryHandler {

   private static final List dontSave = Arrays.asList(new Object[] {
         GraphEAConstants.EA_JAWE_GRAPH_WORKFLOW_PARTICIPANT_ORIENTATION,
         GraphEAConstants.EA_JAWE_GRAPH_BLOCK_PARTICIPANT_ORIENTATION,
         GraphEAConstants.EA_JAWE_GRAPH_WORKFLOW_PARTICIPANT_ORDER,
         GraphEAConstants.EA_JAWE_GRAPH_BLOCK_PARTICIPANT_ORDER,
         GraphEAConstants.EA_JAWE_GRAPH_START_OF_WORKFLOW,
         GraphEAConstants.EA_JAWE_GRAPH_END_OF_WORKFLOW,
         GraphEAConstants.EA_JAWE_GRAPH_START_OF_BLOCK,
         GraphEAConstants.EA_JAWE_GRAPH_END_OF_BLOCK,
         GraphEAConstants.EA_JAWE_GRAPH_PARTICIPANT_ID,
         GraphEAConstants.EA_JAWE_GRAPH_OFFSET,
         GraphEAConstants.EA_JAWE_GRAPH_BREAK_POINTS,
         GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE,
         JaWEEAHandler.EA_EDITING_TOOL,
         JaWEEAHandler.EA_EDITING_TOOL_VERSION,
         JaWEEAHandler.EA_JAWE_EXTERNAL_PACKAGE_ID,
         JaWEEAHandler.EA_JAWE_CONFIGURATION,
         JaWEEAHandler.EA_JAWE_TYPE
   });

   private static final List dontLoad = Arrays.asList(new Object[] {
         GraphEAConstants.EA_JAWE_GRAPH_WORKFLOW_PARTICIPANT_ORIENTATION,
         GraphEAConstants.EA_JAWE_GRAPH_BLOCK_PARTICIPANT_ORIENTATION,
         GraphEAConstants.EA_JAWE_GRAPH_WORKFLOW_PARTICIPANT_ORDER,
         GraphEAConstants.EA_JAWE_GRAPH_BLOCK_PARTICIPANT_ORDER,
         GraphEAConstants.EA_JAWE_GRAPH_START_OF_WORKFLOW,
         GraphEAConstants.EA_JAWE_GRAPH_END_OF_WORKFLOW,
         GraphEAConstants.EA_JAWE_GRAPH_START_OF_BLOCK,
         GraphEAConstants.EA_JAWE_GRAPH_END_OF_BLOCK,
         GraphEAConstants.EA_JAWE_GRAPH_PARTICIPANT_ID,
         GraphEAConstants.EA_JAWE_GRAPH_OFFSET,
         GraphEAConstants.EA_JAWE_GRAPH_BREAK_POINTS,
         GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE,
         GraphEAConstants.EA_JAWE_GRAPH_PARTICIPANT_ID_OLD,
         GraphEAConstants.EA_JAWE_GRAPH_WORKFLOW_PARTICIPANT_ORDER_OLD,
         GraphEAConstants.EA_JAWE_GRAPH_BLOCK_PARTICIPANT_ORDER_OLD,
         GraphEAConstants.EA_JAWE_GRAPH_START_OF_WORKFLOW_OLD,
         GraphEAConstants.EA_JAWE_GRAPH_END_OF_WORKFLOW_OLD,
         GraphEAConstants.EA_JAWE_GRAPH_START_OF_BLOCK_OLD,
         GraphEAConstants.EA_JAWE_GRAPH_END_OF_BLOCK_OLD,
         GraphEAConstants.EA_JAWE_GRAPH_OFFSET_OLD_X,
         GraphEAConstants.EA_JAWE_GRAPH_OFFSET_OLD_Y,
         GraphEAConstants.EA_JAWE_GRAPH_TRANSITION_STYLE_OLD,
         GraphEAConstants.EA_JAWE_GRAPH_BREAK_POINTS_OLD
   });

   public void fromXML(Node node, XMLCollection cel) {
      if (node == null || !node.hasChildNodes())
         return;
      String nameSpacePrefix = XMLUtil.getNameSpacePrefix(node);

      XMLElement newOne = cel.generateNewElement();
      String elName = newOne.toName();

      NodeList children = node.getChildNodes();
      int lng = children.getLength();

      for (int i = 0; i < lng; i++) {
         Node child = children.item(i);
         if (child.getNodeName().equals(nameSpacePrefix + elName)) {
            newOne = cel.generateNewElement();
            
            if (newOne instanceof ExtendedAttribute) {               
               if (dontLoad.contains(child.getAttributes().item(0).getNodeValue()))                  
                  continue;               
            }
            
            if (newOne instanceof XMLComplexElement) {
               fromXML(children.item(i), (XMLComplexElement) newOne);
            } else {
               fromXML(children.item(i), (XMLSimpleElement) newOne);
            }
            cel.add(newOne);
         } 
      }
   }
   
   public void toXML(Node parent, XMLCollection cel) {
      if (!cel.isEmpty() || cel.isRequired()) {
         if (parent != null) {
            String elName = cel.toName();
            Node node = parent;
            // Specific code for handling Deadlines and Tools
            if (!(elName.equals("Deadlines") || elName.equals("Tools"))) {
               node = (parent.getOwnerDocument()).createElement(xpdlPrefix+elName);
            }
            for (Iterator it = cel.toElements().iterator(); it.hasNext();) {
               XMLElement el = (XMLElement) it.next();

               if (el instanceof ExtendedAttribute) {
                  ExtendedAttribute extA = (ExtendedAttribute) el;

                  if (dontSave.contains(extA.getName()))
                     continue;
               }

               if (el instanceof XMLSimpleElement) {
                  toXML(node, (XMLSimpleElement) el);
               } else {
                  toXML(node, (XMLComplexElement) el);
               }
            }
            // If Deadlines or Tools are handled, node==parent
            if (node != parent && node.getChildNodes().getLength() != 0) {
               parent.appendChild(node);
            }
         }
      }
   }
}
