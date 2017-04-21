;    Together Filesize Lister 
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
;----------------------------------------------------------------------------------
!ifndef TOGETHER_CUSTOMPAGE_SETJAVA
!define TOGETHER_CUSTOMPAGE_SETJAVA

!include tog.nsh
!include nsDialogs.nsh
!include WinMessages.nsh
!include LogicLib.nsh
!include x64.nsh
!include StrFunc.nsh

!define TOG_CUSTOMPAGE_SETJAVA_TITLE "Choose Java Virtual Machine"
!define TOG_CUSTOMPAGE_SETJAVA_SUBTITLE "Choose which path of the Java virtual machine to install ${APP_NAME}."
!define TOG_CUSTOMPAGE_SETJAVA_TEXT "Java virtual machine is needed to install ${APP_NAME}."
!define TOG_CUSTOMPAGE_SETJAVA_TEXT_EXTA "Choose a path of the Java virtual machine. To choose a different path, click Browse and select another path."
!define TOG_CUSTOMPAGE_SETJAVA_TEXT_EXTB "Click Browse and select a path."
!define TOG_CUSTOMPAGE_SETJAVA_TEXT_INSTALL "Click Install to start the installation."
!define TOG_CUSTOMPAGE_SETJAVA_TEXT_NEXT "Click Next to continue."

; Variables : Controls
Var tog.SetJavaCustomPage
Var tog.SetJavaCustomPage.Text
Var tog.SetJavaCustomPage.SelectedRadio
Var tog.SetJavaCustomPage.JavaHomeRadio
Var tog.SetJavaCustomPage.ExistRadio
Var tog.SetJavaCustomPage.UserRadio
Var tog.SetJavaCustomPage.JavaHomeBox
Var tog.SetJavaCustomPage.ExistBox
Var tog.SetJavaCustomPage.UserBox
Var tog.SetJavaCustomPage.JavaHomeEdit
Var tog.SetJavaCustomPage.ExistEdit
Var tog.SetJavaCustomPage.UserEdit
Var tog.SetJavaCustomPage.UserBrowseButton
Var tog.SetJavaCustomPage.SelectedExist
Var tog.SetJavaCustomPage.Button.Next
;Java virtual machine info
Var TOG_JavaHome
Var TOG_ExistJDK
Var TOG_ExistJRE
Var TOG_JavaBrowse
Var TOG_ExistJavaCount
Var TOG_Index
Var TOG_JavaFullVersionSupport ; minimum version of java that our program support 03222012/Kot
Var TOG_UserJavaHomeVersion    ; user set their java_home to which version 03222012/Kot
Var TOG_UserHasRightJavaVersion    ; user set their java_home to which version 03222012/Kot
	
!macro TOG_CUSTOMPAGE_SETJAVA APP_NAME JAVA_PATH TARGET_VM
Page custom create_page_setjava leave_page_setjava

