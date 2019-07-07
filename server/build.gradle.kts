import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinTest

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

val kotlinVersion = "1.3.41"
val ebeanVersion = "11.41.1"
val ktorVersion = "1.1.3"
val logbackVersion = "1.2.3"

dependencies {
    compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")

    compile("org.slf4j:slf4j-api:1.7.25")
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

        testCompile("$e.test:ebean-test-config:11.39.1")

        kapt("$e:kotlin-querybean-generator:11.39.3")
    }

    compile("org.postgresql:postgresql:42.2.2")
    compile("com.h2database:h2:1.4.199")

    testCompile("org.avaje.composite:junit:1.1")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    withType<Test> {
        testLogging.showStandardStreams = true
        testLogging.exceptionFormat = TestExceptionFormat.FULL
    }
}

ebean {
    debugLevel = 1
}