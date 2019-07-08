import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

repositories {
    maven { url = uri("https://dl.bintray.com/kotlin/ktor") }
    mavenCentral()
    jcenter()
}

buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("io.ebean") version "11.40.1"
}

val ebeanVersion = ext.get("ebeanVersion")
val ebeanQueryBeanGeneratorVersion = ext.get("ebeanQueryBeanGeneratorVersion")
val ktorVersion = ext.get("ktorVersion")
val postgresVersion = ext.get("postgresVersion")
val logbackVersion = ext.get("logbackVersion")
val h2Version = ext.get("h2Version")

val jvmTargetVersion = ext.get("jvmTargetVersion") as String

val mainClass = "coffee.service.ServiceKt"

dependencies {
    compile(embeddedKotlin("stdlib-jdk8"))

    runtimeOnly("org.slf4j:slf4j-api:1.7.25")
    compile("org.avaje.composite:logback:1.1")

    "io.ktor".let { k ->
        compile("$k:ktor-server-netty:$ktorVersion")
        compile("$k:ktor-html-builder:$ktorVersion")
        compile("$k:ktor-jackson:$ktorVersion")
        compile("$k:ktor-auth:$ktorVersion")
        compile("$k:ktor-auth-ldap:$ktorVersion")
    }

    "io.ebean".let { e ->
        compile("$e:ebean:$ebeanVersion")
        compile("$e:ebean-querybean:$ebeanVersion")

        kapt("$e:kotlin-querybean-generator:$ebeanQueryBeanGeneratorVersion")
    }

    compile("org.postgresql:postgresql:$postgresVersion")
    compile("com.h2database:h2:$h2Version")
    compile(project(":common"))

    testCompile("org.avaje.composite:junit:1.1")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = jvmTargetVersion
    }

    withType<Test> {
        testLogging.showStandardStreams = true
        testLogging.exceptionFormat = TestExceptionFormat.FULL
    }

    val jar = getByName<Jar>("jar") {
        dependsOn(":web:assemble")
        classifier = "fatJar"
        manifest {
            attributes["Main-Class"] = mainClass
        }
        into("javascript") {
            from(File("${project(":web").buildDir}/web/"))
        }
        configurations.compile.forEach { file: File ->
            from(zipTree(file.absoluteFile)).into("")
        }
    }

    getByName("assemble") {
        dependsOn("jar")
    }

    register<JavaExec>("runServer") {
        group = "application"
        dependsOn("jar")
        main = "coffee.service.ServiceKt"
        classpath(jar)
        jvmArgs()
    }

}

ebean {
    debugLevel = 1
}
