package org.enhydra.jawe.wfmopen;

import java.util.List;

import org.enhydra.jawe.base.panel.PanelContainer;
import org.enhydra.jawe.base.panel.panels.XMLGroupPanel;
import org.enhydra.shark.xpdl.XMLElement;

/**
 * Creates special group panel for WfMOpen Tool agent ext. attrib.
 * 
 * @author Sasa Bojanic
 */
public class WfMOpenGroupPanel extends XMLGroupPanel {

   public WfMOpenGroupPanel(PanelContainer pc,
                        XMLElement myOwnerL,
                        List elements,
                        String title,
                        boolean isVertical,
                        boolean hasBorder,
                        boolean hasEmptyBorder) {

      super(pc, myOwnerL, elements, title, isVertical, hasBorder, hasEmptyBorder);
   }

   public void setElements() {
      super.setElements();
      getOwner().setValue(null);
   }   
}
