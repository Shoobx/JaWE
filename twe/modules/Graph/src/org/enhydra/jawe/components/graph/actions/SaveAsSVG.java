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

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.swing.JOptionPane;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.ResourceManager;
import org.enhydra.jawe.components.graph.Graph;
import org.enhydra.jawe.components.graph.GraphController;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

public class SaveAsSVG extends ActionBase {

   public SaveAsSVG(JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {    
      GraphController gc = (GraphController)jawecomponent;
      
      if (gc.getSelectedGraph() != null)
         setEnabled(true);
      else      
         setEnabled(false);      
   }
   
   public void actionPerformed(ActionEvent e) {
      // Create file output stream
      try {
         String file = JaWEManager.getInstance().getJaWEController().saveDialog(
               ResourceManager.getLanguageDependentString("SaveAsSVGLabel"), 2,
               JaWEManager.getInstance().getDisplayNameGenerator().getDisplayName(
                     ((GraphController) jawecomponent).getSelectedGraph().getXPDLObject()));
         if (file != null && file.length() > 0) {
            saveGraphAsSVG(file, ((GraphController) jawecomponent).getSelectedGraph());
         }
      } catch (Exception ex) {
    	  ex.printStackTrace();
         String msg = ResourceManager.getLanguageDependentString("ErrorSVGSavingFailed");
         JaWEManager.getInstance().getJaWEController().message(msg, JOptionPane.WARNING_MESSAGE);
      }
   }

   public static void saveGraphAsSVG(String file, Graph graph)
         throws Exception {
      FileOutputStream fos = new FileOutputStream(new File(file));
      // Created writer with UTF-8 encoding
      Writer out = new OutputStreamWriter(fos, JaWEManager.getInstance().getJaWEController().getControllerSettings().getEncoding());
      // Get a DOMImplementation
      DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
      // Create an instance of org.w3c.dom.Document
      String svgNS = "http://www.w3.org/2000/svg";
      Document document = domImpl.createDocument(svgNS, "svg", null);
      // Create an instance of the SVG Generator
      SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
      // Render into the SVG Graphics2D implementation
      graph.paint(svgGenerator);
      // Use CSS style attribute
      boolean useCSS = true;
      // Finally, stream out SVG to the writer
      svgGenerator.stream(out, useCSS);
      // Close the file output stream
      fos.flush();
      fos.close();
   }

}
