::Copyright (C) 2011 Together Teamsolutions Co., Ltd. 
::
::This program is free software: you can redistribute it and/or modify
::it under the terms of the GNU General Public License as published by
::the Free Software Foundation, either version 3 of the License, or
::(at your option) any later version.
::
::This program is distributed in the hope that it will be useful,
::but WITHOUT ANY WARRANTY; without even the implied warranty of
::MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
::GNU General Public License for more details.
::
::You should have received a copy of the GNU General Public License
::along with this program. If not, see http://www.gnu.org/licenses
::
@echo off
rem *********************************************
rem *  Initialize environment variables
rem *********************************************
rem **** initail data ****
SET APP_NAME=X
SET APP_FULL_NAME=X

SET SET_VERSION=off
SET SET_RELEASE=off
SET SET_BUILDID=off
SET SET_JDKHOME=off
SET SET_INSTALLDIR=off
SET SET_REBRANDING=off
SET SET_BRANDINGDIR=off
SET SET_LANGUAGE=off
::SET SET_APPNAME=off
SET SET_PROJECTNAME=off

SET BUILDID=
SET SET_APP_NAME=off
SET SET_APP_FULL_NAME=off
SET INSTALLDIR=
SET REBRANDING=false
SET BRANDINGDIR=
SET LANGUAGE=English
::SET APPNAME=twe
::SET PROJECTNAME=Together Workflow Editor

rem ***** init version from version.properties file
find "version=" < version.properties > version.txt
for /F "tokens=1,2* delims==" %%i in (version.txt) do SET VERSION=%%j
del version.txt>nul

rem ***** init release from version.properties file
find "release=" < version.properties > release.txt
for /F "tokens=1,2* delims==" %%i in (release.txt) do SET RELEASE=%%j
del release.txt>nul


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
if %~1==-buildid goto buildid
if %~1==-jdkhome goto jdkhome
if %~1==-instdir goto instdir
if %~1==-rebranding goto rebranding
if %~1==-brandingdir goto brandingdir
if %~1==-language goto language
if %~1==-appname goto appname
::if %~1==-projectname goto projectname
if %~1==-appfullname goto appfullname
goto error

:default

call .\tools\trr\readregistry.exe
if errorlevel==1 goto end
for /F "tokens=1,2* delims==" %%i in (instdir.txt) do SET JDKHOME=%%j
del instdir.txt>nul

goto start

:init
find "jdk.dir=" < build.properties > javadir.txt
for /F "tokens=1,2* delims==" %%i in (javadir.txt) do SET JDKHOME=%%j
del javadir.txt>nul
if "X%JDKHOME%"=="X" goto initjava
goto initbuildid

:initjava
call .\tools\trr\readregistry.exe
if errorlevel==1 goto end
for /F "tokens=1,2* delims==" %%i in (instdir.txt) do SET JDKHOME=%%j
del instdir.txt>nul

:initbuildid
find "buildid=" < build.properties > buildid.txt
for /F "tokens=1,2* delims==" %%i in (buildid.txt) do SET BUILDID=%%j
del buildid.txt>nul

:initinstall
find "install.dir=" < build.properties > install.txt
for /F "tokens=1,2* delims==" %%i in (install.txt) do SET INSTALLDIR=%%j
del install.txt>nul

:initrebranding
if exist rebranding.properties (
find "rebranding=" < rebranding.properties > rebranding.txt
for /F "tokens=1,2* delims==" %%i in (rebranding.txt) do SET REBRANDING=%%j
del rebranding.txt>nul
)

:initbrandingdir
if exist rebranding.properties (
find "branding.dir=" < rebranding.properties > brandingdir.txt
for /F "tokens=1,2* delims==" %%i in (brandingdir.txt) do SET BRANDINGDIR=%%j
del brandingdir.txt>nul
)

:initlanguage
if exist rebranding.properties (
find "language=" < rebranding.properties > language.txt
for /F "tokens=1,2* delims==" %%i in (language.txt) do SET LANGUAGE=%%j
del language.txt>nul
)

:initappname
find "app.name=" < project.properties > appname.txt
for /F "tokens=1,2* delims==" %%i in (appname.txt) do SET APP_NAME=%%j
del appname.txt>nul

:initprojectname
find "app.full.name=" < project.properties > projectname.txt
for /F "tokens=1,2* delims==" %%i in (projectname.txt) do SET APP_FULL_NAME=%%j
del projectname.txt>nul

goto start

rem *********************************************************
rem *  Edit parameters (build.properties)
rem *********************************************************
:make
if exist build.properties del build.properties
echo buildid=%BUILDID%>build.properties
echo jdk.dir=^%JDKHOME%>>build.properties
echo install.dir=%INSTALLDIR%>>build.properties

