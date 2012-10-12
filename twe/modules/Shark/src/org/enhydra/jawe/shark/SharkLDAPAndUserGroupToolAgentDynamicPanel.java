package org.enhydra.jawe.shark;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.enhydra.jawe.JaWEEAHandler;
import org.enhydra.jawe.base.panel.PanelContainer;
import org.enhydra.jawe.base.panel.PanelGenerator;
import org.enhydra.jawe.base.panel.panels.XMLBasicPanel;
import org.enhydra.jawe.base.panel.panels.XMLComboPanel;
import org.enhydra.jawe.base.panel.panels.XMLPanel;
import org.enhydra.jxpdl.XMLAttribute;
import org.enhydra.jxpdl.elements.Application;
import org.enhydra.jxpdl.elements.ExtendedAttribute;
import org.enhydra.jxpdl.elements.FormalParameter;
import org.enhydra.jxpdl.elements.FormalParameters;
import org.enhydra.jxpdl.utilities.SequencedHashMap;
import org.w3c.dom.Node;

/**
 * Creates a table panel.
 * 
 * @author Sasa Bojanic
 */
public class SharkLDAPAndUserGroupToolAgentDynamicPanel extends XMLBasicPanel {

   protected Map mthds;

   protected Map dplmthds;

   protected String namePrefix;

