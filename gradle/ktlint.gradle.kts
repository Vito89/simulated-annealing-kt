val ktlint by configurations.creating

dependencies.add("ktlint", "com.pinterest:ktlint:0.50.0")
// https://pinterest.github.io/ktlint/1.0.0/install/integrations/

val ktlintCheck by
tasks.creating(JavaExec::class) {
    group = LifecycleBasePlugin.VERIFICATION_GROUP
    description = "Check Kotlin code style"
    classpath = ktlint
    mainClass.set("com.pinterest.ktlint.Main")
    args(
        "**/src/**/*.kt",
        "**.kts",
        "!**/build/**",
    )
}

val ktlintFormat by
tasks.register<JavaExec>("ktlintFormat") {
    group = LifecycleBasePlugin.VERIFICATION_GROUP
    description = "Check Kotlin code style and format"
    classpath = ktlint
    mainClass.set("com.pinterest.ktlint.Main")
    jvmArgs("--add-opens", "java.base/java.lang=ALL-UNNAMED")
    args(
        "-F",
        "**/src/**/*.kt",
        "**.kts",
        "!**/build/**",
    )
}

tasks["ktlintCheck"].dependsOn(ktlintFormat)

tasks["check"].dependsOn(ktlintCheck)
