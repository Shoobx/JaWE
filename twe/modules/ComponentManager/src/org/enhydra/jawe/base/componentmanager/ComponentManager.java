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

package org.enhydra.jawe.base.componentmanager;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEComponentSettings;
import org.enhydra.jawe.JaWEConstants;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.PropertyMgr;
import org.enhydra.jawe.ResourceManager;
import org.enhydra.jawe.Utils;

/**
 * Used to manage JaWE components.
 * 
 * @author Sasa Bojanic
 */
public class ComponentManager {
    public static final String PROPERTYFILE_PATH = "org/enhydra/jawe/base/componentmanager/properties/";

    public static final String PROPERTYFILE_NAME = "componentmanager.properties";

    protected Map componentMap = new HashMap();

    protected PropertyMgr propertyMgr;

    protected Properties properties = new Properties();

    public ComponentManager() {
    }

    public void setPropertyMgr(PropertyMgr pm) {
        this.propertyMgr = pm;
    }

    protected void registerComponents(JaWEComponent comp) {
        JaWEManager.getInstance().getJaWEController().registerJaWEComponent(comp);
    }

    public JaWEComponent getComponent(String name) {
        return (JaWEComponent) componentMap.get(name);
    }

    public List getComponents() {
        return new ArrayList(componentMap.values());
    }

    public void addComponent(JaWEComponent comp) {
        componentMap.put(comp.getName(), comp);
    }

