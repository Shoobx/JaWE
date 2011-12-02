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
!ifndef TOGETHER_CUSTOMPAGE_DIRECTORY
!define TOGETHER_CUSTOMPAGE_DIRECTORY

!include tog.nsh
!include nsDialogs.nsh
!include FileFunc.nsh
!include LogicLib.nsh

!define TOG_CUSTOMPAGE_DIRECTORY_TEXT "Setup will install ${APP_NAME} in the following folder. To install in a different folder, click Browse and select another folder. "
!define TOG_CUSTOMPAGE_DIRECTORY_TEXT_INSTALL "Click Install to start the installation."
!define TOG_CUSTOMPAGE_DIRECTORY_TEXT_NEXT "Click Next to continue."

; Variables : Controls
Var tog.DirectoryCustomPage
Var tog.DirectoryCustomPage.Text
Var tog.DirectoryCustomPage.DirectoryBox
Var tog.DirectoryCustomPage.Directory
Var tog.DirectoryCustomPage.BrowseButton
Var tog.DirectoryCustomPage.DefaultButton
Var tog.DirectoryCustomPage.RequiredSpace
Var tog.DirectoryCustomPage.AvailableSpace
Var tog.DirectoryCustomPage.Button.Next
; Space Info
Var tog.Space.Required
Var tog.Space.Available
Var tog.Space.Mega
; Drive Check
Var tog.Drive.Found

; Define standard directory page; we use its title and subtitle
!define MUI_DIRECTORYPAGE
; Define for auto-complete edit control
!define SHACF_FILESYSTEM 1

!macro TOG_CUSTOMPAGE_DIRECTORY APP_NAME DEFAULT_DIR
Page custom create_page_directory leave_page_directory

Function create_page_directory
    # Insert pre-custom function
    !insertmacro TOG_CUSTOMPAGE_FUNCTION_CUSTOM PRE
    Push $0
    Push $1

    ;(0) Create header title & header subtitle
    ;    Both use the info from standard MUI_DIRECTORYPAGE
    !insertmacro MUI_HEADER_TEXT_PAGE $(MUI_TEXT_DIRECTORY_TITLE) $(MUI_TEXT_DIRECTORY_SUBTITLE)

    ;(1) Create a Together directory dialog page
    nsDialogs::Create 1018
    Pop $tog.DirectoryCustomPage
    GetDlgItem $tog.DirectoryCustomPage.Button.Next $HWNDPARENT 1

    ;(2) Check whether middle Button is "Next >" or "Install"
    System::Call user32::GetWindowText(i$tog.DirectoryCustomPage.Button.Next,t.s,i${NSIS_MAX_STRLEN})
    Pop $0
    StrCpy $1 "${TOG_CUSTOMPAGE_DIRECTORY_TEXT}"
    ${If} "&Next >" == $0
        StrCpy $1 "$1 ${TOG_CUSTOMPAGE_DIRECTORY_TEXT_NEXT}"
    ${ElseIf} "&Install" == $0
        StrCpy $1 "$1 ${TOG_CUSTOMPAGE_DIRECTORY_TEXT_INSTALL}"
    ${EndIf}

    ;(3) Create a text control
    nsDialogs::CreateControl STATIC ${WS_VISIBLE}|${WS_CHILD}|${WS_CLIPSIBLINGS} 0 0u 0u 100% 20u "$1"
    Pop $tog.DirectoryCustomPage.Text

    ;(4) Create a groupbox control
    ${NSD_CreateGroupBox} 0u 52u 100% 50u "Destination Folder"
    Pop $tog.DirectoryCustomPage.DirectoryBox

    ;(5) Create a edit control (NOT FINISH YET: MUST ASSIGN MESSAGE TO DISABLE 'NEXT' BUTTON UNTIL GET THE EXISTING FOLDER)****************
    ${NSD_CreateDirRequest} 10u 66u 70% 12u "$INSTDIR"
    Pop $tog.DirectoryCustomPage.Directory
    ${NSD_OnChange} $tog.DirectoryCustomPage.Directory OnChange_DirRequest
    System::Call shlwapi::SHAutoComplete(i$tog.DirectoryCustomPage.Directory,i${SHACF_FILESYSTEM})

    ;(6) Create a 'Browse' button control
    ${NSD_CreateBrowseButton} 228u 65u 60u 14u "Browse..."
    Pop $tog.DirectoryCustomPage.BrowseButton
    ${NSD_OnClick} $tog.DirectoryCustomPage.BrowseButton OnClick_BrowseButton_Directory

    ;(7) Create 'SpaceRequired' label
    Call GetInstalledSize ; SectionGetSize ${INSTALL_SECTION} $tog.Space.Required
    nsDialogs::CreateControl STATIC ${WS_VISIBLE}|${WS_CHILD}|${WS_CLIPSIBLINGS} 0 0u 115u 75% 10u "Space required:"
    Pop $tog.DirectoryCustomPage.RequiredSpace

    ;(8) Create 'SpaceAvailable' label
    nsDialogs::CreateControl STATIC ${WS_VISIBLE}|${WS_CHILD}|${WS_CLIPSIBLINGS} 0 0u 125u 75% 10u "Space available:"
    Pop $tog.DirectoryCustomPage.AvailableSpace

    ;(9) Create 'Default' button control
    ${NSD_CreateButton} 228u 80u 60u 14u "Default"
    Pop $tog.DirectoryCustomPage.DefaultButton
    ${NSD_OnClick} $tog.DirectoryCustomPage.DefaultButton OnClick_Default

    # Insert show-custom function
    !insertmacro TOG_CUSTOMPAGE_FUNCTION_CUSTOM SHOW
    Call UpdateInstallSpace
    Call UpdateFreeSpace
    Call CheckDrive ; Check existing drives for whether enabling 'Next' Button
    Call CheckSpace ; Check whether the space is available for installation
    nsDialogs::Show
    Pop $1
    Pop $0
