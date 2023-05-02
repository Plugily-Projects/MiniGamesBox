plugins {
    id("plugily.projects.java-conventions")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    java
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        archiveClassifier.set("")
    }
}

description = "MiniGamesBox-Utils"
