;    Together Workflow Editor
;    Copyright (C) 2011 Together Teamsolutions Co., Ltd.
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

;----------------------------------------------------------------------------------
; TWE installation script
;   All output message will be written to file ..\..\log_twe.txt
;----------------------------------------------------------------------------------
;Use only for testing with makensisw.exe
;!define VERSION "3.0"    ; Uncomment this for build with makensisw.exe
;!define RELEASE "1" ; Uncomment this for build with makensisw.exe
;!define TWE_DIR "..\..\output\twe-${VERSION}-${RELEASE}"
;!define OUT_DIR ".\..\..\distribution\twe-${VERSION}-${RELEASE}\community"
;!define LICENSE ".\..\..\licenses\License.txt"
;!define LANGUAGE "English"
;!define BUILDID "99999999-9999"
;----------------------------------------------------------------------------------
!include "LogicLib.nsh"
!include "WinMessages.nsh"
!include "FileFunc.nsh"   ; for ${GetSize} for EstimatedSize registry entry
!include "MUI.nsh"
!include "Sections.nsh"
!include "InstallOptions.nsh"
!include "x64.nsh"
!include "togSetJava.nsh"
!include "togDirectory.nsh"
!include "togStartOption.nsh"
!include "EnvVarUpdate.nsh"
!define SHCNE_ASSOCCHANGED 0x08000000
!define SHCNF_IDLIST 0

;Version Information
VIProductVersion "${VERSION}.${RELEASE}.0"
  
Name "$(NAME)" ;Define your own software name here
!define SRCDIR "${TWE_DIR}\..\..\installation\Windows"

!define MUI_ICON "${SRCDIR}\twe-install.ico"
!define MUI_UNICON "${SRCDIR}\twe-uninstall.ico"

RequestExecutionLevel admin

BrandingText "$(Name)"
;---------------------------------------------------------------------------------------
;Configuration
;---------------------------------------------------------------------------------------
; General

;Remember install folder
InstallDirRegKey HKCU "Software\$(Name)" ""

!define MUI_HEADERIMAGE
!define MUI_HEADERIMAGE_BITMAP "${SRCDIR}\twe.bmp"
!define MUI_HEADERIMAGE_UNBITMAP "${SRCDIR}\twe.bmp"
!define MUI_ABORTWARNING

;ShowInstDetails show

; Compress
;------------
SetCompress          auto
SetCompressor        bzip2
SetDatablockOptimize on
SetDateSave          on

;--------------------------------
;Modern UI Configuration

  !define MUI_CUSTOMPAGECOMMANDS

  !define MUI_WELCOMEPAGE

; smaller fonts
  !define MUI_WELCOMEPAGE_TITLE_3LINES

  !define MUI_WELCOMEFINISHPAGE_BITMAP "${SRCDIR}\twe-wizard.bmp"
  !define MUI_LICENSEPAGE
;  !define MUI_DIRECTORYPAGE
;  !define MUI_STARTMENUPAGE
  !define MUI_FINISHPAGE

; smaller fonts
   !define MUI_FINISHPAGE_TITLE_3LINES
      
   !define MUI_FINISHPAGE_LINK $(home_page_link)
   !define MUI_FINISHPAGE_LINK_LOCATION $(home_page_link_location)

   !define MUI_FINISHPAGE_NOAUTOCLOSE ;To allow ShowInstDetails log
   !define MUI_FINISHPAGE_RUN
   !define MUI_FINISHPAGE_RUN_FUNCTION "StartJaWE"
   ;!define MUI_FINISHPAGE_CANCEL_ENABLED

   !define MUI_TEXT_FINISH_RUN "$(Start) $(Name)"

   
   !define MUI_FINISHPAGE_SHOWREADME "$INSTDIR\doc\twe-current.doc.pdf"
   !define MUI_FINISHPAGE_SHOWREADME_TEXT "Show Documentation"
   !define MUI_FINISHPAGE_SHOWREADME_NOTCHECKED

   ;!define MUI_UNTEXT_CONFIRM_TITLE $(MUI_UNTEXT_CONFIRM_TITLE)

  !define MUI_UNINSTALLER
  !define MUI_UNCONFIRMPAGE

;--------------------------------
;Variables

  Var STARTMENU_FOLDER
  Var MUI_TEMP
  Var JAVAHOME
  Var DEFAULT_BROWSER
  Var ADD_STARTMENU
  Var ADD_PINTOTASKBAR
  Var ADD_QUICKLAUNCH
  Var ADD_DESKTOP
  Var ENABLE_DESKTOP
  Var ENABLE_QUICKLAUNCH
  Var ENABLE_PINTOTASKBAR

  Var SILENT
  Var CREATE_STARTUP_MENU
  Var CREATE_QUICK_LAUNCH_ICON
  Var CREATE_DESKTOP_ICON
  Var CREATE_PINTOTASKBAR  
  Var DefaultDir
;Variable for window registry 
  !define AppUserModelID "Together.Workflow.Editor"
  
  !insertmacro MUI_PAGE_WELCOME
  !insertmacro MUI_PAGE_LICENSE "$(license_text)"

;Languages
# LANG_ENGLISH
  !include "${SRCDIR}\MUI_${LANGUAGE}.nsh"
# LANG_SERBIANLATIN
#  !include "${SRCDIR}\MUI_Serbian.nsh"
  

;  This constant specifies Windows uninstall key for your application.
!define REG_UNINSTALL "Software\Microsoft\Windows\CurrentVersion\Uninstall\$(Name)"

;==========================================================================

!ifdef INNER
  !echo "Inner invocation"                  ; just to see what's going on
  OutFile "$%TEMP%\tempinstaller.exe"       ; not really important where this is
  SetCompress off                           ; for speed
