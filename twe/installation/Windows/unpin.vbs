Set objShell = CreateObject("Shell.Application")
set filesystem = CreateObject("scripting.Filesystemobject")
Set objFolder = objShell.Namespace(Wscript.Arguments(0))
Set objFolderItem = objFolder.ParseName(WScript.Arguments(1))
Set colVerbs = objFolderItem.Verbs
For Each objVerb in colVerbs
	If Replace(objVerb.name, "&", "") = "Unpin from Taskbar" then
                objVerb.DoIt
        end if
	If Replace(objVerb.name, "&", "") = "Unpin from taskbar" then
                objVerb.DoIt
        end if
Next