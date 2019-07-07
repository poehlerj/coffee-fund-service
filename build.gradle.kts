buildscript {
    val kotlinVersion = "1.3.41"

    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("org.jetbrains.kotlin:kotlin-allopen:$kotlinVersion")
    }
}

allprojects {
    apply {
        plugin("kotlin")
        plugin("idea")
    }

    group = "coffee"

    repositories {
        mavenCentral()
        jcenter()
        maven { url = uri("https://dl.bintray.com/kotlin/ktor") }
    }
}