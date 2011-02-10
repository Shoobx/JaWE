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

/**
 * Miroslav Popov, Sep 19, 2005 miroslav.popov@gmail.com
 */
package org.enhydra.jawe.base.controller;

import java.awt.Insets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.BarFactory;
import org.enhydra.jawe.ButtonPropertyChangedListener;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.base.controller.actions.defaultactions.Copy;
import org.enhydra.jawe.base.controller.actions.defaultactions.Cut;
import org.enhydra.jawe.base.controller.actions.defaultactions.Delete;
import org.enhydra.jawe.base.controller.actions.defaultactions.Duplicate;
import org.enhydra.jawe.base.controller.actions.defaultactions.EditProperties;
import org.enhydra.jawe.base.controller.actions.defaultactions.New;
import org.enhydra.jawe.base.controller.actions.defaultactions.Paste;
import org.enhydra.jawe.base.controller.actions.defaultactions.Redo;
import org.enhydra.jawe.base.controller.actions.defaultactions.Undo;
import org.enhydra.jxpdl.XMLUtil;

/**
 * @author Miroslav Popov
 */
public class JaWEActions {

   public static final String EDIT_TOOLBAR = "jawe_edittoolbar";

   public static final String UNDO_REDO_TOOLBAR = "jawe_undoredo_toolbar";

   public static final String EDIT_MENU = "jawe_editmenu";

   public static final String NEW_ACTION = "New";

   public static final String DUPLICATE_ACTION = "Duplicate";

   public static final String REFERENCES = "References";

   public static final String UNDO_ACTION = "Undo";

   public static final String REDO_ACTION = "Redo";

   public static final String CUT_ACTION = "Cut";

   public static final String COPY_ACTION = "Copy";

   public static final String PASTE_ACTION = "Paste";

   public static final String DELETE_ACTION = "Delete";

   public static final String EDIT_PROPERTIES_ACTION = "EditProperties";

   protected JaWEController controller;

   protected Map commands = new HashMap();

   public JaWEActions(JaWEController controller) {
      this.controller = controller;
   }

   public void init() {
      Undo undo = new Undo(controller);
      Redo redo = new Redo(controller);

      Cut cut = new Cut(controller);
      Copy copy = new Copy(controller);
      Paste paste = new Paste(controller);
      Delete delete = new Delete(controller);
      EditProperties eprop = new EditProperties(controller);
      New newA = new New(controller);
      Duplicate duplicate = new Duplicate(controller);

      Action references = null;
      String clsName = "org.enhydra.jawe.base.controller.actions.defaultactions.References";
      try {
         references = (ActionBase) Class.forName(clsName).getConstructor(new Class[] {
            JaWEComponent.class
         }).newInstance(new Object[] {
            controller
         });
      } catch (Exception e) {
      }

      commands.put(JaWEActions.NEW_ACTION, newA);
      commands.put(JaWEActions.DUPLICATE_ACTION, duplicate);
      commands.put(JaWEActions.REFERENCES, references);
      commands.put(JaWEActions.UNDO_ACTION, undo);
      commands.put(JaWEActions.REDO_ACTION, redo);
      commands.put(JaWEActions.CUT_ACTION, cut);
      commands.put(JaWEActions.COPY_ACTION, copy);
      commands.put(JaWEActions.PASTE_ACTION, paste);
      commands.put(JaWEActions.DELETE_ACTION, delete);
      commands.put(JaWEActions.EDIT_PROPERTIES_ACTION, eprop);
   }

   public Map getCommands() {
      return new HashMap(commands);
   }

   public ActionBase getAction(String action_name) {
      if (action_name == null)
         return null;

      return (ActionBase) commands.get(action_name);
   }

   public void disableAction(String action_name) {
      Action a = (Action) commands.get(action_name);

      if (a != null)
         a.setEnabled(false);
   }

   public void enableAction(String action_name) {
      Action a = (Action) commands.get(action_name);

      if (a != null)
         a.setEnabled(true);
   }

