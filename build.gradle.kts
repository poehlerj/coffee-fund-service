import org.jetbrains.kotlin.js.inline.util.collectNamedFunctionsAndWrappers

buildscript {
    repositories {
        mavenCentral()
        maven { url = uri("https://kotlin.bintray.com/kotlinx") }
        maven { url = uri("https://dl.bintray.com/kotlin/kotlin-eap") }
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.41")
        embeddedKotlin("kotlin-allopen")
    }
}

allprojects {
    apply {
        plugin("idea")
    }

    ext {
        set("productionMode", false)

        set("ebeanVersion", "11.41.1")
        set("ebeanGradlePluginVersion", "11.40.1")
        set("ebeanQueryBeanGeneratorVersion", "11.39.3")

        set("ktorVersion", "1.1.3")
        set("logbackVersion", "1.2.3")
        set("postgresVersion", "42.2.2")
        set("h2Version", "1.4.199")

        set("jvmTargetVersion", "1.8")
    }

    group = "coffee"
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.ALL
}

