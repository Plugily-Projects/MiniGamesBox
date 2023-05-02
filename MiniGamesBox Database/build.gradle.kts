plugins {
    id("plugily.projects.java-conventions")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    java
}

dependencies {
    implementation("com.zaxxer:HikariCP:5.0.1")
}

tasks{
    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveClassifier.set("")
    }
}

description = "MiniGamesBox-Database"
