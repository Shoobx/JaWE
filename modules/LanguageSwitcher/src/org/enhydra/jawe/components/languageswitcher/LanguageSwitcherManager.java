/**
 * Miroslav Popov, Apr 5, 2006
 * miroslav.popov@gmail.com
 */
package org.enhydra.jawe.components.languageswitcher;

import java.util.List;

import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEComponentSettings;
import org.enhydra.jawe.JaWEComponentView;
import org.enhydra.shark.xpdl.XMLCollection;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.elements.Package;

/**
 * @author Miroslav Popov
 *
 */
public class LanguageSwitcherManager implements JaWEComponent {

   protected String type = JaWEComponent.OTHER_COMPONENT;
   
   protected LanguageSwitcherMenu menu;
   
   protected LanguageSwitcherSettings settings;

   public LanguageSwitcherManager (JaWEComponentSettings settings) throws Exception {
      this.settings = (LanguageSwitcherSettings) settings;
      this.settings.init(this);      
      init();
   }

   public JaWEComponentSettings getSettings() {
      return settings;
   }
   
   protected void init () { 
      menu = new LanguageSwitcherMenu(this);
      menu.configure();
      menu.init();
   }

   public JaWEComponentView getView () {
      return menu;
   }

   public String getType() {
      return type;
   }   

   public void setType(String type) {
      this.type = type; 
   }
   
   public String getName () {
      return "LanguageSwitcherComponent";
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
   }
   
   public boolean isUpdateInProgress() {
      return false;
   }   
   
}
