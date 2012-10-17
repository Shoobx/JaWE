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

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.swing.JScrollPane;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.panel.PanelContainer;
import org.enhydra.jawe.base.panel.StandardPanelGenerator;
import org.enhydra.jawe.base.panel.panels.XMLActualParametersPanel;
import org.enhydra.jawe.base.panel.panels.XMLBasicPanel;
import org.enhydra.jawe.base.panel.panels.XMLComboPanel;
import org.enhydra.jawe.base.panel.panels.XMLGroupPanel;
import org.enhydra.jawe.base.panel.panels.XMLMultiLineHighlightPanelWithChoiceButton;
import org.enhydra.jawe.base.panel.panels.XMLMultiLineTextPanelWithOptionalChoiceButtons;
import org.enhydra.jawe.base.panel.panels.XMLPanel;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.XPDLConstants;
import org.enhydra.jxpdl.elements.ActualParameter;
import org.enhydra.jxpdl.elements.ActualParameters;
import org.enhydra.jxpdl.elements.Application;
import org.enhydra.jxpdl.elements.BasicType;
import org.enhydra.jxpdl.elements.ExtendedAttribute;
import org.enhydra.jxpdl.elements.FormalParameter;
import org.enhydra.jxpdl.elements.FormalParameters;
import org.enhydra.jxpdl.elements.SchemaType;

/**
 * Creates a table panel.
 * 
 * @author Sasa Bojanic
 */
public class XMLSpecialActualParametersPanel extends XMLActualParametersPanel {

   protected String taName = null;

   protected FormalParameters fps;

   protected JScrollPane jsp;

   protected XMLPanel emptyPanel;

   public XMLSpecialActualParametersPanel(PanelContainer pc,
                                          ActualParameters myOwner,
                                          FormalParameters fps,
                                          String tooltip) {

      super(pc, myOwner, fps, tooltip);
      setPreferredSize(new Dimension(700, 500));

   }

   public void setElements() {
      ActualParameters aps = (ActualParameters) myOwner;
      if (fps != null && fps.size() > 0) {
         XMLGroupPanel gp = (XMLGroupPanel) ((JScrollPane) getComponent(0)).getViewport()
            .getView();
         gp.setElements();
         for (int i = 0; i < fps.size(); i++) {
            if (i >= aps.size()) {
               ActualParameter el = (ActualParameter) ((XMLPanel) gp.getPanel(i)).getOwner();
               aps.add(el);
            }
         }
         while (aps.size() > fps.size()) {
            int pos = aps.size() - 1;
            ActualParameter ap = (ActualParameter) aps.remove(aps.size() - 1);
         }

      } else {
         aps.clear();
      }
   }

   public void setFormalParameters(FormalParameters fps) {
      this.fps = fps;
      ActualParameters aps = (ActualParameters) myOwner;
      String aplabel = pc.getSettings().getLanguageDependentString(aps.toName() + "Key");
      if (jsp == null) {
         jsp = new JScrollPane();
         jsp.setAlignmentX(Component.LEFT_ALIGNMENT);
         jsp.setAlignmentY(Component.TOP_ALIGNMENT);
         jsp.setPreferredSize(new Dimension(500, 1000));
         jsp.setMinimumSize(new Dimension(500, 1000));
         emptyPanel = new XMLBasicPanel(pc, myOwner, aplabel, true, false, false, null);
         jsp.setViewportView(emptyPanel);
         add(jsp);
      }

      taName = getTAName(fps);
      List<XMLPanel> panels = new ArrayList<XMLPanel>();

      List<ActualParameter> apslist = new ArrayList<ActualParameter>(aps.toElements());

      if (fps != null) {
         int ss = apslist.size();
         while (apslist.size() < fps.size()) {
            apslist.add((ActualParameter) aps.generateNewElement());
         }
         while (apslist.size() > fps.size()) {
            apslist.remove(apslist.size() - 1);
         }
         if (apslist.size() != ss) {
            getPanelContainer().panelChanged(this, null);
         }
         Map chm = XMLUtil.getPossibleVariables(XMLUtil.getWorkflowProcess(myOwner));
         for (int i = 0; i < fps.size(); i++) {
            FormalParameter fp = (FormalParameter) fps.get(i);
            ActualParameter ap = apslist.get(i);
            XMLPanel p = null;
            if (!fp.getMode().equals(XPDLConstants.FORMAL_PARAMETER_MODE_IN)) {
               List choices = getChoices(new ArrayList(XMLUtil.getPossibleVariables(XMLUtil.getWorkflowProcess(myOwner))
                                            .values()),
                                         fp.getDataType().getDataTypes().getChoosen(),
                                         true);
               p = new XMLComboPanel(getPanelContainer(),
                                     ap,
                                     fp.getName().equals("") ? fp.getId() : fp.getName(),
                                     choices,
                                     false,
                                     true,
                                     false,
                                     false,
                                     true,
                                     true,
                                     JaWEManager.getInstance()
                                        .getJaWEController()
                                        .canModifyElement(aps),
                                     fp.getDescription().equals("") ? null
                                                                   : fp.getDescription());
            } else {
               List choices = getChoices(((StandardPanelGenerator) getPanelContainer().getPanelGenerator()).getExpressionChoices(XMLUtil.getWorkflowProcess(aps)),
                                         fp.getDataType().getDataTypes().getChoosen(),
                                         false);
               List<List> mc = new ArrayList<List>();
               mc.add(choices);

               p = new XMLMultiLineHighlightPanelWithChoiceButton(getPanelContainer(),
                                                                  ap,
                                                                  fp.getName().equals("") ? fp.getId()
                                                                                         : fp.getName(),
                                                                  false,
                                                                  true,
                                                                  XMLMultiLineTextPanelWithOptionalChoiceButtons.SIZE_SMALL,
                                                                  false,
                                                                  mc,
                                                                  JaWEManager.getInstance()
                                                                     .getJaWEController()
                                                                     .canModifyElement(aps),
                                                                  null,
                                                                  fp.getDescription()
                                                                     .equals("") ? null
                                                                                : fp.getDescription());
            }
            panels.add(p);
         }
      }
      XMLPanel gp = emptyPanel;
      if (panels.size() > 0) {
         gp = new XMLGroupPanel(getPanelContainer(),
                                aps,
                                panels,
                                aplabel,
                                true,
                                true,
                                false,
                                null);
      }
      jsp.setViewportView(gp);

   }

   protected String getTAName(FormalParameters fps) {
      String taName = null;
      if (fps != null) {
         Application app = XMLUtil.getApplication(fps);
         if (app != null) {
            ExtendedAttribute ea = app.getExtendedAttributes()
               .getFirstExtendedAttributeForName(SharkConstants.EA_TOOL_AGENT_CLASS);

            if (ea != null) {
               taName = ea.getVValue();
            }
         }
      }
      return taName;
   }

   protected List getChoices(List vars, XMLElement dt, boolean doFiltering) {
      List filter = null;
      if (doFiltering) {
         if (dt instanceof BasicType) {
            filter = Arrays.asList(new String[] {
               ((BasicType) dt).getType()
            });

         } else if (dt instanceof SchemaType) {
            filter = Arrays.asList(new String[] {
               XMLUtil.getShortClassName(SchemaType.class.getName())
            });
         }
      }
      return SharkUtils.getPossibleVariableChoices(vars, filter, 2, false);
   }

}
