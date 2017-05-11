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
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.components.graph.Graph;
import org.enhydra.jawe.components.graph.GraphController;

/**
 * Prints selected graph.
 * 
 * @author Sasa Bojanic
 */
public class PrinterPageFormat extends ActionBase {

   public PrinterPageFormat(JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {
      GraphController gc = (GraphController) jawecomponent;

      if (gc.getSelectedGraph() != null)
         setEnabled(true);
      else
         setEnabled(false);
   }

   public void actionPerformed(ActionEvent e) {
      Graph selectedGraph = ((GraphController) jawecomponent).getSelectedGraph();
      if (selectedGraph == null)
         return;

      PageFormat f = PrinterJob.getPrinterJob().pageDialog(selectedGraph.getPageFormat());
      if (f != null) {
         selectedGraph.setPageFormat(f);
      }
   }
}
