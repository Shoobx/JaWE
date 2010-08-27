package org.enhydra.jawe.components.graph.actions;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.components.graph.GraphController;

/**
 * @author Sasa Bojanic
 */
public abstract class SetToolboxMode extends ActionBase {

   protected Cursor cursor;

   protected String type;

   protected String subType;

   public SetToolboxMode(GraphController jawecomponent, String type, String subType) {
      super(jawecomponent);
      this.type=type;
      this.subType=subType;
   }

   public void actionPerformed(ActionEvent e) {
      GraphController gc = (GraphController) jawecomponent;

      if (cursor == null) {
         ImageIcon curIc;
         Point hotSpot;
         Image curIm;

         JFrame fr = JaWEManager.getInstance().getJaWEController().getJaWEFrame();
         try {
            curIc=getIcon();
            hotSpot = new Point(curIc.getIconWidth() / 2, curIc.getIconHeight() / 2);
            curIm = curIc.getImage();
            cursor = fr.getToolkit().createCustomCursor(curIm,
                                                        hotSpot,
                                                        "ToolboxMode"+subType+"20x20");
         } catch (Exception ex) {
            JaWEManager.getInstance()
               .getLoggingManager()
               .error("Missing cursor image for ToolboxMode["+type+","+subType+"]!");
         }
      }

      gc.getGraphMarqueeHandler().setType(type,
                                          subType,
                                          cursor);
   }
   
   protected ImageIcon getIcon () {
      return JaWEManager.getInstance().getJaWEController().getJaWETypes().getType(subType).getIcon();
   }
}
