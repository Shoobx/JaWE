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
!ifndef TOGETHER_CUSTOMPAGE_STARTOPTION
!define TOGETHER_CUSTOMPAGE_STARTOPTION

!include tog.nsh
!include nsDialogs.nsh
!include WinMessages.nsh
!include LogicLib.nsh

!define TOG_CUSTOMPAGE_STARTOPTION_TITLE "Choose Start Options"
!define TOG_CUSTOMPAGE_STARTOPTION_SUBTITLE "Choose which Start options of ${APP_NAME} you want to install."

!define TOG_CUSTOMPAGE_STARTOPTION_TEXT "Check the options you want to install and uncheck the options you don't want to install. When Start menu is checked, you can also enter a name to create a new name of your Start menu."
!define TOG_CUSTOMPAGE_STARTOPTION_TEXT_INSTALL "Click Install to start the installation."
!define TOG_CUSTOMPAGE_STARTOPTION_TEXT_NEXT "Click Next to continue."

; Variables : Controls
Var tog.StartOptionCustomPage
Var tog.StartOptionCustomPage.Text
Var tog.StartOptionCustomPage.Button.Next
Var tog.StartOptionCustomPage.Check.StartMenu
Var tog.StartOptionCustomPage.Edit.StartMenu
Var tog.StartOptionCustomPage.Check.Shortcut
Var tog.StartOptionCustomPage.Check.QuickLaunch
Var tog.StartOptionCustomPage.Check.PintoTaskbar

!macro TOG_CUSTOMPAGE_STARTOPTION APP_NAME CHECK_STARTMENU EDIT_STARTMENU ENABLE_SHORTCUT CHECK_SHORTCUT ENABLE_QUICKLAUNCH CHECK_QUICKLAUNCH ENABLE_PINTOTASKBAR CHECK_PINTOTASKBAR
Page custom create_page_startoption leave_page_startoption

Function create_page_startoption
    # Insert pre-custom function
    !insertmacro TOG_CUSTOMPAGE_FUNCTION_CUSTOM PRE
    Push $0
    Push $1

    ;(0) Create header title & header subtitle and Set name of Start menu as ${APP_NAME} and Set every CheckBox state as ${BST_CHECKED}, except QUICKLAUNCH
    !insertmacro MUI_HEADER_TEXT_PAGE "${TOG_CUSTOMPAGE_STARTOPTION_TITLE}" "${TOG_CUSTOMPAGE_STARTOPTION_SUBTITLE}"

    ${If} ${EDIT_STARTMENU} == ""
        StrCpy ${EDIT_STARTMENU} "${APP_NAME}"
    ${EndIf}

    ;(1) Create a Together startoption dialog page
    nsDialogs::Create 1018
    Pop $tog.StartOptionCustomPage
    GetDlgItem $tog.StartOptionCustomPage.Button.Next $HWNDPARENT 1

    ;(2) Create a text control
    StrCpy $1 "${TOG_CUSTOMPAGE_STARTOPTION_TEXT}"
    System::Call user32::GetWindowText(i$tog.StartOptionCustomPage.Button.Next,t.s,i${NSIS_MAX_STRLEN})
    Pop $0
    ${If} "&Next >" == $0
        StrCpy $1 "$1 ${TOG_CUSTOMPAGE_STARTOPTION_TEXT_NEXT}"
    ${ElseIf} "&Install" == $0
        StrCpy $1 "$1 ${TOG_CUSTOMPAGE_STARTOPTION_TEXT_INSTALL}"
    ${EndIf}
    nsDialogs::CreateControl STATIC ${WS_VISIBLE}|${WS_CHILD}|${WS_CLIPSIBLINGS} 0 0u 0u 100% 27u "$1"
    Pop $tog.StartOptionCustomPage.Text

    ;(3) Create a checkbox control for "Create Start menu entry"
    ${NSD_CreateCheckBox} 0u 40u 100u 12u "Create Start Menu entry:"
    Pop $tog.StartOptionCustomPage.Check.StartMenu
    ${NSD_SetState} $tog.StartOptionCustomPage.Check.StartMenu ${CHECK_STARTMENU}
    ${NSD_OnClick} $tog.StartOptionCustomPage.Check.StartMenu OnClickCheckBox_StartMenu

    ;(4) Create a edit control for "Start menu entry"
    ${NSD_CreateText} 100u 40u 180u 12u "${EDIT_STARTMENU}"
    Pop $tog.StartOptionCustomPage.Edit.StartMenu
    ${NSD_OnChange} $tog.StartOptionCustomPage.Edit.StartMenu OnChangeEdit_StartMenu

    ;(5) Create a checkbox control for "Create Shortcut on Desktop"
    ${NSD_CreateCheckBox} 0u 54u 100u 12u "Create Shortcut on Desktop"
    Pop $tog.StartOptionCustomPage.Check.Shortcut
	${If} ${ENABLE_SHORTCUT} == "on"
		${NSD_SetState} $tog.StartOptionCustomPage.Check.Shortcut ${CHECK_SHORTCUT}
		${NSD_OnClick}  $tog.StartOptionCustomPage.Check.Shortcut OnClickCheckBox_Shortcut
	${Else}
		${NSD_SetState} $tog.StartOptionCustomPage.Check.Shortcut 0
		EnableWindow 	$tog.StartOptionCustomPage.Check.Shortcut 0
	${EndIf}

    ;(6) Create a checkbox control for "Create Quick Launch"
    ${NSD_CreateCheckBox} 0u 68u 100u 12u "Create Quick Launch"
    Pop $tog.StartOptionCustomPage.Check.QuickLaunch
	${If} ${ENABLE_QUICKLAUNCH} == "on"
		${NSD_SetState} $tog.StartOptionCustomPage.Check.QuickLaunch ${CHECK_QUICKLAUNCH}
		${NSD_OnClick}  $tog.StartOptionCustomPage.Check.QuickLaunch OnClickCheckBox_QuickLaunch
	${Else}
		${NSD_SetState} $tog.StartOptionCustomPage.Check.QuickLaunch 0
		EnableWindow 	$tog.StartOptionCustomPage.Check.QuickLaunch 0
	${EndIf}
	
    ;(7) Create a checkbox control for "Create Pin to taskbar"
    ${NSD_CreateCheckBox} 0u 82u 100u 12u "Create Pin to Taskbar"
    Pop $tog.StartOptionCustomPage.Check.PintoTaskbar
	${If} ${ENABLE_PINTOTASKBAR} == "on"
		${NSD_SetState} $tog.StartOptionCustomPage.Check.PintoTaskbar ${CHECK_PINTOTASKBAR}
		${NSD_OnClick}  $tog.StartOptionCustomPage.Check.PintoTaskbar OnClickCheckBox_PintoTaskbar
	${Else}
		${NSD_SetState} $tog.StartOptionCustomPage.Check.PintoTaskbar 0
		EnableWindow 	$tog.StartOptionCustomPage.Check.PintoTaskbar 0
	${EndIf}
	
    Call UpdateEdit_StartMenu

    # Insert show-custom function
    !insertmacro TOG_CUSTOMPAGE_FUNCTION_CUSTOM SHOW
    nsDialogs::Show
    Pop $1
    Pop $0
