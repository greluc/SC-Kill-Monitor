/**************************************************************************************************
 * SC Kill Monitor                                                                                *
 * Copyright (C) 2025-2025 SC Kill Monitor Team                                                   *
 *                                                                                                *
 * This file is part of SC Kill Monitor.                                                          *
 *                                                                                                *
 * SC Kill Monitor is free software: you can redistribute it and/or modify                        *
 * it under the terms of the GNU General Public License as published by                           *
 * the Free Software Foundation, either version 3 of the License, or                              *
 * (at your option) any later version.                                                            *
 *                                                                                                *
 * SC Kill Monitor is distributed in the hope that it will be useful,                             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                                 *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                                  *
 * GNU General Public License for more details.                                                   *
 *                                                                                                *
 * You should have received a copy of the GNU General Public License                              *
 * along with SC Kill Monitor. If not, see https://www.gnu.org/licenses/                          *
 **************************************************************************************************/

val checkstyleVersion="11.0.0" // https://github.com/checkstyle/checkstyle
val annotationsVersion="26.0.2" // https://mvnrepository.com/artifact/org.jetbrains/annotations https://github.com/JetBrains/java-annotations
val junitVersion = "5.13.4" // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-api
val junitLauncherVersion = "1.13.4" // https://mvnrepository.com/artifact/org.junit.platform/junit-platform-launcher
val mockitoVersion = "5.18.0" // https://mvnrepository.com/artifact/org.mockito/mockito-core
val atlantaFxVersion = "2.1.0" // https://mvnrepository.com/artifact/io.github.mkpaz/atlantafx-base
val log4j2Version = "2.25.2" // https://mvnrepository.com/artifact/org.apache.logging.log4j/log4j-core https://mvnrepository.com/artifact/org.apache.logging.log4j/log4j-api
val jacksonVersion = "2.19.2" // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
val semver4jVersion = "6.0.0" // https://mvnrepository.com/artifact/org.semver4j/semver4j
val mockitoAgent = configurations.create("mockitoAgent")

plugins {
  id("java")
  id("application")
  id("idea")
  id("jacoco")
  id("checkstyle")
  id("org.beryx.jlink") version "3.1.3" // https://plugins.gradle.org/plugin/org.beryx.jlink
  id("io.freefair.lombok") version "8.14" // https://plugins.gradle.org/plugin/io.freefair.lombok
  id("org.cyclonedx.bom") version "2.3.1" // https://github.com/CycloneDX/cyclonedx-gradle-plugin
  id("org.javamodularity.moduleplugin") version "1.8.15" // https://plugins.gradle.org/plugin/org.javamodularity.moduleplugin
  id("org.openjfx.javafxplugin") version "0.1.0" // https://plugins.gradle.org/plugin/org.openjfx.javafxplugin
}

group = "de.greluc.sc.sckm"
version = "1.6.0"
description = "SC Kill Monitor"

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.jetbrains:annotations:$annotationsVersion")
  implementation("io.github.mkpaz:atlantafx-base:${atlantaFxVersion}")
  implementation("org.apache.logging.log4j:log4j-core:${log4j2Version}")
  implementation("org.apache.logging.log4j:log4j-api:${log4j2Version}")
  implementation("com.fasterxml.jackson.core:jackson-databind:${jacksonVersion}")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${jacksonVersion}")
  implementation("org.semver4j:semver4j:${semver4jVersion}")
  testImplementation("org.mockito:mockito-core:${mockitoVersion}")
  mockitoAgent("org.mockito:mockito-core:${mockitoVersion}") { isTransitive = false }
  testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher:${junitLauncherVersion}")
}

java {
  sourceCompatibility = JavaVersion.VERSION_24
  targetCompatibility = JavaVersion.VERSION_24
  toolchain.languageVersion.set(JavaLanguageVersion.of(24))
  modularity.inferModulePath = true
  withSourcesJar()
}

javafx {
  version = "24"
  modules = listOf("javafx.controls", "javafx.fxml")
}

idea {
  module {
    inheritOutputDirs = true
    isDownloadJavadoc = true
    isDownloadSources = true
  }
}

application {
  mainModule.set("de.greluc.sc.sckm")
  mainClass.set("de.greluc.sc.sckm.ScKillMonitorApp")
  applicationDefaultJvmArgs = listOf("--enable-native-access=javafx.graphics")
}

checkstyle {
  toolVersion = checkstyleVersion
}

jlink {
  options.addAll(listOf("--strip-debug", "--compress", "zip-6", "--no-header-files", "--no-man-pages"))
  launcher{
    name = "SC Kill Monitor"
  }
  jpackage {
    if (org.gradle.internal.os.OperatingSystem.current().isWindows) {
      installerOptions.addAll(
        listOf(
          "--win-per-user-install",
          "--win-dir-chooser",
          "--win-menu",
          "--win-shortcut",
          "--vendor", "SC Kill Monitor Team",
          "--about-url", "https://github.com/greluc/SC-Kill-Monitor/wiki",
          "--app-version", version.toString(),
          "--copyright", "Copyright (C) 2025-2025 SC Kill Monitor Team",
          "--description", (project.description ?: "SC Kill Monitor")))
      //imageOptions.add("--win-console")
    }
  }
}

configurations {
  compileOnly {
    extendsFrom(configurations.annotationProcessor.get())
  }
}

tasks {
  withType(JavaCompile::class.java) {
    options.encoding = "UTF-8"
  }

  build {
    finalizedBy(cyclonedxBom)
  }

  javadoc {
    options {
      (this as CoreJavadocOptions).addStringOption("Xdoclint:none", "-quiet")
    }
    setDestinationDir(project.file("docs/javadoc"))
  }

  cyclonedxBom {
    projectType.set("library")
    schemaVersion.set("1.6")
    destination.set(project.file("docs"))
    outputName.set("bom")
    outputFormat.set("all")
    includeBomSerialNumber.set(true)
    includeLicenseText.set(true)
  }

  test {
    useJUnitPlatform()
    jvmArgs("-javaagent:${mockitoAgent.asPath}")
    finalizedBy(jacocoTestReport)
  }

  jacocoTestReport {
    dependsOn(test)
    reports {
      xml.required.set(true)
      csv.required.set(true)
      html.required.set(true)
    }
  }
}
