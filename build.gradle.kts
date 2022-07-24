plugins {
    kotlin("jvm") version("1.6.0")
    id("com.github.johnrengelman.shadow") version "7.1.1"
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.3"
}

group = "run.dn5"
version = "1.1-beta.5"
description = "Synchronize chats between servers under Velocity, BungeeCord."
val artifactName =  "${rootProject.name}-${rootProject.version}.jar"

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(project(":common"))
    implementation(project(":paper"))
    implementation(project(":waterfall"))
    implementation(project(":velocity"))
}

tasks {
    shadowJar{
        archiveFileName.set(artifactName)
    }
    register("preDebug"){
        dependsOn("clean", "shadowJar")
        doLast {
            listOf("paper", "paper2", "waterfall", "velocity").forEach {
                copy {
                    from("$buildDir/libs/${artifactName}")
                    into(".debug/$it/plugins")
                }
            }
        }
    }
}

subprojects {
    group = parent!!.group
    version = parent!!.version
    description = parent!!.description

    apply {
        plugin("org.jetbrains.kotlin.jvm")
        plugin("org.jetbrains.gradle.plugin.idea-ext")
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        if(project.name != "common") {
            implementation(project(":common"))
        }
        implementation("com.google.code.gson:gson:2.9.0")
        compileOnly("org.jetbrains.kotlin:kotlin-stdlib:1.7.0")
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }


    tasks {
        processResources {
            filteringCharset = "UTF-8"
            filesMatching(listOf("bungee.yml", "plugin.yml")) {
                expand(mapOf(
                    "name" to rootProject.name,
                    "id" to rootProject.name.toLowerCase(),
                    "group" to project.group,
                    "version" to project.version,
                    "description" to project.description,
                    "author" to "ddPn08"
                ))
            }
        }
        compileKotlin {
            kotlinOptions.jvmTarget = "17"
        }
    }
}