[![Release](https://jitpack.io/v/FormikoLudo/Utils.svg)]
(https://jitpack.io/#FormikoLudo/Utils)

# Utils
Tools common to all projects.

This project is a fresh restart from [usual](https://github.com/HydrolienF/usual) with minimal tool class for each Formiko Ludo project.

## Use

Use this library in a Gradle kts project with:
(replace LAST_VERSION by the last version from the release tab.)
```kts
repositories {
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.FormikoLudo:Utils:LAST_VERSION")
}
```


## Build

Build with `./gradlew assemble`. Jar will be in `build/libs/`

Publish to maven local with `./gradlew publishToMavenLocal`.
