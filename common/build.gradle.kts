plugins {
    kotlin("plugin.serialization") version "1.6.0"
}
repositories {
    gradlePluginPortal()
}

dependencies {
    compileOnly("net.kyori:adventure-api:4.11.0")
    implementation("com.charleskorn.kaml:kaml:0.46.0")
    implementation("club.minnced:discord-webhooks:0.8.2")
    implementation("net.dv8tion:JDA:5.0.0-alpha.13") {
        exclude(module = "opus-java")
    }
}