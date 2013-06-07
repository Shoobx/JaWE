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

import org.enhydra.jawe.shark.business.SharkConstants;
import org.enhydra.jawe.shark.business.SharkUtils;
import org.enhydra.jxpdl.XMLAttribute;
import org.enhydra.jxpdl.elements.Application;
import org.enhydra.jxpdl.elements.ExtendedAttribute;

public class MailToolAgentElement extends ToolAgentElementBase {

   public MailToolAgentElement(Application app, String name) {
      super(app, name);
   }

   public void setValue(String v) {
      if (isReadOnly) {
         throw new RuntimeException("Can't set the value of read only element!");
      }
      if (v == null) {
         String val = "0";
         if (getAppModeAttribute().toValue().equals("RECEIVE")) {
            val = "1";
         }
         SharkUtils.updateSingleExtendedAttribute(this,
                                                  ((Application) this.getParent()).getExtendedAttributes(),
                                                  SharkConstants.EA_APP_MODE,
                                                  null,
                                                  val, true, false);
         val = "synchronous";
         if (getSendMailExecutionModeAttribute().toValue().equals("ASYNCHR")) {
            val = "asynchronous";
         }
         SharkUtils.updateSingleExtendedAttribute(this,
                                                  ((Application) this.getParent()).getExtendedAttributes(),
                                                  SharkConstants.EA_MAIL_TOOL_AGENT_SEND_EXECUTION_MODE,
                                                  null,
                                                  val, true, false);
      } else {
         this.value = v;
      }
   }

   public XMLAttribute getAppModeAttribute() {
      return (XMLAttribute) get(SharkConstants.EA_APP_MODE);
   }

   public XMLAttribute getSendMailExecutionModeAttribute() {
      return (XMLAttribute) get("MailSendExecutionMode");
   }

   protected void fillStructure() {
      super.fillStructure();
      XMLAttribute appMode = new XMLAttribute(this,
                                              SharkConstants.EA_APP_MODE,
                                              false,
                                              new String[] {
                                                    "SEND", "RECEIVE"
                                              },
                                              0);
      XMLAttribute sendExecMode = new XMLAttribute(this,
                                                   "MailSendExecutionMode",
                                                   false,
                                                   new String[] {
                                                         "SYNCHR", "ASYNCHR"
                                                   },
                                                   0);
      add(appMode);
      add(sendExecMode);
   }

   protected void handleStructure() {
      ExtendedAttribute ea = ((Application) this.getParent()).getExtendedAttributes()
         .getFirstExtendedAttributeForName(SharkConstants.EA_APP_MODE);
      if (ea != null) {
         if (ea.getVValue().equals("1")) {
            getAppModeAttribute().setValue("RECEIVE");
         } else {
            getAppModeAttribute().setValue("SEND");
         }
      }
      ea = ((Application) this.getParent()).getExtendedAttributes()
         .getFirstExtendedAttributeForName(SharkConstants.EA_MAIL_TOOL_AGENT_SEND_EXECUTION_MODE);
      if (ea != null) {
         if (ea.getVValue().equals("synchronous")) {
            getSendMailExecutionModeAttribute().setValue("SYNCHR");
         } else {
            getSendMailExecutionModeAttribute().setValue("ASYNCHR");
         }
      }

   }

}
