package org.enhydra.jawe.components;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.Utils;
import org.enhydra.shark.xpdl.XMLBaseForCollectionAndComplex;
import org.enhydra.shark.xpdl.XMLCollection;
import org.enhydra.shark.xpdl.XMLComplexChoice;
import org.enhydra.shark.xpdl.XMLComplexElement;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.elements.DataTypes;
import org.enhydra.shark.xpdl.elements.Package;

/**
 *  TreeView hierarchy tree model.
 * 
 *  @author Zoran Milakovic
 *  @author Sasa Bojanic
 */
public class XPDLTreeModel extends DefaultTreeModel {

   JaWEComponent comp;
   
   public XPDLTreeModel (JaWEComponent comp) {
      super(new XPDLTreeNode(), true);
      this.comp = comp;
   }

   public XPDLTreeNode getRootNode () {
      return (XPDLTreeNode)super.getRoot();
   }
   
   public void clearTree () {
      getRootNode().removeAllChildren();
   }
   
   public XPDLTreeNode insertNode (XMLElement el) {
      XPDLTreeNode n=null;
      if (el instanceof Package) {
         n=findNode(getRootNode(), el);
         if (n==null) {
            n=insertNode(getRootNode(),el);
         }
      } else {
         XPDLTreeNode parent=findNode(el.getParent());
         if (parent!=null) {
            if (parent.getXPDLElement() instanceof XMLCollection) {
               Set hidden=getHiddenElements((XMLCollection)parent.getXPDLElement());
               if (hidden.contains(el)) {
                  return null;
               }
            }
            n=findNode(parent, el);
            if (n==null) {
               n=insertNode(parent,el);
            } else {
               n=null;
            }
         } else {
//            System.err.println("Can't find parent node for element "+el);
         }
      }
      return n;
   }
   
   protected XPDLTreeNode insertNode (XPDLTreeNode parent,XMLElement el) {
      XPDLTreeNode n=createNode(el);
      insertNodeInto(n, parent, parent.getChildCount());
      if (el instanceof XMLBaseForCollectionAndComplex) {
         XMLBaseForCollectionAndComplex xbcc=(XMLBaseForCollectionAndComplex)el;
         Set hidden=null;
         if (xbcc instanceof XMLComplexElement) {
            hidden=getHiddenElements((XMLComplexElement)xbcc);
         } else {
            hidden=getHiddenElements((XMLCollection)xbcc);
         }
         if (hidden.size()==1 && hidden.contains("*")) {
            return n;
         }
         Iterator it=xbcc.toElements().iterator();
         while (it.hasNext()) {
            XMLElement sbel=(XMLElement)it.next();
            if (hidden.contains(sbel)) {
               continue;
            }
            insertNode(n,sbel);
         }
      } else if (el instanceof XMLComplexChoice && !(el instanceof DataTypes)) {
         XMLComplexChoice xcc=(XMLComplexChoice)el;
         Iterator it=xcc.getChoices().iterator();
         while (it.hasNext()) {
            XMLElement sbel=(XMLElement)it.next();
            insertNode(n,sbel);
         }         
      }      
      return n;
   }
   
   public XPDLTreeNode removeNode (XMLElement el) {
      XPDLTreeNode n=findNode(el);
      if (n!=null) {
         //XPDLTreeNode parent=(XPDLTreeNode)n.getParent();
         //parent.rem
//         n.removeFromParent();
         removeNodeFromParent(n);
      }
      return n;
   }

   public void repositionNode (XMLElement el,int newpos) {
      XPDLTreeNode n=findNode(el);
      if (n!=null) {
         XPDLTreeNode parent=(XPDLTreeNode)n.getParent();
         // TODO: repositioning should now consider the possible hidden elements
         if (parent.getChildCount()<newpos+1) {
            return;
         }
         removeNodeFromParent(n);
//         n.removeFromParent();
         insertNodeInto(n, parent, newpos);
      }
   }   
   
   public XPDLTreeNode findNode (XMLElement el) {
      XPDLTreeNode lRoot=getRootNode();
      XPDLTreeNode n=findNode(lRoot,el);
//      if (n==null) {
//         System.err.println("Can't find node for element "+el);
//      }
      return n;
   }

   protected XPDLTreeNode findNode (XPDLTreeNode start,XMLElement el) {
      if (start.getUserObject()==el) {
         return start;
      }
      Enumeration nodes=start.children();
      while (nodes.hasMoreElements()) {
         XPDLTreeNode chld=(XPDLTreeNode)nodes.nextElement();
         XPDLTreeNode fn=findNode(chld,el);
         if (fn!=null) {
            return fn;
         }         
      }
      return null;      
   }
       
   protected XPDLTreeNode createNode (XMLElement el) {
      return new XPDLTreeNode(el);
   }
   
   public boolean isLeaf(Object node) {
      return ((TreeNode)node).isLeaf();
   }

   protected Set getHiddenElements (XMLComplexElement cel) {
      Set hidden=new HashSet();
      String hstr = (String)comp.getSettings().getSetting("HideSubElements." + cel.toName() );

      String[] hstra = Utils.tokenize(hstr, " ");
      if (hstra != null) {
         for (int i = 0; i < hstra.length; i++) {
            XMLElement el = cel.get(hstra[i]);
            if (el != null) {
               hidden.add(el);
            } else if (cel instanceof Package) {
               Package pkg = (Package) cel;
               if (hstra[i].equals(pkg.getNamespaces().toName())) {
                  hidden.add(pkg.getNamespaces());
               }
            }
         }
      }      
      return hidden;
   }

   protected Set getHiddenElements(XMLCollection col) {
      Set hidden = new HashSet();
      String elAttr = (String) comp.getSettings().getSetting("HideElements." + col.toName());
      String[] els = Utils.tokenize(elAttr, " ");
      for (int i = 0; i < els.length; i++) {
         String key = els[i];
         String hstr = (String) comp.getSettings().getSetting("HideElements." + col.toName() + "." + key);
         String[] hstra = Utils.tokenize(hstr, " ");
         if (hstra != null) {
            for (int k = 0; k < hstra.length; k++) {
               if (hstra[k].equals("*")) {
                  hidden.addAll(col.toElements());
                  return hidden;
               }
               Iterator ci = col.toElements().iterator();
               while (ci.hasNext()) {
                  XMLElement el = (XMLElement) ci.next();
                  if (key.equals("")) {
                     if (el.toValue().equals(hstra[k])) {
                        hidden.add(el);
                     }
                  } else {
                     if (el instanceof XMLComplexElement) {
                        XMLElement sel = ((XMLComplexElement) el).get(key);
                        if (sel != null) {
                           if (sel.toValue().equals(hstra[k])) {
                              hidden.add(el);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
      return hidden;
   }
   
}


