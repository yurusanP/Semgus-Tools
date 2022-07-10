dependencies {
  val deps: java.util.Properties by rootProject.ext
  api("org.jetbrains", "annotations", version = deps.getProperty("version.annotations"))
  implementation(project(":semgus-pretty"))
  implementation("com.github.SemGuS-git", "Semgus-Java", version = deps.getProperty("version.semgus-java"))
  testImplementation(project(":semgus-test-util"))
  testImplementation("org.junit.jupiter", "junit-jupiter", version = deps.getProperty("version.junit"))
}
