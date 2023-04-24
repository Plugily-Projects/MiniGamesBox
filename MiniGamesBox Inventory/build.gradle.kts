plugins {
    id("plugily.projects.java-conventions")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    java
}

repositories {
    maven("https://jitpack.io")
}

dependencies {
    implementation("fr.mrmicky:FastInv:3.0.3")
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveClassifier.set("")
        relocate("fr.mrmicky.fastinv", "plugily.projects.minigamesbox.inventory.utils.fastinv")
    }
}

description = "MiniGamesBox-Inventory"