   public SharkLDAPAndUserGroupToolAgentDynamicPanel(PanelContainer pc, Application myOwner) {

      super(pc, myOwner, "", true, false, false);
      boolean enableEditing = !myOwner.isReadOnly();
      String className = JaWEEAHandler.getJaWEType(myOwner);
      FormalParameters fps = new FormalParameters(myOwner.getApplicationTypes());
      fps.setNotifyListeners(false);
      fps.setNotifyMainListeners(false);

      String taName = null;
      ExtendedAttribute eataname = myOwner.getExtendedAttributes()
         .getFirstExtendedAttributeForName(SharkConstants.EA_TOOL_AGENT_CLASS);
      if (eataname != null) {
         taName = eataname.getVValue();
      }

      String methodName = "";
      ExtendedAttribute eamethodname = myOwner.getExtendedAttributes()
         .getFirstExtendedAttributeForName(SharkConstants.EA_APP_NAME);
      if (eamethodname != null) {
         methodName = eamethodname.getVValue();
      }

      if (className == null) {
         if (taName.equals(SharkConstants.TOOL_AGENT_LDAP)) {
            className = SharkConstants.TOOL_AGENT_LDAP;
         } else if (taName.equals(SharkConstants.TOOL_AGENT_USERGROUP)) {
            className = SharkConstants.TOOL_AGENT_USERGROUP;
         }
      }
      mthds = getMethods(className);

      if (!mthds.containsKey(methodName)) {
         fps.makeAs(myOwner.getApplicationTypes().getFormalParameters());
         fps.setReadOnly(true);
         mthds.put("", fps);
      } else {
         fps = (FormalParameters) mthds.get(methodName);
         if (!fps.equals(myOwner.getApplicationTypes().getFormalParameters())) {
            if (enableEditing) {
               pc.panelChanged(this, null);
            }
         }
      }
      XMLAttribute attrMethodName = new XMLAttribute(null, "Method", true);
      attrMethodName.setValue(methodName);
      final XMLComboPanel mp = new XMLComboPanel(getPanelContainer(),
                                                 attrMethodName,
                                                 new ArrayList(mthds.keySet()),
                                                 true,
                                                 true,
                                                 false,
                                                 false,
                                                 enableEditing);

      mp.getComboBox().addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent ae) {
            Object sel = mp.getSelectedItem();
            setFormalParameters(sel.toString());
            validate();
         }
      });
      mp.getComboBox().getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
         public void keyPressed(KeyEvent e) {
            Object sel = mp.getSelectedItem();
            setFormalParameters(sel.toString());
            validate();
         }
      });
      mp.getComboBox().addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent e) {
            Object sel = mp.getSelectedItem();
            setFormalParameters(sel.toString());
            validate();
         }
      });

      PanelGenerator pg = pc.getPanelGenerator();
      XMLPanel fp = pg.getPanel(fps);
      add(mp);
      add(fp);

   }

   public void setElements() {
      FormalParameters fps = ((Application) getOwner()).getApplicationTypes()
         .getFormalParameters();
      FormalParameters fps2 = (FormalParameters) ((XMLPanel) getComponent(1)).getOwner();

      ((XMLPanel) getComponent(0)).setElements();

      ExtendedAttribute eamethod = ((Application) getOwner()).getExtendedAttributes()
         .getFirstExtendedAttributeForName(SharkConstants.EA_APP_NAME);
      String method = eamethod != null ? eamethod.getVValue() : "";
      String methodNew = ((XMLPanel) getComponent(0)).getOwner().toValue();
      if (!method.equals(methodNew) || !fps.equals(fps2)) {
         SharkUtils.updateSingleExtendedAttribute(null,
                                                  ((Application) getOwner()).getExtendedAttributes(),
                                                  SharkConstants.EA_APP_NAME,
                                                  methodNew,
                                                  true);
         fps.makeAs(fps2);
      }
   }

   public void setFormalParameters(String mthName) {
      PanelGenerator pg = pc.getPanelGenerator();
      remove(1);
      XMLPanel fpPanel = pg.getPanel((FormalParameters) mthds.get(mthName));
      add(fpPanel, 1);
      validate();
   }

   public void cleanup() {
   }

   protected Map getMethods(String className) {
      Map m = new SequencedHashMap();
      try {
         List<String> mths = new ArrayList();
         if (SharkConstants.TOOL_AGENT_LDAP.equals(className)) {
            mths.addAll(SharkConstants.TOOL_AGENT_LDAP_POSSIBLE_METHODS_LIST);
         } else if (SharkConstants.TOOL_AGENT_USERGROUP.equals(className)) {
            mths.addAll(SharkConstants.TOOL_AGENT_USERGROUP_POSSIBLE_METHODS_LIST);
         }
         for (int i = 0; i < mths.size(); i++) {
            boolean addAdditionalMethod = false;
            String methodId = mths.get(i);
            Class[] ptypes = new Class[] {
               String.class
            };
            if (methodId.equals(SharkConstants.TOOL_AGENT_LDAP_METHOD_checkPassword)
                || methodId.equals(SharkConstants.TOOL_AGENT_LDAP_METHOD_doesGroupBelongToGroup)
                || methodId.equals(SharkConstants.TOOL_AGENT_LDAP_METHOD_doesUserBelongToGroup)
                || methodId.equals(SharkConstants.TOOL_AGENT_LDAP_METHOD_getGroupAttribute)
                || methodId.equals(SharkConstants.TOOL_AGENT_LDAP_METHOD_getUserAttribute)
                || methodId.equals(SharkConstants.TOOL_AGENT_USERGROUP_METHOD_doesGroupBelongToGroup)
                || methodId.equals(SharkConstants.TOOL_AGENT_USERGROUP_METHOD_doesUserBelongToGroup)
                || methodId.equals(SharkConstants.TOOL_AGENT_USERGROUP_METHOD_getUserAttribute)
                || methodId.equals(SharkConstants.TOOL_AGENT_USERGROUP_METHOD_getGroupAttribute)
                || methodId.equals(SharkConstants.TOOL_AGENT_USERGROUP_METHOD_validateUser)) {
               ptypes = new Class[] {
                     String.class, String.class
               };
            } else if (methodId.equals(SharkConstants.TOOL_AGENT_LDAP_METHOD_getAllOrganizationalUnitEntries)
                       || methodId.equals(SharkConstants.TOOL_AGENT_LDAP_METHOD_getAllUserEntries)
                       || methodId.equals(SharkConstants.TOOL_AGENT_USERGROUP_METHOD_getAllGroups)
                       || methodId.equals(SharkConstants.TOOL_AGENT_USERGROUP_METHOD_getAllUsers)) {
               ptypes = new Class[] {};
            }
            List<Class> ret = new ArrayList<Class>();
            for (int j = 0; j < ptypes.length; j++) {
               ret.add(ptypes[j]);
            }
            if (methodId.startsWith("does")
                || methodId.equals(SharkConstants.TOOL_AGENT_LDAP_METHOD_checkPassword)
                || methodId.equals(SharkConstants.TOOL_AGENT_USERGROUP_METHOD_validateUser)) {
               ret.add(boolean.class);
            } else if (methodId.startsWith("getAll")
                       || methodId.equals(SharkConstants.TOOL_AGENT_USERGROUP_METHOD_getGroups)
                       || methodId.equals(SharkConstants.TOOL_AGENT_USERGROUP_METHOD_getObjects)) {
               ret.add(String[].class);
            } else {
               ret.add(String.class);
            }
            FormalParameters fps = getFPs(methodId, ret);
            if (fps != null) {
               fps.setReadOnly(true);
               m.put(methodId, fps);
            } else {
               System.err.println("Ignoring method " + methodId);
            }
         }
      } catch (Exception ex) {

      }
      return m;
   }

   protected FormalParameters getFPs(String mthName, List<Class> ms) {
      FormalParameters fps = new FormalParameters(((Application) getOwner()).getApplicationTypes());
      fps.setNotifyListeners(false);
      fps.setNotifyMainListeners(false);
      for (int i = 0; i < ms.size(); i++) {
         FormalParameter fp = (FormalParameter) fps.generateNewElement();
         Class cls = ms.get(i);
         if (cls == Long.class
             || cls == long.class || cls == Long[].class || cls == long[].class) {
            fp.getDataType().getDataTypes().setBasicType();
            fp.getDataType().getDataTypes().getBasicType().setTypeINTEGER();
         } else if (cls == Boolean.class
                    || cls == boolean.class || cls == Boolean[].class
                    || cls == boolean[].class) {
            fp.getDataType().getDataTypes().setBasicType();
            fp.getDataType().getDataTypes().getBasicType().setTypeBOOLEAN();
         } else if (cls == Double.class
                    || cls == double.class || cls == Double[].class
                    || cls == double[].class) {
            fp.getDataType().getDataTypes().setBasicType();
            fp.getDataType().getDataTypes().getBasicType().setTypeFLOAT();
         } else if (cls == String.class || cls == String[].class) {
            fp.getDataType().getDataTypes().setBasicType();
            fp.getDataType().getDataTypes().getBasicType().setTypeSTRING();
         } else if (cls == Date.class || cls == Date[].class) {
            fp.getDataType().getDataTypes().setBasicType();
            fp.getDataType().getDataTypes().getBasicType().setTypeDATETIME();
         } else if (cls == Node.class || cls == Node[].class) {
            fp.getDataType().getDataTypes().setSchemaType();
         } else {
            System.err.println("Can't recognize param "
                               + i + ", cls=" + cls.getName() + " for method " + mthName);
            return null;
         }
         fp.setIsArray(cls.isArray());
         fp.setId("Arg" + (i + 1));
         if (i == ms.size() - 1) {
            fp.setId(SharkConstants.TOOL_AGENT_FORMAL_PARAMETER_RESULT_CL);
            fp.setModeOUT();
         }
         fps.add(fp);
      }
      return fps;
   }

}
