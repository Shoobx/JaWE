;--------------------------------------------------------------------------  
;--- Add/Remove system macros: ---
;--------------------------------------------------------------------------  

Var AR_SecFlags
Var AR_RegFlags

;--------------------------------------------------------------------------  
;This macro reads component installed flag from the registry and
;changes checked state of the section on the components page.
;Input: section index constant name specified in Section command.
;--------------------------------------------------------------------------  
!macro InitSection SecName

  ClearErrors
  ;Reading component status from registry
  ReadRegDWORD $AR_RegFlags HKLM \
    "${REG_UNINSTALL}\Components\${SecName}" "Installed"
  IfErrors "default_${SecName}"
    ;Status will stay default if registry value not found
    ;(component was never installed)
  IntOp $AR_RegFlags $AR_RegFlags & 0x0001  ;Turn off all other bits
  SectionGetFlags ${${SecName}} $AR_SecFlags  ;Reading default section flags
  IntOp $AR_SecFlags $AR_SecFlags & 0xFFFE  ;Turn lowest (enabled) bit off
  IntOp $AR_SecFlags $AR_RegFlags | $AR_SecFlags      ;Change lowest bit
  ;Writing modified flags
  SectionSetFlags ${${SecName}} $AR_SecFlags
  Goto "end_${SecName}"	
  
 "default_${SecName}:"
  ClearErrors
  StrCpy $AR_RegFlags ""
  ReadRegStr $AR_RegFlags HKLM "${REG_UNINSTALL}" "DisplayName"
  IfErrors "end_${SecName}"
  SectionGetFlags ${${SecName}} $AR_SecFlags
  IntOp $AR_SecFlags $AR_SecFlags & 0xFFFE  ;Turn lowest (enabled) bit off
  SectionSetFlags ${${SecName}} $AR_SecFlags

 "end_${SecName}:"
!macroend
;--------------------------------------------------------------------------  
;This macro reads section flag set by user and removes the section
;if it is not selected.
;Then it writes component installed flag to registry
;Input: section index constant name specified in Section command.
;--------------------------------------------------------------------------  
!macro FinishSection SecName

#	DetailPrint "FinishSection-${SecName}-start-${${SecName}}"
#	StrCmp ${SecName} "Conductor" 0 "start_${SecName}"
#	MessageBox MB_OK "usao"
#  SectionGetFlags ${${SecName}} $AR_SecFlags  ;Reading section flags
#  ;Checking lowest bit:
#  IntOp $AR_SecFlags $AR_SecFlags & 0x0001
#	MessageBox MB_OK "usao=$AR_SecFlags"
#	
# "start_${SecName}:"

  SectionGetFlags ${${SecName}} $AR_SecFlags  ;Reading section flags
  ;Checking lowest bit:
  IntOp $AR_SecFlags $AR_SecFlags & 0x0001
  IntCmp $AR_SecFlags 1 "leave_${SecName}"

  ;Reading component status from registry - vladan
  ClearErrors
  ReadRegDWORD $AR_RegFlags HKLM \
    "${REG_UNINSTALL}\Components\${SecName}" "Installed"
  IfErrors "exit_${SecName}"
	IntCmp $AR_RegFlags 0 "exit_${SecName}"
  ;Reading component status from registry - vladan

    ;Section is not selected:
    ;Calling Section uninstall macro and writing zero installed flag
    !insertmacro "Remove_${${SecName}}"
  	SectionGetFlags ${${SecName}} $AR_SecFlags  ;Reading section flags, check if remove section is aborted
  	IntOp $AR_SecFlags $AR_SecFlags & 0x0001
  	IntCmp $AR_SecFlags 1 "leave_${SecName}"
    WriteRegDWORD HKLM "${REG_UNINSTALL}\Components\${SecName}" \
                  "Installed" 0
    Goto "exit_${SecName}"

 "leave_${SecName}:"
    ;Section is selected:
    WriteRegDWORD HKLM "${REG_UNINSTALL}\Components\${SecName}" \
                  "Installed" 1

 "exit_${SecName}:"
#	DetailPrint "FinishSection-${SecName}-end"
!macroend
;--------------------------------------------------------------------------  
;This macro is used to call section's Remove_... macro
;from the uninstaller.
;Input: section index constant name specified in Section command.
;--------------------------------------------------------------------------  
!macro RemoveSection SecName

  !insertmacro "Remove_${${SecName}}"

!macroend
;--------------------------------------------------------------------------  
;--- End of Add/Remove macros ---
;--------------------------------------------------------------------------  
; selected subsection full or partly
!define SUB_SECTION_ON   0x00000041
;====================================================
; StrStr - Finds a given string in another given string.
;               Returns -1 if not found and the pos if found.
;          Input: head of the stack - string to find
;                      second in the stack - string to find in
;          Output: head of the stack
;====================================================
!macro StrStr label
  Push $0
  Exch
  Pop $0 ; $0 now have the string to find
  Push $1
  Exch 2
  Pop $1 ; $1 now have the string to find in
  Exch
  Push $2
  Push $3
  Push $4
  Push $5

  StrCpy $2 -1
  StrLen $3 $0
  StrLen $4 $1
  IntOp $4 $4 - $3

  unStrStr_loop_${label}:
    IntOp $2 $2 + 1
    IntCmp $2 $4 0 0 unStrStrReturn_notFound_${label}
    StrCpy $5 $1 $3 $2
    StrCmp $5 $0 unStrStr_done_${label} unStrStr_loop_${label}

  unStrStrReturn_notFound_${label}:
    StrCpy $2 -1

  unStrStr_done_${label}:
    Pop $5
    Pop $4
    Pop $3
    Exch $2
    Exch 2
    Pop $0
    Pop $1
!macroend
;====================================================
; RemoveFromPath - Remove a given dir from the path
;     Input: head of the stack
;====================================================
!macro RemoveFromPath label
  Exch $0
  Push $1
  Push $2
  Push $3
  Push $4
  
    StrLen $2 $0
    ReadRegStr $1 HKLM "SYSTEM\CurrentControlSet\Control\Session Manager\Environment" "PATH"
    Push $1
    Push $0
    !insertmacro StrStr ${label} ; Find $0 in $1
    Pop $0 ; pos of our dir
    IntCmp $0 -1 unRemoveFromPath_done_${label}
      ; else, it is in path
      StrCpy $3 $1 $0 ; $3 now has the part of the path before our dir
      IntOp $2 $2 + $0 ; $2 now contains the pos after our dir in the path (';')
      IntOp $2 $2 + 1 ; $2 now containts the pos after our dir and the semicolon.
      StrLen $0 $1
      StrCpy $1 $1 $0 $2
      StrCpy $3 "$3$1"

      WriteRegExpandStr HKLM "SYSTEM\CurrentControlSet\Control\Session Manager\Environment" "PATH" $3
      SendMessage ${HWND_BROADCAST} ${WM_WININICHANGE} 0 "STR:Environment" /TIMEOUT=5000
  
  unRemoveFromPath_done_${label}:
    Pop $4
    Pop $3
    Pop $2
    Pop $1
    Pop $0
!macroend
