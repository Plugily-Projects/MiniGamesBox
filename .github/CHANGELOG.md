### 1.4.2 Release (09.06.2025)
* Added BLOCK_IN_GAME_INTERACTIONS option to block interactions whileas ingame
* Added onArrowHitRemoveProjectile event to remove projectiles which got shoot from InGame Players
* Added newer mc version door and sign types
* Added by default link at config.yml where to translate the project
* Added full 1.21.5 support
* Fixed ActionBarManager and Renamed ActionBar.ActionBarType enums to match better use case
* Fixed on bungeemode arena shutdown was to slow to overwrite shuffling and joining of next arena
* Fixed arena forcestart
* Fixed InventoryView on newer MC versions
* Fixed empty messages get build / sended
* Fixed playername update on updateStats could cause NPE in rare cases such as direct kick if no playername is recognised by server.
* Fixed MaterialUtils matching of Materials in different mc versions
* Updated XSeries to 13.3.1

### 1.4.1 Release (01.05.2025)
* Fixed player names in statistics/Leaderboard module are replaced by papi placeholders instead of our player placeholder which caused empty player names
* Fixed InventoryManager saving if player got items with attributes, now they get cleaned beforehand the attributes get saved
* Fixed messageIssueColor does not repeat after value on string
* Changed Provide ScoreboardLines with Player to reduce doupled code in subprojects
* Changed By default for lower 1.13 versions remove special chars from Scoreboard lines
* Updated Fastboard to support 1.21.5
* Updated XSeries to 13.2.0
* Removed PagedFastInv as PaginatedFastInv now available


### 1.4.0 Release (13.03.2025)
* Fixed scoreboard on FUll Game is showing waiting stage instead of starting
* Fixed leaving while full game stopped the game
* Changed java compatibility backport to 1.8

### 1.3.17 Release (04.03.2025)
* Changed Scoreboard API (now paket based) (Thanks MrMicky-FR FastBoard)
* Changed Scoreboard on lower 1.12.2 now supports 30 chars
* Changed Scoreboard on 1.13+ now supports unlimited chars
* Changed Scoreboard on 1.20.3 will hide red score numbers
* Changed pom files to build java 8 builds without errors
* Changed Potion methods to support all versions
* Removed ProtocolSupport for Scoreboard Actions
* Fixed Progress must be between 0 and 1
* Fixed ItemBuilder.glowEffect

### (1.3.11-)1.3.16 Release (17.02.2025)
* Added 1.21.1-4 support
* Added (3) new ConfigOptions for ArmorStands (BLOCK_IN_GAME_ARMOR_STAND_DESTROY, BLOCK_IN_GAME_ARMOR_STAND_CHECK, BLOCK_IN_GAME_ARMOR_STAND_INTERACT)
* Fixed arena start time divider did not match from config.yml
* Fixed spectator can't fly after sneaking out of first person mode
* Fixed player collissions on spectator mode
* Fixed sending of leave message for leaving player and the counting in arena
* Fixed ActionBars did not convert player and arena placeholders by default
* Fixed compatibility for BannerColoring in 1.20+
* Fixed sign updates as cached objects got overwrite (#59)
* Fixed CommandArgument could have multiple permissions while only the first one was checked
* Changed onDisable process to make sure all data gets saved even on mysql
* Changed Simplified and fixed getting of custom texture Skulls (1.20+)
* Changed Simplified and fixed ArenaWallSign on 1.20.5+
* Changed Attempt to fix incompatibles with other plugins which using scriptengine by rewritting name of own scriptengine [Changed ScriptEngine name to "plugilyprojects"]

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
