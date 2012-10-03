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

import java.util.List;

import org.enhydra.jawe.base.panel.PanelContainer;
import org.enhydra.jawe.base.panel.panels.XMLMultiLineTextPanelWithOptionalChoiceButtons;
import org.enhydra.jxpdl.XMLElement;

/**
 * Creates panel with JLabel and JTextArea.
 * 
 * @author Sasa Bojanic
 */
public class XMLMultiLineTextPanelForSMTPEAs extends
                                            XMLMultiLineTextPanelWithOptionalChoiceButtons {

   protected List configStringChoices;
   protected List commonInfoChoices;
   
   public XMLMultiLineTextPanelForSMTPEAs(PanelContainer pc,
                                          XMLElement myOwner,
                                          boolean isVertical,
                                          int type,
                                          boolean wrapLines,
                                          boolean isEnabled,
                                          List<List> choices) {
      this(pc,
           myOwner,
           myOwner.toName(),
           myOwner.isRequired(),
           isVertical,
           type,
           wrapLines,
           choices,
           isEnabled);
   }

   public XMLMultiLineTextPanelForSMTPEAs(PanelContainer pc,
                                          XMLElement myOwner,
                                          String labelKey,
                                          boolean isFalseRequired,
                                          boolean isVertical,
                                          int type,
                                          boolean wrapLines,
                                          List<List> choices,
                                          boolean isEnabled) {

      super(pc,
            myOwner,
            labelKey,
            isFalseRequired,
            isVertical,
            type,
            wrapLines,
            choices,
            isEnabled);
      this.configStringChoices = choices.get(0);
      this.commonInfoChoices = choices.get(1);
   }

   public void appendText(String txt) {
      if (configStringChoices.contains(txt)) {
         txt = "{config_string:"+txt+"}";
      } else if (commonInfoChoices.contains(txt)) {
            txt = "{"+txt+"}";
      } else {
         txt = "{process_variable:"+txt+"}";         
      }
      super.appendText(txt);
   }

}
