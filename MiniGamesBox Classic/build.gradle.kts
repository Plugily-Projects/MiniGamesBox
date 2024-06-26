/*
 *  MiniGamesBox - Library box with massive content that could be seen as minigames core.
 *  Copyright (C) 2023 Plugily Projects - maintained by Tigerpanzer_02 and contributors
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

plugins {
    id("plugily.projects.java-conventions")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    java
}

dependencies {
    implementation("me.tigerhix.lib:scoreboard:1.4.4") { isTransitive = false }
    implementation("com.github.cryptomorin:XSeries:9.10.0") { isTransitive = false }
    implementation(project(":MiniGamesBox-API", "shadow"))
    implementation(project(":MiniGamesBox-Inventory", "shadow"))
    implementation(project(":MiniGamesBox-Database", "shadow"))
    implementation(project(":MiniGamesBox-Utils", "shadow"))
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("io.papermc:paperlib:1.0.8")
    implementation("org.openjdk.nashorn:nashorn-core:15.4")
    implementation("org.ow2.asm:asm:9.6")
    compileOnly("com.mojang:authlib:3.11.50")
    compileOnly("de.simonsator:DevelopmentPAFSpigot:1.0.67")
    compileOnly("de.simonsator:Party-and-Friends-MySQL-Edition-Spigot-API:1.5.4-RELEASE")
    compileOnly("de.simonsator:Spigot-Party-API-For-RedisBungee:1.0.3-SNAPSHOT")
    compileOnly("com.alessiodp.parties:parties-api:3.2.9")
    compileOnly("me.clip:placeholderapi:2.11.3")
}

tasks {
    compileJava {
        dependsOn(":MiniGamesBox-API:jar")
    }

    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveClassifier.set("")
        relocate("org.openjdk.nashorn", "plugily.projects.minigamesbox.classic.utils.skript.nashorn")
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