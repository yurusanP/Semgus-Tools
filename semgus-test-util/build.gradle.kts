dependencies {
  val deps: java.util.Properties by rootProject.ext
  api("org.jetbrains", "annotations", version = deps.getProperty("version.annotations"))
  implementation(project(":semgus-sketch"))
  implementation("com.googlecode.json-simple", "json-simple", version = deps.getProperty("version.json-simple"))
  implementation("com.github.SemGuS-git", "Semgus-Java", version = deps.getProperty("version.semgus-java"))
  implementation(project(":semgus-parser-util"))
}
