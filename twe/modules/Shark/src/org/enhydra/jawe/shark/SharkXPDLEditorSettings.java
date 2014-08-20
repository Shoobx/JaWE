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

import java.util.Properties;

import org.enhydra.jawe.AdditionalResourceManager;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.base.editor.TogWEStandardXPDLEditorSettings;

public class SharkXPDLEditorSettings extends TogWEStandardXPDLEditorSettings {

   public void loadDefault(JaWEComponent comp, Properties properties) {
      properties.setProperty(AdditionalResourceManager.ADDITIONAL_LANGUAGE_PROPERTY_FILE
                             + "1", SharkPanelGenerator.APP_DEF_CHOICES_FILE);
      properties.setProperty(AdditionalResourceManager.ADDITIONAL_LANGUAGE_PROPERTY_FILE
                             + "2", SharkPanelGenerator.ASS_MGR_CHOICES_FILE);
      super.loadDefault(comp, properties);
   }

}
