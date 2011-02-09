/**
* Together Workflow Editor
* Copyright (C) 2010 Together Teamsolutions Co., Ltd. 
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

/**
 * Miroslav Popov, Oct 4, 2005
 * miroslav.popov@gmail.com
 */
package org.enhydra.jawe.base.controller;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.enhydra.jawe.ChoiceButton;
import org.enhydra.jawe.ChoiceButtonListener;
import org.enhydra.jxpdl.utilities.SequencedHashMap;

/**
 * @author Miroslav Popov
 *  
 */
public class JaWETypeChoiceButton extends ChoiceButton implements ActionListener{

   protected ChoiceButtonListener parent;
   
   protected SequencedHashMap choiceMap=new SequencedHashMap();
   protected JPopupMenu popup=new JPopupMenu();

   protected Class choiceType;
   protected Class xpdlChoiceType;
   protected Class xpdlChoiceTypeParentForEA;
   
   public JaWETypeChoiceButton(Class choiceType,Class xpdlChoiceType,Class xpdlChoiceTypeParentForEA,ChoiceButtonListener parent,ImageIcon icon) {
      this.parent = parent;
      this.choiceType=choiceType;
      this.xpdlChoiceType=xpdlChoiceType;
      this.xpdlChoiceTypeParentForEA=xpdlChoiceTypeParentForEA;
      
      setIcon(icon);
      addActionListener(this);
      setMargin(new Insets(1, 2, 1, 2));
      setSize(new Dimension(10, 8));
      setAlignmentY(0.5f);
   }

   public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == this) {
         refresh();
         if (choiceMap.size() > 1) {
            popup.show(this.getParent(), this.getX(), this.getY()+this.getHeight());
         }
      } else {
         JMenuItem selected = (JMenuItem) ae.getSource();
         int sel=popup.getComponentIndex(selected);
         Object obj=choiceMap.getValue(sel);
         parent.selectionChanged(this,obj);
         choiceMap.clear();
      }
      
   }

   protected void refresh() {
      choiceMap.clear();
      popup.removeAll();
      List choices = parent.getChoices(this);
      if (choices != null) {
         Iterator it = choices.iterator();
         while (it.hasNext()) {
            JaWEType jtype = (JaWEType) it.next();
            choiceMap.put(jtype.getDisplayName(), jtype);
         }
      } 

      if (choiceMap.size() > 1) {
         String[] names = new String[choiceMap.size()];
         choiceMap.keySet().toArray(names);
         for (int i = 0; i < choiceMap.size(); i++) {
            JMenuItem mi = popup.add(names[i]);
            Icon icon = ((JaWEType)choiceMap.getValue(i)).getIcon();
            if (icon != null)
	            mi.setIcon(icon);
            mi.addActionListener(this);
         }
      }
      
      if (choiceMap.size() == 1)
         parent.selectionChanged(this, choiceMap.getValue(0));
      
      if (choiceMap == null || choiceMap.size() == 0) {
         parent.selectionChanged(this, null);
      }
   }

   public Class getChoiceType () {
      return choiceType;
   }   

   public Class getXPDLChoiceType () {
      return xpdlChoiceType;
   }   

   public Class getXPDLChoiceTypeParentForEA () {
      return xpdlChoiceTypeParentForEA;
   }   
   
}