Function create_page_setjava

	# Define minimum version of java that our application support. 03222012/Kot
	StrCpy $TOG_JavaFullVersionSupport "${TARGET_VM}"
	
    # Insert pre-custom function
    !insertmacro TOG_CUSTOMPAGE_FUNCTION_CUSTOM PRE
    Push $0
    Push $1
	Push $R0
	Push $R1
	Push $R9
	
    ;(0) Create header title & header subtitle and Get Java virtual machine info from %JAVA_HOME%, JDK and JRE
    !insertmacro MUI_HEADER_TEXT_PAGE "${TOG_CUSTOMPAGE_SETJAVA_TITLE}" "${TOG_CUSTOMPAGE_SETJAVA_SUBTITLE}"
	StrCpy $TOG_ExistJavaCount 0
    Call GetJavaInfo

    ;(1) Create a Together setjava dialog page
    nsDialogs::Create 1018
    Pop $tog.SetJavaCustomPage
    GetDlgItem $tog.SetJavaCustomPage.Button.Next $HWNDPARENT 1

    ;(2) Create a text control
    StrCpy $1 "${TOG_CUSTOMPAGE_SETJAVA_TEXT}"
    ${If} $TOG_JavaHome != ""
        StrCpy $1 "$1 ${TOG_CUSTOMPAGE_SETJAVA_TEXT_EXTA}"
    ${ElseIf} $TOG_ExistJDK != ""
    ${OrIf}   $TOG_ExistJRE != ""
        StrCpy $1 "$1 ${TOG_CUSTOMPAGE_SETJAVA_TEXT_EXTA}"
    ${Else}
        StrCpy $1 "$1 ${TOG_CUSTOMPAGE_SETJAVA_TEXT_EXTB}"
    ${EndIf}
    System::Call 'user32::GetWindowText(i$tog.SetJavaCustomPage.Button.Next,t.s,i${NSIS_MAX_STRLEN})'
    Pop $0
    ${If} "&Next >" == $0
        StrCpy $1 "$1 ${TOG_CUSTOMPAGE_SETJAVA_TEXT_NEXT}"
    ${ElseIf} "&Install" == $0
        StrCpy $1 "$1 ${TOG_CUSTOMPAGE_SETJAVA_TEXT_INSTALL}"
    ${EndIf}
    nsDialogs::CreateControl STATIC ${WS_VISIBLE}|${WS_CHILD}|${WS_CLIPSIBLINGS} 0 0u 0u 100% 27u "$1"
    Pop $tog.SetJavaCustomPage.Text

    ;(3) Create a radio button for JAVAHOME
    ${NSD_CreateRadioButton} 10u 40u 10u 10u ""
    Pop $tog.SetJavaCustomPage.JavaHomeRadio
    ${NSD_AddStyle} $tog.SetJavaCustomPage.JavaHomeRadio ${WS_GROUP}
    ${NSD_OnClick} $tog.SetJavaCustomPage.JavaHomeRadio OnClickJavaHomeRadio
        ;(3.1) Create a groupbox control
        ${NSD_CreateGroupBox} 30u 28u 85% 30u "%JAVA_HOME% environmental variable"
        Pop $tog.SetJavaCustomPage.JavaHomeBox
        ;(3.2) Create an edit control
        ${NSD_CreateText} 40u 40u 78% 12u ""
        Pop $tog.SetJavaCustomPage.JavaHomeEdit

    ;(4) Create a radio button for Exiting JVM
    ${NSD_CreateRadioButton} 10u 80u 10u 10u ""
    Pop $tog.SetJavaCustomPage.ExistRadio
    ${NSD_OnClick} $tog.SetJavaCustomPage.ExistRadio OnClickExistRadio
        ;(4.1) Create a groupbox control
        ${NSD_CreateGroupBox} 30u 60u 85% 48u "Existing path of Java virtual machine"
        Pop $tog.SetJavaCustomPage.ExistBox
        ;(4.2) Create a listbox control
        ${NSD_CreateListBox} 40u 72u 78% 29u ""
        Pop $tog.SetJavaCustomPage.ExistEdit

        ${NSD_OnChange} $tog.SetJavaCustomPage.ExistEdit OnChangeExistCurSel

    ;(5) Create a radio button for user-defined JVM
    ${NSD_CreateRadioButton} 10u 122u 10u 10u ""
    Pop $tog.SetJavaCustomPage.UserRadio
    ${NSD_OnClick} $tog.SetJavaCustomPage.UserRadio OnClickUserRadio
        ;(5.1) Create a groupbox control
        ${NSD_CreateGroupBox} 30u 110u 85% 30u "User-defined path of Java virtual machine"
        Pop $tog.SetJavaCustomPage.UserBox
        ;(5.2) Create an edit control
        ${NSD_CreateText} 40u 122u 55% 12u "$TOG_JavaBrowse"
        Pop $tog.SetJavaCustomPage.UserEdit
        ${NSD_OnChange} $tog.SetJavaCustomPage.UserEdit OnChangeUserEdit
        System::Call 'shlwapi::SHAutoComplete(i$tog.SetJavaCustomPage.UserEdit OnChangeUserEdit,i"1")'
        ;(5.3) Create a user-browse button
        ${NSD_CreateBrowseButton} 214u 120u 60u 14u "Browse..."
        Pop $tog.SetJavaCustomPage.UserBrowseButton
        ${NSD_OnClick} $tog.SetJavaCustomPage.UserBrowseButton OnClick_BrowseButton_SetJava

    ;(6) Manage user-interfaces of controls
    ;(*) Check for the first-time page
    ${If} $tog.SetJavaCustomPage.SelectedRadio == ""
        ${If} $TOG_JavaHome != ""
            StrCpy $tog.SetJavaCustomPage.SelectedRadio "JAVAHOME"
        ${ElseIf} $TOG_ExistJDK != ""
        ${OrIf}   $TOG_ExistJRE != ""
            StrCpy $tog.SetJavaCustomPage.SelectedRadio "EXIST"
            StrCpy $tog.SetJavaCustomPage.SelectedExist "0"
        ${Else}
            StrCpy $tog.SetJavaCustomPage.SelectedRadio "USER"
        ${EndIf}
    ${EndIf}

    ;(**) Disable controls or setup controls
    ${If}  $TOG_JavaHome == ""
        EnableWindow $tog.SetJavaCustomPage.JavaHomeRadio 0
    ${Else}
        system::Call 'user32::SetWindowText(i$tog.SetJavaCustomPage.JavaHomeEdit, t"$TOG_JavaHome")'
    ${EndIf}
    EnableWindow $tog.SetJavaCustomPage.JavaHomeEdit  0 ;<< JAVA_HOME always disable
    ${If}    $TOG_ExistJDK == ""
    ${AndIf} $TOG_ExistJRE == ""
        EnableWindow $tog.SetJavaCustomPage.ExistRadio 0
        EnableWindow $tog.SetJavaCustomPage.ExistEdit  0
    ${Else}
		nsArray::Length Array_JavaVersion
		Pop $R0
		${ForEach} $TOG_Index 1 $R0 + 1
			nsArray::Get Array_JavaVersion $TOG_Index
			Pop $R1
			${NSD_LB_AddString} $tog.SetJavaCustomPage.ExistEdit "$R1"			
		${Next}
    ${EndIf}

    ;(***) For n-time page, Set for clicking "< Back" or "Next >" button to unchange
    ${If} $tog.SetJavaCustomPage.SelectedRadio == "JAVAHOME"
        SendMessage $tog.SetJavaCustomPage.JavaHomeRadio ${BM_SETCHECK} ${BST_CHECKED} 0
        SendMessage $tog.SetJavaCustomPage.ExistEdit ${LB_SETCURSEL} "-1" 0
    ${ElseIf} $tog.SetJavaCustomPage.SelectedRadio == "EXIST"
        SendMessage $tog.SetJavaCustomPage.ExistRadio ${BM_SETCHECK} ${BST_CHECKED} 0
        SendMessage $tog.SetJavaCustomPage.ExistEdit ${LB_SETCURSEL} $tog.SetJavaCustomPage.SelectedExist 0
    ${ElseIf} $tog.SetJavaCustomPage.SelectedRadio == "USER"
        SendMessage $tog.SetJavaCustomPage.UserRadio ${BM_SETCHECK} ${BST_CHECKED} 0
        SendMessage $tog.SetJavaCustomPage.ExistEdit ${LB_SETCURSEL} "-1" 0
    ${EndIf}

    # Insert show-custom function
    !insertmacro TOG_CUSTOMPAGE_FUNCTION_CUSTOM SHOW
    Call Update_Next_Button
    nsDialogs::Show
	Pop $R9
	Pop $R1
	Pop $R0
    Pop $1
    Pop $0
