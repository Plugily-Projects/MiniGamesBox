plugins {
    id("plugily.projects.java-conventions")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    java
}

dependencies {
    implementation("me.tigerhix.lib:scoreboard:1.2.0")
    implementation("com.github.cryptomorin:XSeries:9.3.1")
    implementation(project(":MiniGamesBox-Inventory", "shadow"))
    implementation(project(":MiniGamesBox-Database", "shadow"))
    implementation(project(":MiniGamesBox-Utils", "shadow"))
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("io.papermc:paperlib:1.0.8")
    compileOnly("com.mojang:authlib:3.11.50")
    compileOnly("de.simonsator:DevelopmentPAFSpigot:1.0.67")
    compileOnly("de.simonsator:Party-and-Friends-MySQL-Edition-Spigot-API:1.5.4-RELEASE")
    compileOnly("de.simonsator:Spigot-Party-API-For-RedisBungee:1.0.3-SNAPSHOT")
    compileOnly("com.alessiodp.parties:parties-api:3.2.9")
    compileOnly("me.clip:placeholderapi:2.11.3")
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveClassifier.set("")
        relocate("com.cryptomorin.xseries.particles", "plugily.projects.minigamesbox.classic.utils.version.xseries")
        relocate("com.cryptomorin.xseries", "plugily.projects.minigamesbox.classic.utils.version.xseries")
        relocate("me.tigerhix.lib.scoreboard", "plugily.projects.minigamesbox.classic.utils.scoreboard")
        relocate("org.bstats", "plugily.projects.minigamesbox.classic.utils.bstats")
        relocate("io.papermc.lib", "plugily.projects.minigamesbox.classic.utils.paperlib")
    }

    processResources {
        filesMatching("**/plugin.yml") {
            expand(project.properties)
        }
    }
}

description = "MiniGamesBox-Classic"