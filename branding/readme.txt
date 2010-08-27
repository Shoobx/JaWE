   How to re-brand TWE


To re-brand TWE you basically need to do 2 things:

1) to edit build.properties file in the root of the project and:
      a) set rebranding property value to true

            rebranding=true

      b) if you want TWE setup.exe installation to use language different than
         English, also change the value of property language, e.g.:

            language=PortugueseBR

2) edit/remove/add files from branding folder sub-folders


The following description explains the meaning of the sub-folders and
how can you perform TWE branding by changing their content:


Sub-Folder     Description
----------     -----------
aboutbox       Edit aboutbox.properties file to define what will be shown in the TWE's aboutbox
               If you don't want to show license information in the aboutbox, change the value
               of showLicenseInfo property to false.
               Example aboutbox.properties file is put here to show you how to do it - it
               specifies XYZ editor instead of TWE editor...

activityicons  If you put any icons into this folder, these will be the icons you will be able
               to chose for the "Icon" property from Activity's property panel. If you don't put
               any icons here, the default set of icons will be used from this panel.
               6 sample icons are added here to show the use case.

config         If you want to change some of the existing TWE configuration modes, you should add
               new folder into this sub-folder that would contain modified TWE property files.
               Also, 'defaultconfig' file in the root of config folder specify default configuration
               to use during TWE startup.
               Example file in-here shows how to set default startup configuration to 'default'
               and contains 2 configuration files which are modified (comparing to original ones):
                  a) towebasic.properties -> the default language is set to Portuguese
                                          -> configured not to have default transient packages
                  b) togwegraphcontroller.properties -> configured to show grid
                                                     -> configured to show transition conditions
                                                     -> configured to show text at right of the activity box
                                                     -> changed graph background color
                                                     -> changed graph grid color

doc            If you want to change documentation and pictures appearing in the documentation,
               in this folder you should put the modified twe.xml file and in Images sub-folder
               you should put all the pictures from the original folder you want to override.
               Example twe.xml file changes all the occurences of TWE with XYZ, all occurences
               of Together Workflow Editor with XYZ Workflow Editor, all occurences of
               Together Teamlösungen EDV-Dienstleistungen GmbH. with XYZ Company, all the URLs
               to together site with URLs to google site, etc. Example images folder provides
               two images to override - these images in their original form contain Together
               company logo and TWE logo.
               For the list of all the re-placable images you should look into
               TWE_HOME\doc\Images folder

examples       If you put anything in this folder, the original examples folder will be replaced
               by this one.
               Example shows one XPDL file which normally is not the part of TWE distribution.

i18n           If you put language property file(s) here, it will override the original one, or
               will add one if it does not exist in the original distribution.
               Example provides JaWE_pt.properties file where all the occurences of TWE and Together
               are replaced by XYZ

lib            If you need to add additional jar to be used, or you want to override an existing
               one, put it in this folder.

images         If you put image here, it will override the original image coming with distribution.
               Example images put into this folder are the ones for the splash screen, jawe frame icon
               and icon for the generic activity.
               For the list of all the re-placable images you should look at folder:
               TWE_HOME\modules\core\Utilities\src\org\enhydra\jawe\images

installation   In the icons sub-folder you can put images for the TWE installer to use.
               In the Modern UI sub-folder you put the splash screen image and in its
               Language files sub-folder you put your modification of installer language/branding file.
               Example shows the names of the images you can replace and modification of
               Brazilian Portuguese language/branding file which refers to the editor as to XYZ
               Editor.

license        In this folder you put your own license. This license can appear in the about dialog
               if you set proper configuration switch in aboutbox.properties file (read remarks
               for aboutbox folder) and in the root folder of editor binary distribution.
               Example contains dummy license file with XYZ license.

registry       This folder contains icons that will be used to register application in windows registry.
               Example shows icon for XPDL file extension and icon for the editor itself.






