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

package org.enhydra.jawe.components;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jxpdl.XMLBaseForCollectionAndComplex;
import org.enhydra.jxpdl.XMLCollection;
import org.enhydra.jxpdl.XMLComplexChoice;
import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.elements.DataTypes;
import org.enhydra.jxpdl.elements.Package;

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

      String[] hstra = XMLUtil.tokenize(hstr, " ");
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
      String[] els = XMLUtil.tokenize(elAttr, " ");
      for (int i = 0; i < els.length; i++) {
         String key = els[i];
         String hstr = (String) comp.getSettings().getSetting("HideElements." + col.toName() + "." + key);
         String[] hstra = XMLUtil.tokenize(hstr, " ");
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