FunctionEnd

Function leave_page_setjava
	Push $R1
	Push $R2
    # Insert leave-custom function
    !insertmacro TOG_CUSTOMPAGE_FUNCTION_CUSTOM LEAVE
    ${If} $tog.SetJavaCustomPage.SelectedRadio == "JAVAHOME"
        StrCpy ${JAVA_PATH} $TOG_JavaHome
    ${ElseIf} $tog.SetJavaCustomPage.SelectedRadio == "EXIST"
		StrCpy $R2 $tog.SetJavaCustomPage.SelectedExist
		IntOp $R2 $R2 + 1
		nsArray::Get Array_JavaVersion $R2
		Pop $R1
		StrCpy ${JAVA_PATH} $R1
    ${Else}
        StrCpy ${JAVA_PATH} $TOG_JavaBrowse
    ${EndIf}
	Pop $R2
	Pop $R1
FunctionEnd

Function OnChangeExistCurSel
    StrCpy $tog.SetJavaCustomPage.SelectedRadio "EXIST"
    SendMessage $tog.SetJavaCustomPage.JavaHomeRadio ${BM_SETCHECK} ${BST_UNCHECKED} 0
    SendMessage $tog.SetJavaCustomPage.ExistRadio ${BM_SETCHECK} ${BST_CHECKED} 0
    SendMessage $tog.SetJavaCustomPage.UserRadio ${BM_SETCHECK} ${BST_UNCHECKED} 0
    SendMessage $tog.SetJavaCustomPage.ExistEdit ${LB_GETCURSEL} 0 0 $tog.SetJavaCustomPage.SelectedExist
    Call Update_Next_Button