!else
  !echo "Outer invocation"
 
  ; Call makensis again, defining INNER.  This writes an installer for us which, when
  ; it is invoked, will just write the uninstaller to some location, and then exit.
  ; Be sure to substitute the name of this script here.
   
  !system "$\"${NSISDIR}\makensis$\" /DINNER /O..\..\log_nsis_inner.txt /DPROJECT_FULL_NAME=$\"${PROJECT_FULL_NAME}$\" /DSHORT_NAME=$\"${SHORT_NAME}$\" /DSHORT_UPPER_NAME=$\"${SHORT_UPPER_NAME}$\" /DVERSION=${VERSION} /DRELEASE=${RELEASE} /DTWE_DIR=$\"${TWE_DIR}$\" /DOUT_DIR=$\"${OUT_DIR}$\" /DLICENSE=$\"${LICENSE}$\" /DLANGUAGE=${LANGUAGE} /DBUILDID=${BUILDID} twe.nsi" = 0
 
  ; So now run that installer we just created as %TEMP%\tempinstaller.exe.  Since it
  ; calls quit the return value isn't zero.
 
  !system "$%TEMP%\tempinstaller.exe" = 2
 
  ; That will have written an uninstaller binary for us.  Now we sign it with your
  ; favourite code signing tool.
 !if "${SERVER_TIMEOUT}" == "true"
    ;time out and not found signtool_path  can't sign ->error
      !if "${SIGNTOOL_PATH}" != ""
             !error
     !endif
  !else
   ;normal mode[return success/fail]
   !if "${SIGN_SETUP_TIMESTAMP}" == "true"
      !if "${SIGNTOOL_PATH}" != ""
         !system "$\"${SIGNTOOL_PATH}$\" sign /f $\"${KEY_PATH}$\" /p ${PASSWORD} /d $\"${FULL_NAME}$\" /du $\"http://www.together.at$\" /t $\"${TIMESTAMP_URL}$\" $\"$%TEMP%\uninstall.exe$\"" = 0
      !endif
   !endif
   
   !if "${SIGN_SETUP_TIMESTAMP}" == "false"
      !if "${SIGNTOOL_PATH}" != "" 
         !system "$\"${SIGNTOOL_PATH}$\" sign /f $\"${KEY_PATH}$\" /p ${PASSWORD} /d $\"${FULL_NAME}$\" /du $\"http://www.together.at$\" $\"$%TEMP%\uninstall.exe$\"" = 0
      !endif
   !endif
!endif

;  !system "SIGNCODE <signing options> $%TEMP%\uninstaller.exe" = 0
 
  ; Good.  Now we can carry on writing the real installer.
 
  OutFile "${OUT_DIR}\${SHORT_NAME}-${VERSION}-${RELEASE}.exe"   ; The file to write
!endif

;==========================================================================  
;----------------------------------------------------------------------------------
# License page
   LicenseLangString license_text ${LANG_ENGLISH} "${LICENSE}"
   LicenseForceSelection checkbox
;--------------------------------
;Pages Define our own pages

  !insertmacro TOG_CUSTOMPAGE_SETJAVA "${PROJECT_FULL_NAME} ${VERSION}-${RELEASE}" $JAVAHOME ;
  !insertmacro TOG_CUSTOMPAGE_DIRECTORY "${PROJECT_FULL_NAME} ${VERSION}-${RELEASE}" $DefaultDir  ;
  !insertmacro TOG_CUSTOMPAGE_STARTOPTION "${PROJECT_FULL_NAME} ${VERSION}-${RELEASE}" $ADD_STARTMENU $STARTMENU_FOLDER $ENABLE_DESKTOP $ADD_DESKTOP $ENABLE_QUICKLAUNCH $ADD_QUICKLAUNCH $ENABLE_PINTOTASKBAR $ADD_PINTOTASKBAR
  !insertmacro MUI_PAGE_INSTFILES
  !insertmacro MUI_PAGE_FINISH

;--------------------------------

;---------------------------------------------------------------------------------------
;Modern UI System
;---------------------------------------------------------------------------------------
;Installer Sections
;---------------------------------------------------------------------------------------
Function PinToTaskbar
   IfFileExists "$SMPROGRAMS\$STARTMENU_FOLDER\$(ABBREVIATION) ${VERSION}-${RELEASE}.lnk" StartPinToTaskbar Empty
      StartPinToTaskbar:
;         MessageBox MB_OK|MB_ICONSTOP "$INSTDIR"
         SetOutPath "$INSTDIR"
         StrCpy $0 "pin.vbs"
         File /oname=$0 'pin.vbs'
;         MessageBox MB_OK|MB_ICONSTOP "$0"
         nsExec::Exec '"$SYSDIR\CScript.exe"  "$0" //e:vbscript "$SMPROGRAMS\$STARTMENU_FOLDER" "$(ABBREVIATION) ${VERSION}-${RELEASE}.lnk" //B //NOLOGO'
         Delete 'pin.vbs'
      Empty:
FunctionEnd

!macro PINFROMTASKBAR un
Function ${un}PinFromTaskbar
 IfFileExists "$SMPROGRAMS\$STARTMENU_FOLDER\$(ABBREVIATION) ${VERSION}-${RELEASE}.lnk" StartUnPinToTaskbar Empty
     StartUnPinToTaskbar:
        IfFileExists "$INSTDIR\unpin.vbs" StartUnPin Empty
        StartUnPin:
            StrCpy $0 "$INSTDIR\unpin.vbs"
              nsExec::Exec '"$SYSDIR\CScript.exe" "$0" //e:vbscript "$SMPROGRAMS\$STARTMENU_FOLDER" "$(ABBREVIATION) ${VERSION}-${RELEASE}.lnk" //B //NOLOGO'
     Empty:
FunctionEnd
!macroend
; Insert function as an installer and uninstaller function.
!insertmacro PINFROMTASKBAR ""
!insertmacro PINFROMTASKBAR "un."

Section "Install" Install
   
   SectionIn 1 2 3 
   SetOverwrite try
   SetShellVarContext all

   Push $R0

#----------------- setting for silent installation --
   ${if} $SILENT == "YES"
      ${if} $CREATE_STARTUP_MENU == "on"
         StrCpy $ADD_STARTMENU "1"
      ${else}
         StrCpy $ADD_STARTMENU "0"
      ${endif}
      ${if} $CREATE_QUICK_LAUNCH_ICON == "on"
         StrCpy $ADD_QUICKLAUNCH "1"
      ${else}
         StrCpy $ADD_QUICKLAUNCH "0"
      ${endif}
      ${if} $CREATE_DESKTOP_ICON == "on"
         StrCpy $ADD_DESKTOP "1"
      ${else}
         StrCpy $ADD_DESKTOP "0"
      ${endif}
      ${if} $CREATE_PINTOTASKBAR == "on"
         StrCpy $ADD_PINTOTASKBAR "1"
      ${else}
         StrCpy $ADD_PINTOTASKBAR "0"
      ${endif}
   ${endif}
#------ Clear Pin from taskbar previous version -----
  Call PinFromTaskbar
   
#----------------------------------------------------
   
  SetOutPath "$INSTDIR"
  ;File /r ".\..\..\..\prepare\*.*"
  File "${TWE_DIR}\*.*"
  File /r "${TWE_DIR}\bin"
  File /r "${TWE_DIR}\config"
  File /r "${TWE_DIR}\dist"
  File /r /x api /x *.test.pdf "${TWE_DIR}\doc"
  File /r "${TWE_DIR}\examples"
  File /r "${TWE_DIR}\lib"
  File /r "${TWE_DIR}\licenses"
  File pin.vbs
  File unpin.vbs
  
  
  !ifndef INNER