FunctionEnd

Function leave_page_directory
    # Insert leave-custom function
    !insertmacro TOG_CUSTOMPAGE_FUNCTION_CUSTOM LEAVE
FunctionEnd

Function OnChange_DirRequest
    Pop $0
    System::Call 'user32::GetWindowText(i$tog.DirectoryCustomPage.Directory, t.r0, i${NSIS_MAX_STRLEN})'
    StrCpy $INSTDIR $0
    Call UpdateFreeSpace
    Call CheckDrive ; Check existing drives for whether enabling 'Next' Button
	${If} $tog.Drive.Found == "yes" 
		Call CheckSpace ; Check whether the space is available for installation
	${EndIf}
FunctionEnd

Function OnClick_Default
    StrCpy $INSTDIR "${DEFAULT_DIR}"
    system::Call 'user32::SetWindowText(i$tog.DirectoryCustomPage.Directory, t"$INSTDIR\")' ;<< Add '\' for illustration (LIFT)
FunctionEnd

Function OnClick_BrowseButton_Directory
    Pop $0
    Push $INSTDIR ; input string "C:\Program Files\ProgramName"
    Call GetParent
    Pop $R0 ; first part "C:\Program Files"

    Push $INSTDIR ; input string "C:\Program Files\ProgramName"
    Push "\" ; input chop char
    Call GetLastPart
    Pop $R1 ; last part "ProgramName"

    nsDialogs::SelectFolderDialog /NOUNLOAD "Select the folder to install $R0 in." $R0
    Pop $0
    ${If} $0 == "error" # returns 'error' if 'cancel' was pressed?
        Return
    ${EndIf}
    ${If} $0 != ""
        ${If} $0 == "C:\"
            StrCpy $INSTDIR "$0$R1"
        ${Else}
            StrCpy $INSTDIR "$0\$R1"
        ${EndIf}
        system::Call 'user32::SetWindowText(i$tog.DirectoryCustomPage.Directory, t"$INSTDIR\")' ;<< Add '\' for illustration (LIFT)
    ${EndIf}
FunctionEnd

Function GetParent
    Exch $R0 ; input string
    Push $R1
    Push $R2
    Push $R3
    StrCpy $R1 0
    StrLen $R2 $R0
loop:
    IntOp $R1 $R1 + 1
    IntCmp $R1 $R2 get 0 get
    StrCpy $R3 $R0 1 -$R1
    StrCmp $R3 "\" get
        Goto loop
get:
    StrCpy $R0 $R0 -$R1
    Pop $R3
    Pop $R2
    Pop $R1
    Exch $R0 ; output string
FunctionEnd

Function GetLastPart
    Exch $0 ; chop char
    Exch
    Exch $1 ; input string
    Push $2
    Push $3
    StrCpy $2 0
loop:
    IntOp $2 $2 - 1
    StrCpy $3 $1 1 $2
    StrCmp $3 "" 0 +3
        StrCpy $0 ""
        Goto exit2
    StrCmp $3 $0 exit1
        Goto loop
exit1:
    IntOp $2 $2 + 1
    StrCpy $0 $1 "" $2
exit2:
    Pop $3
    Pop $2
    Pop $1
    Exch $0 ; output string
FunctionEnd

