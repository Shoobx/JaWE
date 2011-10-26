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

package org.enhydra.jawe.components.graph.overviewpanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEComponentSettings;
import org.enhydra.jawe.JaWEComponentView;
import org.enhydra.jawe.components.graph.Graph;
import org.enhydra.jawe.components.graph.GraphController;
import org.enhydra.jawe.components.graph.GraphOverview;
import org.enhydra.jxpdl.XMLCollection;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.elements.Package;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.AbstractCellView;

/**
 * @author Miroslav Popov
 */
public class GraphOverviewPanel extends JPanel implements GraphOverview, JaWEComponentView, GraphSelectionListener, ComponentListener {
      
   protected String type = JaWEComponent.SPECIAL_COMPONENT;
   
   protected Graph graph;
   protected GraphController controller;
   
   protected boolean update = false;
   public JaWEComponentSettings getSettings() {
      return null;
   }
   
   public void configure () {         
   }
   
   public void init () {      
   }
   
   public void init(GraphController pController) {
      this.controller = pController;
   }
   
   public JaWEComponent getJaWEComponent () {
      return controller;
   }

   public JComponent getDisplay () {
      return this;
   }
         
   public JaWEComponentView getView() {
      return this;
   }

   public String getType() {
      return type;
   }   

   public void setType(String type) {
      this.type = type; 
   }
   
   public String getName() {
      return "GraphOverview";
   }
   
   public boolean adjustXPDL (Package pkg) {
      return false;
   }

   public List checkValidity (XMLElement el,boolean fullCheck) {
      return null;
   }

   public boolean canCreateElement(XMLCollection col) {
      return true;
   }

   public boolean canInsertElement(XMLCollection col, XMLElement el) {
      return true;
   }

   public boolean canModifyElement(XMLElement el) {
      return true;
   }

   public boolean canRemoveElement(XMLCollection col, XMLElement el) {
      return true;
   }

   public boolean canDuplicateElement(XMLCollection col, XMLElement el) {
      return true;
   }

   public boolean canRepositionElement(XMLCollection col, XMLElement el) {
      return true;
   }

   public GraphOverviewPanel () {
      this.addComponentListener(this);
   }
   
   public void displayGraph() {
      Graph sg = controller.getSelectedGraph();
      if (sg != null) {       
         graph = new Graph(sg);
         graph.setGridEnabled(false);
         graph.setGridVisible(false);
         graph.setMarqueeHandler(new GraphOverviewMarqueeHandler(controller));
         graph.setUI(new GraphOverviewGraphUI());
         sg.addComponentListener(this);
         
         graph.setAntiAliased(true);
         setBorder(BorderFactory.createEtchedBorder());
         graph.setEditable(false);
         graph.setMoveable(false);
         graph.setSelectionEnabled(true);
         graph.addGraphSelectionListener(this);
         if (sg.getSelectionCell() != null)
            graph.setSelectionCell(sg.getSelectionCell());
         setLayout(new BorderLayout());         

         removeAll();
         add(graph, BorderLayout.CENTER);
         componentResized(null);
         repaint();
      } else {
         graph=null;
         if (getComponentCount() != 0) {
            removeAll();
            repaint();
         }
      }
   }
    
   public Graph getGraph () {
      return graph;
   }
   
   public void valueChanged(GraphSelectionEvent e) {
      if (!controller.isUpdateInProgress()) {
      if (update) return;
      try {
         update = true;
         if (controller.getSelectedGraph() != null) {
            if (e.getCell() != null) {
               controller.getSelectedGraph().setSelectionCells(graph.getSelectionCells());//   e.getCells());
               controller.getSelectedGraph().scrollCellToVisible(e.getCell());
            }
         }
      } finally {
         update = false;
      }
      }
   }
      
   public void setUpdate(boolean update) {
      this.update = update;
   }
   
   public boolean getUpdate() {
      return update;
   }
   
   //
   // Component Listener
   //

   public void componentResized(ComponentEvent e) {
      if (graph==null) return;
   	Rectangle r = null;
      if(AbstractCellView.getBounds(graph.getGraphLayoutCache().getRoots())!=null) {
         r = AbstractCellView.getBounds(graph.getGraphLayoutCache().getRoots()).getBounds();
      } else {
         r = controller.getSelectedGraph().getBounds();
      }
      
      double scale = 0.5;
      if (r != null) {
          
         Dimension d = new Dimension(r.x+r.width, r.y+r.height);
         Dimension s = getSize();
         double sx = s.getWidth()*0.95/d.getWidth();
         double sy = s.getHeight()*0.95/d.getHeight();
         scale = Math.min(Math.max(Math.min(sx, sy), 0.05), 0.5);
      }
      graph.setScale(scale);
      repaint();
   }

   public void componentShown(ComponentEvent e) {
      componentResized(e);
   }

   public void componentHidden(ComponentEvent e) { }

   public void componentMoved(ComponentEvent e) { }  

   public void setUpdateInProgress(boolean inProgress) {
   }
   
   public boolean isUpdateInProgress() {
      return false;
   }
   
}
