;    Together Filesize Lister 
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
;----------------------------------------------------------------------------------
; readregistry.nsi
;
; Read registry value
;-------------------------------------------------------------------------------
; The name of the installer (not really used in a silent install)
Name "ReadRegistry"

; Set to silent mode
SilentInstall silent
; The file to write
OutFile "readregistry.exe"
;-------------------------------------------------------------------------------
; The stuff to install
Section ""
	SetRegView 64
	ReadRegStr $1 HKLM "SOFTWARE\JavaSoft\Java Development Kit" "CurrentVersion"
	ReadRegStr $2 HKLM "SOFTWARE\JavaSoft\Java Development Kit\$1" "JavaHome"
	StrCmp $2 "" tryWith32  procede
	
tryWith32:	
	SetRegView 32
	ReadRegStr $1 HKLM "SOFTWARE\JavaSoft\Java Development Kit" "CurrentVersion"
	ReadRegStr $2 HKLM "SOFTWARE\JavaSoft\Java Development Kit\$1" "JavaHome"
	StrCmp $2 "" continue
	
procede:
	StrCpy $3 $1 1 ; major version	
	StrCpy $4 $1 1 2 ; minor version	
	IntFmt $5 "%u" $3
	IntFmt $6 "%u" $4    
	IntCmp $6 4 0 continue

	IfFileExists $2\bin\javac.exe "" continue	; check if javac.exe exist
	IfFileExists $2\jre\lib\jsse.jar OK	; check if jsse.jar exist
	
	continue:
  ReadEnvStr $2 "JAVA_HOME"
  StrCmp $2 "" error
  
	OK:
  Push "$2"
  Push "\"
  Push "/"
	Call ReplaceChar
	Pop $1
	Delete "instdir.txt"
  FileOpen $9 instdir.txt "w"
	FileWrite $9 "jdk.dir=$1"
	FileClose $9 
	Goto end
	
	error:
	MessageBox MB_OK "Default value of property variable jdk.dir cannot be set. Set this variable from command line with syntah: -jdkhome jdk_home_dir"
	Abort
	
	end:
	ExecWait "SET JDKHOME=$2"
SectionEnd
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
  Exch  	; stack="\","2-ti param","C:\JBuilder\jdk1.3.1", ...
  Exch $1 ; $1="\", stack="1-vi param","2-gi param","C:\JBuilder\jdk1.3.1", ...
  Exch    ; stack="2-gi param","1-vi param","C:\JBuilder\jdk1.3.1", ...
	Exch 2	; stack="C:\JBuilder\jdk1.3.1","1-vi param","2-gi param", ...
  Exch $0 ; $0="C:\JBuilder\jdk1.3.1", stack="0-ti param","1-vi param","2-gi param", ...
  Exch 2  ; stack="2-gi param","1-vi param","0-ti param", ...

  Push $3	; Len("C:\JBuilder\jdk1.3.1")
  Push $4	; index of string
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
  	StrCpy $6 $4				; pozition where found char witch need to change
  	StrCpy $5 $0 $6   	; $5="C:"
  	StrCpy $5 "$5$2"   	; $5="C:/"
  	StrCpy $7 $4				; pozition where found char witch need to change
  	IntOp $7 $7 + 1 		; next pozition
  	StrCpy $6 $0 $3 $7  ; $6="JBuilder\jdk1.3.1"
  	StrCpy $0 $5				; $0="C:/"
  	StrCpy $0 "$0$6" 		; $0="C:/JBuilder\jdk1.3.1"
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
