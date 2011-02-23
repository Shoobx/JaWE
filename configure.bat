@echo off
rem #    Together Workflow Editor
rem #    Copyright (C) 2010 Together Teamsolutions Co., Ltd.
rem #
rem #    This program is free software: you can redistribute it and/or modify
rem #    it under the terms of the GNU General Public License as published by
rem #    the Free Software Foundation, either version 3 of the License, or 
rem #    (at your option) any later version.
rem #
rem #    This program is distributed in the hope that it will be useful, 
rem #    but WITHOUT ANY WARRANTY; without even the implied warranty of
rem #    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
rem #    GNU General Public License for more details.
rem # 
rem #    You should have received a copy of the GNU General Public License
rem #    along with this program. If not, see http://www.gnu.org/licenses
rem #-----------------------------------------------------------------------
cls
rem *********************************************
rem *  Initialize environment variables
rem *********************************************

SET SET_VERSION=off
SET SET_RELEASE=off
SET SET_JDKHOME=off
SET SET_INSTALLDIR=off
SET SET_REBRANDING=off
SET SET_LANGUAGE=off

SET VERSION=4.0
SET RELEASE=1
SET INSTALLDIR=
SET REBRANDING=false
SET LANGUAGE=English


if %~1.==. goto skiphelp
if %~1==-help goto help

:skiphelp
if exist build.properties goto init
goto default

rem *********************************************
rem *  Set properties values from user input
rem *********************************************
:start
if %~1.==. goto make
if %~1==-version goto version
if %~1==-release goto release
if %~1==-jdkhome goto jdkhome
if %~1==-instdir goto instdir
if %~1==-rebranding goto rebranding
if %~1==-language goto language
goto error

:default

call .\util\make\readregistry.exe
if errorlevel==1 goto end
for /F "tokens=1,2* delims==" %%i in (instdir.txt) do SET JDKHOME=%%j
del instdir.txt>nul

goto start

:init
find "jdk.dir=" < build.properties > javadir.txt
for /F "tokens=1,2* delims==" %%i in (javadir.txt) do SET JDKHOME=%%j
del javadir.txt>nul
if "X%JDKHOME%"=="X" goto initjava
goto initversion

:initjava
call .\util\make\readregistry.exe
if errorlevel==1 goto end
for /F "tokens=1,2* delims==" %%i in (instdir.txt) do SET JDKHOME=%%j
del instdir.txt>nul

:initversion
find "version=" < build.properties > version.txt
for /F "tokens=1,2* delims==" %%i in (version.txt) do SET VERSION=%%j
del version.txt>nul

:initrelease
find "release=" < build.properties > release.txt
for /F "tokens=1,2* delims==" %%i in (release.txt) do SET RELEASE=%%j
del release.txt>nul

:initinstall
find "install.dir=" < build.properties > install.txt
for /F "tokens=1,2* delims==" %%i in (install.txt) do SET INSTALLDIR=%%j
del install.txt>nul

:initrebranding
find "rebranding=" < build.properties > rebranding.txt
for /F "tokens=1,2* delims==" %%i in (rebranding.txt) do SET REBRANDING=%%j
del rebranding.txt>nul

:initlanguage
find "language=" < build.properties > language.txt
for /F "tokens=1,2* delims==" %%i in (language.txt) do SET LANGUAGE=%%j
del language.txt>nul

goto start

rem *********************************************************
rem *  Edit parameters (build.properties)
rem *********************************************************
:make
if exist build.properties del build.properties
echo version=^%VERSION%>build.properties
echo release=^%RELEASE%>>build.properties
echo jdk.dir=^%JDKHOME%>>build.properties
echo install.dir=%INSTALLDIR%>>build.properties
echo rebranding=%REBRANDING%>>build.properties
echo language=%LANGUAGE%>>build.properties
goto end


rem *********************************************************
rem *  Display ERROR message
rem *********************************************************
:error
echo.
echo Invalid option(s) used with configure.bat
echo.


rem *********************************************************
rem *  Display HELP message
rem *********************************************************
:help
echo.
echo Possible parameters for configure script:
echo.
echo configure             - Creates build.properties file with default values for all possible parameters.
echo                         It can work only if there is a default JAVA registered with the system.
echo configure -help       - Displays Help screen
echo configure -version    - Sets the version number for the project.
echo configure -release    - Sets the release number for the project.
echo configure -jdkhome    - Sets the "JAVA HOME" location of Java to be used to compile the project.
echo configure -instdir    - Sets the location of the installation dir used when executing make script 
echo                         with install target specified.
echo configure -rebranding - Flag that determines if the project will be "rebranded" with the context of branding folder.
echo                         Possible values [true/false].
echo configure -language   - Used by NSIS when creating setup (normally used for rebranding). 
echo                         Currently possible values [English/Portuguese/PortugueseBR].
echo.
echo Multiple parameters can be specified at ones.
echo.
echo.
echo Example:
echo.
echo configure -version 3.3 -release 1 -jdkhome C:/jdk1.6 -instdir C:/JaWE
echo.
goto end


rem *********************************************************
rem *  Set VERSION parameter value
rem *********************************************************
:version
if %SET_VERSION%==on goto error
shift
if "X%~1"=="X" goto error
SET VERSION=%~1
SET SET_VERSION=on
shift
if "X%~1"=="X" goto make
goto start


rem *********************************************************
rem *  Set RELEASE parameter value
rem *********************************************************
:release
if %SET_RELEASE%==on goto error
shift
if "X%~1"=="X" goto error
SET RELEASE=%~1
SET SET_RELEASE=on
shift
if "X%~1"=="X" goto make
goto start


rem *********************************************************
rem *  Set JDKHOME parameter value
rem *********************************************************
:jdkhome
if %SET_JDKHOME%==on goto error
shift
if "X%~1"=="X" goto error
SET JDKHOME=%~f1
SET SET_JDKHOME=on
shift
if "X%~1"=="X" goto make
goto start


rem *********************************************************
rem *  Set INSTDIR parameter value
rem *********************************************************
:instdir
if %SET_INSTALLDIR%==on goto error
shift
if "X%~1"=="X" goto error
SET INSTALLDIR=%~f1
SET SET_INSTALLDIR=on
shift
if "X%~1"=="X" goto make
goto start

rem *********************************************************
rem *  Set REBRANDING parameter value
rem *********************************************************
:rebranding
if %SET_REBRANDING%==on goto error
shift
if "X%~1"=="X" goto error
SET REBRANDING=%~1
SET SET_REBRANDING=on
shift
if "X%~1"=="X" goto make
goto start

rem *********************************************************
rem *  Set LANGUAGE parameter value
rem *********************************************************
:language
if %SET_LANGUAGE%==on goto error
shift
if "X%~1"=="X" goto error
SET LANGUAGE=%~1
SET SET_LANGUAGE=on
shift
if "X%~1"=="X" goto make
goto start

rem *********************************************************
rem *  Reset evironment variables
rem *********************************************************
:end
SET VERSION=
SET RELEASE=
SET JDKHOME=
SET INSTALLDIR=
SET REBRANDING=
SET LANGUAGE=