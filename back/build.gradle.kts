plugins {
    kotlin("jvm") version "1.9.23"
    id ("application")
    id ("org.openjfx.javafxplugin") version "0.1.0"
}



group = "org.kobalt"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
javafx {
    version = "21"
    modules ("javafx.controls","javafx.web")
}

application {
    mainClass.set("org.kobalt.Main")
}