;    Together Workflow Editor
;    Copyright (C) 2010 Together Teamsolutions Co., Ltd.
;
;    This program is free software: you can redistribute it and/or modify
;    it under the terms of the GNU General Public License as published by
;    the Free Software Foundation, either version 3 of the License, or 
;    (at your option) any later version.
; 
;    This program is distributed in the hope that it will be useful, 
;    but WITHOUT ANY WARRANTY; without even the implied warranty of
;    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
;    GNU General Public License for more details.
; 
;    You should have received a copy of the GNU General Public License
;    along with this program. If not, see http://www.gnu.org/licenses
;-----------------------------------------------------------------------

# Language
# LANG_ENGLISH
   !insertmacro MUI_LANGUAGE "English"
   
   LangString NAME ${LANG_ENGLISH} "Together Workflow Editor ${VERSION}-${RELEASE}" 
   LangString MID_NAME ${LANG_ENGLISH} "Together Workflow Editor" 
   LangString ABBREVIATION ${LANG_ENGLISH} "TWE" 
   !define SHORT_NAME "twe" 

   VIAddVersionKey /LANG=${LANG_ENGLISH} "FileVersion" "${VERSION}-${RELEASE}"
   VIAddVersionKey /LANG=${LANG_ENGLISH} "LegalCopyright" "© Together Teamsolutions Co., Ltd. All rights reserved."
   VIAddVersionKey /LANG=${LANG_ENGLISH} "FileDescription" "Together Workflow Editor"
   VIAddVersionKey /LANG=${LANG_ENGLISH} "ProductName" "Together Workflow Editor"
   VIAddVersionKey /LANG=${LANG_ENGLISH} "ProductVersion" "${VERSION}-${RELEASE}"
   VIAddVersionKey /LANG=${LANG_ENGLISH} "Comments" "Together Workflow Editor is a graphical application for Process Definition Modelling. It is compatible with WfMC specification - XPDL (XML Process Definition Language) version 1.0 - 102502"
   VIAddVersionKey /LANG=${LANG_ENGLISH} "CompanyName" "Together Teamsolutions Co., Ltd."
   VIAddVersionKey /LANG=${LANG_ENGLISH} "LegalTrademarks" "Together (R) is a registered trademark of Together Teamsolutions Co., Ltd."
   VIAddVersionKey /LANG=${LANG_ENGLISH} "Current version" "${VERSION}-${RELEASE}"

   LangString home_page_link ${LANG_ENGLISH} "Visit Together Workflow Editor Homepage"
   LangString home_page_link_location ${LANG_ENGLISH} "http://www.together.at/prod/workflow/twe"

   LangString TEXT_IO_TITLE ${LANG_ENGLISH} "Java Setup"
   LangString TEXT_IO_SUBTITLE ${LANG_ENGLISH} "Choose J2SDK 1.4.1 or more"
   LangString TEXT_IO_PAGESUBTITLE_A ${LANG_ENGLISH} "Important notice!!!"
   LangString TEXT_IO_PAGESUBTITLE_B ${LANG_ENGLISH} "${NAME} works only with Java 1.4.1 or more JVM. JAVA can be found on http://java.sun.com/j2se/1.4"
   LangString TEXT_IO_PAGETITLE_A ${LANG_ENGLISH} ": Set javahome dir"
   LangString TEXT_IO_TITLE_A ${LANG_ENGLISH} "${NAME} Setup: Java home directory"
   LangString TEXT_IO_CANCEL_A ${LANG_ENGLISH} "&Cancel"
   LangString TEXT_IO_NEXT_A ${LANG_ENGLISH} "&Next >"
   LangString TEXT_IO_BACK_A ${LANG_ENGLISH} "< &Back"
   LangString TEXT_IO_SHORTCUTSTITLE ${LANG_ENGLISH} "Choose Start Options"
   LangString TEXT_IO_SHORTCUTSSUBTITLE ${LANG_ENGLISH} "Select from the following start options."
   LangString TEXT_IO_SHORTCUTS_GROUPBOX_TITLE ${LANG_ENGLISH} "Choose shortcuts to create"
   LangString TEXT_IO_CHECKBOX_DESKTOP_TITLE ${LANG_ENGLISH} "Create desktop shortcut"
   LangString TEXT_IO_CHECKBOX_QUICKLAUNCH_TITLE ${LANG_ENGLISH} "Create quick launch shortcut"
   
   LangString MUI_TEXT_INSTALLING_TITLE ${LANG_ENGLISH}  "Installing"
   LangString MUI_TEXT_INSTALLING_SUBTITLE ${LANG_ENGLISH}  "Please wait while $(^Name) is being installed."
   LangString MUI_TEXT_FINISH_TITLE ${LANG_ENGLISH}  "Installation completed"
   LangString MUI_TEXT_FINISH_SUBTITLE ${LANG_ENGLISH}  "$(^Name) installation is completed successfully."
   LangString MUI_TEXT_ABORT_TITLE ${LANG_ENGLISH}  "Installation Aborted"
   LangString MUI_TEXT_ABORT_SUBTITLE ${LANG_ENGLISH}  "$(^Name) installation is aborted"
   LangString MUI_UNTEXT_UNINSTALLING_TITLE ${LANG_ENGLISH}  "Uninstalling"
   LangString MUI_UNTEXT_UNINSTALLING_SUBTITLE ${LANG_ENGLISH}  "Please wait while $(^Name) is being uninstalled."
   LangString MUI_UNTEXT_FINISH_TITLE ${LANG_ENGLISH}  "Uninstallation completed"
   LangString MUI_UNTEXT_FINISH_SUBTITLE ${LANG_ENGLISH}  "$(^Name) uninstallation completed successfully."
   LangString MUI_UNTEXT_ABORT_TITLE ${LANG_ENGLISH}  "Uninstallation aborted"
   LangString MUI_UNTEXT_ABORT_SUBTITLE ${LANG_ENGLISH}  "$(^Name) uninstallation is aborted"

   LangString INSTALL_FAILED_DETAIL ${LANG_ENGLISH}  "${NAME} installation failed (output: $0)"
   LangString INSTALL_FAILED_MESSAGE ${LANG_ENGLISH}  "${NAME} installation failed"
   LangString INSTALL_SUCCED_DETAIL ${LANG_ENGLISH}  "${NAME} installation succeded (output: $0)"

   LangString xpdlfile ${LANG_ENGLISH}  "XPDL Together Workflow Editor file"
   
   LangString Documentation ${LANG_ENGLISH}  "Documentation"
   LangString Manual ${LANG_ENGLISH} "Manual" 
   LangString Uninstall ${LANG_ENGLISH}  "Uninstall"

   LangString Publisher ${LANG_ENGLISH}  "Together Teamsolutions Co., Ltd."
   LangString URLInfoAbout ${LANG_ENGLISH}  "www.together.at"
   LangString URLUpdateInfo ${LANG_ENGLISH}  "http://www.together.at/prod/workflow/twe"
   LangString HelpLink ${LANG_ENGLISH}  "http://www.together.at/prod/workflow/twe"

   LangString Start ${LANG_ENGLISH}  "Start"
 
    LangString TEXT_IO_JAVATITLE ${LANG_ENGLISH} "Java Configuration"
    LangString TEXT_IO_JAVASUBTITLE ${LANG_ENGLISH} "Java Development Kit"
    LangString TEXT_IO_JVM ${LANG_ENGLISH} "Choose the version of Java virtual machine"
    LangString TEXT_IO_JAVA_HOME ${LANG_ENGLISH} "Path from JAVA_HOME environment variable"
    LangString TEXT_IO_JAVA_HOME_DIR ${LANG_ENGLISH} "Choose the Java virtual machine to use"
    LangString TEXT_IO_JAVA_FOLDER ${LANG_ENGLISH} "Browse for folder of Java virtual machine to use"
    LangString TEXT_IO_PATH ${LANG_ENGLISH} "Path :"
	
	LangString javadir_text ${LANG_ENGLISH} "Choose the Java virtual machine to use"
    
  LangString javac_not_exist ${LANG_ENGLISH} "File java.exe does not exist in directory $TEMP1\bin"
  LangString jsse_jre_not_exist ${LANG_ENGLISH} "File jsse.jar does not exist in directory $TEMP1\jre\lib"
  LangString jsse_not_exist ${LANG_ENGLISH} "File jsse.jar does not exist in directory $TEMP1\lib"
  LangString inst_opt_error ${LANG_ENGLISH} "InstallOptions error:$\r$\n$TEMP1"
