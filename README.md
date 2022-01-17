# MC-Chaos-Mod
A Chaos mod made for mc (kinda like gtaV chaos)

## Version: 1.0b5
***\*Note there will be bugs as this is in very early development!***
## Authors:

 - May (Python Core & Plugin and Optional Mod)
     - [Github](https://github.com/MayD524)
     - Discord: MayActual#8008
 - Sweden (Plugin Core)
     - [Github](https://github.com/SwedenWarfare)
     - Discord: Gustavus Adolphus#9723

## Features: 

 - 30+ (customizable) commands
    - Ability to edit and add commands
    -  Ability to chain commands
 - Optional mod for allowing tts and more commands
 - Configurable overlay
 - Usable with other mods as well! (But you have to add the commands yourself)
 


## How To Use The Plugin :

### Requirements:

 - MC Version: 1.18.1
 - [Spigot (Latest)](https://getbukkit.org/get/bf7ac3b5bc08ea97d22919680d240a80)
 - [Python >=3.10](https://www.python.org/ftp/python/3.10.2/python-3.10.2-amd64.exe)
 - At least 6GB of ram to use for the minecraft server
  
### How To Use:

- Minecraft
  1. Run the spigot server 
  2. Enable EULA 
  3. Run spigot again lol
  4. put the plugin in the plugins folder and restart the server
<br>
- Python
  1. Run the python script *"main.py"* in the twitch_chat folder (may be renamed later)
  2. All requests go to that http server (localhost:8080) by default
<br>
- Update Config
  1. Run the config menu *"main.py"* in the config_menu folder
  2. Make your edits and hit "export" to export the config to js and css (for the overlay)
    - The config.json should be placed in both the config_menu AND the twitch_chat folders
  3. Place the **config.js** in **overlay/json/config.js**
  4. Place the **styles.css** in **overlay/css/styles.css**
<br>
- Overlay (OBS)
  1. Make a web view in OBS pointing to *"overlay/overlay.html"*
  2. Right click the webview and select "Interact" and hit *"start"*
    - Open properties and hit refresh current cache (at the bottom of the properties window)
  3. Enjoy
   
