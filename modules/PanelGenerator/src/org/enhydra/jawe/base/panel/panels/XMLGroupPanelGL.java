package org.enhydra.jawe.base.panel.panels;

import java.awt.GridLayout;
import java.util.List;

import org.enhydra.jawe.base.panel.PanelContainer;
import org.enhydra.shark.xpdl.XMLElement;

/**
 * @author Sasa Bojanic
 */
public class XMLGroupPanelGL extends XMLGroupPanel {

   public XMLGroupPanelGL(PanelContainer pc,
                        XMLElement myOwnerL,
                        List elements,
                        String title,
                        boolean isVertical,
                        boolean hasBorder,
                        boolean hasEmptyBorder) {

      super(pc, myOwnerL, elements,title,isVertical, hasBorder, hasEmptyBorder);

   }

   protected void initPanel (boolean isVertical,List elements) {
      if (isVertical) {
         setLayout(new GridLayout(3,1));
      } else {
         setLayout(new GridLayout(1,3));
      }
      super.initPanel(isVertical, elements);
   }
   
   protected void initTopLeft (boolean isVertical,boolean rightAllignment) {
   }
   
   protected void initBottomRight (boolean isVertical,boolean rightAllignment) {
   }
   
}
