Set objShell = CreateObject("Shell.Application")
set filesystem = CreateObject("scripting.Filesystemobject")
Set objFolder = objShell.Namespace(Wscript.Arguments(0))
Set objFolderItem = objFolder.ParseName(WScript.Arguments(1))
Set colVerbs = objFolderItem.Verbs
For Each objVerb in colVerbs
		If Replace(objVerb.name, "&", "") = "Pin to Taskbar" then
                objVerb.DoIt
        end if
Next