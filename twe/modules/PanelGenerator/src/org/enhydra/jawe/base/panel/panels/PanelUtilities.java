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

import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.enhydra.jawe.BarFactory;
import org.enhydra.jawe.ButtonPropertyChangedListener;
import org.enhydra.jawe.ChoiceButtonListener;
import org.enhydra.jawe.NewActionBase;
import org.enhydra.jawe.Settings;
import org.enhydra.jawe.base.controller.JaWEType;
import org.enhydra.jawe.base.controller.JaWETypeChoiceButton;
import org.enhydra.jawe.base.panel.PanelContainer;
import org.enhydra.jxpdl.XMLAttribute;
import org.enhydra.jxpdl.XMLCollection;
import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLSimpleElement;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.elements.Member;

/**
 * Various panel utilities.
 * 
 * @author Zoran Milakovic
 * @author Sasa Bojanic
 */

public class PanelUtilities {

   public static List toXMLElementViewList(PanelContainer pc,
                                           Collection c,
                                           boolean lDepStr) {
      List vec = new ArrayList();
      Iterator it = c.iterator();
      while (it.hasNext()) {
         Object next = it.next();
         if (next instanceof XMLElement) {
            XMLElement xmlEl = (XMLElement) next;
            vec.add(new XMLElementView(pc, xmlEl, XMLElementView.TONAME));
         } else {
            vec.add(new XMLElementView(pc, (String) next, lDepStr));
         }

      }
      return vec;
   }

   public static JButton createToolbarButton(Settings s, Action a) {
      return createToolbarButton(s, a, null);
   }

   public static JButton createToolbarButton(Settings s,
                                             Action a,
                                             ChoiceButtonListener cbl) {
      if (a == null)
         return null;

      String actionName = (String) a.getValue(Action.NAME);
      JButton b = null;
      ImageIcon curIc = (ImageIcon) s.getSetting("DefaultAction.Icon." + actionName);
      if (a instanceof NewActionBase && cbl != null) {
         b = new JaWETypeChoiceButton(JaWEType.class,
                                      ((NewActionBase) a).getXPDLTypeClass(),
                                      ((NewActionBase) a).getXPDLTypeClassParentForEA(),
                                      cbl,
                                      curIc);
      } else {
         b = new JButton(curIc) {
            public float getAlignmentY() {
               return 0.5f;
            }
         };
      }

      b.setName(actionName);
      b.setMargin(new Insets(1, 1, 1, 1));
      b.setRequestFocusEnabled(false);

      b.addActionListener(a);
      a.addPropertyChangeListener(new ButtonPropertyChangedListener(b));

      String tip = s.getLanguageDependentString(actionName + BarFactory.TOOLTIP_POSTFIX);
      if (tip != null) {
         b.setToolTipText(tip);
      }

      return b;
   }

   public static boolean isForModalDialog(XMLElement el, boolean isEditAction) {
      // In the case element is member of some collection and collection does not have
      // parent element
      if (el.getParent() instanceof XMLCollection && el.getParent().getParent() == null) {
         return true;
      }
      if (isEditAction) {
         return false;
      }
      boolean onlySimpleOrAttr = true;
      if (el instanceof XMLSimpleElement) {
         return true;
      } else if (el instanceof XMLComplexElement && !(el instanceof Member)) {
         List subEls = ((XMLComplexElement) el).toElements();
         for (int i = 0; i < subEls.size(); i++) {
            Object se = subEls.get(i);
            if (!(se instanceof XMLSimpleElement || se instanceof XMLAttribute)) {
               onlySimpleOrAttr = false;
               break;
            }
         }
      }

      return onlySimpleOrAttr;
   }

   public static boolean isModifyingEvent(KeyEvent e) {
      if (e.isShiftDown()
          && (e.getKeyCode() == KeyEvent.VK_SHIFT || e.getKeyCode() == KeyEvent.VK_HOME))
         return false;
      if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_X)
         return true;
      if ((!e.isAltDown() && !e.isControlDown() && !e.isAltGraphDown())
          || (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_V)) {
         int kc = e.getKeyCode();
         if (kc == KeyEvent.VK_ESCAPE
             || kc == KeyEvent.VK_LEFT || kc == KeyEvent.VK_RIGHT || kc == KeyEvent.VK_UP
             || kc == KeyEvent.VK_DOWN) {
            return false;
         }
         return true;
      }

      return false;
   }

   public static Set getHiddenElements(PanelContainer pc,
                                       String panelName,
                                       XMLCollection col) {
      Set hidden = new HashSet();
      String elAttr = pc.getSettings().getSettingString("HideElements."
                                                        + panelName + "." + col.toName());
      String[] els = XMLUtil.tokenize(elAttr, " ");
      for (int k = 0; k < els.length; k++) {
         String key = els[k];
         String hstr = pc.getSettings().getSettingString("HideElements."
                                                         + panelName + "." + col.toName()
                                                         + "." + key);
         String[] hstra = XMLUtil.tokenize(hstr, " ");
         if (hstra != null) {
            for (int i = 0; i < hstra.length; i++) {
               if (hstra[i].equals("*")) {
                  hidden.addAll(col.toElements());
                  return hidden;
               }
               Iterator ci = col.toElements().iterator();
               while (ci.hasNext()) {
                  XMLElement el = (XMLElement) ci.next();
                  if (key.equals("")) {
                     if (el.toValue().equals(hstra[i])) {
                        hidden.add(el);
                     }
                  } else {
                     if (el instanceof XMLComplexElement) {
                        XMLElement sel = ((XMLComplexElement) el).get(key);
                        if (sel != null) {
                           if (sel.toValue().equals(hstra[i])) {
                              hidden.add(el);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
      // System.out.println("There are "+hidden.size()+" hidden elements for panel
      // "+panelName+",
      // col="+col+", ks="+keyStarts+", colsize="+col.size());
      return hidden;
   }

}