   public JButton getActionButton(String action_name) {
      Action a = (Action) commands.get(action_name);

      String aname = action_name;
      if (a != null) {
         aname = (String) a.getValue(Action.NAME);
      }
      // String label = controller.getSettings().getLanguageDependentString(aname +
      // BarFactory.LABEL_POSTFIX);
      // if (label == null) {
      // label = aname;
      // }

      JButton b = null;
      // if (a instanceof NewActionBase) {
      // ImageIcon curIc = controller.getControllerSettings().getIconFor(action_name);
      // b = new JaWETypeChoiceButton(JaWEType.class,
      // ((NewActionBase)a).getXPDLTypeClass(), controller, curIc);
      // } else {
      b = new JButton(controller.getControllerSettings().getIconFor(action_name)) {
         public float getAlignmentY() {
            return 0.5f;
         }
      };
      // }

      b.setName(aname);
      b.setMargin(new Insets(1, 1, 1, 1));
      b.setRequestFocusEnabled(false);

      b.setActionCommand(aname);
      if (a != null) {
         b.addActionListener(a);
         b.setEnabled(a.isEnabled());
         a.addPropertyChangeListener(new ButtonPropertyChangedListener(b));
      } else {
         b.setEnabled(false);
      }
      String tip = controller.getSettings()
         .getLanguageDependentString(aname + BarFactory.TOOLTIP_POSTFIX);
      if (tip != null) {
         b.setToolTipText(tip);
      }

      return b;
   }

   public JMenuItem getActionMenuItem(String action_name, boolean addBCListener) {
      Action a = (Action) commands.get(action_name);

      String aname = action_name;
      if (a != null) {
         aname = (String) a.getValue(Action.NAME);
      }
      String label = controller.getSettings()
         .getLanguageDependentString(aname + BarFactory.LABEL_POSTFIX);
      if (label == null) {
         label = aname;
      }

      JMenuItem mi = new JMenuItem(label);
      mi.setName(aname);

      mi.setHorizontalTextPosition(SwingConstants.RIGHT);
      mi.setIcon(controller.getControllerSettings().getIconFor(action_name));

      BarFactory.setAccelerator(mi, controller.getSettings()
         .getLanguageDependentString(aname + BarFactory.ACCELERATION_POSTFIX));
      BarFactory.setMnemonic(mi, controller.getSettings()
         .getLanguageDependentString(aname + BarFactory.MNEMONIC_POSTFIX));

      mi.setActionCommand(aname);
      if (a != null) {
         mi.addActionListener(a);
         if (addBCListener) {
            a.addPropertyChangeListener(new ButtonPropertyChangedListener(mi));
         }
         mi.setEnabled(a.isEnabled());
      } else {
         mi.setEnabled(false);
      }

      return mi;
   }

   public JToolBar getActionToolbar(String toolbar_name) {
      if (toolbar_name == null)
         return null;

      JToolBar tcomp = new JToolBar();
      tcomp.putClientProperty("JToolBar.isRollover", Boolean.TRUE);

      if (toolbar_name.equals(JaWEActions.EDIT_TOOLBAR)) {
         String actionOrder = controller.getControllerSettings()
            .getDefaultActionsEditOrder();

         String[] act = XMLUtil.tokenize(actionOrder, BarFactory.ACTION_DELIMITER);

         for (int i = 0; i < act.length; i++) {
            if (act[i].equals(BarFactory.ACTION_SEPARATOR)) {
               tcomp.addSeparator();
            } else {
               AbstractButton co = getActionButton(act[i]);
               if (co != null)
                  tcomp.add(co);
            }
         }

         tcomp.setName(controller.getSettings()
            .getLanguageDependentString("editToolbar" + BarFactory.LABEL_POSTFIX));
      } else if (toolbar_name.equals(JaWEActions.UNDO_REDO_TOOLBAR)) {
         tcomp.add(getActionButton(JaWEActions.UNDO_ACTION));
         tcomp.add(getActionButton(JaWEActions.REDO_ACTION));
      }

      return tcomp;
   }

   public JMenu getActionMenu(String menu_name, boolean addBCListener) {
      if (menu_name == null)
         return null;

      JMenu mcomp = new JMenu();

      if (menu_name.equals(JaWEActions.EDIT_MENU)) {
         mcomp.setText(controller.getSettings()
            .getLanguageDependentString("edit" + BarFactory.LABEL_POSTFIX));

         BarFactory.setMnemonic(mcomp, controller.getSettings()
            .getLanguageDependentString("edit" + BarFactory.MNEMONIC_POSTFIX));

         String actionOrder = controller.getControllerSettings()
            .getDefaultActionsEditOrder();
         String[] act = XMLUtil.tokenize(actionOrder, BarFactory.ACTION_DELIMITER);

         for (int i = 0; i < act.length; i++) {
            if (act[i].equals(BarFactory.ACTION_SEPARATOR)) {
               mcomp.addSeparator();
            } else {
               JMenuItem mi = getActionMenuItem(act[i], addBCListener);
               if (mi != null)
                  mcomp.add(mi);
            }
         }
      }

      return mcomp;
   }

   public void enableDisableActions() {
      for (Iterator it = commands.values().iterator(); it.hasNext();) {
         ActionBase ab=(ActionBase)it.next();
         if (ab!=null) {
            ab.enableDisableAction();
         }
      }
   }

}
