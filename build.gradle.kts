import java.util.Properties

plugins {
  id("java-library")
  id("idea")
}

var deps: Properties by rootProject.ext
deps = Properties()
file("gradle/deps.properties").reader().use(deps::load)

allprojects {
  group = "org.semgus"
  version = deps.getProperty("version.project")
}

subprojects {
  apply {
    plugin("java-library")
    plugin("idea")
  }

  val javaVersion = 17

  java {
    toolchain {
      languageVersion.set(JavaLanguageVersion.of(javaVersion))
    }
  }

  tasks.withType<JavaCompile>().configureEach {
    modularity.inferModulePath.set(true)
    options.apply {
      encoding = "UTF-8"
      isDeprecation = true
      release.set(javaVersion)
      compilerArgs.addAll(listOf("-Xlint:unchecked", "--enable-preview"))
    }
  }

  tasks.withType<Test>().configureEach {
    jvmArgs = listOf("--enable-preview")
    useJUnitPlatform()
    enableAssertions = true
    reports.junitXml.mergeReruns.set(true)
    testLogging.showStandardStreams = true
    testLogging.showCauses = true
  }

  tasks.withType<JavaExec>().configureEach {
    jvmArgs = listOf("--enable-preview")
    enableAssertions = true
  }
}
