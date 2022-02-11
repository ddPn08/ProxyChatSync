plugins {
    kotlin("plugin.serialization") version "1.6.0"
}
repositories {
    gradlePluginPortal()
}

dependencies{
    compileOnly("net.kyori:adventure-api:4.9.3")
    implementation("com.charleskorn.kaml:kaml:0.40.0")
    implementation("net.dv8tion:JDA:5.0.0-alpha.5")
}