    public void init() {
        if (propertyMgr == null) {
            if (Utils.checkFileExistence(PROPERTYFILE_NAME)
                    || Utils.checkResourceExistence(PROPERTYFILE_PATH, PROPERTYFILE_NAME)) {
                try {
                    Utils.manageProperties(properties, PROPERTYFILE_PATH, PROPERTYFILE_NAME);
                } catch (Exception e) {
                }
            } else {
                try {
                    Utils.manageProperties(properties, JaWEConstants.JAWE_BASIC_PROPERTYFILE_PATH,
                            JaWEConstants.JAWE_BASIC_PROPERTYFILE_NAME);
                } catch (Exception e) {
                }
            }
        } else {
            if (Utils.checkFileExistence(PROPERTYFILE_NAME)
                    || Utils.checkResourceExistence(PROPERTYFILE_PATH, PROPERTYFILE_NAME)) {
                properties = propertyMgr.loadProperties(PROPERTYFILE_PATH, PROPERTYFILE_NAME);
            } else {
                properties = propertyMgr.loadProperties(JaWEConstants.JAWE_BASIC_PROPERTYFILE_PATH,
                        JaWEConstants.JAWE_BASIC_PROPERTYFILE_NAME);
            }
        }

        ClassLoader cl = getClass().getClassLoader();

        List compToAdd = ResourceManager.getResourceStrings(properties, "Component.Add.", false);

        // main components
        String mcomp = ResourceManager.getResourceString(properties, "Main.ComponentOrder");
        String[] mcomps = Utils.tokenize(mcomp, " ");
        for (int i = 0; i < mcomps.length; i++) {
            int position = shoudBeAdded(compToAdd, mcomps[i]);
            if (position != -1) {
                compToAdd.remove(position);
                String component = ResourceManager
                        .getResourceString(properties, "Component.Add." + mcomps[i]);
                String settings = ResourceManager.getResourceString(properties, "Settings." + mcomps[i]);
                try {
                    JaWEComponentSettings set = (JaWEComponentSettings) cl.loadClass(settings).newInstance();
                    set.setPropertyMgr(propertyMgr);

                    Constructor c = Class.forName(component).getConstructor(
                            new Class[] { JaWEComponentSettings.class });
                    JaWEComponent jc = (JaWEComponent) c.newInstance(new Object[] { set });
                    jc.setType(JaWEComponent.MAIN_COMPONENT);
                    registerComponents(jc);
                    JaWEManager.getInstance().getLoggingManager().debug(
                            "ComponentManager -> component " + jc.getName() + " added to JaWE");
                    componentMap.put(jc.getName(), jc);
                } catch (Throwable thr) {
                    JaWEManager.getInstance().getLoggingManager().error(
                            "ComponentManager -> error while adding JaWE component " + component + "!", thr);
                }
            }
        }

        // special components
        String scomp = ResourceManager.getResourceString(properties, "Special.ComponentOrder");
        String[] scomps = Utils.tokenize(scomp, " ");
        for (int i = 0; i < scomps.length; i++) {
            int pos = shoudBeAdded(compToAdd, scomps[i]);
            if (pos != -1) {
                compToAdd.remove(pos);
                String component = ResourceManager
                        .getResourceString(properties, "Component.Add." + scomps[i]);
                String settings = ResourceManager.getResourceString(properties, "Settings." + scomps[i]);
                try {
                    JaWEComponentSettings set = (JaWEComponentSettings) cl.loadClass(settings).newInstance();
                    set.setPropertyMgr(propertyMgr);

                    Constructor c = Class.forName(component).getConstructor(
                            new Class[] { JaWEComponentSettings.class });
                    JaWEComponent jc = (JaWEComponent) c.newInstance(new Object[] { set });
                    jc.setType(JaWEComponent.SPECIAL_COMPONENT);
                    registerComponents(jc);
                    JaWEManager.getInstance().getLoggingManager().debug(
                            "ComponentManager -> component " + jc.getName() + " added to JaWE");
                    componentMap.put(jc.getName(), jc);
                } catch (Throwable thr) {
                    JaWEManager.getInstance().getLoggingManager().error(
                            "ComponentManager -> error while adding JaWE component " + component + "!", thr);
                }
            }
        }

        // tree components
        String tcomp = ResourceManager.getResourceString(properties, "Tree.ComponentOrder");
        String[] tcomps = Utils.tokenize(tcomp, " ");
        for (int i = 0; i < tcomps.length; i++) {
            int pos = shoudBeAdded(compToAdd, tcomps[i]);
            if (pos != -1) {
                compToAdd.remove(pos);
                String component = ResourceManager
                        .getResourceString(properties, "Component.Add." + tcomps[i]);
                String settings = ResourceManager.getResourceString(properties, "Settings." + tcomps[i]);
                try {
                    JaWEComponentSettings set = (JaWEComponentSettings) cl.loadClass(settings).newInstance();
                    set.setPropertyMgr(propertyMgr);

                    Constructor c = Class.forName(component).getConstructor(
                            new Class[] { JaWEComponentSettings.class });
                    JaWEComponent jc = (JaWEComponent) c.newInstance(new Object[] { set });
                    jc.setType(JaWEComponent.TREE_COMPONENT);
                    registerComponents(jc);
                    JaWEManager.getInstance().getLoggingManager().debug(
                            "ComponentManager -> component " + jc.getName() + " added to JaWE");
                    componentMap.put(jc.getName(), jc);
                } catch (Throwable thr) {
                    JaWEManager.getInstance().getLoggingManager().error(
                            "ComponentManager -> error while adding JaWE component " + component + "!", thr);
                }
            }
        }

        // other components
        String ocomp = ResourceManager.getResourceString(properties, "Other.ComponentOrder");
        String[] ocomps = Utils.tokenize(ocomp, " ");
        for (int i = 0; i < ocomps.length; i++) {
            int pos = shoudBeAdded(compToAdd, ocomps[i]);
            if (pos != -1) {
                compToAdd.remove(pos);
                String component = ResourceManager
                        .getResourceString(properties, "Component.Add." + ocomps[i]);
                String settings = ResourceManager.getResourceString(properties, "Settings." + ocomps[i]);
                try {
                    JaWEComponentSettings set = (JaWEComponentSettings) cl.loadClass(settings).newInstance();
                    set.setPropertyMgr(propertyMgr);

                    Constructor c = Class.forName(component).getConstructor(
                            new Class[] { JaWEComponentSettings.class });
                    JaWEComponent jc = (JaWEComponent) c.newInstance(new Object[] { set });
                    jc.setType(JaWEComponent.OTHER_COMPONENT);
                    registerComponents(jc);
                    JaWEManager.getInstance().getLoggingManager().debug(
                            "ComponentManager -> component " + jc.getName() + " added to JaWE");
                    componentMap.put(jc.getName(), jc);
                } catch (Throwable thr) {
                    JaWEManager.getInstance().getLoggingManager().error(
                            "ComponentManager -> error while adding JaWE component " + component + "!", thr);
                }
            }
        }

        for (int i = 0; i < compToAdd.size(); i++) {
            String component = ResourceManager.getResourceString(properties, "Component.Add."
                    + compToAdd.get(i));
            String settings = ResourceManager.getResourceString(properties, "Settings." + compToAdd.get(i));
            try {
                JaWEComponentSettings set = (JaWEComponentSettings) cl.loadClass(settings).newInstance();
                set.setPropertyMgr(propertyMgr);

                Constructor c = Class.forName(component).getConstructor(
                        new Class[] { JaWEComponentSettings.class });
                JaWEComponent jc = (JaWEComponent) c.newInstance(new Object[] { set });
                registerComponents(jc);
                JaWEManager.getInstance().getLoggingManager().debug(
                        "ComponentManager -> component " + jc.getName() + " added to JaWE");
                componentMap.put(jc.getName(), jc);
            } catch (Throwable thr) {
                JaWEManager.getInstance().getLoggingManager().error(
                        "ComponentManager -> error while adding JaWE component " + component + "!", thr);
            }
        }

        String upperComponent = ResourceManager.getResourceString(properties, "UpperStatus");
        String upperSettings = ResourceManager.getResourceString(properties, "UpperStatus.Settings");
        if (null != upperComponent && !"".equals(upperComponent)) {
            try {
                JaWEComponentSettings set = (JaWEComponentSettings) cl.loadClass(upperSettings).newInstance();
                set.setPropertyMgr(propertyMgr);

                Constructor c = Class.forName(upperComponent).getConstructor(
                        new Class[] { JaWEComponentSettings.class });
                JaWEComponent jc = (JaWEComponent) c.newInstance(new Object[] { set });
                jc.setType(JaWEComponent.UPPER_STATUS_COMPONENT);
                registerComponents(jc);
                JaWEManager.getInstance().getLoggingManager().debug(
                        "ComponentManager -> component " + jc.getName() + " added to JaWE");
                componentMap.put(jc.getName(), jc);
            } catch (Throwable thr) {
                JaWEManager.getInstance().getLoggingManager().error(
                        "ComponentManager -> error while adding JaWE component " + upperComponent + "!", thr);
            }
        }

        String lowerComponent = ResourceManager.getResourceString(properties, "LowerStatus");
        String lowerSettings = ResourceManager.getResourceString(properties, "LowerStatus.Settings");
        if (null != lowerComponent && !"".equals(lowerComponent)) {
            try {
                JaWEComponentSettings set = (JaWEComponentSettings) cl.loadClass(lowerSettings).newInstance();
                set.setPropertyMgr(propertyMgr);

                Constructor c = Class.forName(lowerComponent).getConstructor(
                        new Class[] { JaWEComponentSettings.class });
                JaWEComponent jc = (JaWEComponent) c.newInstance(new Object[] { set });
                jc.setType(JaWEComponent.LOWER_STATUS_COMPONENT);
                registerComponents(jc);
                JaWEManager.getInstance().getLoggingManager().debug(
                        "ComponentManager -> component " + jc.getName() + " added to JaWE");
                componentMap.put(jc.getName(), jc);
            } catch (Throwable thr) {
                JaWEManager.getInstance().getLoggingManager().error(
                        "ComponentManager -> error while adding JaWE component " + lowerComponent + "!", thr);
            }
        }

        JaWEManager.getInstance().getJaWEController().getJaWEFrame().rearrangeFrame();

    }

    protected int shoudBeAdded(List compList, String compName) {
        if (compName == null)
            return -1;
        for (int i = 0; i < compList.size(); i++) {
            String name = (String) compList.get(i);
            if (compName.equals(name))
                return i;
        }

        return -1;
    }

}