if exist rebranding.properties del rebranding.properties
if %REBRANDING%==true (
   echo rebranding=%REBRANDING%>>rebranding.properties
   echo branding.dir=%BRANDINGDIR%>>rebranding.properties
   echo language=%LANGUAGE%>>rebranding.properties
)
::echo app.name=%APPNAME%>>build.properties
::echo project.name=%PROJECTNAME%>>build.properties

if %SET_VERSION%==off if %SET_RELEASE%==off goto makeProjectProprties
if exist version.properties del version.properties
echo #####################>version.properties
echo version=^%VERSION%>>version.properties
echo release=^%RELEASE%>>version.properties
echo #####################>>version.properties

:makeProjectProprties
if %SET_APP_NAME%==off if %SET_APP_FULL_NAME%==off goto end
if exist project.properties del project.properties
echo # Copyright (C) 2011 Together Teamsolutions Co., Ltd. >project.properties
echo # >>project.properties
echo # This program is free software: you can redistribute it and/or modify >>project.properties
echo # it under the terms of the GNU General Public License as published by >>project.properties
echo # the Free Software Foundation, either version 3 of the License, or >>project.properties
echo # (at your option) any later version. >>project.properties
echo # >>project.properties
echo # This program is distributed in the hope that it will be useful, >>project.properties
echo # but WITHOUT ANY WARRANTY; without even the implied warranty of >>project.properties
echo # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the >>project.properties
echo # GNU General Public License for more details. >>project.properties
echo # >>project.properties
echo # You should have received a copy of the GNU General Public License >>project.properties
echo # along with this program. If not, see http://www.gnu.org/licenses >>project.properties
echo # >>project.properties
echo app.name=^%APP_NAME%>>project.properties
echo app.full.name=^%APP_FULL_NAME%>>project.properties

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
echo configure              - Creates build.properties file with default values for all possible parameters.
echo                          It can work only if there is a default JAVA registered with the system.
echo configure -help        - Displays Help screen
echo configure -appname     - Sets the short name for the project.
echo configure -appfullname - Sets the full name for the project.
echo configure -version     - Sets the version number for the project.
echo configure -release     - Sets the release number for the project.
echo configure -buildid     - Sets the build id for the project.
echo configure -jdkhome     - Sets the "JAVA HOME" location of Java to be used to compile the project.
echo configure -instdir     - Sets the location of the installation dir used when executing make script 
echo                          with install target specified.
echo configure -rebranding  - Flag that determines if the project will be "rebranded" with the context of branding folder.
echo                          Possible values [true/false].
echo configure -brandingdir - Sets the location of the branding folder used when re-branding application.
echo configure -language    - Used by NSIS when creating setup (normally used for rebranding). 
echo                          Currently possible values [English/Portuguese/PortugueseBR].
echo.
echo Multiple parameters can be specified at once.
echo.
echo.
echo Example:
echo.
echo configure -version %VERSION% -release %RELEASE% -buildid 20120801-0808 -jdkhome C:/jdk1.8 -instdir C:/JaWE
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
rem *  Set BUILDID parameter value
rem *********************************************************
:buildid
if %SET_BUILDID%==on goto error
shift
if "X%~1"=="X" goto error
SET BUILDID=%~1
SET SET_BUILDID=on
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
rem *  Set BRANDINGDIR parameter value
rem *********************************************************
:brandingdir
if %SET_BRANDINGDIR%==on goto error
shift
if "X%~1"=="X" goto error
SET BRANDINGDIR=%~1
SET SET_BRANDINGDIR=on
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
rem *  Set APPNAME parameter value
rem *********************************************************
:appname
if %SET_APP_NAME%==on goto error
shift
if "X%~1"=="X" goto error
SET APP_NAME=%~1
SET SET_APP_NAME=on
shift
if "X%~1"=="X" goto make
goto start

rem *********************************************************
rem *  Set APP_FULL_NAME parameter value
rem *********************************************************
:appfullname
if %SET_APP_FULL_NAME%==on goto error
shift
if "X%~1"=="X" goto error
SET APP_FULL_NAME=%~1
SET SET_APP_FULL_NAME=on
shift
if "X%~1"=="X" goto make
goto start

rem *********************************************************
rem *  Set PROJECTNAME parameter value
rem *********************************************************
:projectname
if %SET_PROJECTNAME%==on goto error
shift
if "X%~1"=="X" goto error
SET PROJECTNAME=%~1
SET SET_PROJECTNAME=on
shift
if "X%~1"=="X" goto make
goto start

rem *********************************************************
rem *  Reset evironment variables
rem *********************************************************
:end
SET VERSION=
SET RELEASE=
SET BUILDID=
SET JDKHOME=
SET INSTALLDIR=
SET REBRANDING=
SET BRANDINGDIR=
SET LANGUAGE=
SET APPNAME=
SET PROJECTNAME=
