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

import java.awt.Dimension;

import javax.swing.Box;

import org.enhydra.jawe.base.panel.PanelContainer;
import org.enhydra.jawe.base.panel.panels.XMLBasicPanel;
import org.enhydra.jawe.base.panel.panels.XMLCheckboxPanel;
import org.enhydra.jawe.base.panel.panels.XMLPanel;
import org.enhydra.jawe.base.panel.panels.XMLTextPanel;
import org.enhydra.jawe.shark.business.SharkConstants;
import org.enhydra.jawe.shark.business.SharkUtils;
import org.enhydra.jxpdl.XMLAttribute;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.elements.DeadlineDuration;

/**
 * Creates a panel for defining special shark's deadline expression.
 * 
 * @author Sasa Bojanic
 */
public class DeadlineExpressionPanel extends XMLBasicPanel {

   protected XMLTextPanel year = null;

   protected XMLTextPanel month = null;

   protected XMLTextPanel day = null;

   protected XMLTextPanel hour = null;

   protected XMLTextPanel minute = null;

   protected XMLTextPanel second = null;

   protected XMLCheckboxPanel workdaysOnly = null;

   public DeadlineExpressionPanel(final PanelContainer pc, final DeadlineDuration myOwner, String title, final boolean isEnabled, String tooltip) {

      super(pc, myOwner, title, true, true, true, tooltip);

      Dimension textDim = new Dimension(100, 20);

      year = new XMLTextPanel(pc, myOwner, pc.getLanguageDependentString("YKey"), true, false, true, isEnabled, textDim, null);
      year.setText("");
      month = new XMLTextPanel(pc, myOwner, pc.getLanguageDependentString("MKey"), true, false, true, isEnabled, textDim, null);
      month.setText("");
      day = new XMLTextPanel(pc, myOwner, pc.getLanguageDependentString("DKey"), true, false, true, isEnabled, textDim, null);
      day.setText("");
      hour = new XMLTextPanel(pc, myOwner, pc.getLanguageDependentString("hKey"), true, false, true, isEnabled, textDim, null);
      hour.setText("");
      minute = new XMLTextPanel(pc, myOwner, pc.getLanguageDependentString("mKey"), true, false, true, isEnabled, textDim, null);
      minute.setText("");
      second = new XMLTextPanel(pc, myOwner, pc.getLanguageDependentString("sKey"), true, false, true, isEnabled, textDim, null);
      second.setText("");

      boolean wdo = false;
      String dev = myOwner.toValue();
      String[] tks = XMLUtil.tokenize(dev, " ");
      for (int i = 0; i < tks.length; i++) {
         if (tks[i].equals(SharkConstants.DEADLINE_EXPRESSION_WORKDAYS_ONLY)) {
            wdo = true;
         } else if (tks[i].endsWith(SharkConstants.DEADLINE_EXPRESSION_YEAR)) {
            String tv = SharkUtils.getTimeValue(tks[i], SharkConstants.DEADLINE_EXPRESSION_YEAR);
            year.setText(tv);
         } else if (tks[i].endsWith(SharkConstants.DEADLINE_EXPRESSION_MONTH)) {
            String tv = SharkUtils.getTimeValue(tks[i], SharkConstants.DEADLINE_EXPRESSION_MONTH);
            month.setText(tv);
         } else if (tks[i].endsWith(SharkConstants.DEADLINE_EXPRESSION_DAY)) {
            String tv = SharkUtils.getTimeValue(tks[i], SharkConstants.DEADLINE_EXPRESSION_DAY);
            day.setText(tv);
         } else if (tks[i].endsWith(SharkConstants.DEADLINE_EXPRESSION_HOUR)) {
            String tv = SharkUtils.getTimeValue(tks[i], SharkConstants.DEADLINE_EXPRESSION_HOUR);
            hour.setText(tv);
         } else if (tks[i].endsWith(SharkConstants.DEADLINE_EXPRESSION_MINUTE)) {
            String tv = SharkUtils.getTimeValue(tks[i], SharkConstants.DEADLINE_EXPRESSION_MINUTE);
            minute.setText(tv);
         } else if (tks[i].endsWith(SharkConstants.DEADLINE_EXPRESSION_SECOND)) {
            String tv = SharkUtils.getTimeValue(tks[i], SharkConstants.DEADLINE_EXPRESSION_SECOND);
            second.setText(tv);
         }
      }

      XMLAttribute wdoatt = new XMLAttribute(myOwner, "WDO", false);
      wdoatt.setValue(String.valueOf(wdo));
      workdaysOnly = new XMLCheckboxPanel(pc, wdoatt, pc.getLanguageDependentString("WorkDaysOnlyKey"), false, isEnabled, true, null);

      XMLPanel p = new XMLBasicPanel(pc, myOwner, "", false, false, false, null);
      p.add(year);
      p.add(month);
      p.add(day);
      p.add(hour);
      p.add(minute);
      p.add(second);
      p.add(Box.createRigidArea(textDim));
      p.add(Box.createHorizontalGlue());
      add(p);
      add(Box.createRigidArea(new Dimension(20, 10)));
      add(workdaysOnly);
      add(Box.createRigidArea(new Dimension(20, 55)));
      add(Box.createVerticalGlue());

      if (myOwner.getScriptType().equals("")) {
         getPanelContainer().panelChanged(this, null);
      }
   }

   public boolean validateEntry() {
      // return !getValue().equals("");
      return true;
   }

   public void setElements() {
      if (!getOwner().isReadOnly()) {
         getOwner().setValue((String) getValue());
      }
   }

   public Object getValue() {
      String Y = year.getText();
      String M = month.getText();
      String D = day.getText();
      String h = hour.getText();
      String m = minute.getText();
      String s = second.getText();

      String ret = "";
      ret = appendTime(ret, Y, SharkConstants.DEADLINE_EXPRESSION_YEAR);
      ret = appendTime(ret, M, SharkConstants.DEADLINE_EXPRESSION_MONTH);
      ret = appendTime(ret, D, SharkConstants.DEADLINE_EXPRESSION_DAY);
      ret = appendTime(ret, h, SharkConstants.DEADLINE_EXPRESSION_HOUR);
      ret = appendTime(ret, m, SharkConstants.DEADLINE_EXPRESSION_MINUTE);
      ret = appendTime(ret, s, SharkConstants.DEADLINE_EXPRESSION_SECOND);

      if (!ret.trim().equals("") && workdaysOnly.getCheckboxStatus()) {
         ret += " " + SharkConstants.DEADLINE_EXPRESSION_WORKDAYS_ONLY;
      }
      return ret;
   }

   public void cleanup() {
   }

   protected String appendTime(String current, String timevalue, String tsuffix) {
      String newt = current;
      if (!timevalue.trim().equals("")) {
         if (!newt.trim().equals("")) {
            newt += " ";
         }
         newt += timevalue + tsuffix;
      }
      return newt;
   }

}
