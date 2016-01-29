x-aurora
simplify copy and paste
current status:
UI and Logic: Not connected
Logic and Chrome Plugin: Connected
Logic and Word Plugin: Connected
Logic and data synchronization: not Connected

Testing step:
1. Open Chrome -> Tools -> Extension or (chrome://extensions/) -> Load unpacked extensions -> Select Chrome Plugin folder under Xaurora Folder
2. Open XAuroraWordPlugin.sln under Word Plugin\XAuroraWordPlugin folder under Xaurora Folder (Visual Studio 2015 Professional is the most preferable)
3. Run the XAuroraWordPlugin.sln, the program will automatically run a Microsoft Word application (depends on the word version installed on the PC, currently available for Microsoft Word 2010/2013/2013 Professional Plus/365/2016)
4. Import the X-aurora project into Eclipse, Run Main.java. (In case of error, it is caused by JAVAFX library and the configuration conflicts between Eclipse and Netbeans IDE, Currently since UI and Logic is not connected, there will not be any problem)
5. Browse any webpage other than the default dummy block list (wikipedia)
6. The text data of the repective webpage will be pasted directly on the Word Document

Current Problem:
1. Maximum data transmission length is only 1024 bytes, will be modified in the next sprint
2. Some special Unicode Character may not be able to paste correctly (e.g �)
