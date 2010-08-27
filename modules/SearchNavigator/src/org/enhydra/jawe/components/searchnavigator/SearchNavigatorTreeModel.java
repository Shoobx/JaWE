package org.enhydra.jawe.components.searchnavigator;

import java.util.Stack;

import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.components.XPDLTreeModel;
import org.enhydra.jawe.components.XPDLTreeNode;
import org.enhydra.shark.xpdl.XMLCollection;
import org.enhydra.shark.xpdl.XMLComplexChoice;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.elements.Implementation;
import org.enhydra.shark.xpdl.elements.Package;

/**
 *  Search Navigator hierarchy tree model.
 * 
 *  @author Sasa Bojanic
 */
public class SearchNavigatorTreeModel extends XPDLTreeModel {

   public SearchNavigatorTreeModel (JaWEComponent comp) {
      super(comp);
   }

   public XPDLTreeNode insertNode(XMLElement el) {
      Stack toInsert=new Stack();
      toInsert.push(el);
      XMLElement parent;
      while ((parent=el.getParent())!=null) {
         if (!(parent instanceof XMLCollection || parent instanceof XMLComplexChoice || parent instanceof Implementation)) {
            toInsert.push(parent);
         }
         el=parent;
      }
      
//      System.err.println("There are "+toInsert.size()+" tree objects to insert!");
      XPDLTreeNode n=null;
      XPDLTreeNode p=null;
      while (!toInsert.isEmpty()) {
         XMLElement elToIns=(XMLElement)toInsert.pop();
//         System.err.println("Inserting el "+elToIns);
         n=findNode(elToIns);
//         System.err.println("... already exists - "+(n!=null));
         if (n==null) {
            if (elToIns instanceof Package) {
//               System.err.println("... inserting package");
               n=super.insertNode(elToIns);
            } else {
//               System.err.println("... inserting node");
               n=insertNode(p,elToIns);
            }   
         }
         p=n;
      }
      return n;
   }
   
   protected XPDLTreeNode insertNode (XPDLTreeNode parent,XMLElement el) {
      XPDLTreeNode n=createNode(el);
      insertNodeInto(n, parent, parent.getChildCount());
      return n;
   }
    
   protected XPDLTreeNode createNode (XMLElement el) {
      return new XPDLTreeNode(el) {
         public String toString () {
            if (userObject instanceof XMLElement) { 
               return  JaWEManager.getInstance().getDisplayNameGenerator().getDisplayName(((XMLElement)userObject));
            }
               
            return userObject.toString();                        
         }
      };
   }
}
