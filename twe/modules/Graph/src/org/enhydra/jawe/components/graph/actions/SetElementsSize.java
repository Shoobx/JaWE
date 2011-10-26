/**
 * Together Workflow Editor
 * Copyright (C) 2011 Together Teamsolutions Co., Ltd. 
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

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.editor.StandardXPDLElementEditor;
import org.enhydra.jawe.base.editor.XPDLElementEditor;
import org.enhydra.jawe.components.graph.Graph;
import org.enhydra.jawe.components.graph.GraphCommonInterface;
import org.enhydra.jawe.components.graph.GraphController;
import org.enhydra.jawe.components.graph.GraphUtilities;
import org.enhydra.jawe.components.graph.SizeObject;
import org.enhydra.jawe.components.graph.WorkflowElement;
import org.enhydra.jxpdl.XMLCollectionElement;
import org.enhydra.jxpdl.elements.NodeGraphicsInfo;

/**
 * Sets the performer expression for all the activities contained inside selected
 * CommonExpressionParticipant.
 * 
 * @author Sasa Bojanic
 */
public class SetElementsSize extends ActionBase {

   public SetElementsSize(JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {
      GraphController gc = (GraphController) jawecomponent;

      boolean en = false;
      if (gc.getSelectedGraph() != null) {
         Graph g = gc.getSelectedGraph();
         Object[] scells = g.getSelectionCells();
         if (!g.getXPDLObject().isReadOnly() && scells != null && scells.length > 0) {
            en = true;
         }
      }
      setEnabled(en);
   }

   public void actionPerformed(ActionEvent e) {
      GraphController gc = (GraphController) jawecomponent;
      if (gc.getSelectedGraph() != null) {
         Object[] cell = gc.getSelectedGraph().getSelectionCells();
         List els2edit = new ArrayList();
         int defW = 90;
         int defH = 60;
         for (int i = 0; i < cell.length; i++) {

            if (cell[i] instanceof GraphCommonInterface) {
               NodeGraphicsInfo ngi = JaWEManager.getInstance()
                  .getXPDLUtils()
                  .getNodeGraphicsInfo((XMLCollectionElement) ((WorkflowElement) cell[i]).getPropertyObject());
               if (els2edit.size() == 0) {
                  defW = ngi.getWidth();
                  defH = ngi.getHeight();
               }
               els2edit.add(ngi);
            }
         }
         if (els2edit.size() > 0) {
            SizeObject so = new SizeObject();
            so.setWidth(defW);
            so.setHeight(defH);
            StandardXPDLElementEditor ed = new StandardXPDLElementEditor();
            ed.editXPDLElement(so);
            int width = so.getWidth();
            int height = so.getHeight();
            JaWEManager.getInstance().getJaWEController().startUndouableChange();
            boolean isgraphrotated = gc.getSelectedGraph()
               .getGraphManager()
               .isGraphRotated();
            int participantNameWidth = GraphUtilities.getGraphController()
               .getGraphSettings()
               .getLaneNameWidth();

            for (int i = 0; i < els2edit.size(); i++) {
               NodeGraphicsInfo ngi = (NodeGraphicsInfo) els2edit.get(i);

               int oldw = ngi.getWidth();
               int oldh = ngi.getHeight();
               int x = (int) Double.parseDouble(ngi.getCoordinates().getXCoordinate());
               int y = (int) Double.parseDouble(ngi.getCoordinates().getYCoordinate());
               // int xcorr = 0;
               // int ycorr = 0;
               x += (oldw - width) / 2;
               y += (oldh - height) / 2;

               if (!isgraphrotated) {
                  if (x <= participantNameWidth) {
                     // xcorr = (participantNameWidth+1)-x;
                     x = participantNameWidth + 1;
                  }
               } else {
                  if (y <= participantNameWidth) {
                     // ycorr = (participantNameWidth+1)-y;
                     y = participantNameWidth + 1;
                  }
               }
               // System.out.println("xc1="+xcorr+",yc1="+ycorr);
               if (x < 1) {
                  // xcorr = 1-x+1;
                  x = 1;
               }
               if (y < 1) {
                  // ycorr = 1-y+1;
                  y = 1;
               }
               // System.out.println("xc2="+xcorr+",yc2="+ycorr);
               ngi.getCoordinates().setXCoordinate(String.valueOf(x));
               ngi.getCoordinates().setYCoordinate(String.valueOf(y));
               ngi.setWidth(width);
               ngi.setHeight(height);

            }

            List toSelect = new ArrayList(JaWEManager.getInstance()
               .getJaWEController()
               .getSelectionManager()
               .getSelectedElements());
            JaWEManager.getInstance().getJaWEController().endUndouableChange(toSelect);
         }
      }
   }
}
