val kotlinTest: String by project
val junitVersion: String by project

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlinTest")
}
