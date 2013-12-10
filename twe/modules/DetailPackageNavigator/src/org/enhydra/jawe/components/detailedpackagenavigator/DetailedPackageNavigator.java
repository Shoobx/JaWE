package org.enhydra.jawe.components.detailedpackagenavigator;

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
import org.enhydra.shark.xpdl.XMLCollection;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.XMLElementChangeInfo;
import org.enhydra.shark.xpdl.elements.Package;

/**
 *  Used to handle events in TreeView tree.
 *
 *  @author Zoran Milakovic
 */
public class DetailedPackageNavigator implements Observer, TreeSelectionListener, JaWEComponent {

   protected String type = JaWEComponent.TREE_COMPONENT;
   protected DetailedPackageNavigatorPanel panel;

   protected boolean updateInProgress=false;

   protected DetailedPackageNavigatorSettings settings;
   
   public DetailedPackageNavigator(JaWEComponentSettings settings) throws Exception {
      this.settings = (DetailedPackageNavigatorSettings)settings;
      this.settings.init(this);
      init();
      JaWEManager.getInstance().getJaWEController().addObserver(this);
   }

   public JaWEComponentSettings getSettings() {
      return settings;
   }
   
   public DetailedPackageNavigatorSettings getNavigatorSettings() {
      return settings;
   }
   
   public void update(Observable o, Object arg) {
      if (!(arg instanceof XPDLElementChangeInfo)) return;
      XPDLElementChangeInfo info=(XPDLElementChangeInfo)arg;
      int action=info.getAction();      
      if (!(action == XMLElementChangeInfo.UPDATED ||
            action == XMLElementChangeInfo.INSERTED ||
            action == XMLElementChangeInfo.REMOVED ||
            action == XMLElementChangeInfo.REPOSITIONED ||
            action == XPDLElementChangeInfo.VALIDATION_ERRORS ||
            action == XPDLElementChangeInfo.SELECTED ||
            action == XPDLElementChangeInfo.UNDOABLE_ACTION_ENDED ||
            action == XPDLElementChangeInfo.UNDO ||
            action == XPDLElementChangeInfo.REDO)) 
         return;
      
      long start=System.currentTimeMillis();
      JaWEManager.getInstance().getLoggingManager().info("DetailedPackageNavigator -> update for event "+info+" started ...");
      if (action==XPDLElementChangeInfo.UNDOABLE_ACTION_ENDED ||
            action==XPDLElementChangeInfo.UNDO ||
            action==XPDLElementChangeInfo.REDO) {
         for (int i=0; i<info.getChangedSubElements().size(); i++) {
            update((XPDLElementChangeInfo)info.getChangedSubElements().get(i));
         }
      } else {
         update(info);
      }
      JaWEManager.getInstance().getLoggingManager().info("DetailedPackageNavigator -> update ended...");
      long end=System.currentTimeMillis();
      double diffs=(end-start)/1000.0;
      JaWEManager.getInstance().getLoggingManager().debug("THE UPDATE OF DETAILED PKG NAVIG COMPONENT LASTED FOR "+diffs+" SECONDS!");
   }

   public void update (XPDLElementChangeInfo info) {
      if (updateInProgress) return;
      if (info.getSource()==this) {
         return;
      }
      updateInProgress=true;
      try {
         panel.handleXPDLChangeEvent(info);
      } finally {
         updateInProgress=false;
      }
   }

   protected void init () {
      panel=new DetailedPackageNavigatorPanel(this);
      panel.configure();
   }

   public void valueChanged(TreeSelectionEvent e) {
      if (updateInProgress) return;
      JaWEManager.getInstance().getLoggingManager().info("DetailedPackageNavigator -> selection changed ...");

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
//            if (node.getUserObject() instanceof org.enhydra.shark.xpdl.XMLElement) {
//               JaWEManager.getInstance().getLoggingManager().info(
//                     "node.getUserObject().getClass() = " + node.getUserObject());
//            }
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
      JaWEManager.getInstance().getLoggingManager().info("DetailedPackageNavigator -> oabservers notified about selection change!");
   }

   public JaWEComponentView getView () {
       return panel;
    }

    public JComponent getDisplay () {
       return panel.getDisplay();
    }

    public String getType() {
       return type;
    }   

    public void setType(String type) {
       this.type = type; 
    }
    
    public String getName () {
       return "DetailedPackageNavigatorComponent";
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
