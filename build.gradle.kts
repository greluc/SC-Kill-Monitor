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

val checkstyleVersion="10.21.4" // https://github.com/checkstyle/checkstyle
val annotationsVersion="26.0.2" // https://mvnrepository.com/artifact/org.jetbrains/annotations https://github.com/JetBrains/java-annotations
val junitVersion = "5.11.4" // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-api
val mockitoVersion = "5.15.2" // https://mvnrepository.com/artifact/org.mockito/mockito-core
val atlantaFxVersion = "2.0.1" // https://mvnrepository.com/artifact/io.github.mkpaz/atlantafx-base
val log4j2Version = "2.24.3" // https://mvnrepository.com/artifact/org.apache.logging.log4j/log4j-core https://mvnrepository.com/artifact/org.apache.logging.log4j/log4j-api
val jacksonVersion = "2.18.3" // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
val mockitoAgent = configurations.create("mockitoAgent")

plugins {
  id("java")
  id("application")
  id("idea")
  id("jacoco")
  id("checkstyle")
  id("io.freefair.lombok") version "8.12.2.1" // https://plugins.gradle.org/plugin/io.freefair.lombok
  id("org.cyclonedx.bom") version "2.2.0" // https://github.com/CycloneDX/cyclonedx-gradle-plugin
  id("dev.hydraulic.conveyor") version "1.12" // https://plugins.gradle.org/plugin/dev.hydraulic.conveyor
  id("org.javamodularity.moduleplugin") version "1.8.15" // https://plugins.gradle.org/plugin/org.javamodularity.moduleplugin
  id("org.openjfx.javafxplugin") version "0.1.0" // https://plugins.gradle.org/plugin/org.openjfx.javafxplugin
}

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
  testImplementation("org.mockito:mockito-core:${mockitoVersion}")
  mockitoAgent("org.mockito:mockito-core:${mockitoVersion}") { isTransitive = false }
  testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
}

base {
  group = "de.greluc.sc"
  version = "1.3.0.RC4"
  description = "SC Kill Monitor - See who griefed you!"
}

configurations {
  compileOnly {
    extendsFrom(configurations.annotationProcessor.get())
  }
}

java {
  sourceCompatibility = JavaVersion.VERSION_23
  targetCompatibility = JavaVersion.VERSION_23
  toolchain.languageVersion.set(JavaLanguageVersion.of(23))
  modularity.inferModulePath = true
  withSourcesJar()
}

tasks.withType(JavaCompile::class.java) {
  options.encoding = "UTF-8"
}

idea {
  module {
    inheritOutputDirs = true
    isDownloadJavadoc = true
    isDownloadSources = true
  }
}

application {
  mainModule = "de.greluc.sc.sckm"
  mainClass = "de.greluc.sc.sckm.ScKillMonitorApp"
}

javafx {
  version = "23"
  modules = listOf("javafx.controls", "javafx.fxml")
}

checkstyle {
  toolVersion = checkstyleVersion
}

tasks.cyclonedxBom {
  setProjectType("library")
  setSchemaVersion("1.6")
  setDestination(project.file("docs/bom"))
  setOutputName("bom")
  setOutputFormat("all")
  setIncludeBomSerialNumber(true)
  setIncludeLicenseText(true)
}

tasks.javadoc {
  options {
    (this as CoreJavadocOptions).addStringOption("Xdoclint:none", "-quiet")
  }
  setDestinationDir(project.file("docs/javadoc"))
}

tasks.test {
  useJUnitPlatform()
  finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
  dependsOn(tasks.test)
  reports {
    xml.required.set(true)
    csv.required.set(true)
    html.required.set(true)
  }
}

tasks {
  test {
    jvmArgs("-javaagent:${mockitoAgent.asPath}")
  }
}

