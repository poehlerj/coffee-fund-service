import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile

repositories {
    mavenCentral()
    jcenter()
}

plugins {
    id("kotlin-multiplatform")
}

kotlin {
    targets {
        targetFromPreset(presets.getAt("js"), "js") {
            tasks.getByName(compilations.getByName("main").compileKotlinTaskName, delegateClosureOf<KotlinJsCompile> {
                kotlinOptions {
                    moduleKind = "umd"
                    outputFile = "${project.buildDir}/js/${project.name}.js"
                }
            })
        }
        targetFromPreset(presets.getAt("jvm"), "jvm")

    }

    sourceSets {
        getByName("commonMain") {
            dependencies {
                kotlin("kotlin-stdlib-common")
            }

        }
        getByName("jsMain") {
            dependencies {
                implementation(kotlin("stdlib-js"))
            }

        }
        getByName("jvmMain") {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
            }
        }
    }
}
