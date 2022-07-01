dependencies {
  val deps: java.util.Properties by rootProject.ext
  api("org.jetbrains", "annotations", version = deps.getProperty("version.annotations"))
  implementation("commons-io", "commons-io", version = deps.getProperty("version.commons-io"))
  testImplementation(project(":semgus-test-util"))
  testImplementation("org.junit.jupiter", "junit-jupiter", version = deps.getProperty("version.junit"))
}
