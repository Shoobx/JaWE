package org.enhydra.jawe.components.graph;

import java.awt.Color;

import javax.swing.UIManager;

import org.jgraph.graph.PortRenderer;

/**
 * Class used to display port object.
 * 
 * @author Sasa Bojanic
 */
public class DefaultGraphPortRenderer extends PortRenderer implements GraphPortRendererInterface {

   public DefaultGraphPortRenderer() {
      Color fg = UIManager.getColor("MenuItem.selectionBackground");
      Color bg = GraphUtilities.getGraphController().getGraphSettings().getHandleColor();
      setForeground(fg);
      setBackground(bg);
   }

}
