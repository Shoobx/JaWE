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

SET VERSION=3.3
SET TARGET=buildAll
SET OLD_JAVA_HOME=%JAVA_HOME%
SET JAVA_HOME=

if %SYSTEMROOT% == "" goto errorsystemroot

if not exist build.properties call configure.bat
find "jdk.dir" < build.properties >javahome.txt
for /F "tokens=1,2* delims==" %%i in (javahome.txt) do SET JAVA_HOME=%%j
del javahome.txt>nul
if "X%JAVA_HOME%"=="X" goto errorjava

find "version" < build.properties >version.txt
for /F "tokens=1,2* delims==" %%i in (version.txt) do SET VERSION=%%j
del version.txt>nul

if "X%~1"=="X" goto help
if %~1==help goto help
if %~1==buildAll goto continue
if %~1==buildNoDoc goto continue
if %~1==buildDoc goto continue
if %~1==debug goto continue
if %~1==dependencies goto continue
if %~1==dependency_tws goto continue
if %~1==install goto continue
if %~1==clean goto continue
if %~1==distributions goto continue

goto error

:continue
SET TARGET=%~1

set OLDCLASSPATH=%CLASSPATH%
set OLDPATH=%PATH%
set PATH="%JAVA_HOME%\bin"
set CLASSPATH="%CD%\tools\ant\ant.jar"
set CLASSPATH=%CLASSPATH%;"%CD%\tools\ant\ant-launcher.jar"
set CLASSPATH=%CLASSPATH%;"%CD%\tools\ant\antcontrib.jar"
set CLASSPATH=%CLASSPATH%;"%JAVA_HOME%\lib\tools.jar"

java -cp %CLASSPATH% org.apache.tools.ant.Main -DSYSTEMROOT=%SYSTEMROOT% %TARGET%

set CLASSPATH=%OLDCLASSPATH%
set OLDCLASSPATH=
set PATH=%OLDPATH%
set OLDPATH=
goto end

:help
echo.
echo Parameters value for using with make.bat :
echo.
echo make                     - Displays Help screen
echo make help                - Displays Help screen
echo make buildAll            - Builds and configures TWE with documentation
echo make buildNoDoc          - Builds and configures TWE without documentation
echo make buildDoc            - Builds documentation only
echo make debug               - Builds TWE JAR files with included debug information
echo make dependencies        - Creates TWS dependencies within distributions folder
echo make dependency_tws      - Creates TWS dependencies within distributions folder
echo make install             - Installs and configures TWE into directory defined by parameter install.dir in build.properties file. 
echo                            You can set this parameter value by using command: configure -instdir PATH_TO_DIR.
echo                            It should be called only after make buildAll target is executed!
echo make clean               - Removes the output and distribution folder (in order to start a new compilation from scratch)
echo make distributions       - Builds and configures TWE with all documentations and creates distribution package
goto end

:errorsystemroot
echo.
echo          Enviroment variable SYSTEMROOT does not exist.
echo.
echo Set variable SYSTEMROOT to system root of your Windows installation ...
echo.
pause
goto end

:error
echo.
echo Invalid parameter value!
echo.
goto help

:errorjava
echo.
echo Invalid java parameter value in build.properties file.
echo Please use configure command to set this parameter to right value.
echo.
echo     configure.bat -jdkhome java_home_directory
echo.
goto end

:end
SET VERSION=
SET TARGET=
SET JAVA_HOME=%OLD_JAVA_HOME%
