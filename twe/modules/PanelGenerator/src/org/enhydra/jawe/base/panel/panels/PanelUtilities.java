package org.enhydra.jawe.base.panel.panels;

import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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
import org.enhydra.shark.xpdl.XMLAttribute;
import org.enhydra.shark.xpdl.XMLComplexElement;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.XMLSimpleElement;

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

   public static boolean isForModalDialog(XMLElement el) {
      boolean onlySimpleOrAttr = true;
      if (el instanceof XMLSimpleElement) {
         return true;
      } else if (el instanceof XMLComplexElement) {
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

}