FunctionEnd

Function OnChangeUserEdit
    StrCpy $tog.SetJavaCustomPage.SelectedRadio "USER"
    SendMessage $tog.SetJavaCustomPage.ExistEdit ${LB_SETCURSEL} "-1" 0
    SendMessage $tog.SetJavaCustomPage.JavaHomeRadio ${BM_SETCHECK} ${BST_UNCHECKED} 0
    SendMessage $tog.SetJavaCustomPage.ExistRadio ${BM_SETCHECK} ${BST_UNCHECKED} 0
    SendMessage $tog.SetJavaCustomPage.UserRadio ${BM_SETCHECK} ${BST_CHECKED} 0
    Pop $0
    System::Call 'user32::GetWindowText(i$tog.SetJavaCustomPage.UserEdit, t.r0, i${NSIS_MAX_STRLEN})'
    IfFileExists $0\bin\javaw.exe 0 +3
        StrCpy $TOG_JavaBrowse $0
        goto +2
    StrCpy $TOG_JavaBrowse ""
    Call Update_Next_Button
FunctionEnd

Function OnClick_BrowseButton_SetJava
    StrCpy $tog.SetJavaCustomPage.SelectedRadio "USER"
    SendMessage $tog.SetJavaCustomPage.ExistEdit ${LB_SETCURSEL} "-1" 0
    SendMessage $tog.SetJavaCustomPage.JavaHomeRadio ${BM_SETCHECK} ${BST_UNCHECKED} 0
    SendMessage $tog.SetJavaCustomPage.ExistRadio ${BM_SETCHECK} ${BST_UNCHECKED} 0
    SendMessage $tog.SetJavaCustomPage.UserRadio ${BM_SETCHECK} ${BST_CHECKED} 0
    nsDialogs::SelectFolderDialog /NOUNLOAD "Select Java Virtual Machine."
    Pop $0
    ${If} $0 == "error" # returns 'error' if 'cancel' was pressed?
        Call Update_Next_Button
        Return
    ${EndIf}
    IfFileExists $0\bin\javaw.exe 0 +3
        StrCpy $TOG_JavaBrowse $0
        goto javabrowse_end
    StrCpy $TOG_JavaBrowse ""
    MessageBox MB_ICONEXCLAMATION "Browse the path [PATH] in which [Path]\bin\javaw.exe exists."
javabrowse_end:
    system::Call 'user32::SetWindowText(i$tog.SetJavaCustomPage.UserEdit, t"$TOG_JavaBrowse")'
    Call Update_Next_Button
FunctionEnd

Function OnClickJavaHomeRadio
    StrCpy $tog.SetJavaCustomPage.SelectedRadio "JAVAHOME"
    SendMessage $tog.SetJavaCustomPage.ExistEdit ${LB_SETCURSEL} "-1" 0
    Call Update_Next_Button
FunctionEnd

Function OnClickExistRadio
    StrCpy $tog.SetJavaCustomPage.SelectedRadio "EXIST"
    SendMessage $tog.SetJavaCustomPage.ExistEdit ${LB_SETCURSEL} $tog.SetJavaCustomPage.SelectedExist 0
    Call Update_Next_Button
FunctionEnd

Function OnClickUserRadio
    StrCpy $tog.SetJavaCustomPage.SelectedRadio "USER"
    SendMessage $tog.SetJavaCustomPage.ExistEdit ${LB_SETCURSEL} "-1" 0
    Call Update_Next_Button
