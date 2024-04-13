plugins {
    // Apply the java-library plugin for API and implementation separation.
    `java-library`
    `maven-publish` // Add ./gradlew publishToMavenLocal
    jacoco
}

version = "0.0.5"
group = "fr.formiko.utils"

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
    maven ("https://jitpack.io")
}

dependencies {
    // Use JUnit Jupiter for testing.
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // This dependency is exported to consumers, that is to say found on their compile classpath.
    api(libs.commons.math3)

    // This dependency is used internally, and not exposed to consumers on their own compile classpath.
    implementation(libs.guava)

    implementation("com.github.jitpack:gradle-simple:1.0")
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.assemble {
    dependsOn("jar")
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()

    testLogging {
		events("passed", "skipped", "failed")
        showStandardStreams = true
	}

    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}
tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
}


publishing {
    publications {
        create<MavenPublication>("maven") {
		    from(components["java"])
        }
    }
}