/**
 * Miroslav Popov, Oct 3, 2005
 * miroslav.popov@gmail.com
 */
package org.enhydra.jawe.base.controller;

import java.awt.Color;

import javax.swing.ImageIcon;

/**
 * @author Miroslav Popov
 *
 */
public class JaWEType {

   protected Class type;
   protected String typeId;   
   protected String displayName;
   protected ImageIcon typeIcon;
   protected Color color;
   
   public JaWEType(Class classType, String typeId) {
      this.type = classType;
      this.typeId = typeId;
   }
   
   public JaWEType(Class classType, String typeId, String displayName, ImageIcon icon, Color color) {
      this.type = classType;
      this.typeId = typeId;
      this.displayName = displayName;
      this.typeIcon = icon;
      this.color = color;
   }

   public JaWEType(JaWEType old) {
      this.type = old.type;
      this.typeId = old.typeId;
      this.displayName = old.displayName;
      this.typeIcon = old.typeIcon;
      this.color = old.color;
   }
   
   public Class getClassType() {
      return type;
   }
   
   public String getTypeId() {
      return typeId;
   }
   
   public String getDisplayName() {
      return displayName;
   }
   
   public ImageIcon getIcon() {
      return typeIcon;
   }
   
   public Color getColor() {
      return new Color(color.getRed(),color.getGreen(),color.getBlue());
   }

   public void setColor(Color color) {
      this.color = color;
   }

   public void setDisplayName(String displayName) {
      this.displayName = displayName;
   }

   public void setIcon(ImageIcon icon) {
      this.typeIcon = icon;
   }
   
}