Function CheckDrive
    ${GetDrives} "HDD" CheckDriveFound
    ${If} $tog.Drive.Found == "no"
        EnableWindow $tog.DirectoryCustomPage.Button.Next 0
        ShowWindow $tog.DirectoryCustomPage.AvailableSpace ${SW_HIDE}
    ${Else}
        EnableWindow $tog.DirectoryCustomPage.Button.Next 1
        ShowWindow $tog.DirectoryCustomPage.AvailableSpace ${SW_NORMAL}
    ${EndIf}
FunctionEnd

Function CheckDriveFound
    StrCpy $tog.Drive.Found "no"
    StrCpy $1 $INSTDIR 3
    StrCmp $1 $9 0 +3
        StrCpy $tog.Drive.Found "yes"
        return
    Push $0 #Search for next drive
FunctionEnd

Function CheckSpace
	System::Int64Op 1024 * 1024
	Pop $tog.Space.Mega
	System::Int64Op $tog.Space.Available / $tog.Space.Mega
	Pop $tog.Space.Available
	System::Int64Op $tog.Space.Required  / $tog.Space.Mega
	Pop $tog.Space.Required
    ${If} $tog.Space.Available < $tog.Space.Required
        MessageBox MB_ICONEXCLAMATION "Space is not enough for installation!"
        EnableWindow $tog.DirectoryCustomPage.Button.Next 0
    ${EndIf}
FunctionEnd

Function GetInstalledSize
    Push $0
    Push $1
    StrCpy $tog.Space.Required 0
    ${ForEach} $1 0 10 + 1
        ${If} ${SectionIsSelected} $1
            SectionGetSize $1 $0
            IntOp $tog.Space.Required $tog.Space.Required + $0
        ${Endif}
    ${Next}
    Pop $1
    Pop $0
    IntOp $tog.Space.Required $tog.Space.Required * 1024
FunctionEnd

Function UpdateInstallSpace
    IntOp $0 $tog.Space.Required + 0
    ${If} $0 > 1024
    ${OrIf} $0 < 0
        System::Int64Op $0 % 1024
        Pop $2
        System::Int64Op $0 / 1024
        Pop $0
        IntOp $2 $2 / 100
        StrCpy $1 "KB"
        ${If} $0 > 1024
        ${OrIf} $0 < 0
            System::Int64Op $0 % 1024
            Pop $2
            System::Int64Op $0 / 1024
            Pop $0
            IntOp $2 $2 / 100
            StrCpy $1 "MB"
            ${If} $0 > 1024
            ${OrIf} $0 < 0
                System::Int64Op $0 % 1024
                Pop $2
                System::Int64Op $0 / 1024
                Pop $0
                IntOp $2 $2 / 100
                StrCpy $1 "GB"
            ${EndIf}
        ${EndIf}
    ${Else}
        System::Int64Op $0 % 1024
        Pop $2
        System::Int64Op $0 / 1024
        Pop $0
        IntOp $2 $2 / 100
        StrCpy $1 "KB"
    ${EndIf}
    
    StrCpy $2 $2 1
    SendMessage $tog.DirectoryCustomPage.RequiredSpace ${WM_SETTEXT} 0 "STR:Space required: $0.$2$1"
FunctionEnd

Function UpdateFreeSpace
    ${GetRoot} $INSTDIR $0
    StrCpy $1 " bytes"

    System::Call kernel32::GetDiskFreeSpaceEx(tr0,*l,*l,*l.r0)
	StrCpy $tog.Space.Available $0
    ${If} $0 > 1024
    ${OrIf} $0 < 0
        System::Int64Op $0 % 1024
        Pop $2
        System::Int64Op $0 / 1024
        Pop $0
        IntOp $2 $2 / 100
        StrCpy $1 "KB"
        ${If} $0 > 1024
        ${OrIf} $0 < 0
            System::Int64Op $0 % 1024
            Pop $2
            System::Int64Op $0 / 1024
            Pop $0
            IntOp $2 $2 / 100
            StrCpy $1 "MB"
            ${If} $0 > 1024
            ${OrIf} $0 < 0
                System::Int64Op $0 % 1024
                Pop $2
                System::Int64Op $0 / 1024
                Pop $0
                IntOp $2 $2 / 100
                StrCpy $1 "GB"
            ${EndIf}
        ${EndIf}
    ${Else}
        System::Int64Op $0 % 1024
        Pop $2
        System::Int64Op $0 / 1024
        Pop $0
        IntOp $2 $2 / 100
        StrCpy $1 "KB"
    ${EndIf}
    
    StrCpy $2 $2 1
    SendMessage $tog.DirectoryCustomPage.AvailableSpace ${WM_SETTEXT} 0 "STR:Space available: $0.$2$1"
FunctionEnd
!macroend
!endif ;TOGETHER_CUSTOMPAGE_DIRECTORY