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

package org.enhydra.jawe.shark;

import java.util.Map;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.display.DisplayNameGenerator;
import org.enhydra.jawe.base.label.LabelGenerator;
import org.enhydra.jawe.base.tooltip.StandardTooltipGenerator;
import org.enhydra.jawe.base.tooltip.TooltipGeneratorSettings;
import org.enhydra.jxpdl.XMLCollectionElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.XPDLConstants;
import org.enhydra.jxpdl.elements.Activity;
import org.enhydra.jxpdl.elements.ExtendedAttribute;
import org.enhydra.jxpdl.elements.ExtendedAttributes;
import org.enhydra.jxpdl.utilities.SequencedHashMap;

/**
 * Used to generate tooltips for representing XPDL entities.
 * 
 * @author Sasa Bojanic
 */
public class SharkTooltipGenerator extends StandardTooltipGenerator {

   public SharkTooltipGenerator() throws Exception {
      super();
   }

   public SharkTooltipGenerator(TooltipGeneratorSettings settings) {
      super(settings);
   }

   public String getTooltip(Activity el) {
      String tt = super.getTooltip(el);

      Map vars = XMLUtil.getWorkflowProcess(el).getAllVariables();
      ExtendedAttributes eas = el.getExtendedAttributes();
      Map toDisplay = new SequencedHashMap();
      DisplayNameGenerator dng = JaWEManager.getInstance().getDisplayNameGenerator();
      for (int i = 0; i < eas.size(); i++) {
         ExtendedAttribute ea = (ExtendedAttribute) eas.get(i);
         if (ea.getName().equals(SharkConstants.VTP_VIEW)
             || ea.getName().equals(SharkConstants.VTP_UPDATE)) {
            XMLCollectionElement var = (XMLCollectionElement) vars.get(ea.getVValue());
            if (var != null) {
               putEAKeyValue(dng,
                             toDisplay,
                             var,
                             ea.getName().equals(SharkConstants.VTP_VIEW));
            }
         }
      }
      if (toDisplay.size() > 0) {
         String tt2 = makeTooltip(el, toDisplay);
         tt = tt.substring(0, tt.length() - HTML_CLOSE.length());
         tt += LINE_BREAK;
         tt += "<hr>";
         tt = tt + tt2.substring(HTML_OPEN.length(), tt2.length());
      }
      if (el.getActivityType() == XPDLConstants.ACTIVITY_TYPE_NO) {
         String tt2 = makeSMTPTooltip(eas);
         if (!tt2.equals("")) {
            tt = tt.substring(0, tt.length() - HTML_CLOSE.length());
            tt += LINE_BREAK;
            tt += "<hr>";
            tt = tt + tt2.substring(HTML_OPEN.length(), tt2.length());
         }
      }

      return tt;
   }

   protected String makeTooltip(XMLElement el, Map elements) {
      if (el instanceof Activity) {
         if (elements == null)
            return "";
         Activity act = (Activity) el;
         LabelGenerator lg = JaWEManager.getInstance().getLabelGenerator();

         if (!(act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_TASK_APPLICATION)) {
            elements.remove(settings.getLanguageDependentString("StartModeKey"));
            elements.remove(settings.getLanguageDependentString("FinishModeKey"));
            if (!(act.getActivityType() == XPDLConstants.ACTIVITY_TYPE_NO)) {
               elements.remove(settings.getLanguageDependentString("PriorityKey"));
               elements.remove(settings.getLanguageDependentString("LimitKey"));
            }
         }
      }

      return super.makeTooltip(el, elements);
   }

   protected String makeSMTPTooltip(ExtendedAttributes el) {
      Map toDisplay = new SequencedHashMap();
      ExtendedAttribute ea = el.getFirstExtendedAttributeForName(SharkConstants.EA_SMTP_EVENT_AUDIT_MANAGER_MODE);
      makeSMTPEntry(ea, toDisplay);
      ea = el.getFirstExtendedAttributeForName(SharkConstants.EA_SMTP_EVENT_AUDIT_MANAGER_EXECUTION_MODE);
      makeSMTPEntry(ea, toDisplay);
      ea = el.getFirstExtendedAttributeForName(SharkConstants.EA_SMTP_EVENT_AUDIT_MANAGER_GROUP_EMAIL_ONLY);
      makeSMTPEntry(ea, toDisplay);
      ea = el.getFirstExtendedAttributeForName(SharkConstants.EA_SMTP_EVENT_AUDIT_MANAGER_SUBJECT);
      makeSMTPEntry(ea, toDisplay);
      ea = el.getFirstExtendedAttributeForName(SharkConstants.EA_SMTP_EVENT_AUDIT_MANAGER_CONTENT);
      makeSMTPEntry(ea, toDisplay);
      ea = el.getFirstExtendedAttributeForName(SharkConstants.EA_SMTP_EVENT_AUDIT_MANAGER_ATTACHMENTS);
      makeSMTPEntry(ea, toDisplay);
      ea = el.getFirstExtendedAttributeForName(SharkConstants.EA_SMTP_EVENT_AUDIT_MANAGER_ATTACHMENT_NAMES);
      makeSMTPEntry(ea, toDisplay);
      ea = el.getFirstExtendedAttributeForName(SharkConstants.EA_SMTP_EVENT_AUDIT_MANAGER_DM_ATTACHMENTS);
      makeSMTPEntry(ea, toDisplay);
      if (toDisplay.size()>0) {
         return super.makeTooltip(el, toDisplay);
      } else {
         return "";
      }
   }

   protected void makeSMTPEntry(ExtendedAttribute ea, Map toDisplay) {
      if (ea != null) {
         String lbl = getSettings().getLanguageDependentString(ea.getName() + "Key");
         if (lbl == null) {
            lbl = ea.getName();
         }
         if (lbl.endsWith("?")) {
            lbl = lbl.substring(0, lbl.length() - 1);
         }
         toDisplay.put(lbl, ea.getVValue());
      }
   }

   protected void putEAKeyValue(DisplayNameGenerator dng,
                                Map toPut,
                                XMLCollectionElement el,
                                boolean readOnly) {
      String n = settings.getLanguageDependentString("ReadOnlyKey");
      if (!readOnly) {
         n = settings.getLanguageDependentString("EditKey");
      }
      toPut.put(dng.getDisplayName(el), n);
   }

}
