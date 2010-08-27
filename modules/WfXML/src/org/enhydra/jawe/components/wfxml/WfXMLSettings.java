/**
 * Miroslav Popov, Dec 2, 2005
 * miroslav.popov@gmail.com
 */
package org.enhydra.jawe.components.wfxml;

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
}
