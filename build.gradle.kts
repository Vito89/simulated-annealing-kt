import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion: String by project

plugins {
    val kotlinVersion = "1.8.22"
    val detektVersion = "1.19.0"

    kotlin("jvm") version kotlinVersion apply false
    id("io.gitlab.arturbosch.detekt") version detektVersion apply false
}

allprojects {
    group = "com.vito"
    version = "0.0.1-SNAPSHOT"
}

subprojects {
    apply(plugin = "kotlin")

    apply(from = "$rootDir/gradle/ktlint.gradle.kts")
    apply(plugin = "io.gitlab.arturbosch.detekt")

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = JavaVersion.VERSION_17.toString()
        }
    }

    tasks.getByName<Jar>("jar") {
        enabled = false
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            events = setOf(
                TestLogEvent.FAILED,
                TestLogEvent.PASSED,
                TestLogEvent.SKIPPED
            )
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
            showExceptions = true
            showCauses = true
            showStackTraces = true
        }
    }

    tasks.withType<Detekt> {
        exclude("resources/")
        exclude("build/")
        config.setFrom(files("$rootDir/gradle/detekt-config.yml"))
    }
}
