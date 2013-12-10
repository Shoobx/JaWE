package org.enhydra.jawe.components.simplenavigator;

import java.util.Iterator;

import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.components.XPDLTreeModel;
import org.enhydra.jawe.components.XPDLTreeNode;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.XMLUtil;
import org.enhydra.shark.xpdl.elements.ActivitySet;
import org.enhydra.shark.xpdl.elements.Package;
import org.enhydra.shark.xpdl.elements.WorkflowProcess;

/**
 * TreeView hierarchy tree model.
 * 
 * @author Sasa Bojanic
 */
public class SimpleNavigatorTreeModel extends XPDLTreeModel {

   public SimpleNavigatorTreeModel(JaWEComponent comp) {
      super(comp);
   }

   public XPDLTreeNode insertNode(XMLElement el) {
      if (el instanceof Package
          || el instanceof WorkflowProcess || el instanceof ActivitySet) {

         if (el instanceof Package) {
            return super.insertNode(el);
         }

         XPDLTreeNode parent = null;
         if (el instanceof WorkflowProcess) {
            parent = findNode(XMLUtil.getPackage(el));
         } else if (el instanceof ActivitySet) {
            parent = findNode(XMLUtil.getWorkflowProcess(el));
         }
         if (parent != null) {
            XPDLTreeNode n = findNode(parent, el);
            if (n == null) {
               n = insertNode(parent, el);
            } else {
               n = null;
            }
            return n;
         }
      }

      return null;
   }

   protected XPDLTreeNode insertNode(XPDLTreeNode parent, XMLElement el) {
      if (el instanceof Package
          || el instanceof WorkflowProcess || el instanceof ActivitySet) {
         XPDLTreeNode n = createNode(el);
         insertNodeInto(n, parent, parent.getChildCount());

         if (el instanceof Package
             || el instanceof WorkflowProcess || el instanceof ActivitySet) {

            if (el instanceof Package) {
               Iterator it = ((Package) el).getWorkflowProcesses()
                  .toElements()
                  .iterator();
               while (it.hasNext()) {
                  insertNode(n, (WorkflowProcess) it.next());
               }
            } else if (el instanceof WorkflowProcess) {
               Iterator it = ((WorkflowProcess) el).getActivitySets()
                  .toElements()
                  .iterator();
               while (it.hasNext()) {
                  insertNode(n, (ActivitySet) it.next());
               }
            }
         }
         return n;
      }
      return null;
   }

   protected XPDLTreeNode createNode(XMLElement el) {
      return new XPDLTreeNode(el) {
         public String toString() {
            if (userObject instanceof XMLElement) {
               return JaWEManager.getInstance()
                  .getDisplayNameGenerator()
                  .getDisplayName(((XMLElement) userObject));
            }

            return userObject.toString();
         }
      };
   }

   public boolean isLeaf(Object node) {
      Object uo = ((XPDLTreeNode) node).getUserObject();
      if (uo instanceof XMLElement) {
         if (uo instanceof Package) {
            return ((Package) uo).getWorkflowProcesses().size() == 0;
         } else if (uo instanceof WorkflowProcess) {
            WorkflowProcess wp = (WorkflowProcess) uo;
            return wp.getActivitySets().size() == 0;
         } else if (uo instanceof ActivitySet) {
            return true;
         }
      }
      return false;
   }

}
