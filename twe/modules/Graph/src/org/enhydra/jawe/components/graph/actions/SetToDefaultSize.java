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

package org.enhydra.jawe.components.graph.actions;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.components.graph.GraphActivityViewInterface;
import org.enhydra.jawe.components.graph.GraphArtifactViewInterface;
import org.enhydra.jawe.components.graph.GraphController;
import org.enhydra.jawe.components.graph.GraphUtilities;
import org.enhydra.jawe.components.graph.JaWEParentMap;
import org.enhydra.jawe.components.graph.WorkflowElement;
import org.enhydra.jxpdl.XMLCollectionElement;
import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.XPDLConstants;
import org.enhydra.jxpdl.elements.Activity;
import org.enhydra.jxpdl.elements.Artifact;
import org.enhydra.jxpdl.elements.NodeGraphicsInfo;
import org.jgraph.graph.AbstractCellView;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;

/**
 * Set selected graph object's sizes to default values.
 * 
 * @author Sasa Bojanic
 */
public class SetToDefaultSize extends ActionBase {

   public SetToDefaultSize(JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {
      GraphController gc = (GraphController) jawecomponent;
      setEnabled(gc.getSelectedGraph() != null
                 && !gc.getSelectedGraph().getXPDLObject().isReadOnly());
   }

   public void actionPerformed(ActionEvent e) {
      GraphController gcon = (GraphController) jawecomponent;
//      GraphLayoutCache glc = gcon.getSelectedGraph().getGraphLayoutCache();
      Object[] cells = gcon.getSelectedGraph().getSelectionCells();
//      gcon.setUpdateInProgress(true);
      JaWEManager.getInstance().getJaWEController().startUndouableChange();
      boolean isgraphrotated = gcon.getSelectedGraph().getGraphManager().isGraphRotated();
      int participantNameWidth = GraphUtilities.getGraphController()
         .getGraphSettings()
         .getLaneNameWidth();
//      Map propertyMap = new HashMap();

      for (int i = 0; i < cells.length; i++) {
         DefaultGraphCell cell = (DefaultGraphCell) cells[i];
         if (cell instanceof WorkflowElement) {
            XMLComplexElement el = ((WorkflowElement) cell).getPropertyObject();
            if (el instanceof Activity || el instanceof Artifact) {
               int width = GraphUtilities.getGraphController()
                  .getGraphSettings()
                  .getActivityWidth();
               int height = GraphUtilities.getGraphController()
                  .getGraphSettings()
                  .getActivityHeight();

               if (el instanceof Activity) {
                  Activity act = (Activity) el;
                  if (act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_EVENT_START
                      || act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_EVENT_END) {
                     width = GraphUtilities.getGraphController()
                        .getGraphSettings()
                        .getEventRadius() * 2 + 1;
                     height = GraphUtilities.getGraphController()
                        .getGraphSettings()
                        .getEventRadius() * 2 + 1;
                  } else if (act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_ROUTE) {
                     width = GraphUtilities.getGraphController()
                        .getGraphSettings()
                        .getGatewayWidth();
                     height = GraphUtilities.getGraphController()
                        .getGraphSettings()
                        .getGatewayHeight();
                  }
               } else if (el instanceof Artifact) {
                  Artifact art = (Artifact) el;
                  if (art.getArtifactType()
                     .equals(XPDLConstants.ARTIFACT_TYPE_ANNOTATION)) {
                     width = GraphUtilities.getGraphController()
                        .getGraphSettings()
                        .getAnnotationWidth();
                     height = GraphUtilities.getGraphController()
                        .getGraphSettings()
                        .getAnnotationHeight();
                  } else {
                     width = GraphUtilities.getGraphController()
                        .getGraphSettings()
                        .getDataObjectWidth();
                     height = GraphUtilities.getGraphController()
                        .getGraphSettings()
                        .getDataObjectHeight();
                  }
               }
               NodeGraphicsInfo ngi = JaWEManager.getInstance()
                  .getXPDLUtils()
                  .getNodeGraphicsInfo((XMLCollectionElement) el);
               if (ngi != null) {
                  int oldw = ngi.getWidth();
                  int oldh = ngi.getHeight();
                  int x = (int) Double.parseDouble(ngi.getCoordinates().getXCoordinate());
                  int y = (int) Double.parseDouble(ngi.getCoordinates().getYCoordinate());
//                  int xcorr = 0;
//                  int ycorr = 0;
                  x += (oldw - width) / 2;
                  y += (oldh - height) / 2;

                  if (!isgraphrotated) {
                     if (x <= participantNameWidth) {
//                        xcorr = (participantNameWidth+1)-x;
                        x = participantNameWidth + 1;
                     }
                  } else {
                     if (y <= participantNameWidth) {
//                        ycorr = (participantNameWidth+1)-y;
                        y = participantNameWidth + 1;
                     }
                  }
//                  System.out.println("xc1="+xcorr+",yc1="+ycorr);
                  if (x < 1) {
//                     xcorr = 1-x+1;
                     x = 1;
                  }
                  if (y < 1) {
//                     ycorr = 1-y+1;
                     y = 1;
                  }
//                  System.out.println("xc2="+xcorr+",yc2="+ycorr);
                  ngi.getCoordinates().setXCoordinate(String.valueOf(x));
                  ngi.getCoordinates().setYCoordinate(String.valueOf(y));
                  ngi.setWidth(width);
                  ngi.setHeight(height);

//                  AbstractCellView view = (AbstractCellView) glc.getMapping(cell, false);
//                  Rectangle origr = null;
//                  if (view instanceof GraphActivityViewInterface) {
//                     origr = ((GraphActivityViewInterface) view).getOriginalBounds();
//                  } else {
//                     origr = ((GraphArtifactViewInterface) view).getOriginalBounds();
//                  }
//                  int xr = (int) (origr.getX() + (origr.getWidth() - width) / 2.0);
//                  int yr = (int) (origr.getY() + (origr.getHeight() - height) / 2.0);
//                  xr+=xcorr;
//                  yr+=ycorr;
//
//                  Rectangle newR = new Rectangle(xr, yr, width, height);
//                  Map attributes = new HashMap();
//                  attributes.put(GraphConstants.BOUNDS, newR);
//                  glc.editCell(cell, attributes);
//                  AttributeMap map = new AttributeMap(cell.getAttributes());
//                  propertyMap.put(cell, map);
                  
               }
            }
         }
      }
      List toSelect = new ArrayList(JaWEManager.getInstance()
         .getJaWEController()
         .getSelectionManager()
         .getSelectedElements());
      JaWEManager.getInstance().getJaWEController().endUndouableChange(toSelect);
//      gcon.getSelectedGraph().getGraphManager().updateModelAndArrangeParticipants(null,propertyMap, new JaWEParentMap(),null,"a",null,true);
//      gcon.setUpdateInProgress(false);
//
//      gcon.getSelectedGraph().refresh();
   }
}
