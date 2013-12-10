package org.enhydra.jawe.shark.business;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.enhydra.jxpdl.XMLCollection;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.elements.Activity;
import org.enhydra.jxpdl.elements.ExtendedAttribute;
import org.enhydra.jxpdl.elements.ExtendedAttributes;

public class DeadlineEmailConfigurationElements extends XMLCollection {

   protected ExtendedAttributes eas;

   public DeadlineEmailConfigurationElements(ExtendedAttributes eas) {
      super((Activity) eas.getParent(), "NonDefaultDeadlineEmailConfiguration", false);
      this.eas = eas;
      notifyMainListeners = false;
      notifyListeners = false;
      handleStructure();
   }

   public void add(XMLElement el) {
      super.add(el);
      EmailConfigurationElement cel = (EmailConfigurationElement) el;
      cel.setCanSetValue(true);
      cel.setPersisted(true);
      cel.setValue(null);
   }

   public boolean add(int no, XMLElement el) {
      boolean ret = super.add(no, el);
      EmailConfigurationElement cel = (EmailConfigurationElement) el;
      cel.setCanSetValue(true);
      cel.setPersisted(true);
      cel.setValue(null);
      return ret;
   }

   protected XMLElement removeElement(int no) {
      XMLElement el = super.removeElement(no);
      ((EmailConfigurationElement) el).setConfigurable(false);
      el.setValue(null);
      return el;
   }

   public XMLElement generateNewElement() {
      EmailConfigurationElement ece = new EmailConfigurationElement(eas,
                                                                    true,
                                                                    false,
                                                                    true,
                                                                    false,
                                                                    "");
      ece.setCanSetValue(false);
      return ece;
   }

   protected void handleStructure() {
      Iterator it = eas.toElements().iterator();
      List<String> possiblePostfixes = new ArrayList<String>();
      while (it.hasNext()) {
         ExtendedAttribute ea = (ExtendedAttribute) it.next();
         String eaname = ea.getName();
         for (String ean : SharkConstants.SMTP_DEADLINE_HANDLER_POSSIBLE_EAS_LIST) {
            if (eaname.startsWith(ean + "_")) {
               String posPf = eaname.substring((ean + "_").length());
               if (!possiblePostfixes.contains(posPf) && !posPf.equals("")) {
                  possiblePostfixes.add(posPf);
               }
            }
         }
      }
      for (String posPf : possiblePostfixes) {
         EmailConfigurationElement ece = new EmailConfigurationElement(eas,
                                                                       true,
                                                                       false,
                                                                       true,
                                                                       false,
                                                                       posPf);
         if (ece.isPersisted()) {
            super.add(ece);
         }
      }
   }
}
