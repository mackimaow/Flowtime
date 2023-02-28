import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack

val kotlinVersion = "1.8.10"
val serializationVersion = "1.5.0"
val ktorVersion = "2.0.3"
val logbackVersion = "1.2.11"
val kotlinWrappersVersion = "1.0.0-pre.354"

// <Stuff I added>
// common
val exposedVersion = "0.40.1"
// jvm
val sqliteVersion = "3.30.1"
val kotlinxDatetime = "0.4.0"
// javascript
val muiCoreVersion = "5.11.3"
val muiEmotionReactVersion = "11.10.5"
val muiEmotionStyleVersion = muiEmotionReactVersion
val robotoFontVersion = "4.5.8"
val muiMaterialIconsVersion = "5.11.0"
val muiLabVersion = "5.0.0-alpha.115"
val kotlinStyledVersion = "5.3.6-pre.473"
val kotlinMuiVersion = "5.9.1-pre.474"
val kotlinMuiIconsVersion = "5.10.9-pre.474"
var reactCookieVersion = "4.1.1"
var universalCookieVersion = "4.0.4"
val kotlinUnionVersion = "1.0.0"
val coroutinesVersion = "1.6.4"
val reactTableVersion = "7.8.0"
val reactTableWrapperVersion = "8.7.9-pre.496"
val reactColorVersion = "2.19.3"
val reactTransitionGroupVersion = "4.4.5"
// <Stuff I added />

plugins {
    kotlin("multiplatform") version "1.8.10"
    application
    kotlin("plugin.serialization") version "1.8.10"
}

group = "io.github.mackimaow"
version = "1.0.0"

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        withJava()
    }
    js(IR) {
        binaries.executable()
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("io.ktor:ktor-serialization:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinxDatetime")
                implementation("io.github.mackimaow:kotlin-union:$kotlinUnionVersion")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-server-cors:$ktorVersion")
                implementation("io.ktor:ktor-server-compression:$ktorVersion")
                implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
                implementation("io.ktor:ktor-server-netty:$ktorVersion")
                implementation("ch.qos.logback:logback-classic:$logbackVersion")
                // <Stuff I added >
                // exposed
                implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
                implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
                implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
                implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
                // sqlite
                implementation("org.xerial:sqlite-jdbc:$sqliteVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
                // ktor sessions
                implementation("io.ktor:ktor-server-sessions:$ktorVersion")
                // <Stuff I added />
            }
        }

        val jsMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-js:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
                implementation(project.dependencies.enforcedPlatform("org.jetbrains.kotlin-wrappers:kotlin-wrappers-bom:$kotlinWrappersVersion"))
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom")
                // <Stuff I added>
                // Kotlin styled
                implementation(
                    "org.jetbrains.kotlin-wrappers:kotlin-styled:$kotlinStyledVersion"
                )
                // Material UI
                implementation(npm(
                    "@mui/material",
                    "$muiCoreVersion"
                ))
                implementation(npm(
                    "@emotion/react",
                    "$muiEmotionReactVersion"
                ))
                implementation(npm(
                    "@emotion/styled",
                    "$muiEmotionStyleVersion"
                ))
                implementation(npm(
                    "@mui/icons-material",
                    "$muiMaterialIconsVersion"
                ))
                // Roboto Font (used in Material UI)
                implementation(npm(
                    "@fontsource/roboto",
                    "$robotoFontVersion"
                ))
                // MUI experimental features
                implementation(npm("@mui/lab", "$muiLabVersion"))
                // Material UI Kotlin Extensions:
                implementation("org.jetbrains.kotlin-wrappers:kotlin-mui:$kotlinMuiVersion")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-mui-icons:$kotlinMuiIconsVersion")
                // React-Cookie
                implementation(npm(
                    "react-cookie",
                    "$reactCookieVersion"
                ))
                // universal-cookie
                implementation(npm(
                    "universal-cookie",
                    "$universalCookieVersion"
                ))
                // react table
                implementation(
                    "org.jetbrains.kotlin-wrappers:kotlin-tanstack-react-table:$reactTableWrapperVersion"
                )
                implementation(
                    "org.jetbrains.kotlin-wrappers:kotlin-tanstack-table-core:$reactTableWrapperVersion"
                )
                // react-color
                implementation(npm(
                    "react-color",
                    "$reactColorVersion"
                ))
                // react-transition-group
                implementation(npm(
                    "react-transition-group",
                    "$reactTransitionGroupVersion"
                ))
                // <Stuff I added />
            }
        }
    }
}

application {
    applicationDefaultJvmArgs = listOf(
        "--add-opens=java.base/java.time=ALL-UNNAMED",
        "-Dio.netty.tryReflectionSetAccessible=true"
    ) //to run JVM part
    mainClass.set("ServerKt")
}

// include JS artifacts in any JAR we generate
tasks.getByName<Jar>("jvmJar") {
    val taskName = if (project.hasProperty("isProduction")
        || project.gradle.startParameter.taskNames.contains("installDist")
    ) {
        "jsBrowserProductionWebpack"
    } else {
        "jsBrowserDevelopmentWebpack"
    }
    val webpackTask = tasks.getByName<KotlinWebpack>(taskName)
    dependsOn(webpackTask) // make sure JS gets compiled first
    from(File(webpackTask.destinationDirectory, webpackTask.outputFileName)) // bring output file along into the JAR
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}

distributions {
    main {
        contents {
            from("$buildDir/libs") {
                rename("${rootProject.name}-jvm", rootProject.name)
                into("lib")
            }
        }
    }
}

// Alias "installDist" as "stage" (for cloud providers)
tasks.create("stage") {
    dependsOn(tasks.getByName("installDist"))
}

tasks.getByName<JavaExec>("run") {
    classpath(tasks.getByName<Jar>("jvmJar")) // so that the JS artifacts generated by `jvmJar` can be found and served
}
