repositories {
    maven("https://jitpack.io")
    maven("https://raw.githubusercontent.com/ucchyocean/mvn-repo/master")
    maven("https://papermc.io/repo/repository/maven-public/")
}
dependencies {
    compileOnly("io.papermc.paper:paper-api:1.18.1-R0.1-SNAPSHOT")
    compileOnly("com.github.LeonMangler:SuperVanish:6.2.19")
    compileOnly("net.luckperms:api:5.4")
    compileOnly(fileTree("libs") { include("*.jar") })
}