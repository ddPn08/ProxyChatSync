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
    compileOnly("com.velocitypowered:velocity-api:3.0.1")
    kapt("com.velocitypowered:velocity-api:3.1.0")
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

kotlin.sourceSets["main"].kotlin.srcDir(generateTemplates.map{ it.outputs })
rootProject.idea.project.settings.taskTriggers.afterSync(generateTemplates)