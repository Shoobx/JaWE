@echo off
cls
rem *********************************************
rem *  Initialize environment variables
rem *********************************************

SET SET_JDKHOME=off

SET JDKHOME=%JAVA_HOME%

if exist build.properties goto init

rem *********************************************
rem *  Set properties values from user input
rem *********************************************
:start
if %~1.==. goto make
if %~1==-help goto help
if %~1==-jdkhome goto jdkhome
goto error

rem *********************************************
rem *  Set default values
rem *********************************************
:default
SET JDKHOME=%JAVA_HOME%

goto make


rem *********************************************
rem *  Init build.properties parameters
rem *********************************************
:init
find "jdk.dir" < build.properties > javadir.txt
for /F "tokens=1,2* delims==" %%i in (javadir.txt) do SET JDKHOME=%%j
del javadir.txt>nul
if "X%JDKHOME%"=="X" goto initjava
goto start

:initjava
JDKHOME=%JAVA_HOME%
goto start

rem *********************************************************
rem *  Edit parameters (build.properties)
rem *********************************************************
:make
if exist build.properties del build.properties
echo jdk.dir=^%JDKHOME%>build.properties

set OLDCLASSPATH=%CLASSPATH%
set OLDPATH=%PATH%
set CLASSPATH="%CD%\lib\ant.jar"
set CLASSPATH=%CLASSPATH%;"%CD%\lib\ant-launcher.jar"
set CLASSPATH=%CLASSPATH%;"%CD%\lib\xercesImpl.jar"

set PATH="%JDKHOME%\bin"
java -cp %CLASSPATH% org.apache.tools.ant.Main -DSYSTEMROOT=%SYSTEMROOT%

set PATH=%OLDPATH%
set CLASSPATH=%OLDCLASSPATH%
set OLDCLASSPATH=
set OLDPATH=

goto end


rem *********************************************************
rem *  Display ERROR message
rem *********************************************************
:error
echo.
echo Invalid options using with configure.bat
echo.


rem *********************************************************
rem *  Display HELP message
rem *********************************************************
:help
echo.
echo Parameters value for using with configure.bat :
echo.
echo configure       - Make build.properties file with default 'jdk.dir' value
echo.
echo configure -help - Display this screen
echo.
echo configure [-jdkhome jdk_home_dir]
echo.
echo.
echo Examples :
echo.
echo configure -jdkhome C:/j2sdk1.4.2
echo.
goto end

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
goto error


rem *********************************************************
rem *  Reset evironment variables
rem *********************************************************
:end
SET JDKHOME=

SET SET_JDKHOME=
