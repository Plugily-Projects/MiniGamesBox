rootProject.name = "minigamesbox"
include(":MiniGamesBox-Inventory")
include(":MiniGamesBox-Classic")
include(":MiniGamesBox-Database")
include(":MiniGamesBox-Utils")
project(":MiniGamesBox-Inventory").projectDir = file("MiniGamesBox Inventory")
project(":MiniGamesBox-Classic").projectDir = file("MiniGamesBox Classic")
project(":MiniGamesBox-Database").projectDir = file("MiniGamesBox Database")
project(":MiniGamesBox-Utils").projectDir = file("MiniGamesBox Utils")
