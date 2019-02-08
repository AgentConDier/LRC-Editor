# LRC Editor 
Version 2.2.3

[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

#### Fork by AgentConDier
This Fork changes the following:
* File search is now recursive (Renaming/Deleting is not tho)
* Some hackery enables you to save files recursively as well. This somewhat broke the save dialog which now does some counterintuitive things:
    * Tapping on "set as song file name" or "set as lrc file name" will *usually* also set the path. Don't believe the "save location" display's lies!
    * *Usually* means "if you opened the lyrics file from within the app". If you opened it from the file explorer, the app will use that file's uri to save the file automatically (Note: The app will not ask you if you really want to overwrite your lrc file in this case).
    * The green text that appears after a save now displays the uri of the file the lyrics were written to. This is a bit harder to decipher, but is always accurate.
    * Saving files may or may not be completely broken and/or contain bugs. It was made to work with Android 9 (Pie).
* .lrc files are expected to have a corresponding .mp3 or .m4a file with the same name (except the extension, of course). This behaviour is compatible with the stock Huawei music player. The corresponding music file is automatically loaded when you tap on the .lrc file.
* If a .lrc file is missing its audio file, it is marked orange in the file list
* If an audio file is missing its lyrics file, it is colored blue. Tapping on it leads you to the creation screen, and also automatically loads the song
* Free dark mode. (Sorry, I just had to)


(The links below link to the original app on the play store, not the forked version)

[![Say: Thanks](https://img.shields.io/badge/Say%20Thanks-!-1EAEDB.svg)](https://play.google.com/store/apps/details?id=com.cg.lrceditor)

An Android App that helps you to create and edit .lrc files easily

[<img src="https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png" alt="Download from Google Play" height="80">](https://play.google.com/store/apps/details?id=com.cg.lrceditor)
