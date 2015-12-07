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
!include nsDialogs.nsh
!include FileFunc.nsh
!include LogicLib.nsh

!ifndef TOG_CUSTOMPAGE_FUNCTION_CUSTOM
!define TOG_CUSTOMPAGE_FUNCTION_CUSTOM
!macro TOG_CUSTOMPAGE_FUNCTION_CUSTOM TYPE
    !ifdef TOG_CUSTOMPAGE_CUSTOMFUNCTION_${TYPE}
        Call "${TOG_CUSTOMPAGE_CUSTOMFUNCTION_${TYPE}}"
        !undef TOG_CUSTOMPAGE_CUSTOMFUNCTION_${TYPE}
    !endif
!macroend
!endif ;TOG_CUSTOMPAGE_FUNCTION_CUSTOM
