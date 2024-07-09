### 1.3.10 Release (09.07.2024)
* Fixed multiverse teleportation problems on a multiworld server 

### 1.3.9 Release (18.06.2024)
* Major API Revamp for exposing methods that were not exposed previously to external plugins
* Fixed move false not working
* Added support for 1.20.5, 1.20.6, 1.21

### 1.3.8 Release (24.04.2024)
* Relocated NashornScriptEngine to not conflict with other plugins with ScriptEngine
* Fixed Arena stops if arena players are less than defined min players (developers: if needed plugin should call stopGame)
* Fixed crafting was possible in rare cases if Item-Move option in config.yml disabled
* Fixed mysql user statistic reset when users got kicked directly from the server
* Optimized statistic performance
* Changed default language string In-Game.Join.Arena-Not_Configured to be less confusing
* Updated to XSeries 9.10.0

### 1.3.7 Release (15.01.2024)
* Added playernames to FileStats
* Saving users mysql stats before server shutdown on game end
* Moved FoodLose/HungerLose to core
* Fixed LanguageMigrator file version fetching
* Fixed Java Heap Space issue when getProgressBar division zero
* Fixed user kit is null if not selected

### 1.3.6 Release (13.01.2024)
* Fixed IAE Cannot measure distance between worlds if players are in different worlds
* Fixed NPE on Setup MaterialLocationItem / Multi Handleritem RIGHT_CLICK_AIR
* Fixed kit loading
* Fixed arena starting
* Removed legacy particles on newer version to prevent IllegalArgumentException because no class found of modern material
* Moved Plugin-Chat-Format to Chat.Format in config.yml
* Moved Default Kit to config.yml, removed Kits section, added Kit section with Kit.Enabled and Kit.Default
* Removed default_kit from kitsname.yml files
* Adjusted rewards are now by default disabled
* Added more debug messages

### 1.3.5 Release (03.01.2024)
* Added new command /pluginadmin locale <locale> to set your locale
* Fixed GameProfile API 1.20 change
* Fixed none kits related plugins issues

### 1.3.4 Release (31.12.2023)
* Moved kits.yml into kits folder, separate file per kit
* Fixed script engine on upper java 11/(15) (File size increased)
* Fixed VersionUtils.spawnEntity on spigot upper 1.15

### 1.3.3 Release (29.11.2023)
* Added 1.20.2 support
* Added kits.yml mechanic
* Added SEPARATE_ARENA_SPECTATORS to adjust the chat for spectators to be on there own
* Changed config value Separate-Arena-Chat to Chat.Separate.Arena
* Fixed rewards enabling
* Fixed bossbar disable
* Fixed locale registration if services are unavailable
* Fixed player name on chat is removed while PAPI enabled
* Fixed NPE on command usage /pluginindicator stop

### 1.3.2 Release (09.08.2023)
* Fixed placeholders of core statistics found on StatisticType

### 1.0.0 Initial Release (2023)
* Added paged fast inv
* Added some more methods to itembuilder
* Refactored code
* It's now possible to configure more arena timings such as shorten waiting and restarting
* Powerups don't need HolographicDisplays anymore
* language.properties files are depreciated and all files are now yml
* General file overhaul
* Specialitems can now have rewards and permissions
* Its possible to add own specialitems on different stages
* Refactored messages and language.yml
* Removed some static methods
* Made it plugin unspecific to allow the core to work on its own with variables
* Added arena timer placeholder
* All stat placeholder will now automatically also registered with papi
* Added permissions.yml with custom permissions such as exp boost
* Refactored spectator gui to match functions from hypixel
  (speed, nightvision, auto teleport, first person mode, visibility)
* Its possible to add more items into spectator settings menu (own permissions and commands supported)
