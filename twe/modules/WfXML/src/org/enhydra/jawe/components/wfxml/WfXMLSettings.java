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

/**
 * Miroslav Popov, Dec 2, 2005
 * miroslav.popov@gmail.com
 */
package org.enhydra.jawe.components.wfxml;

import java.awt.Color;
import java.net.URL;
import java.util.Properties;

import javax.swing.Action;
import javax.swing.ImageIcon;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.AdditionalResourceManager;
import org.enhydra.jawe.JaWEAction;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEComponentSettings;
import org.enhydra.jawe.ResourceManager;
import org.enhydra.jawe.Utils;
import org.enhydra.jawe.components.wfxml.actions.CleanPage;
import org.enhydra.jawe.components.wfxml.actions.Download;
import org.enhydra.jawe.components.wfxml.actions.Update;
import org.enhydra.jawe.components.wfxml.actions.Upload;

/**
 * @author Miroslav Popov
 */
public class WfXMLSettings extends JaWEComponentSettings {

    public void init(JaWEComponent comp) {
        PROPERTYFILE_PATH = "org/enhydra/jawe/components/wfxml/properties/";
        PROPERTYFILE_NAME = "wfxml.properties";
        super.init(comp);
    }

    public void loadDefault(JaWEComponent comp, Properties properties) {
        // defaults
        arm = new AdditionalResourceManager(properties);

        Color color;
        try {
           color = Utils.getColor(ResourceManager.getResourceString(properties, "BackgroundColor"));
        } catch (Exception e) {
           color = Utils.getColor("R=245,G=245,B=245");
        }
        componentSettings.put("BackgroundColor", color);
        
        ImageIcon iconI;
        URL iconURL = ResourceManager.getResource(properties, "Image.ConnectToEngine");
        if (iconURL != null)
            iconI = new ImageIcon(iconURL);
        else
            iconI = new ImageIcon(ResourceManager.class.getClassLoader().getResource(
                    "org/enhydra/jawe/images/connect.png"));
        componentSettings.put("ConnectToEngine", iconI);

        loadDefaultMenusToolbarsAndActions(comp);

        componentSettings.putAll(Utils.loadAllMenusAndToolbars(properties));
        componentAction.putAll(Utils.loadActions(properties, comp, componentAction));
    }

    public String getToolbarActionOrder(String toolbarName) {
        return (String) componentSettings.get(toolbarName + "Toolbar");
    }

    public void loadDefaultMenusToolbarsAndActions(JaWEComponent comp) {
        // toolbar
        componentSettings.put("defaultToolbarToolbar", "Download Upload Update - CleanPage");

        // actions
        ActionBase action;
        ImageIcon icon;
        String langDepName;
        JaWEAction ja;

        // Download
        action = new Download(comp);
        icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource(
                "org/enhydra/jawe/images/downloadxpdl.png"));
        langDepName = "Download";
        ja = new JaWEAction(action, icon, langDepName);
        componentAction.put(action.getValue(Action.NAME), ja);

        // CleanPage
        action = new CleanPage(comp);
        icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource(
                "org/enhydra/jawe/images/cleanpage.gif"));
        langDepName = "CleanPage";
        ja = new JaWEAction(action, icon, langDepName);
        componentAction.put(action.getValue(Action.NAME), ja);

        // Upload
        action = new Upload(comp);
        icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource(
                "org/enhydra/jawe/images/uploadxpdl.png"));
        langDepName = "Upload";
        ja = new JaWEAction(action, icon, langDepName);
        componentAction.put(action.getValue(Action.NAME), ja);

        // Update
        action = new Update(comp);
        icon = new ImageIcon(ResourceManager.class.getClassLoader().getResource(
                "org/enhydra/jawe/images/updatexpdl.png"));
        langDepName = "Update";
        ja = new JaWEAction(action, icon, langDepName);
        componentAction.put(action.getValue(Action.NAME), ja);

    }

    public ImageIcon getConnectImage() {
        return (ImageIcon) componentSettings.get("ConnectToEngine");
    }
    
    public Color getBackgroundColor() {
       return (Color) componentSettings.get("BackgroundColor");
    }
}
