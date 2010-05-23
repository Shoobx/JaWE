/**
 * Miroslav Popov, Dec 1, 2005
 * miroslav.popov@gmail.com
 */
package org.enhydra.jawe;

import javax.swing.ImageIcon;

/**
 * @author Miroslav Popov
 *
 */
public class JaWEAction {

   private ActionBase action;
   private String langDepName = null;
   private ImageIcon icon = null;
   
   public JaWEAction() {      
   }
   
   public JaWEAction(ActionBase action) {
      init(action, null, null);
   }
   
   public JaWEAction(ActionBase action, ImageIcon icon) {
      init(action, icon, null);
   }
   
   public JaWEAction(ActionBase action, String langName) {
      init(action, null, langName);
   }
   
   public JaWEAction(ActionBase action, ImageIcon icon, String langName) {
      init(action, icon, langName);
   }
   
   private void init(ActionBase pAction, ImageIcon pIcon, String pLangName) {
      this.action = pAction;
      this.icon = pIcon;
      this.langDepName = pLangName;
   }
   
   public ActionBase getAction() {
      return action;
   }
   
   public ImageIcon getIcon() {
      return icon;
   }
   
   public String getLangDepName() {
      return langDepName;
   }
   
   public void setAction(ActionBase action) {
      this.action = action;
   }
   
   public void setIcon(ImageIcon icon) {
      this.icon = icon;
   }
   
   public void setLangDepName(String langDepName) {
      this.langDepName = langDepName;
   }
   
   
   
}
