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

import java.util.Iterator;
import java.util.List;

import org.enhydra.jawe.base.panel.PanelContainer;
import org.enhydra.jawe.base.panel.panels.XMLMultiLineTextPanelWithOptionalChoiceButtons;
import org.enhydra.jawe.shark.business.SharkConstants;
import org.enhydra.jxpdl.XMLCollectionElement;
import org.enhydra.jxpdl.XMLElement;

/**
 * Creates panel with JLabel and JTextArea.
 * 
 * @author Sasa Bojanic
 */
public class XMLMultiLineTextPanelForSMTPEAs extends
                                            XMLMultiLineTextPanelWithOptionalChoiceButtons {

   protected List sysVariablesChoices;

   protected List configStringChoices;

   protected List xpdlStringChoices;

   public XMLMultiLineTextPanelForSMTPEAs(PanelContainer pc,
                                          XMLElement myOwner,
                                          boolean isVertical,
                                          int type,
                                          boolean wrapLines,
                                          boolean isEnabled,
                                          List<List> choices,
                                          List<String> chTooltips) {
      this(pc,
           myOwner,
           myOwner.toName(),
           myOwner.isRequired(),
           isVertical,
           type,
           wrapLines,
           choices,
           chTooltips,
           isEnabled,
           null);
   }

   public XMLMultiLineTextPanelForSMTPEAs(PanelContainer pc,
                                          XMLElement myOwner,
                                          String labelKey,
                                          boolean isFalseRequired,
                                          boolean isVertical,
                                          int type,
                                          boolean wrapLines,
                                          List<List> choices,
                                          List<String> chTooltips,
                                          boolean isEnabled,
                                          String tooltip) {

      super(pc,
            myOwner,
            labelKey,
            isFalseRequired,
            isVertical,
            type,
            wrapLines,
            choices,
            chTooltips,
            isEnabled,
            tooltip);
      this.sysVariablesChoices = choices.get(0);
      this.configStringChoices = choices.get(2);
      this.xpdlStringChoices = choices.get(3);
   }

   public void appendText(String txt) {
      Iterator it = configStringChoices.iterator();
      String newTxt = null;
      while (it.hasNext()) {
         XMLCollectionElement el = (XMLCollectionElement) it.next();
         if (el.getId().equals(txt)) {
            newTxt = "{" + SharkConstants.CONFIG_STRING_PLACEHOLDER_PREFIX + txt + "}";
            break;
         }
      }
      if (newTxt == null) {
         it = sysVariablesChoices.iterator();
         while (it.hasNext()) {
            XMLCollectionElement el = (XMLCollectionElement) it.next();
            if (el.getId().equals(txt)) {
               newTxt = "{" + txt + "}";
            }
         }
      }
      if (newTxt == null) {
         it = xpdlStringChoices.iterator();
         while (it.hasNext()) {
            XMLCollectionElement el = (XMLCollectionElement) it.next();
            if (el.getId().equals(txt)) {
               newTxt = "{" + SharkConstants.XPDL_STRING_PLACEHOLDER_PREFIX + txt + "}";
            }
         }
      }
      if (newTxt == null) {
         newTxt = "{" + SharkConstants.PROCESS_VARIABLE_PLACEHOLDER_PREFIX + txt + "}";
      }
      super.appendText(newTxt);
   }

}
