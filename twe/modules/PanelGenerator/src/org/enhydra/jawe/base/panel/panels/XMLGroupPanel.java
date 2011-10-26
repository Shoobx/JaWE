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

package org.enhydra.jawe.base.panel.panels;

import java.awt.Component;
import java.util.Arrays;
import java.util.List;

import javax.swing.Box;

import org.enhydra.jawe.base.panel.PanelContainer;
import org.enhydra.jawe.base.panel.SpecialChoiceElement;
import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.XMLElement;

/**
 * Creates titled group panel with vertical or horizontal BoxLayout, that consists of
 * panels of given elements.
 * 
 * @author Sasa Bojanic
 * @author Zoran Milakovic
 */
public class XMLGroupPanel extends XMLBasicPanel {

   protected boolean hasDummyStartEl = false;

   protected boolean hasDummyEndEl = false;

   public XMLGroupPanel(PanelContainer pc,
                        XMLElement myOwnerL,
                        List elements,
                        String title,
                        boolean isVertical,
                        boolean hasBorder,
                        boolean hasEmptyBorder) {

      super(pc, myOwnerL, title, isVertical, hasBorder, hasEmptyBorder);

      boolean isRightAllignment = false;
      if (pc != null) {
         isRightAllignment=pc.getSettings().getSettingBoolean("XMLBasicPanel.RightAllignment");
      }
      

      initPanel(isVertical, isRightAllignment, elements);
   }
   
   public XMLGroupPanel(PanelContainer pc,
                        XMLElement myOwnerL,
                        List elements,
                        String title,
                        boolean isVertical,
                        boolean hasBorder,
                        boolean hasEmptyBorder,
                        boolean isRightAllignment) {

      super(pc, myOwnerL, title, isVertical, hasBorder, hasEmptyBorder);

      initPanel(isVertical, isRightAllignment, elements);
   }

   protected void initPanel (boolean isVertical, boolean isRightAllignment, List elements) {
      initTopLeft(isVertical, isRightAllignment);

      initElements(elements,isVertical);
      
      initBottomRight(isVertical, isRightAllignment);
   }
   
   protected void initElements (List elements, boolean isVertical) {
      for (int i = 0; i < elements.size(); i++) {
         XMLPanel dtdp = null;
         Object el = elements.get(i);
         if (el instanceof XMLElement) {
            dtdp = pc.getPanelGenerator().getPanel((XMLElement) el);
         } else if (el instanceof String) {
            System.out.println("GE|TTING PANEL FOR EL"+el+", AEL="+((XMLComplexElement) myOwner).get((String) el)+", OWNER="+myOwner.toName());
            dtdp = pc.getPanelGenerator()
               .getPanel(((XMLComplexElement) myOwner).get((String) el));
         } else if (el instanceof XMLPanel) {
            dtdp = (XMLPanel) el;
         }
         // dtdp.setEnabled(!myOwner.isReadOnly());
         if (dtdp != null) {
            add(dtdp);
         } else if (el instanceof Component) {
            add((Component)el);
         }
      }
   }
   
   protected void initTopLeft (boolean isVertical,boolean isRightAllignment) {
      if (isVertical) {
         add(Box.createVerticalStrut(5));
         hasDummyStartEl = true;
      } else {
         if (!isRightAllignment) {
            add(Box.createHorizontalGlue());
            hasDummyStartEl = true;
         }
      }      
   }
   
   protected void initBottomRight (boolean isVertical,boolean isRightAllignment) {
      if (isVertical) {
         add(Box.createVerticalGlue());
         hasDummyEndEl = true;
      } else {
         if (isRightAllignment) {
            add(Box.createHorizontalGlue());
            hasDummyEndEl = true;
         }
      }            
   }
   
   public XMLPanel getPanel(int no) {
      int gps = getComponentCount()
                - ((hasDummyStartEl) ? 1 : 0) - ((hasDummyEndEl) ? 1 : 0);
      if (no >= gps || no < 0) {
         throw new RuntimeException("There's no element at position "+no);
      }
      if (hasDummyStartEl) {
         no++;
      }
      return (XMLPanel) getComponent(no);
   }

   public void addToGroup(Object newEl) {
      XMLPanel newPanel = null;
      if (newEl instanceof XMLElement) {
         newPanel = pc.getPanelGenerator().getPanel((XMLElement) newEl);
      } else if (newEl instanceof String) {
         newPanel = pc.getPanelGenerator()
            .getPanel(((XMLComplexElement) myOwner).get((String) newEl));
      } else if (newEl instanceof XMLPanel) {
         newPanel = (XMLPanel) newEl;
      } else {
         throw new RuntimeException("Can't add element "+newEl+" to the group!");
      }
      if (!hasDummyEndEl) {
         add(newPanel);
      } else {
         int cc = getComponentCount();
         add(newPanel, cc - 1);
      }
   }