FunctionEnd

Function Update_Next_Button
    ${If} $tog.SetJavaCustomPage.SelectedRadio == "USER"
        IfFileExists $TOG_JavaBrowse\bin\javaw.exe end_update_next_button 0
            EnableWindow $tog.SetJavaCustomPage.Button.Next 0
            return
    ${ElseIf} $tog.SetJavaCustomPage.SelectedRadio == "EXIST"
        SendMessage $tog.SetJavaCustomPage.ExistEdit ${LB_GETCURSEL} 0 0 $tog.SetJavaCustomPage.SelectedExist
        ${If} $tog.SetJavaCustomPage.SelectedExist == "-1"
            EnableWindow $tog.SetJavaCustomPage.Button.Next 0
            return
        ${EndIf}
    ${EndIf}
end_update_next_button:
    EnableWindow $tog.SetJavaCustomPage.Button.Next 1
FunctionEnd

Function GetJavaInfo
    ;(0) check if user has lover version of java than the minimum requirement; quit the installation 03222012/Kot
	Call Check_JavaVersionNumber
	
	;(1) Check whether %JAVA_HOME% exists
    Call Check_JavaHome
	
    ;(2) Check whether other exisiting JVMs exists, e.g. JDK, JRE
    Call Check_JDK
    Call Check_JRE
FunctionEnd

Function Check_JavaVersionNumber
	;check if user has lover version of java than the minimum requirement; quit the installation 03222012/Kot
	Push $0 
	${if} ${RunningX64}
        SetRegView 64
    ${Else}
        SetRegView 32
    ${Endif}
	# get current version of java
    ReadRegStr $0 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
 
check_java_version_number:	
    ${If} $0 S< $TOG_JavaFullVersionSupport
         ;requirement not met.
		 # show warning to user befor quit install.
		StrCpy $1 $TOG_JavaFullVersionSupport "" 2
		MessageBox MB_OK "${APP_NAME} is support for java $TOG_JavaFullVersionSupport or higher version. $\nPlease install Java $TOG_JavaFullVersionSupport or higher version before install ${APP_NAME}." IDOK quit_instaillation
    ${EndIf}
	
	Pop $0
	goto finish

quit_instaillation:
	Pop $0
	Quit

finish:
FunctionEnd

Function Check_JavaHome
	ReadEnvStr $TOG_JavaHome "JAVA_HOME"

	Call GetUserJavaVersionByJavaHome
	
    ${If} $TOG_JavaHome != ""
		${If} $TOG_UserHasRightJavaVersion != "Yes"
			Goto javahome_donot_exist
		${Else}
        IfFileExists $TOG_JavaHome\bin\javaw.exe 0 +2
            Goto javahome_alive
        goto javahome_donot_exist
		${EndIf}
    ${Else}
        ReadRegStr $TOG_JavaHome HKCU "Environment" "JAVA_HOME"

        IfFileExists $TOG_JavaHome\bin\javaw.exe 0 +2
            Goto javahome_alive
        Goto javahome_donot_exist
    ${EndIf}
javahome_donot_exist:
	StrCpy $TOG_JavaHome "" ;<< %JAVA_HOME% does not exist
javahome_alive:                 ;<< %JAVA_HOME% exists
FunctionEnd

Function GetUserJavaVersionByJavaHome
; add check for JRE version.
Push $R0
Push $R1
Push $R2
Push $R3
Push $R4
Push $R5
Push $R6
 
 ReadEnvStr $R3 "JAVA_HOME"
 StrCpy $R1 "."
 StrCpy $R0 -1
 IntOp $R0 $R0 + 1
  StrCpy $R2 $R3 1 $R0
  StrCmp $R2 "" +2
  StrCmp $R2 $R1 +2 -3
 
 StrCpy $R0 -1
 IntOp $R0 $R0 - 1							;adjust position
 StrCpy $TOG_UserJavaHomeVersion $R3 3 $R0	;get java major and minor version(x.y) from user java_home 03222012/Kot
 StrLen $R4 $TOG_UserJavaHomeVersion
 ${If} $R4 > 2
	${If} $TOG_UserJavaHomeVersion S< $TOG_JavaFullVersionSupport
		StrCpy $TOG_UserHasRightJavaVersion "No"
	${Else}
		StrCpy $TOG_UserHasRightJavaVersion "Yes"
	${EndIf}
 ${ElseIf} $R4 = 2
	StrCpy $R5 $TOG_UserJavaHomeVersion 1 1				; minor version fron java_home 03222012/Kot
	StrCpy $R6 $TOG_JavaFullVersionSupport 1 2			; minor version fron minimum support 03222012/Kot
	${If} $R5 S< $R6
		StrCpy $TOG_UserHasRightJavaVersion "No"
	${Else}
		StrCpy $TOG_UserHasRightJavaVersion "Yes"
	${EndIf}
 ${Else}
	StrCpy $TOG_UserHasRightJavaVersion "No"
 ${EndIf}
 
