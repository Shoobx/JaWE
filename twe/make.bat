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
::cls

SET VERSION=4.4
SET TARGET=buildAll
SET OLD_JAVA_HOME=%JAVA_HOME%
SET JAVA_HOME=

if %SYSTEMROOT% == "" goto errorsystemroot

if not exist build.properties call configure.bat
find "jdk.dir" < build.properties >javahome.txt
for /F "tokens=1,2* delims==" %%i in (javahome.txt) do SET JAVA_HOME=%%j
del javahome.txt>nul

if not exist project.properties call configure.bat
find "app.name=" < project.properties > appname.txt
for /F "tokens=1,2* delims==" %%i in (appname.txt) do SET APP_NAME=%%j
del appname.txt>nul

find "app.full.name=" < project.properties > appfullname.txt
for /F "tokens=1,2* delims==" %%i in (appfullname.txt) do SET APP_FULL_NAME=%%j
del appfullname.txt>nul

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
echo make                     - Displays the Help Information.
echo make help                - Displays the Help Information.
echo make buildAll            - Builds and configures %APP_NAME% with documentation.
echo make buildNoDoc          - Builds and configures %APP_NAME% without documentation.
echo make buildDoc            - Builds documentation only.
echo make clean               - Removes the output and the distributions folder (in order to start a new compilation from scratch)
echo make debug               - Builds %APP_NAME% JAR file(s) with included debug information.
echo make install             - Installs and configures %APP_NAME% into directory defined by parameter install.dir in the build.properties file. 
echo                            Which can be set by using command: configure -instdir PATH_TO_DIR. (execute only 'make buildAll').
echo make distributions       - Builds and configures %APP_NAME% with all documentations and creates distributions package.
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
