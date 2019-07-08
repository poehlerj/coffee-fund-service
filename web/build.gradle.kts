import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile

repositories {
    jcenter()
}

plugins {
    id("kotlin2js")
    id("org.jetbrains.kotlin.frontend")

}

dependencies {
    compile(project(":common"))

    compile(embeddedKotlin("stdlib-js"))
}

tasks {
    val compileKotlin2Js = named<Kotlin2JsCompile>("compileKotlin2Js") {
        kotlinOptions {
            metaInfo = true
            sourceMap = true
            moduleKind = "umd"
            main = "call"
            outputFile = "${project.buildDir}/js/${project.name}.js"
        }
    }

    register<Sync>("assembleWeb") {
        configurations.compile.forEach { file ->
            from(zipTree(file.absolutePath)) {
                includeEmptyDirs = false
                include { fileTreeElement ->
                    val path = fileTreeElement.path
                    path.endsWith(".js") && (path.startsWith("META-INF/resources/") ||
                            !path.startsWith("META-INF/"))
                }
            }
        }
        from(compileKotlin2Js.get().destinationDir)
        into("${project.buildDir}/web")
        dependsOn("classes")
    }

    getByName("assemble") {
        dependsOn("assembleWeb")
    }

}
