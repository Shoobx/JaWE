package org.enhydra.jawe.components.graph.actions;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;

import javax.swing.JOptionPane;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.ResourceManager;
import org.enhydra.jawe.components.graph.Graph;
import org.enhydra.jawe.components.graph.GraphController;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class SaveAsJPG extends ActionBase {

   public SaveAsJPG(JaWEComponent jawecomponent) {
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
      try {
         String file = JaWEManager.getInstance().getJaWEController().saveDialog(
               ResourceManager.getLanguageDependentString("SaveAsJPGLabel"), 1,
               JaWEManager.getInstance().getDisplayNameGenerator().getDisplayName(
                     ((GraphController) jawecomponent).getSelectedGraph().getXPDLObject()));
         if (file != null && file.length() > 0) {
            saveGraphAsJPG(file, ((GraphController) jawecomponent).getSelectedGraph());
         }
      } catch (Exception ex) {
         String msg = ResourceManager.getLanguageDependentString("ErrorJPGSavingFailed");
         JaWEManager.getInstance().getJaWEController().message(msg, JOptionPane.WARNING_MESSAGE);
      }
   }

   public static void saveGraphAsJPG(String file, Graph graph)
         throws Exception {
      BufferedImage img = null;
      Object[] cells = graph.getRoots();

      if (cells.length > 0) {
         Rectangle bounds = graph.getCellBounds(cells).getBounds();// HM, JGraph3.4.1
         graph.toScreen(bounds);

         // Create a Buffered Image
         Dimension d = bounds.getSize();
         img = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
         Graphics2D graphics = img.createGraphics();
         graph.paint(graphics);
      }

      FileOutputStream fos = new FileOutputStream(file);
      JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(fos);
      encoder.encode(img);
      fos.flush();
      fos.close();
   }

}
