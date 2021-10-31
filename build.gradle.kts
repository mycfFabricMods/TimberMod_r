plugins {
    id("fabric-loom") version "0.10-SNAPSHOT"
    val kotlinVersion: String by System.getProperties()
    kotlin("jvm") version kotlinVersion
}

base {
    val archives_base_name: String by project
    archivesName.set(archives_base_name)
}

val javaVersion = JavaVersion.VERSION_16.toString()

val mod_version: String by project
val maven_group: String by project

val minecraft_version: String by project
val yarn_mappings: String by project
val loader_version: String by project
val fabric_version: String by project

version = mod_version
group = maven_group

minecraft {}
repositories {}

dependencies {
    //to change the versions see the gradle.properties file
    minecraft("com.mojang:minecraft:$minecraft_version")
    mappings("net.fabricmc:yarn:$yarn_mappings:v2")
    modImplementation("net.fabricmc:fabric-loader:$loader_version")

    // Fabric API. This is technically optional, but you probably want it anyway.
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabric_version")

    // Kotlin Fabric Language Module (probably not needed)
    //modImplementation("net.fabricmc:fabric-language-kotlin:${project.fabricKotlinVersion}")
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        targetCompatibility = javaVersion
        sourceCompatibility = javaVersion
        options.release.set(javaVersion.toInt())
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions { jvmTarget = javaVersion }
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
    jar { from("LICENSE") { rename { "${it}_${base.archivesName}" } } }
    processResources {
        inputs.property("version", project.version)
        filesMatching("fabric.mod.json") { expand("version" to project.version) }
    }
    java {
        toolchain { languageVersion.set(JavaLanguageVersion.of(javaVersion.toString())) }
        sourceCompatibility = JavaVersion.VERSION_16
        targetCompatibility = JavaVersion.VERSION_16
        withSourcesJar()
    }
}