;     SetOutPath ${INSTDIR}
     File $%TEMP%\uninstall.exe
  !endif

  StrCpy $1 $JAVAHOME
  ; replace char '\' with char '/' at JDK directory string
  Push "$1"
  Push "\"
  Push "/"
   Call ReplaceChar
   Pop $1

   ClearErrors

  SetOutPath "$INSTDIR"

  
   DetailPrint '"$INSTDIR\configure.bat" -jdkhome "$1"'

   StrCpy $0 0
   nsExec::ExecToLog '"$INSTDIR\configure.bat" -jdkhome "$1"'


   StrCmp $0 "0" success1
   DetailPrint $(INSTALL_FAILED_DETAIL)
   MessageBox MB_ICONSTOP|MB_OK $(INSTALL_FAILED_MESSAGE)
   Goto end

   success1:
   DetailPrint $(INSTALL_SUCCED_DETAIL)

  ${If} ${RunningX64}
     SetRegView 64
  ${EndIf}
  ; Write the installation path into the registry
  WriteRegStr HKLM "SOFTWARE\$(Name)" "Install_Dir" "$INSTDIR"

  ; Association a XPDL files with TWE
  WriteRegStr HKCR ".xpdl" "" "xpdlfile"
  WriteRegStr HKCR "xpdlfile" "" $(xpdlfile)
  WriteRegStr HKCR "xpdlfile\DefaultIcon" "" "$INSTDIR\bin\XPDL.ico"

  WriteRegStr HKCR "xpdlfile\Shell\open\command" "" "$\"$JAVAHOME\bin\javaw.exe$\" -Xmx512M -DJaWE_HOME=$\"$INSTDIR$\" -Djava.ext.dirs=$\"$INSTDIR\lib$\" org.enhydra.jawe.JaWE  $\"%1$\""

  ; Write the uninstall keys for Windows
  ; Write the installation path into the registry
  WriteRegStr HKLM "Software\$(Name)" "InstDir" "$INSTDIR"

  StrCpy $8 $STARTMENU_FOLDER
  Push $8
  Call TrimNewlines
  Pop $8
  StrCpy $STARTMENU_FOLDER $8

 ${If} $ADD_STARTMENU != '0'
 ${AndIf} $STARTMENU_FOLDER != ''
  CreateDirectory "$SMPROGRAMS\$STARTMENU_FOLDER"

  ;Create shortcuts
  
  CreateShortCut "$SMPROGRAMS\$STARTMENU_FOLDER\$(ABBREVIATION) ${VERSION}-${RELEASE}.lnk" \
                 "$JAVAHOME\bin\javaw.exe" \
                 "-Xmx512M -DJaWE_HOME=$\"$INSTDIR$\" -Djava.ext.dirs=$\"$INSTDIR\lib$\" org.enhydra.jawe.JaWE" \
                 "$INSTDIR\bin\TWE.ico"
                          
  CreateDirectory "$SMPROGRAMS\$STARTMENU_FOLDER\$(ABBREVIATION) $(Documentation)"
  CreateShortCut "$SMPROGRAMS\$STARTMENU_FOLDER\$(ABBREVIATION) $(Documentation)\$(ABBREVIATION) $(Manual) HTML.lnk" \
                 "$INSTDIR\doc\twe-current.doc.html" \
                 "" \
                 $DEFAULT_BROWSER
  CreateShortCut "$SMPROGRAMS\$STARTMENU_FOLDER\$(ABBREVIATION) $(Documentation)\$(ABBREVIATION) $(Manual) PDF.lnk" \
                 "$INSTDIR\doc\twe-current.doc.pdf" \
                 "" \
                 ""
  CreateShortCut "$SMPROGRAMS\$STARTMENU_FOLDER\$(ABBREVIATION) Homepage.lnk" \
                 $(home_page_link_location) \
                 "" \
                 $DEFAULT_BROWSER

  IfSilent createShortCutUninstallSilent
	
  CreateShortCut "$SMPROGRAMS\$STARTMENU_FOLDER\$(Uninstall).lnk" \
  				 "$INSTDIR\uninstall.exe" \
  				 "" \
  				 "$INSTDIR\uninstall.exe" 0	
  Goto endShortCut
  createShortCutUninstallSilent:
  CreateShortCut "$SMPROGRAMS\$STARTMENU_FOLDER\$(Uninstall).lnk" \
  				 "$INSTDIR\uninstall.exe" \
  				 "/SILENT" \
  				 "$INSTDIR\uninstall.exe" 0
  endShortCut:
	
   WinShell::SetLnkAUMI "$SMPROGRAMS\$STARTMENU_FOLDER\$(ABBREVIATION) ${VERSION}-${RELEASE}.lnk" "${AppUserModelID}"                        
  ${endif}
  ${If} $ADD_QUICKLAUNCH != '0'
  CreateShortCut "$QUICKLAUNCH\$(ABBREVIATION) ${VERSION}-${RELEASE}.lnk" \
                  "$JAVAHOME\bin\javaw.exe" \
                  "-Xmx512M -DJaWE_HOME=$\"$INSTDIR$\" -Djava.ext.dirs=$\"$INSTDIR\lib$\" org.enhydra.jawe.JaWE" \ 
                 "$INSTDIR\bin\TWE.ico" 0  
   WinShell::SetLnkAUMI "$QUICKLAUNCH\$(ABBREVIATION) ${VERSION}-${RELEASE}.lnk" "${AppUserModelID}"                        
  ${endif}
  ${If} $ADD_DESKTOP != '0'
  CreateShortCut "$DESKTOP\$(ABBREVIATION) ${VERSION}-${RELEASE}.lnk" \
                  "$JAVAHOME\bin\javaw.exe" \
                  "-Xmx512M -DJaWE_HOME=$\"$INSTDIR$\" -Djava.ext.dirs=$\"$INSTDIR\lib$\" org.enhydra.jawe.JaWE" \ 
                 "$INSTDIR\bin\TWE.ico" 0
   WinShell::SetLnkAUMI "$DESKTOP\$(ABBREVIATION) ${VERSION}-${RELEASE}.lnk" "${AppUserModelID}"                        
  ${endif}

  ${If} $ADD_PINTOTASKBAR != '0'
    Call PinToTaskbar
  ${endif}

  ; get cumulative size of all files in and under install dir
  ; report the total in KB (decimal)
  ; place the answer into $0  ($1 and $2 get other info we don't care about)
  ${GetSize} "$INSTDIR" "/S=0K" $0 $1 $2
  ; Convert the decimal KB value in $0 to DWORD
  ; put it right back into $0
  IntFmt $0 "0x%08X" $0

  ; Create/Write the reg key with the dword value
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\$(Name)" \   
                           "EstimatedSize" "$0"
  ; Write the uninstall keys for Windows
  WriteRegStr HKLM    "Software\Microsoft\Windows\CurrentVersion\Uninstall\$(Name)" \
                    "DisplayName" "$(MID_NAME)"
  WriteRegStr HKLM    "Software\Microsoft\Windows\CurrentVersion\Uninstall\$(Name)" \
                             "UninstallString" "$INSTDIR\uninstall.exe /SILENT=$SILENT"
	IfSilent writeRegStrSilent
	WriteRegStr HKLM 	"Software\Microsoft\Windows\CurrentVersion\Uninstall\$(Name)" \
  					"UninstallString" '"$INSTDIR\uninstall.exe"'
	Goto continueWriteReg
	writeRegStrSilent:
  WriteRegStr HKLM 	"Software\Microsoft\Windows\CurrentVersion\Uninstall\$(Name)" \
  					"UninstallString" '"$INSTDIR\uninstall.exe" /SILENT'

	continueWriteReg:

  WriteRegStr HKLM     "Software\Microsoft\Windows\CurrentVersion\Uninstall\$(Name)" \
                                     "DisplayIcon" "$INSTDIR\bin\TWE.ico"
  WriteRegStr HKLM    "Software\Microsoft\Windows\CurrentVersion\Uninstall\$(Name)" \
                             "Publisher" $(Publisher)
  WriteRegStr HKLM    "Software\Microsoft\Windows\CurrentVersion\Uninstall\$(Name)" \
                             "DisplayVersion" "${VERSION}-${RELEASE}-${BUILDID}"
  WriteRegStr HKLM    "Software\Microsoft\Windows\CurrentVersion\Uninstall\$(Name)" \
                             "URLInfoAbout" $(URLInfoAbout)
  WriteRegStr HKLM    "Software\Microsoft\Windows\CurrentVersion\Uninstall\$(Name)" \
                             "URLUpdateInfo" $(URLUpdateInfo)
  WriteRegStr HKLM    "Software\Microsoft\Windows\CurrentVersion\Uninstall\$(Name)" \
                             "HelpLink" $(HelpLink)
  WriteRegStr HKLM    "Software\Microsoft\Windows\CurrentVersion\Uninstall\$(Name)" \
                             "StartMenuFolder" "$STARTMENU_FOLDER"                           

  
  ;WriteUninstaller "$%TEMP%\uninstall.exe" ;<<----- Enabled HERE for Warning "Uninstaller script code found but WriteUninstaller never used - no uninstaller will be created"
  Call RefreshShellIcons

; nsExec::ExecToLog 'cacls.exe "$INSTDIR\config" /T /E /C /G Everyone:F'
AccessControl::GrantOnFile "$INSTDIR" "(S-1-1-0)" "FullAccess"
 
end: 

SectionEnd
;---------------------------------------------------------------------------------------
;Installer Functions
;---------------------------------------------------------------------------------------
Function .onInit
!ifdef INNER
 
  ; If INNER is defined, then we aren't supposed to do anything except write out
  ; the installer.  This is better than processing a command line option as it means
  ; this entire code path is not present in the final (real) installer.
 
  WriteUninstaller "$%TEMP%\uninstall.exe" ;;<<----- Disabled HERE for Warning "Uninstaller script code found but WriteUninstaller never used - no uninstaller will be created"
  Quit  ; just bail out quickly when running the "inner" installer
!endif

    ${If} ${RunningX64}
          StrCpy $INSTDIR "$PROGRAMFILES64\${SHORT_NAME}-${VERSION}-${RELEASE}"
    ${else}
          StrCpy $INSTDIR "$PROGRAMFILES\${SHORT_NAME}-${VERSION}-${RELEASE}"
    ${EndIf}
   StrCpy $DefaultDir $INSTDIR
 
; Read default browser path
  ReadRegStr $R9 HKCR "HTTP\shell\open\command" ""
   
  Push $R9 ;original string
  Push '"' ;needs to be replaced
  Push '' ;will replace wrong characters
  Call StrReplace
  
  Push "-"
  Call GetFirstPartRest
  Pop $DEFAULT_BROWSER
 

 #------- seting silent installation -----------------#
  IfFileExists $EXEDIR\twe-${VERSION}-${RELEASE}.silent.properties silent normal
  
  silent:
  SetSilent silent
  StrCpy $SILENT "YES"
  Goto start_silent_initialization
  
  normal:
  SetSilent normal
  StrCpy $SILENT "NO"
  Goto start_initialization
  
  start_silent_initialization:
    IfFileExists $EXEDIR\twe-${VERSION}-${RELEASE}.silent.properties "" continue
    ClearErrors
    FileOpen $9 $EXEDIR\twe-${VERSION}-${RELEASE}.silent.properties r

  loop:
    FileRead $9 $8
    IfErrors error_handle
    Push $8
    Call TrimNewlines
    Push "="
    Call GetFirstPartRest
    Pop $R0 ;1st part
    Pop $R1 ;rest
    StrCmp $R1 "" loop
    StrCmp $R0 "jdk.dir" setjdkdir
    StrCmp $R0 "inst.dir" setinstdir
    StrCmp $R0 "startup.menu.name" setstartupmenuname
    StrCmp $R0 "create.startup.menu" setstartupmenu
    StrCmp $R0 "create.quick.launch.icon" setquicklaunchicon
    StrCmp $R0 "create.desktop.icon" setdesktopicon
    StrCmp $R0 "create.pin.to.taskbar" setpintotaskbar

  Goto loop

    setjdkdir:
        IfFileExists $R1\bin\javaw.exe followJDK recallJava
        recallJava:
            call GetJavaVersion
            Goto finalJDK
        followJDK:
            StrCpy $JAVAHOME $R1
            Goto finalJDK
        finalJDK:    
            Goto loop
    setinstdir:
        StrCpy $INSTDIR $R1
        Goto loop
    setstartupmenuname:
        StrCpy $STARTMENU_FOLDER $R1
        StrCpy $MUI_TEMP $R1
        Goto loop
    setstartupmenu:
        StrCpy $CREATE_STARTUP_MENU $R1 ;yes or no
        StrCmp $CREATE_STARTUP_MENU "on" doSetAddMenu1 doSetAddMenu0
        doSetAddMenu1:
              StrCpy $ADD_STARTMENU "1"
              Goto endAddMenu
        doSetAddMenu0:
              StrCpy $ADD_STARTMENU "0"
        endAddMenu:
        Goto loop
    setquicklaunchicon:
        StrCpy $CREATE_QUICK_LAUNCH_ICON $R1
        StrCmp $CREATE_QUICK_LAUNCH_ICON "on" doAddQLI1 doAddQLI0
        
        doAddQLI0:
               StrCpy $ADD_QUICKLAUNCH '0'
               Goto loop
        doAddQLI1:
               StrCpy $ADD_QUICKLAUNCH '1'
        Goto loop
    setdesktopicon:
        StrCpy $CREATE_DESKTOP_ICON $R1
        StrCmp $CREATE_DESKTOP_ICON "on" doAddDTI1 doAddDTI0

        doAddDTI0:
               StrCpy $ADD_DESKTOP '0'
               Goto loop
        doAddDTI1:
               StrCpy $ADD_DESKTOP '1'
        Goto loop
    setpintotaskbar:
         StrCpy $CREATE_PINTOTASKBAR $R1
         Goto loop

  error_handle:
      Goto loopend

  loopend:
    FileClose $9
    
  
  #------- seting silent installation -----------------#

  start_initialization:
  ;normal_default:
      StrCpy $ADD_STARTMENU       '1'
      StrCpy $ENABLE_DESKTOP       'on'
      StrCpy $ADD_DESKTOP       '1'
      StrCpy $ENABLE_QUICKLAUNCH    'on'
      StrCpy $ADD_QUICKLAUNCH    '0'
      StrCpy $ENABLE_PINTOTASKBAR   'on'
      StrCpy $ADD_PINTOTASKBAR    '1'

        ReadRegStr $R0 HKLM "${REG_UNINSTALL}" "InstDir"
        StrCmp $R0 "" start
        StrCpy $INSTDIR $R0

  start:
  ;Reads components status for registry
  IfSilent end_splash_screen
 
# start splash screen
# the plugins dir is automatically deleted when the installer exits
      InitPluginsDir
      File /oname=$PLUGINSDIR\splash.bmp "${SRCDIR}\twe-splash.bmp"
#optional
#File /oname=$PLUGINSDIR\splash.wav "C:\myprog\sound.wav"
      advsplash::show 1000 600 400 -1 $PLUGINSDIR\splash
      Pop $0 ; $0 has '1' if the user closed the splash screen early,
                      ; '0' if everything closed normal, and '-1' if some error occured.
# end splash screen

  end_splash_screen:
  
  continue:


FunctionEnd
;---------------------------------------------------------------------------------------
;Descriptions
;---------------------------------------------------------------------------------------
!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES
;---------------------------------------------------------------------------------------
;Uninstaller Section
;---------------------------------------------------------------------------------------
!ifdef INNER
Section "Uninstall"
  SetShellVarContext all

  ; remove registry keys
  DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\$(Name)"
  DeleteRegKey HKLM "SOFTWARE\$(Name)"

   DeleteRegKey HKCR ".xpdl"
   DeleteRegKey HKCR "xpdlfile\DefaultIcon"
   DeleteRegKey HKCR "xpdlfile\Shell"
   DeleteRegKey HKCR "xpdlfile"

  ; Unpin from Taskbar
  Call un.PinFromTaskbar
  
  ; MUST REMOVE UNINSTALLER, too
  Delete $INSTDIR\uninstall.exe

  RMDir /r "$INSTDIR"

  
  ; remove shortcuts, if any.
  Delete "$SMPROGRAMS\$STARTMENU_FOLDER\$(ABBREVIATION) $(Documentation)\$(ABBREVIATION) $(Manual) HTML.lnk"
  Delete "$SMPROGRAMS\$STARTMENU_FOLDER\$(ABBREVIATION) $(Documentation)\$(ABBREVIATION) $(Manual) PDF.lnk"
  Delete "$SMPROGRAMS\$STARTMENU_FOLDER\$(ABBREVIATION) Homepage.lnk"
  Delete "$SMPROGRAMS\$STARTMENU_FOLDER\$(ABBREVIATION) ${VERSION}-${RELEASE}.lnk"
  Delete "$SMPROGRAMS\$STARTMENU_FOLDER\$(Uninstall).lnk"
  
  Delete "$DESKTOP\$(ABBREVIATION) ${VERSION}-${RELEASE}.lnk"
  Delete "$QUICKLAUNCH\$(ABBREVIATION) ${VERSION}-${RELEASE}.lnk"

  RMDir "$SMPROGRAMS\$STARTMENU_FOLDER\$(ABBREVIATION) $(Documentation)"
  RMDir "$SMPROGRAMS\$STARTMENU_FOLDER"

  ;Delete empty start menu parent diretories
  StrCpy $MUI_TEMP "$SMPROGRAMS\$MUI_TEMP"

  startMenuDeleteLoop:
    RMDir $MUI_TEMP
    GetFullPathName $MUI_TEMP "$MUI_TEMP\.."

    IfErrors startMenuDeleteLoopDone

    StrCmp $MUI_TEMP $SMPROGRAMS startMenuDeleteLoopDone startMenuDeleteLoop
  startMenuDeleteLoopDone:

  ; remove registry keys
  DeleteRegKey HKCU "Software\$(Name)"
  DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\$(Name)"
  DeleteRegKey HKLM "Software\$(Name)"

SectionEnd
!endif
;---------------------------------------------------------------------------------------
;Uninstaller Functions
;---------------------------------------------------------------------------------------
Function un.onInit

   Push $2
   Push $R0
   
   ;Push "SILENT"
   ;Push "NO"
   ;Call un.GetParameterValue
   ;Pop $2
   ;StrCpy $SILENT $2

   ;StrCmp $SILENT "NO" init_cmd_normal
   ClearErrors
   ${GetParameters} $R0 ; read options
   ${GetOptions} "$R0" "/SILENT" $2 ; check the option by non-case sensitive
   IfErrors init_cmd_normal ; if error, the option not found
   SetSilent silent

   init_cmd_normal:
   
  ${If} ${RunningX64}
    SetRegView 64
  ${EndIf}
  ;Get language from registry
  ReadRegStr $LANGUAGE HKCU "Software\$(NAME)" "Installer Language"
  ReadRegStr $STARTMENU_FOLDER HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\$(Name)" "StartMenuFolder"
  
   Pop $R0
   Pop $2
FunctionEnd
;------------------------------------------------------------------------------
; Call on installation failed
;------------------------------------------------------------------------------
Function .onInstFailed
   Call Cleanup
FunctionEnd
;------------------------------------------------------------------------------
; Call when installation failed or when uninstall started
;------------------------------------------------------------------------------
Function Cleanup
  SetShellVarContext all

  ; remove registry keys
  DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\$(Name)"
  DeleteRegKey HKLM "SOFTWARE\$(Name)"

   DeleteRegKey HKCR ".xpdl"
   DeleteRegKey HKCR "xpdlfile\DefaultIcon"
   DeleteRegKey HKCR "xpdlfile\Shell"
   DeleteRegKey HKCR "xpdlfile"

  ; MUST REMOVE UNINSTALLER, too
  Delete $INSTDIR\uninstall.exe

  RMDir /r "$INSTDIR"

  ; remove shortcuts, if any.
  Delete "$SMPROGRAMS\$STARTMENU_FOLDER\$(ABBREVIATION) $(Documentation)\$(ABBREVIATION) $(Manual) HTML.lnk"
  Delete "$SMPROGRAMS\$STARTMENU_FOLDER\$(ABBREVIATION) $(Documentation)\$(ABBREVIATION) $(Manual) PDF.lnk"
  Delete "$SMPROGRAMS\$STARTMENU_FOLDER\$(ABBREVIATION) ${VERSION}-${RELEASE}.lnk"
  Delete "$SMPROGRAMS\$STARTMENU_FOLDER\$(Uninstall).lnk"
  
  Delete "$DESKTOP\$(ABBREVIATION) ${VERSION}-${RELEASE}.lnk"
  Delete "$QUICKLAUNCH\$(ABBREVIATION) ${VERSION}-${RELEASE}.lnk"

  RMDir "$SMPROGRAMS\$STARTMENU_FOLDER\$(ABBREVIATION) $(Documentation)"
  RMDir "$SMPROGRAMS\$STARTMENU_FOLDER"

  ;Delete empty start menu parent diretories
  StrCpy $MUI_TEMP "$SMPROGRAMS\$MUI_TEMP"

  startMenuDeleteLoop:
    RMDir $MUI_TEMP
    GetFullPathName $MUI_TEMP "$MUI_TEMP\.."

    IfErrors startMenuDeleteLoopDone

    StrCmp $MUI_TEMP $SMPROGRAMS startMenuDeleteLoopDone startMenuDeleteLoop
  startMenuDeleteLoopDone:

  ; remove registry keys
  DeleteRegKey HKCU "Software\$(Name)"
  DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\$(Name)"
  DeleteRegKey HKLM "Software\$(Name)"

FunctionEnd
;-------------------------------------------------------------------------------
; input, top of stack = string for replace string to search for
;        top of stack-1 = string to search for
;        top of stack-2 = string to search in
; output, top of stack string to search in replaces with the string for replace
; modifies no other variables.
;
; Usage:
;   Push "Start @JAVA_DIR@\bin\java.exe"
;   Push "@JAVA_DIR@"
;   Push "C:\j2sdk1.4.0"
;   Call StrReplace
;   Pop $R0
;  ($R0 at this point is "Start C:\j2sdk1.4.0\bin\java.exe")
;-------------------------------------------------------------------------------
Function StrReplace
  Exch $0 ;this will replace wrong characters
  Exch
  Exch $1 ;needs to be replaced
  Exch
  Exch 2
  Exch $2 ;the orginal string
  Push $3 ;counter
  Push $4 ;temp character
  Push $5 ;temp string
  Push $6 ;length of string that need to be replaced
  Push $7 ;length of string that will replace
  Push $R0 ;tempstring
  Push $R1 ;tempstring
  Push $R2 ;tempstring
  StrCpy $3 "-1"
  StrCpy $5 ""
  StrLen $6 $1
  StrLen $7 $0
  Loop:
 IntOp $3 $3 + 1
  StrCpy $4 $2 $6 $3
  StrCmp $4 "" ExitLoop
  StrCmp $4 $1 Replace
  Goto Loop
  Replace:
  StrCpy $R0 $2 $3
  IntOp $R2 $3 + $6
  StrCpy $R1 $2 "" $R2
  StrCpy $2 $R0$0$R1
  IntOp $3 $3 + $7
  Goto Loop
  ExitLoop:
  StrCpy $0 $2
  Pop $R2
  Pop $R1
  Pop $R0
  Pop $7
  Pop $6
  Pop $5
  Pop $4
  Pop $3
  Pop $2
  Pop $1
  Exch $0
FunctionEnd
;------------------------------------------------------------------------------
; ReplaceChar
; input, input string  (C:\JBuilder\jdk1.3.1)
;        char to need replaced (\)
;        char to with replaced (/)
; output, top of stack (C:/JBuilder/jdk1.3.1)
; modifies no other variables.
;
; Usage:
;   Push $1 ; "C:\JBuilder\jdk1.3.1"
;   Push "\"
;   Push "/"
;   Call ReplaceChar
;   Pop $0
;   ; at this point $0 will equal "C:/JBuilder/jdk1.3.1"
;------------------------------------------------------------------------------
Function ReplaceChar
               ; stack="/","\","C:\JBuilder\jdk1.3.1", ... (from top to down)
  Exch $2 ; $2="/", stack="2-gi param","\","C:\JBuilder\jdk1.3.1", ...
  Exch     ; stack="\","2-ti param","C:\JBuilder\jdk1.3.1", ...
  Exch $1 ; $1="\", stack="1-vi param","2-gi param","C:\JBuilder\jdk1.3.1", ...
  Exch    ; stack="2-gi param","1-vi param","C:\JBuilder\jdk1.3.1", ...
   Exch 2   ; stack="C:\JBuilder\jdk1.3.1","1-vi param","2-gi param", ...
  Exch $0 ; $0="C:\JBuilder\jdk1.3.1", stack="0-ti param","1-vi param","2-gi param", ...
  Exch 2  ; stack="2-gi param","1-vi param","0-ti param", ...

  Push $3   ; Len("C:\JBuilder\jdk1.3.1")
  Push $4   ; index of string
  Push $5 ; tmp - form strat to found char witch need to change
  Push $6 ; tmp - from found char witch need to change + 1 to end ($3)
  Push $7 ; tmp - position where found char witch need to change + 1
  StrLen $3 $0
  StrCpy $4 1
  loop:
    StrCpy $5 $0 1 $4
    StrCmp $5 "" exit
    StrCmp $5 $1 change
    IntOp $4 $4 + 1
  Goto loop
  change:
     StrCpy $6 $4            ; pozition where found char witch need to change
     StrCpy $5 $0 $6      ; $5="C:"
     StrCpy $5 "$5$2"      ; $5="C:/"
     StrCpy $7 $4            ; pozition where found char witch need to change
     IntOp $7 $7 + 1       ; next pozition
     StrCpy $6 $0 $3 $7  ; $6="JBuilder\jdk1.3.1"
     StrCpy $0 $5            ; $0="C:/"
     StrCpy $0 "$0$6"       ; $0="C:/JBuilder\jdk1.3.1"
  Goto loop
  exit:
    Pop $7
    Pop $6
    Pop $5
    Pop $4
    Pop $3
    Pop $2
    Pop $1
    Exch $0 ; put $0 on top of stack, restore $0 to original value
FunctionEnd
;-----------------------------------------------------------------------
;----------------------------------------------      
; input:
; Push "HelloAll I am Afrow UK, and I love NSIS" ; Input string 
; Push " " ; search string 
; Call GetFirstStrPart 
; output:
; Pop "$R0" ; first part
; Pop "$R1" ; rest of string
;----------------------------------------------      
Function GetFirstPartRest
  Exch $R1  ; search string
#  Dumpstate::debug
  Exch
#  Dumpstate::debug
  Exch $R0   ; Input string
#  Dumpstate::debug
  Exch
  Push $R2
  Push $R3
  Push $R4
  StrCpy $R4 $R0
  StrLen $R2 $R0
  IntOp $R2 $R2 + 1
#  Dumpstate::debug
  loop:
#     MessageBox MB_OK "loop:-1"
#     Dumpstate::debug
    IntOp $R2 $R2 - 1
#   StrCpy user_var(destination) str [maxlen] [start_offset]
    StrCpy $R3 $R0 1 -$R2
#     MessageBox MB_OK "loop:-2"
#     Dumpstate::debug
    StrCmp $R2 0 exit0
    StrCmp $R3 $R1 exit1 loop ; check if find (go to exit1 otherwise loop)

  exit0:
#  MessageBox MB_OK "exit0"
#  Dumpstate::debug
#  StrCpy $R2 ""
  StrCpy $R1 ""
  StrCpy $R0 ""
  Goto exit2

  exit1:
#  MessageBox MB_OK "exit1"
#  Dumpstate::debug
    IntOp $R2 $R2 - 1
    StrCpy $R3 $R0 "" -$R2
    IntOp $R2 $R2 + 1
    StrCpy $R0 $R0 -$R2
#  MessageBox MB_OK "exit1-1"
#  Dumpstate::debug
     StrLen $R2 $R0
    IntOp $R2 $R2 + 1
    StrCpy $R3 $R4 "" $R2
#  MessageBox MB_OK "exit1-2"
#  Dumpstate::debug
    StrCpy $R1 $R3

  exit2:
#  MessageBox MB_OK "exit2"
#  Dumpstate::debug
    Pop $R4
    Pop $R3
    Pop $R2
#  Dumpstate::debug
#    Exch  
    Exch $R1
    Exch
#  Dumpstate::debug
    Exch $R0
#  Dumpstate::debug
FunctionEnd
;------------------------------------------------------------------------------
Function StartJaWE
  
  Exec "$\"$JAVAHOME\bin\javaw.exe$\" -Xmx512M -DJaWE_HOME=$\"$INSTDIR$\" -Djava.ext.dirs=$\"$INSTDIR\lib$\" org.enhydra.jawe.JaWE"
  
FunctionEnd  

;---------------------------------------------------------------------------------
 ; TrimNewlines
 ; input, top of stack  (e.g. whatever$\r$\n)
 ; output, top of stack (replaces, with e.g. whatever)
 ; modifies no other variables.

 Function TrimNewlines
   Exch $R0
   Push $R1
   Push $R2
   StrCpy $R1 0
 
 loop:
   IntOp $R1 $R1 - 1
   StrCpy $R2 $R0 1 $R1
   StrCmp $R2 "$\r" loop
   StrCmp $R2 "$\n" loop
   IntOp $R1 $R1 + 1
   IntCmp $R1 0 no_trim_needed
   StrCpy $R0 $R0 $R1
 
 no_trim_needed:
   Pop $R2
   Pop $R1
   Exch $R0
 FunctionEnd
;---------------------------------------------------------------------------------------------
Function un.GetParameters
 
   Push $R0
   Push $R1
   Push $R2
   Push $R3
   
   StrCpy $R2 1
   StrLen $R3 $CMDLINE
   
   ;Check for quote or space
   StrCpy $R0 $CMDLINE $R2
   StrCmp $R0 '"' 0 +3
     StrCpy $R1 '"'
     Goto loop
   StrCpy $R1 " "
   
   loop:
     IntOp $R2 $R2 + 1
     StrCpy $R0 $CMDLINE 1 $R2
     StrCmp $R0 $R1 get
     StrCmp $R2 $R3 get
     Goto loop
   
   get:
     IntOp $R2 $R2 + 1
     StrCpy $R0 $CMDLINE 1 $R2
     StrCmp $R0 " " get
     StrCpy $R0 $CMDLINE "" $R2
   
   Pop $R3
   Pop $R2
   Pop $R1
   Exch $R0
FunctionEnd
;------------------------------------------------------------------
; StrStr
; input, top of stack = string to search for
;        top of stack-1 = string to search in
; output, top of stack (replaces with the portion of the string remaining)
; modifies no other variables.
;
; Usage:
;   Push "this is a long ass string"
;   Push "ass"
;   Call StrStr
;   Pop $R0
;  ($R0 at this point is "ass string")
;------------------------------------------------------------------
Function un.customStrStr
 Exch $R1 ; st=haystack,old$R1, $R1=needle
   Exch    ; st=old$R1,haystack
   Exch $R2 ; st=old$R1,old$R2, $R2=haystack
   Push $R3
   Push $R4
   Push $R5
   StrLen $R3 $R1
   StrCpy $R4 0
   ; $R1=needle
   ; $R2=haystack
   ; $R3=len(needle)
   ; $R4=cnt
   ; $R5=tmp
   loop:
     StrCpy $R5 $R2 $R3 $R4
     StrCmp $R5 $R1 done
     StrCmp $R5 "" done
     IntOp $R4 $R4 + 1
     Goto loop
 done:
   StrCpy $R1 $R2 "" $R4
   Pop $R5
   Pop $R4
   Pop $R3
   Pop $R2
   Exch $R1
 FunctionEnd
;---------------------------------------------------------------------------------
; GetParameterValue
; Chris Morgan 5/10/2004
; Searches the command line input, retrieved using GetParameters, for the
; value of an option given the option name.  If no option is found the
; default value is placed on the top of the stack upon function return
;
; Inputs - Top of stack is default if parameter isn't found,
;  second in stack is parameter to search for, ex. "OUTPUT"
; Outputs - Top of the stack contains the value of this parameter
;  So if the command line contained /OUTPUT=somedirectory, "somedirectory"
;  will be on the top of the stack when this function returns
;
; Register usage
;$R0 - default return value if the parameter isn't found
;$R1 - input parameter, for example OUTPUT from the above example
;$R2 - the length of the search, this is the search parameter+2
;      as we have '/OUTPUT='
;$R3 - the command line string
;$R4 - result from StrStr's
;$R5 - search for ' ' or '"'
;---------------------------------------------------------------------------------
Function un.GetParameterValue
  Exch $R0  ; get the default parameter into R1
  Exch      ; exchange the top two
  Exch $R1  ; get the search parameter into $R0

  ;Preserve on the stack the registers we will use in this function
  Push $R2
  Push $R3
  Push $R4
  Push $R5

  Strlen $R2 $R1+2   ; store the length of the search string into R2

  Call un.GetParameters ;get the command line parameters
  Pop $R3            ; store the command line string in R3
  # search for quoted search string
  StrCpy $R5 '"'     ; later on we want to search for a open quote
  Push $R3           ; push the 'search in' string onto the stack
  Push '"/$R1='      ; push the 'search for'
  Call un.customStrStr
  Pop $R4
  StrCpy $R4 $R4 "" 1 # skip quote
  StrCmp $R4 "" 0 next
  # search for non-quoted search string
  StrCpy $R5 ' '     ; later on we want to search for a space
  Push $R3           ; push the command line back on the stack for searching
  Push '/$R1='       ; search for the non-quoted search string
  Call un.customStrStr
  Pop $R4
next:
  StrCmp $R4 "" done       ; if we didn't find anything then we are done
  # copy the value after /$R1=
  StrCpy $R0 $R4 "" $R2  ; copy commandline text beyond parameter into $R0
  # search for the next parameter so we can trim this extra text off
  Push $R0
  Push $R5
  Call un.customStrStr         ; search for the next parameter
  Pop $R4
  StrCmp $R4 "" done
  StrLen $R4 $R4
  StrCpy $R0 $R0 -$R4 ; using the length of the string beyond the value,
                      ;copy only the value into $R0
done:
  Pop $R5
  Pop $R4
  Pop $R3
  Pop $R2
  Pop $R1
  Exch $R0 ; put the value in $R0 at the top of the stack
FunctionEnd
;------------------------------------------------------------------------------
Function WriteToFile
 Exch $0 ;file to write to
 Exch
 Exch $1 ;text to write
 
  FileOpen $0 $0 a #open file
   FileSeek $0 0 END #go to end
   FileWrite $0 $1 #write to file
  FileClose $0
 
 Pop $1
 Pop $0
FunctionEnd
;-----------------------------------------------------------------------

Function GetJavaVersion
  Push $R0
  Push $R1
  Push $R2
  Push $R9
  Push $1
  Push $2
  Push $3
  Push $4
  Push $5
  ;No value by default
  StrCpy $1 ""
  StrCpy $2 ""
  StrCpy $3 ""
  StrCpy $4 ""
  StrCpy $5 ""

  ClearErrors
  ;**********************************************************************
  ;1.)READ JAVA_HOME from all users or Read the registry Environment  "JAVA_HOME"
  ;**********************************************************************
  ReadEnvStr $4 "JAVA_HOME"
  ${if} $4 == ""
    ClearErrors
    ReadRegStr $4 HKCU "Environment" "JAVA_HOME"  
  ${endif}
    
  ClearErrors
  ;**********************************************************************
  ;2.) Read the Sun JDK value from the registry 32bit and 64 bit
  ;**********************************************************************
  ;===========================================
  ; A.)Read the registry JDK 32bit 
  ;===========================================
  ReadRegStr $2 HKLM "SOFTWARE\JavaSoft\Java Development Kit"      "CurrentVersion"
  ReadRegStr $1 HKLM "SOFTWARE\JavaSoft\Java Development Kit\$2"  "JavaHome"
  ${if} $1 != ""   ;found jdk 32bit.
    StrCpy $R9 0   ; count EnumRegKey from "SOFTWARE\JavaSoft\Java Development Kit"
  ${endif}
  
  loopJDK32bit:
    EnumRegKey $R1 HKLM "SOFTWARE\JavaSoft\Java Development Kit" $R9    
    ReadRegStr $R2 HKLM "SOFTWARE\JavaSoft\Java Development Kit\$R1" "JavaHome"
    ${if} $R2 != ""
        ; check if java version >= 1.4.x
        goto incrementJDK32bit
    ${else}
        goto noSunJDK32bit
    ${endif}
    
  incrementJDK32bit:
    IntOp $R9 $R9 + 1
    goto loopJDK32bit
    
  noSunJDK32bit:
  
  ClearErrors
  ;===========================================
  ; B.)Read the registry JDK 64bit 
  ;===========================================
  ${if} ${RunningX64}
    SetRegView 64
    ReadRegStr $2 HKLM "SOFTWARE\JavaSoft\Java Development Kit" "CurrentVersion"
    ReadRegStr $1 HKLM "SOFTWARE\JavaSoft\Java Development Kit\$2" "JavaHome"
    ${if} $1 != ""   ;found jdk 64bit.
        StrCpy $R9 0   ; count EnumRegKey from "SOFTWARE\JavaSoft\Java Development Kit"
    ${endif}
    
    loopJDK64bit:
        SetRegView 64
        EnumRegKey $R1 HKLM "SOFTWARE\JavaSoft\Java Development Kit" $R9    
        ReadRegStr $R2 HKLM "SOFTWARE\JavaSoft\Java Development Kit\$R1" "JavaHome"
        ${if} $R2 != ""
            ; check if java version >= 1.4.x
            goto incrementJDK64bit
        ${else}
            goto noSunJDK64bit
        ${endif}
            
    incrementJDK64bit:
        IntOp $R9 $R9 + 1
        goto loopJDK64bit
                
    noSunJDK64bit:
        SetRegView 32
    
    ClearErrors
  ${endif}
  ;**********************************************************************
  ;3.) Read the Sun JRE value from the registry 32bit and 64 bit
  ;**********************************************************************
  ;===========================================
  ; A.)Read the registry JRE 32bit 
  ;===========================================
  ReadRegStr $2 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
  ReadRegStr $1 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$2" "JavaHome"
    
  ${if} $1 != ""   ;found JRE 32bit.
    StrCpy $R9 0   ; count EnumRegKey from "SOFTWARE\JavaSoft\Java Development Kit"
  ${endif}
 
  loopJRE32bit:
    EnumRegKey $R1 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" $R9
    ReadRegStr $R2 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$R1" "JavaHome"    
    ${if} $R2 != ""
        ; check if java version >= 1.4.x
        goto incrementJRE32bit
    ${else}
       goto noSunJRE32bit
    ${endif}
   
  incrementJRE32bit:
    IntOp $R9 $R9 + 1
    goto loopJRE32bit
    
  noSunJRE32bit:
  
  ClearErrors
  ;===========================================
  ; B.)Read the registry JRE 64bit 
  ;===========================================
  ${if} ${RunningX64}
    SetRegView 64
    ReadRegStr $2 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
    ReadRegStr $1 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$2" "JavaHome"
    ${if} $1 != ""   ;found JRE 64bit.
        StrCpy $R9 0   ; count EnumRegKey from "SOFTWARE\JavaSoft\Java Development Kit"
    ${endif}
    
    loopJRE64bit:
        SetRegView 64
        EnumRegKey $R1 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" $R9
        ReadRegStr $R2 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$R1" "JavaHome"    
        ${if} $R2 != ""
            ; check if java version >= 1.4.x
            goto incrementJRE64bit
        ${else}
            goto noSunJRE64bit
        ${endif}
    
    incrementJRE64bit:
        IntOp $R9 $R9 + 1
        goto loopJRE64bit
                
    noSunJRE64bit:
        SetRegView 32
      
    ClearErrors
  ${endif}
  ;**********************************************************************
      
  Exch  
  Pop $4
  Exch
  Pop $3
  Exch
  Pop $2
  Exch
  Pop $1
  Exch
  Pop $R9
  Exch
  Pop $R2
  Exch
  Pop $R1
  Exch
  Pop $R0
  Exch $5
  
FunctionEnd

Function RefreshShellIcons
  ; By jerome tremblay - april 2003
  System::Call 'shell32.dll::SHChangeNotify(i, i, i, i) v \
  (${SHCNE_ASSOCCHANGED}, ${SHCNF_IDLIST}, 0, 0)'
FunctionEnd
