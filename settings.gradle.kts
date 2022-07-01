rootProject.name = "semgus-tools"

dependencyResolutionManagement {
  @Suppress("UnstableApiUsage") repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
  }
}

include(
  "semgus-parser-util",
  "semgus-pretty",
  "semgus-sketch",
  "semgus-test-util",
)