FunctionEnd

Function leave_page_startoption
    # Insert leave-custom function
    !insertmacro TOG_CUSTOMPAGE_FUNCTION_CUSTOM LEAVE
FunctionEnd

Function OnChangeEdit_StartMenu
    Pop $0
    System::Call 'user32::GetWindowText(i$tog.StartOptionCustomPage.Edit.StartMenu, t.r0, i${NSIS_MAX_STRLEN})'
    ${If} $0 == ""
        StrCpy ${EDIT_STARTMENU} "${APP_NAME}"
    ${Else}
        StrCpy ${EDIT_STARTMENU} "$0"
    ${EndIf}
FunctionEnd

Function UpdateEdit_StartMenu
    ${If} ${CHECK_STARTMENU} == "${BST_CHECKED}"
		StrCpy ${EDIT_STARTMENU} "${APP_NAME}"
		system::Call 'user32::SetWindowText(i$tog.StartOptionCustomPage.Edit.StartMenu, t"${APP_NAME}")'
        EnableWindow $tog.StartOptionCustomPage.Edit.StartMenu 1
    ${Else}
		system::Call 'user32::SetWindowText(i$tog.StartOptionCustomPage.Edit.StartMenu, t"")'
        EnableWindow $tog.StartOptionCustomPage.Edit.StartMenu 0
    ${EndIf}
FunctionEnd

!insertmacro OnClickCheckBoxOnStartOptionPage "STARTMENU"
!insertmacro OnClickCheckBoxOnStartOptionPage "SHORTCUT"
!insertmacro OnClickCheckBoxOnStartOptionPage "QUICKLAUNCH"
!insertmacro OnClickCheckBoxOnStartOptionPage "PINTOTASKBAR"
!macroend

!macro OnClickCheckBoxOnStartOptionPage Option
Function OnClickCheckBox_${Option}
    ${If} ${CHECK_${Option}} == "${BST_CHECKED}"
        StrCpy ${CHECK_${Option}} "${BST_UNCHECKED}"
    ${Else}
        StrCpy ${CHECK_${Option}} "${BST_CHECKED}"
    ${EndIf}
    ${NSD_SetState} $tog.StartOptionCustomPage.Check.${Option} ${CHECK_${Option}}
    Call UpdateEdit_StartMenu
FunctionEnd
!macroend
!endif ;TOGETHER_CUSTOMPAGE_STARTOPTION