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
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.XMLValidationError;
import org.enhydra.jxpdl.elements.Activity;

/**
 * Used to paint Package hierarchy tree.
 */
public class XPDLTreeCellRenderer extends DefaultTreeCellRenderer {

   /**
    * The {@link JaWEComponent} that owns the tree.
    */
   protected JaWEComponent owner;

   /**
    * Default image to show for the node in the tree.
    */
   protected Icon defaultImage;

   /**
    * Warning image to show on top of the image when the {@link XMLElement} has some validation warnings.
    */
   protected Icon warning;

   /**
    * Error image to show on top of the image when the {@link XMLElement} has some validation errors.
    */
   protected Icon error;

   /**
    * Set of {@link XMLElement} instances that have validation errors.
    */
   protected Set errors = new HashSet();

   /**
    * Set of {@link XMLElement} instances that have validation warnings.
    */
   protected Set warnings = new HashSet();

   /**
    * Creates new instance of renderer for the given {@link JaWEComponent}.
    * 
    * @param comp {@link JaWEComponent} instance.
    */
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

   public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean pHasFocus) {

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
         jtype = JaWEManager.getInstance().getJaWEController().getTypeResolver().getJaWEType((XMLElement) userObj);

      if (jtype != null) {
         if (err) {
            // image = jtype.getErrorIcon();
            image = new CombinedIcon(getIcon((XMLElement) userObj, jtype), error);
         } else if (war) {
            image = new CombinedIcon(getIcon((XMLElement) userObj, jtype), warning);
            // image = jtype.getWarningIcon();
         } else
            image = getIcon((XMLElement) userObj, jtype);
      }
      if (image == null) {
         if (err) {
            // image = error;
            image = new CombinedIcon(defaultImage, error);
         } else if (war) {
            // image = warning;
            image = new CombinedIcon(defaultImage, warning);
         } else {
            image = defaultImage;
         }
      }

      setIcon(image);

      return this;
   }

   /**
    * Sets the validation errors and warnings for the {@link XMLElement} instances and their parents.
    * 
    * @param verrors List of {@link XMLElement} instances that have validation warnings or errors.
    */
   public void setValidationErrors(XMLElement element, List verrors) {
      Set serr = new HashSet();
      Set swarn = new HashSet();
      if (verrors != null) {
         for (int i = 0; i < verrors.size(); i++) {
            ValidationError verr = (ValidationError) verrors.get(i);
            if (verr.getType().equals(XMLValidationError.TYPE_ERROR)) {
               serr.add(verr.getElement());
            } else {
               swarn.add(verr.getElement());
            }
         }
      }
      if (element == null || element instanceof org.enhydra.jxpdl.elements.Package) {
         this.errors = new HashSet(serr);
         this.warnings = new HashSet(swarn);
      } else {
         Iterator it = this.errors.iterator();
         Set parents = XMLUtil.getAllParents(element);

         while (it.hasNext()) {
            XMLElement el = (XMLElement) it.next();
            if (XMLUtil.getParentElement(element.getClass(), el) == element || parents.contains(el)) {
               it.remove();
            }
         }
         this.errors.addAll(serr);
         serr = new HashSet(this.errors);

         it = this.warnings.iterator();
         while (it.hasNext()) {
            XMLElement el = (XMLElement) it.next();
            if (XMLUtil.getParentElement(element.getClass(), el) == element || parents.contains(el)) {
               it.remove();
            }
         }
         this.warnings.addAll(swarn);
         swarn = new HashSet(this.warnings);
      }
      Iterator it = serr.iterator();
      while (it.hasNext()) {
         XMLElement el = (XMLElement) it.next();
         this.errors.addAll(XMLUtil.getAllParents(el));
      }
      it = swarn.iterator();
      while (it.hasNext()) {
         XMLElement el = (XMLElement) it.next();
         this.warnings.addAll(XMLUtil.getAllParents(el));
      }
   }

   /**
    * Sets the validation errors.
    * 
    * @param errs Set of {@link XMLElement} instances that have validation errors.
    */
   public void setErrors(Set errs) {
      this.errors = errs;
   }

   /**
    * @return Set of {@link XMLElement} instances that have validation errors.
    */
   public Set getErrors() {
      return errors;
   }

   /**
    * Sets the validation warnings.
    * 
    * @param wrns Set of {@link XMLElement} instances that have validation warnings.
    */
   public void setWarnings(Set wrns) {
      this.warnings = wrns;
   }

   /**
    * @return Set of {@link XMLElement} instances that have validation warnings.
    */
   public Set getWarnings() {
      return warnings;
   }

   /**
    * Searches for the icon of the given {@link XMLElement}.
    * 
    * @param el {@link XMLElement} instance.
    * @param jtype {@link JaWEType} instance.
    * @return Icon for the given {@link XMLElement}.
    */
   public ImageIcon getIcon(XMLElement el, JaWEType jtype) {
      if (el instanceof Activity) {
         String icon = ((Activity) el).getIcon();

         ImageIcon ii = null;
         if (!icon.equals("")) {
            ii = (ImageIcon) Utils.getOriginalActivityIconsMap().get(icon);
         }

         if (ii == null) {
            ii = jtype.getIcon();
         }

         return ii;

      }
      return jtype.getIcon();
   }

}
