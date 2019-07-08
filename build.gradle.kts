buildscript {
    repositories {
        mavenCentral()
        maven { url = uri("https://kotlin.bintray.com/kotlinx") }
        maven { url = uri("https://dl.bintray.com/kotlin/kotlin-eap") }
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.41")
        embeddedKotlin("kotlin-allopen")
        classpath("org.jetbrains.kotlin:kotlin-frontend-plugin:0.0.45")
    }
}

allprojects {
    apply {
        plugin("idea")
    }

    group = "coffee"

    repositories {
        mavenCentral()
        jcenter()
        maven { url = uri("https://dl.bintray.com/kotlin/ktor") }
    }
}