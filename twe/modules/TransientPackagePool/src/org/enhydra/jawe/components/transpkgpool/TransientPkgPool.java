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

package org.enhydra.jawe.components.transpkgpool;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JComponent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEComponentSettings;
import org.enhydra.jawe.JaWEComponentView;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.XPDLElementChangeInfo;
import org.enhydra.jawe.components.XPDLTreeNode;
import org.enhydra.jxpdl.XMLCollection;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLElementChangeInfo;
import org.enhydra.jxpdl.elements.Package;

/**
 *  Used to display the pool of transient Packages.
 *
 *  @author Sasa Bojanic
 */
public class TransientPkgPool implements Observer, JaWEComponent, TreeSelectionListener {

	protected String type = JaWEComponent.SPECIAL_COMPONENT;
   protected boolean updateInProgress=false;

   protected TransientPkgPoolPanel panel;

   protected TransientPkgPoolSettings settings;

   public TransientPkgPool (JaWEComponentSettings settings) throws Exception {
      this.settings=(TransientPkgPoolSettings)settings;
      this.settings.init(this);
      init();
      JaWEManager.getInstance().getJaWEController().addObserver(this);
   }

   public JaWEComponentSettings getSettings() {
      return settings;
   }
   
    public TransientPkgPoolSettings getExtSettings () {
       return settings;
    }

   public void update(Observable o, Object arg) {
      if (!(arg instanceof XPDLElementChangeInfo)) return;
      XPDLElementChangeInfo info=(XPDLElementChangeInfo)arg;
      int action=info.getAction();
      if (!(action == XMLElementChangeInfo.INSERTED ||
            action == XMLElementChangeInfo.REMOVED ||
            action == XPDLElementChangeInfo.SELECTED ||
            action == XPDLElementChangeInfo.VALIDATION_ERRORS))
         return;



      long start=System.currentTimeMillis();
      JaWEManager.getInstance().getLoggingManager().info("TransientPkgPool -> update for event "+info+" started ...");
      update(info);
      JaWEManager.getInstance().getLoggingManager().info("TransientPkgPool -> update ended...");
      long end=System.currentTimeMillis();
      double diffs=(end-start)/1000.0;
      JaWEManager.getInstance().getLoggingManager().debug("THE UPDATE OF TRANSIENT PKG POOL COMPONENT LASTED FOR "+diffs+" SECONDS!");
   }

   public void update (XPDLElementChangeInfo info) {
      if (updateInProgress) return;
      if (info.getSource()==this) {
         return;
      }
      updateInProgress=true;
      try {
         panel.refreshTransientPkgPanel(info);
      } finally {
         updateInProgress=false;
      }
   }

   protected void init () {
      panel=new TransientPkgPoolPanel(this);
      panel.configure();
   }

   public void valueChanged(TreeSelectionEvent e) {
      if (updateInProgress) return;
      JaWEManager.getInstance().getLoggingManager().info("TransientPackagePool -> selection changed ...");

      TreePath oldsel=e.getOldLeadSelectionPath();
      
      TreePath[] sel = e.getPaths();
      List selection = new ArrayList();
      boolean hasAdded = false;
      boolean hasRemoved = false;

      int j = 0;
      for (int i = 0; i < sel.length; i++) {
         if (sel[i] == e.getNewLeadSelectionPath()) {
            j = i;
            break;
         }
      }

      TreePath temp = sel[j];
      sel[j] = sel[sel.length-1];
      sel[sel.length - 1] = temp;

      for (int i = 0; i < sel.length; i++) {
         if (e.isAddedPath(sel[i]))
            hasAdded = true;
         else
            hasRemoved = true;
      }

      if (oldsel==null) {
         hasAdded=false;
         hasRemoved=false;         
      }
      
      for (int i = 0; i < sel.length; i++) {
         if (e.isAddedPath(sel[i])) {
            XPDLTreeNode node = (XPDLTreeNode) sel[i].getLastPathComponent();
            if (node == null || node.getXPDLElement() == null) return;
            selection.add(node.getXPDLElement());
         }
      }

      if (hasAdded && hasRemoved) {
         JaWEManager.getInstance().getJaWEController().getSelectionManager().setSelection(selection, true);
      } else if (hasAdded && !hasRemoved) {
         JaWEManager.getInstance().getJaWEController().getSelectionManager().addToSelection(selection);
      } else {
         selection.clear();
         if (panel.tree.isSelectionEmpty()) {
            selection.add(((XPDLTreeNode) sel[0].getLastPathComponent()).getXPDLElement());
         } else {
            sel = panel.tree.getSelectionPaths();
            for (int i = 0; i < sel.length; i++) {
                  XPDLTreeNode node = (XPDLTreeNode) sel[i].getLastPathComponent();
                  if (node == null || node.getXPDLElement() == null) return;
                  selection.add(node.getXPDLElement());
            }
         }
         JaWEManager.getInstance().getJaWEController().getSelectionManager().setSelection(selection, true);
      }
      JaWEManager.getInstance().getLoggingManager().info(
            "TransientPackagePool -> observers notified about selection changed!");// to " + node + ", xpdlElement=";
//                  + node.getXPDLElement().toName() + ", Id="
//                  + ((XMLComplexElement) node.getXPDLElement()).get("Id").toValue());
   }

   public JaWEComponentView getView () {
       return panel;
    }

    public JComponent getDisplay () {
       return panel.getDisplay();
    }

    public String getComponentType () {
       return type;
    }

    public void setComponentType (String type) {
       this.type=type;
    }

    public String getName () {
       return "TransientPkgPoolComponent";
    }

    public boolean adjustXPDL (Package pkg) {
       return false;
    }

    public List checkValidity (XMLElement el,boolean fullCheck) {
       return null;
    }

    public boolean canCreateElement(XMLCollection col) {
       return true;
    }

    public boolean canInsertElement(XMLCollection col, XMLElement el) {
       return true;
    }

    public boolean canModifyElement(XMLElement el) {
       return true;
    }

    public boolean canRemoveElement(XMLCollection col, XMLElement el) {
       return true;
    }

    public boolean canDuplicateElement(XMLCollection col, XMLElement el) {
       return true;
    }

    public boolean canRepositionElement(XMLCollection col, XMLElement el) {
       return true;
    }

    public void setUpdateInProgress(boolean inProgress) {
       updateInProgress=inProgress;
    }

    public boolean isUpdateInProgress() {
       return updateInProgress;
    }

}
