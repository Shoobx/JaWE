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

package org.enhydra.jawe.base.editor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.XPDLElementChangeInfo;
import org.enhydra.jawe.base.panel.panels.XMLSimpleTablePanel;
import org.enhydra.jawe.base.panel.panels.XMLSimpleTableSelPanel;
import org.enhydra.shark.xpdl.XMLCollection;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.XMLElementChangeInfo;
import org.enhydra.shark.xpdl.XMLUtil;

/**
 * 
 */
public class TogWETableEditor extends TableEditor implements Observer {

   protected boolean updateInProgress = false;

   public TogWETableEditor(TableEditorSettings ts) {
      super(ts);
      setModal(false);
   }

   public void update(Observable o, Object arg) {
      if (!(arg instanceof XPDLElementChangeInfo) || !isVisible() || updateInProgress)
         return;

      try {
         updateInProgress = true;

         XPDLElementChangeInfo info = (XPDLElementChangeInfo) arg;
         int action = info.getAction();

         if (!(action == XMLElementChangeInfo.REMOVED || action == XPDLElementChangeInfo.UNDOABLE_ACTION_ENDED))
            return;

         if (info.getAction() == XPDLElementChangeInfo.UNDOABLE_ACTION_ENDED) {
            for (int i = 0; i < info.getChangedSubElements().size(); i++) {
               XPDLElementChangeInfo subinfo = (XPDLElementChangeInfo) info.getChangedSubElements().get(i);
               if (subinfo.getAction() == XMLElementChangeInfo.REMOVED) {
                  removeFromTable(subinfo);
               }
            }
         } else if (info.getAction() == XMLElementChangeInfo.REMOVED) {
            removeFromTable(info);
         }
      } finally {
         updateInProgress = false;
      }
   }

   protected void init() {
      try {
         setModal(false);
         JaWEManager.getInstance().getJaWEController().addObserver(this);

         initDialog();

      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void removeFromTable(XPDLElementChangeInfo info) {
      List l = info.getChangedSubElements();
      if (l == null || l.size() == 0) {
         l = new ArrayList();
         l.add(info.getChangedElement());
      }
      List elements = panelToEdit.getElements();

      Set toRemove = new HashSet();

      if (elements != null) {
         for (int i = 0; i < l.size(); i++) {
            XMLElement el = (XMLElement) l.get(i);
            for (int j = 0; j < elements.size(); j++) {
               XMLElement te = (XMLElement) elements.get(j);
               if (el==te || XMLUtil.isParentsChild(el, te)) {
                  toRemove.add(te);
               }
            }
         }
         Iterator it = toRemove.iterator();
         while (it.hasNext()) {
            Object toR = it.next();
            panelToEdit.removeRow((XMLElement) toR);
         }
      }

   }

   public void setUpdateInProgress(boolean inProgress) {
      updateInProgress=inProgress;
   }

   public boolean isUpdateInProgress() {
      return updateInProgress;
   }

   protected XMLSimpleTablePanel createSTP (String title, XMLCollection owner, List elements, List columnsToShow) {
      return new XMLSimpleTableSelPanel(inlinePanel, owner, columnsToShow, elements, title, true, true, false); 
   }
   
}