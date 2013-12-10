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

import java.awt.Component;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.enhydra.jawe.CombinedIcon;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.ResourceManager;
import org.enhydra.jawe.Utils;
import org.enhydra.jawe.base.controller.JaWEType;
import org.enhydra.jawe.base.xpdlvalidator.ValidationError;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.XMLUtil;
import org.enhydra.shark.xpdl.XMLValidationError;
import org.enhydra.shark.xpdl.elements.Activity;

/**
 *  Used to paint Package hierarchy tree.
 * 
 *  @author Sasa Bojanic
 *  @author Miroslav Popov
 */
public class XPDLTreeCellRenderer extends DefaultTreeCellRenderer {

   protected JaWEComponent owner;
   protected Icon defaultImage;
   protected Icon warning;
   protected Icon error;
   
   protected Set errors = new HashSet();
   protected Set warnings = new HashSet();
   
   public XPDLTreeCellRenderer(JaWEComponent comp) {
      super();
      this.owner = comp;
      try {
         defaultImage = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/default.gif"));
         warning = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/warning_icon_small.gif"));
         error = new ImageIcon(ResourceManager.class.getClassLoader().getResource("org/enhydra/jawe/images/error_icon_small.gif"));
      } catch (Exception e) {         
      }
   }
   
   public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
         int row, boolean pHasFocus) {

      super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, pHasFocus);

      DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
      Object userObj = node.getUserObject();
      
      boolean err = false;
      boolean war = false;
      
      if (warnings.contains(userObj))
         war = true;
      if (errors.contains(userObj))
         err = true;
      
      Icon image = null;

      JaWEType jtype = null;
      if (userObj instanceof XMLElement)
         jtype = JaWEManager.getInstance().getJaWEController().getTypeResolver().getJaWEType((XMLElement)userObj);
      
      if (jtype != null) {
         if (err) {            
//            image = jtype.getErrorIcon();
            image=new CombinedIcon(getIcon((XMLElement)userObj, jtype),error);
         } else if (war) {
            image=new CombinedIcon(getIcon((XMLElement)userObj, jtype),warning);
//               image = jtype.getWarningIcon();
         } else 
            image = getIcon((XMLElement)userObj, jtype);         
      } 
      if (image == null) {
         if (err) {
//            image = error;
            image=new CombinedIcon(defaultImage,error);
         } else if (war) {
//            image = warning;
            image=new CombinedIcon(defaultImage,warning);
         } else {
            image = defaultImage;
         }
      }      
      
      setIcon(image);

      return this;
   }
   
   public void setValidationErrors(List verrors) {
      Set serr=new HashSet();
      Set swarn=new HashSet();
      if (verrors!=null) {
         for (int i=0; i<verrors.size(); i++) {
            ValidationError verr=(ValidationError)verrors.get(i);
            if (verr.getType().equals(XMLValidationError.TYPE_ERROR)) {
               serr.add(verr.getElement());
            } else {
               swarn.add(verr.getElement());               
            }
         }
      }
      this.errors = new HashSet(serr);
      this.warnings = new HashSet(swarn);
      
      Iterator it=serr.iterator();
      while (it.hasNext()) {
         XMLElement el=(XMLElement)it.next();
         this.errors.addAll(XMLUtil.getAllParents(el));
      }      
      it=swarn.iterator();
      while (it.hasNext()) {
         XMLElement el=(XMLElement)it.next();
         this.warnings.addAll(XMLUtil.getAllParents(el));
      }      
   }
   
   public void setErrors (Set errs) {
      this.errors=errs;
   }

   public Set getErrors () {
      return errors;
   }

   public void setWarnings (Set wrns) {
      this.warnings=wrns;
   }

   public Set getWarnings () {
      return warnings;
   }
   
   public ImageIcon getIcon(XMLElement el,JaWEType jtype) {
      if (el instanceof Activity) {
         String icon = ((Activity)el).getIcon();

         ImageIcon ii = null;
         if (!icon.equals("")) {
            ii=(ImageIcon)Utils.getOriginalActivityIconsMap().get(icon);
         }

         if (ii == null) {
            ii = jtype.getIcon();
         }

         return ii;
         
      }
      return jtype.getIcon();
   }

}