Pop $R6
Pop $R5
Pop $R4
Pop $R3
Pop $R2
Pop $R1
Pop $R0
FunctionEnd

!insertmacro Check_JavaVersion "JDK"
!insertmacro Check_JavaVersion "JRE"

!macroend


 
!macro Check_JavaVersion Version
Function Check_${Version}
    Var /GLOBAL Description_${Version}
    Push $1
    Push $2
	Push $3
    Push $4		; java major version 03222012/Kot
	Push $5		; java minor version 03222012/Kot
    Push $R1
    Push $R2
    Push $R9
    ${if} ${RunningX64}
        SetRegView 64
    ${Else}
        SetRegView 32
    ${Endif}
    ${If} ${Version} == "JDK"
        StrCpy $Description_${Version} "Java Development Kit"
    ${ElseIf} ${Version} == "JRE"
        StrCpy $Description_${Version} "Java Runtime Environment"
    ${Else}
        return
    ${EndIf}
    ReadRegStr $2 HKLM "SOFTWARE\JavaSoft\$Description_${Version}" "CurrentVersion"
    ReadRegStr $1 HKLM "SOFTWARE\JavaSoft\$Description_${Version}\$2" "JavaHome"
    StrCmp $1 "" end_check_${Version} +1 ;Check whether 32-bit JDK exists
    ;HERE: JDK is found.
    StrCpy $R9 0 ; Set $R9 for index at 0
start_check_${Version}:
    ;Count EnumRegKey from "SOFTWARE\Javasoft\Java Development Kit" and start for 0th index
    EnumRegKey $R1 HKLM "SOFTWARE\JavaSoft\$Description_${Version}" $R9
    ReadRegStr $R2 HKLM "SOFTWARE\JavaSoft\$Description_${Version}\$R1" "JavaHome"
	
    StrCmp $R2 "" end_check_${Version} +1 ;Check whether 32-bit JDK exists
        ;Read java version
        StrCpy $1 $R1 1     ; As major version e.g. (1).6
        StrCpy $2 $R1 1 2   ; As minor version e.g. 1.(6)
		StrCpy $3 $R1 1 4   ; As zero number before revision number 1.6.(0)_27
		StrCpy $4 $TOG_JavaFullVersionSupport 1		;get major version of java 03222012/Kot
		StrCpy $5 $TOG_JavaFullVersionSupport 1 2	;get minor version of java 03222012/Kot
	
		;skip if minor version of java lower than minimum requirement 03222012/Kot
		IntCmp $1 $4 0 increase_index_${Version} +1
		
		StrCmp $3 "" increase_index_${Version} +1
	
		;get JavaHome if minor version of java is eq or gt then minimun requirenet.
		IntCmp $2 $5 0 increase_index_${Version} 0	
		    StrCpy $TOG_Exist${Version} $R2
			IntOp $TOG_ExistJavaCount $TOG_ExistJavaCount + 1
			nsArray::Set Array_JavaVersion /key=$TOG_ExistJavaCount $TOG_Exist${Version} /end
	
increase_index_${Version}:
    IntOp $R9 $R9 + 1
    goto start_check_${Version}
end_check_${Version}:
    SetRegView 32
    Pop $R9
    Pop $R2
    Pop $R1
	Pop $5
    Pop $4
	Pop $3
    Pop $2
    Pop $1
FunctionEnd
!macroend

!endif ;TOGETHER_CUSTOMPAGE_SETJAVA