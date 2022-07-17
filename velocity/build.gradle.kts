import org.jetbrains.gradle.ext.settings
import org.jetbrains.gradle.ext.taskTriggers

plugins {
    kotlin("kapt")
    kotlin("plugin.serialization") version "1.6.0"
}

repositories {
    gradlePluginPortal()
    maven("https://nexus.velocitypowered.com/repository/maven-public/")
}

dependencies{
    kapt("com.velocitypowered:velocity-api:3.1.0")
    compileOnly("com.velocitypowered:velocity-api:3.0.1")
    compileOnly("club.minnced:discord-webhooks:0.8.2")
    compileOnly("net.dv8tion:JDA:5.0.0-alpha.13") {
        exclude(module = "opus-java")
    }
}

tasks.build {
    dependsOn("generateTemplates")
}

val generateTemplates = tasks.register<Copy>("generateTemplates") {
    val props = mapOf(
        "name" to rootProject.name,
        "id" to rootProject.name.toLowerCase(),
        "group" to project.group,
        "version" to project.version,
        "description" to project.description,
        "author" to "ddPn08"
    )
    filteringCharset = "UTF-8"
    inputs.properties(props)
    from("src/main/templates")
    into(layout.buildDirectory.dir("generated/source/templates"))
    expand(props)
}
kapt.includeCompileClasspath = false
kotlin.sourceSets["main"].kotlin.srcDir(generateTemplates.map{ it.outputs })
rootProject.idea.project.settings.taskTriggers.afterSync(generateTemplates)