   public void addToGroup(Object newEl,int where) {
      int gps = getComponentCount()
                - ((hasDummyStartEl) ? 1 : 0) - ((hasDummyEndEl) ? 1 : 0);
      if (where >= gps || where < 0) {
         throw new RuntimeException("Invalid position "
                                    + where
                                    + " for adding element into the XMLGroupPanel with size "
                                    + gps);

      }
      XMLPanel newPanel = null;
      if (newEl instanceof XMLElement) {
         newPanel = pc.getPanelGenerator().getPanel((XMLElement) newEl);
      } else if (newEl instanceof String) {
         newPanel = pc.getPanelGenerator()
            .getPanel(((XMLComplexElement) myOwner).get((String) newEl));
      } else if (newEl instanceof XMLPanel) {
         newPanel = (XMLPanel) newEl;
      } else {
         throw new RuntimeException("Can't add element "+newEl+" to the group!");
      }
      if (hasDummyStartEl) {
         where++;
      }
      add(newPanel, where);
   }

   
   public void removeFromGroup(int no) {
      int gps = getComponentCount()
                - ((hasDummyStartEl) ? 1 : 0) - ((hasDummyEndEl) ? 1 : 0);
      if (no >= gps) {
         throw new RuntimeException("There's no element at position "+no);
      }
      if (hasDummyStartEl) {
         no++;
      }
      remove(no);
   }

   public XMLPanel getPanelForElement(XMLElement el) {
      for (int i = 0; i < getComponentCount(); i++) {
         Component c = getComponent(i);
         if (c instanceof XMLPanel) {
            XMLPanel p = (XMLPanel) c;
            if (p instanceof XMLGroupPanel) {
               p = ((XMLGroupPanel) p).getPanelForElement(el);
               if (p != null) {
                  return p;
               }
            } else if (p.getOwner() == el) {
               return p;
            } else if (p.getOwner() instanceof SpecialChoiceElement) {
               SpecialChoiceElement sc = (SpecialChoiceElement) p.getOwner();
               if (sc.getControlledElement() == el) {
                  return p;
               }
            }
         }
      }
      return null;
   }

   public int getPanelPositionForElement(XMLElement el) {
      for (int i = 0; i < getComponentCount(); i++) {
         Component c = getComponent(i);
         if (c instanceof XMLPanel) {
            XMLPanel p = (XMLPanel) c;
            if (p instanceof XMLGroupPanel) {
               p = ((XMLGroupPanel) p).getPanelForElement(el);
               if (p != null) {
                  return i;
               }
            } else if (p.getOwner() == el) {
               return i;
            } else if (p.getOwner() instanceof SpecialChoiceElement) {
               SpecialChoiceElement sc = (SpecialChoiceElement) p.getOwner();
               if (sc.getControlledElement() == el) {
                  return i;
               }
            }
         }
      }
      return -1;
   }
   
   public boolean validateEntry() {
      if (isEmpty() && !getOwner().isRequired())
         return true;
      boolean isOK = true;
      for (int i = 0; i < getComponentCount(); i++) {
         Component c = getComponent(i);
         if (c instanceof XMLPanel) {
            isOK = isOK && ((XMLPanel) c).validateEntry();
         }
      }
      return isOK;
   }

   public boolean isEmpty() {
      boolean isEmpty = true;
      for (int i = 0; i < getComponentCount(); i++) {
         Component c = getComponent(i);
         if (c instanceof XMLPanel) {
            isEmpty = isEmpty && ((XMLPanel) c).isEmpty();
         }
      }
      return isEmpty;
   }

   public void setOwner(XMLElement el) {
      super.setOwner(el);
      this.myOwner = el;
      for (int i = 0; i < getComponentCount(); i++) {
         Component c = getComponent(i);
         if (c instanceof XMLPanel) {
            ((XMLPanel) c).setOwner(el);
         }
      }
   }

   public void setElements() {
      if (!getOwner().isReadOnly()) {
         for (int i = 0; i < getComponentCount(); i++) {
            Component c = getComponent(i);
            if (c instanceof XMLPanel) {
               // System.err.println("Comp no "+(i+1)+" =
               // "+getComponent(i).getClass().getName());
               ((XMLPanel) c).setElements();
            }
         }
      }
   }

   public void updateView() {
      for (int i = 0; i < getComponentCount(); i++) {
         Component c = getComponent(i);
         if (c instanceof XMLPanel) {
            ((XMLPanel) c).updateView();
         }
      }
   }

   public void cleanup() {
      for (int i = 0; i < getComponentCount(); i++) {
         Component c = getComponent(i);
         if (c instanceof XMLPanel) {
            ((XMLPanel) c).cleanup();
         }
      }
   }

}
