import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinJsDce

repositories {
    jcenter()
}

plugins {
    id("kotlin2js")
    id("kotlin-dce-js")
}

dependencies {
    compile(project(":common"))

    compile(embeddedKotlin("stdlib-js"))
}

val productionMode = ext.get("productionMode") as Boolean

tasks {
    named<Kotlin2JsCompile>("compileKotlin2Js") {
        kotlinOptions {
            metaInfo = true
            sourceMap = !productionMode
            moduleKind = "umd"
            main = "call"
            outputFile = "${project.buildDir}/js/${project.name}.js"
            sourceMapEmbedSources = if (productionMode) {
                "never"
            } else {
                "always"
            }
        }
    }.get()

    register<Sync>("assembleWeb") {
        dependsOn("runDceKotlinJs")
        dependsOn("classes")
        dependsOn("compileKotlin2Js")

        from(getByName<KotlinJsDce>("runDceKotlinJs").destinationDir)
        into("${project.buildDir}/web")
    }

    getByName("assemble") {
        dependsOn("assembleWeb")
    }

}
