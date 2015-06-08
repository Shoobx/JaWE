package org.enhydra.jawe.shark;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.enhydra.jawe.base.panel.PanelContainer;
import org.enhydra.jawe.base.panel.PanelGenerator;
import org.enhydra.jawe.base.panel.panels.XMLBasicPanel;
import org.enhydra.jawe.base.panel.panels.XMLComboPanel;
import org.enhydra.jawe.base.panel.panels.XMLPanel;
import org.enhydra.jawe.shark.business.SharkConstants;
import org.enhydra.jawe.shark.business.SharkUtils;
import org.enhydra.jxpdl.XMLAttribute;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.elements.Application;
import org.enhydra.jxpdl.elements.ExtendedAttribute;
import org.enhydra.jxpdl.elements.FormalParameter;
import org.enhydra.jxpdl.elements.FormalParameters;
import org.enhydra.jxpdl.utilities.SequencedHashMap;

/**
 * Creates a table panel.
 * 
 * @author Sasa Bojanic
 */
public class SharkTXWToolAgentDynamicPanel extends XMLBasicPanel {

   protected Map mthds;

   public SharkTXWToolAgentDynamicPanel(PanelContainer pc, TXWToolAgentElement myOwner, String tooltip) {

      super(pc, myOwner, "", true, false, false, tooltip);
      Application app = XMLUtil.getApplication(myOwner);
      boolean enableEditing = !myOwner.isReadOnly();
      String className = SharkConstants.TOOL_AGENT_TXW;
      FormalParameters fps = new FormalParameters(app.getApplicationTypes());
      fps.setNotifyListeners(false);
      fps.setNotifyMainListeners(false);

      String taName = null;
      ExtendedAttribute eataname = app.getExtendedAttributes().getFirstExtendedAttributeForName(SharkConstants.EA_TOOL_AGENT_CLASS);
      if (eataname != null) {
         taName = eataname.getVValue();
      }
      if (taName.equals(SharkConstants.TOOL_AGENT_QUARTZ) || taName.equals(SharkConstants.TOOL_AGENT_SCHEDULER)) {
         eataname = app.getExtendedAttributes().getFirstExtendedAttributeForName(SharkConstants.EA_TOOL_AGENT_CLASS_PROXY);
         if (eataname != null) {
            taName = eataname.getVValue();
         }
      }
      String methodName = "";
      ExtendedAttribute eamethodname = app.getExtendedAttributes().getFirstExtendedAttributeForName(SharkConstants.EA_APP_NAME);
      if (eamethodname != null) {
         methodName = eamethodname.getVValue();
      }

      mthds = getMethods(className);

      if (!mthds.containsKey(methodName)) {
         fps.makeAs(app.getApplicationTypes().getFormalParameters());
         fps.setReadOnly(true);
         mthds.put("", fps);
      } else {
         fps = (FormalParameters) mthds.get(methodName);
         if (!fps.equals(app.getApplicationTypes().getFormalParameters())) {
            if (enableEditing) {
               pc.panelChanged(this, null);
            }
         }
      }
      XMLAttribute attrToolAgentClass = myOwner.getToolAgentClass();
      XMLPanel tap = getPanelContainer().getPanelGenerator().getPanel(attrToolAgentClass);

      XMLAttribute attrMethodName = new XMLAttribute(null, "Method", true);
      attrMethodName.setValue(methodName);
      final XMLComboPanel mp = new XMLComboPanel(getPanelContainer(), attrMethodName, new ArrayList(mthds.keySet()), true, true, false, false, enableEditing);

      mp.getComboBox().addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent ae) {
            Object sel = mp.getSelectedItem();
            setFormalParameters(sel.toString());
            validate();
         }
      });

      PanelGenerator pg = pc.getPanelGenerator();
      XMLPanel fp = pg.getPanel(fps);
      add(tap);
      add(mp);
      add(fp);

   }

   public void setElements() {
      FormalParameters fps = XMLUtil.getApplication(getOwner()).getApplicationTypes().getFormalParameters();
      FormalParameters fps2 = (FormalParameters) ((XMLPanel) getComponent(2)).getOwner();

      ((XMLPanel) getComponent(1)).setElements();

      ExtendedAttribute eamethod = XMLUtil.getApplication(getOwner()).getExtendedAttributes().getFirstExtendedAttributeForName(SharkConstants.EA_APP_NAME);
      String method = eamethod != null ? eamethod.getVValue() : "";
      String methodNew = ((XMLPanel) getComponent(1)).getOwner().toValue();
      if (!method.equals(methodNew) || !fps.equals(fps2)) {
         SharkUtils.updateSingleExtendedAttribute(null,
                                                  (XMLUtil.getApplication(getOwner())).getExtendedAttributes(),
                                                  SharkConstants.EA_APP_NAME,
                                                  null,
                                                  methodNew,
                                                  true,
                                                  false);
         fps.makeAs(fps2);
      }
   }

   public void setFormalParameters(String mthName) {
      PanelGenerator pg = pc.getPanelGenerator();
      remove(2);
      XMLPanel fpPanel = pg.getPanel((FormalParameters) mthds.get(mthName));
      add(fpPanel, 2);
      validate();
   }

   public void cleanup() {
   }

   protected Map getMethods(String className) {
      Map m = new SequencedHashMap();
      try {
         List<String> mths = new ArrayList(SharkConstants.TOOL_AGENT_TXW_POSSIBLE_METHODS_LIST);
         for (int i = 0; i < mths.size(); i++) {
            String mthName = mths.get(i);
            FormalParameters fps = getFPs(mthName);
            if (fps != null) {
               fps.setReadOnly(true);
               m.put(mthName, fps);
            } else {
               System.err.println("Ignoring method " + mthName);
            }
         }
      } catch (Exception ex) {

      }
      return m;
   }

   protected FormalParameters getFPs(String mthName) {
      boolean isSendReceive = mthName.indexOf("sendReceive") != -1;
      boolean isText = mthName.indexOf("Text") != -1;

      FormalParameters fps = new FormalParameters(XMLUtil.getApplication(getOwner()).getApplicationTypes());
      fps.setNotifyListeners(false);
      fps.setNotifyMainListeners(false);

      FormalParameter fp = (FormalParameter) fps.generateNewElement();
      fp.getDataType().getDataTypes().setBasicType();
      fp.getDataType().getDataTypes().getBasicType().setTypeSTRING();
      fp.setIsArray(false);
      fp.setId(SharkConstants.TOOL_AGENT_FORMAL_PARAMETER_URL);
      fp.setModeIN();
      fp.setDescription("String representing the URL of TXW Web Service operation to execute (e.g. http://localhost:8080/txw/TXWService).");
      fps.add(fp);

      if (isText) {
         fp = (FormalParameter) fps.generateNewElement();
         fp.getDataType().getDataTypes().setBasicType();
         fp.getDataType().getDataTypes().getBasicType().setTypeSTRING();
         fp.setIsArray(false);
         fp.setId(SharkConstants.TOOL_AGENT_FORMAL_PARAMETER_TEXT);
         fp.setModeIN();
         fp.setDescription("Text representing the data for calling TXW Web Service.");
         fps.add(fp);
      } else {
         fp = (FormalParameter) fps.generateNewElement();
         fp.getDataType().getDataTypes().setSchemaType();
         fp.setIsArray(false);
         fp.setId(SharkConstants.TOOL_AGENT_FORMAL_PARAMETER_XML);
         fp.setModeIN();
         fp.setDescription("XML Node representing the data for calling TXW Web Service.");
         fps.add(fp);
      }

      fp = (FormalParameter) fps.generateNewElement();
      fp.getDataType().getDataTypes().setBasicType();
      fp.getDataType().getDataTypes().getBasicType().setTypeSTRING();
      fp.setIsArray(false);
      fp.setId(SharkConstants.TOOL_AGENT_FORMAL_PARAMETER_WS_UNAME);
      fp.setModeIN();
      fp.setDescription("Username used to authenticate to TXW Web Service.");
      fps.add(fp);

      fp = (FormalParameter) fps.generateNewElement();
      fp.getDataType().getDataTypes().setBasicType();
      fp.getDataType().getDataTypes().getBasicType().setTypeSTRING();
      fp.setIsArray(false);
      fp.setId(SharkConstants.TOOL_AGENT_FORMAL_PARAMETER_WS_PASSWD);
      fp.setModeIN();
      fp.setDescription("Password used to authenticate to TXW Web Service.");
      fps.add(fp);

      fp = (FormalParameter) fps.generateNewElement();
      fp.getDataType().getDataTypes().setBasicType();
      fp.getDataType().getDataTypes().getBasicType().setTypeINTEGER();
      fp.setIsArray(false);
      fp.setId(SharkConstants.TOOL_AGENT_FORMAL_PARAMETER_RETURN_CODE);
      fp.setModeOUT();
      fp.setDescription("Should be set to 'true' if send/receive operation should be called.");
      fps.add(fp);

      if (isSendReceive) {
         if (isText) {
            fp = (FormalParameter) fps.generateNewElement();
            fp.getDataType().getDataTypes().setBasicType();
            fp.getDataType().getDataTypes().getBasicType().setTypeSTRING();
            fp.setIsArray(false);
            fp.setId(SharkConstants.TOOL_AGENT_FORMAL_PARAMETER_RETURN_TEXT);
            fp.setModeOUT();
            fp.setDescription("Text representing the data received from TXW Web Service call.");
            fps.add(fp);
         } else {
            fp = (FormalParameter) fps.generateNewElement();
            fp.getDataType().getDataTypes().setSchemaType();
            fp.setIsArray(false);
            fp.setId(SharkConstants.TOOL_AGENT_FORMAL_PARAMETER_RETURN_XML);
            fp.setModeOUT();
            fp.setDescription("XML Node representing the data received from TXW Web Service call.");
            fps.add(fp);
         }
      }

      return fps;
   }
}
