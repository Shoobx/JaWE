@echo off
cls

SET VERSION=6.6
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

if "X%~1"=="X" goto buildAll
if %~1==help goto help
if %~1==buildAll goto continue
if %~1==buildNoDoc goto buildNoDoc
if %~1==buildDocBook goto buildDocBook
if %~1==dependencies goto dependencies
if %~1==install goto install
if %~1==clean goto continue
if %~1==cleanAll goto continue
if %~1==distributions goto buildDistributions

goto error

:continue
SET TARGET=%~1
goto continuebuild

:buildDocBook
SET TARGET=buildDocBook
goto continuebuild

:buildAll
SET TARGET=buildAll
goto continuebuild

:buildNoDoc
SET TARGET=buildNoDoc
goto continuebuild

:dependencies
SET TARGET=dependencies
goto continuebuild

:install
SET TARGET=install
goto continuebuild

:buildDistributions
SET TARGET=buildDistributions
goto continuebuild

:continuebuild
set OLDCLASSPATH=%CLASSPATH%
set OLDPATH=%PATH%
set PATH="%JAVA_HOME%\bin"
set CLASSPATH="%CD%\util\ant\ant.jar"
set CLASSPATH=%CLASSPATH%;"%CD%\util\ant\ant-launcher.jar"
set CLASSPATH=%CLASSPATH%;"%CD%\util\ant\antcontrib.jar"
set CLASSPATH=%CLASSPATH%;"%CD%\util\ant\ant-nodeps.jar"
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
echo make                 - builds and configures TWE with javadoc and docbook documentation
echo make help            - Display this screen
echo make install         - Install and configure TWE
echo make buildAll        - builds and configures TWE with javadoc and docbook documentation
echo make buildNoDoc      - builds and configures TWE without javadoc and docbook documentation
echo make buildDocBook    - builds docbook documentation
echo make distributions   - builds and configures TWE with all documentations and creates distribution package
echo make dependencies    - builds and configures TWE, and creates TWS dependencies within distributions folder
echo make clean           - removes the output and distribution folder (in order to start a new compilation from scratch)
echo make cleanAll        - removes the same things as 'clean' target plus doc/tmp folder used to quickly generate doc book docu
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
