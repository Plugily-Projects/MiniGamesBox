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
    implementation("me.tigerhix.lib:scoreboard:1.4.5") { isTransitive = false }
    compileOnly(project(":MiniGamesBox-Database", "shadow"))
    compileOnly("com.github.cryptomorin:XSeries:11.2.0") { isTransitive = false }
}

tasks{
    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveClassifier.set("")
    }
}

description = "MiniGamesBox-API"
