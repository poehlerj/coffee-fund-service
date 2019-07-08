import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile

repositories {
    mavenCentral()
    jcenter()
}

plugins {
    id("kotlin-multiplatform")
}

val productionMode = ext.get("productionMode") as Boolean

kotlin {
    targets {
        targetFromPreset(presets.getAt("js"), "js") {
            tasks.getByName(compilations.getByName("main").compileKotlinTaskName, delegateClosureOf<KotlinJsCompile> {
                kotlinOptions {
                    moduleKind = "umd"
                    sourceMap = !productionMode
                    outputFile = "${project.buildDir}/js/${project.name}.js"
                    sourceMapEmbedSources = if (productionMode) {
                        "never"
                    } else {
                        "always"
                    }
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
