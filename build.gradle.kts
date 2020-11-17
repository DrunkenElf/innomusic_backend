import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val exposed_version: String by project



val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

defaultTasks("clean", "build")

plugins {
    application
    id("java-library")
    id("java")
    kotlin("jvm") version "1.4.20-RC"
    id("com.github.johnrengelman.shadow") version "6.1.0"
}



group = "com.inno.music"
version = "0.0.1"


application{
    mainClassName = "io.ktor.server.netty.EngineMain"
    //mainClass.set("io.ktor.server.netty.EngineMain")
}


repositories {
    mavenLocal()
    jcenter()
    maven { url = uri("https://kotlin.bintray.com/ktor") }
    maven { url = uri("https://kotlin.bintray.com/kotlin-js-wrappers") }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-html-builder:$ktor_version")
    implementation("org.jetbrains:kotlin-css-jvm:1.0.0-pre.31-kotlin-1.2.41")
    implementation("io.ktor:ktor-server-host-common:$ktor_version")
    implementation("io.ktor:ktor-locations:$ktor_version")
    implementation("io.ktor:ktor-gson:$ktor_version")

    implementation("kr.jadekim:ktor-extension:1.0.0")

    implementation("org.jetbrains.ktor:1.4.2")

    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")

    implementation("com.zaxxer:HikariCP:3.4.5")

    implementation("org.postgresql:postgresql:42.2.12")

    implementation("io.ktor:ktor-network-tls-certificates:$ktor_version")
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
}


kotlin.sourceSets["main"].kotlin.srcDirs("src")
kotlin.sourceSets["test"].kotlin.srcDirs("test")

sourceSets["main"].resources.srcDirs("resources")
sourceSets["test"].resources.srcDirs("testresources")

tasks {
    named<ShadowJar>("shadowJar") {
        archiveBaseName.set(rootProject.name)
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to application.mainClassName))
        }
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }
}