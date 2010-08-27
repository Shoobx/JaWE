package org.enhydra.jawe.components;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 *  Utility class for handling the tree.
 * 
 *  @author Zoran Milakovic
 *  @author Sasa Bojanic
 */
public class XPDLTreeUtil {

   /**
    * Expands/Collapse specified tree to a certain level.
    * 
    * @param tree jtree to expand to a certain level
    * @param level the level of expansion
    */
   public static void expandOrCollapsToLevel(JTree tree, TreePath treePath,int level,boolean expand) {
      try {
         expandOrCollapsePath(tree,treePath,level,0,expand);
      }catch(Exception e) {
e.printStackTrace();
         //do nothing
      }
   }
   
   public static void expandOrCollapsePath (JTree tree,TreePath treePath,int level,int currentLevel,boolean expand) {
//      System.err.println("Exp level "+currentLevel+", exp="+expand);
      if (expand && level<=currentLevel && level>0) return;
      
      TreeNode treeNode = ( TreeNode ) treePath.getLastPathComponent();
      TreeModel treeModel=tree.getModel();
      if ( treeModel.getChildCount(treeNode) >= 0 ) {
         for ( int i = 0; i < treeModel.getChildCount(treeNode); i++  ) {
            TreeNode n = ( TreeNode )treeModel.getChild(treeNode, i);
            TreePath path = treePath.pathByAddingChild( n );
            expandOrCollapsePath(tree,path,level,currentLevel+1,expand);
         }
         if (!expand && currentLevel<level) return;
      }      
      if (expand) {
         tree.expandPath( treePath );
//         System.err.println("Path expanded at level "+currentLevel+"-"+treePath);
      } else {
         tree.collapsePath(treePath);
//         System.err.println("Path collapsed at level "+currentLevel+"-"+treePath);
      }
   }
   
   
}


