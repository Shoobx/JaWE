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

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.components.graph.Graph;
import org.enhydra.jawe.components.graph.GraphController;

/**
 * Prints selected graph.
 * 
 * @author Sasa Bojanic
 */
public class Print extends ActionBase {

   public Print(JaWEComponent jawecomponent) {
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

      PrinterJob printJob = PrinterJob.getPrinterJob();
      // Create an Attribute set
      PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();

      // A different way to bring Up Native Dialog from Java
      aset.add(javax.print.attribute.standard.DialogTypeSelection.NATIVE);
      // Looks like this class is being moved to javax.print.attribute.standard for Java 7

      Frame f = JaWEManager.getInstance().getJaWEController().getJaWEFrame();
      aset.add(new sun.print.DialogOwner(f));

      if (printJob.printDialog(aset)) {
         PageFormat pageFormat = selectedGraph.getPageFormat();

         printJob.setPrintable(selectedGraph, pageFormat);
         try {
            printJob.print();
         } catch (Exception e2) {
         }
      }
   }

}
