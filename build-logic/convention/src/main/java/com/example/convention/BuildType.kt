package com.example.convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.BuildType
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

internal fun Project.configureBuildTypes(
    commonExtension: CommonExtension<*, *, *, *, *>,
    extensionType: ExtensionType
) {
    commonExtension.run {
        buildFeatures {
            buildConfig = true
        }
        val apiKey = gradleLocalProperties(rootDir).getProperty("API_KEY")
        val baseUrl = gradleLocalProperties(rootDir).getProperty("BASE_URL")
        when (extensionType) {
            ExtensionType.APPLICATION -> {
                extensions.configure<ApplicationExtension> {
                    buildTypes {
                        debug {
                            configureDebugBuildType(apiKey, baseUrl)
                        }
                        release {
                            configureReleaseBuildType(apiKey, commonExtension, baseUrl)
                        }
                    }
                }
            }

            ExtensionType.LIBRARY -> {
                extensions.configure<LibraryExtension> {
                    buildTypes {
                        debug {
                            configureDebugBuildType(apiKey, baseUrl)
                        }
                        release {
                            configureReleaseBuildType(apiKey, commonExtension, baseUrl)
                        }
                    }
                }
            }
        }

    }
}

private fun BuildType.configureDebugBuildType(
    apiKey: String,
    baseUrl: String
) {
    buildConfigField("String", "API_KEY", apiKey)
    buildConfigField("String", "BASE_URL", baseUrl)
}

private fun BuildType.configureReleaseBuildType(
    apiKey: String,
    commonExtension: CommonExtension<*, *, *, *, *>,
    baseUrl: String
) {
    buildConfigField("String", "API_KEY", "\"$apiKey\"")
    buildConfigField("String", "BASE_URL", baseUrl)

    isMinifyEnabled = false
    proguardFiles(
        commonExtension.getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
    )
}

enum class ExtensionType {
    APPLICATION,
    LIBRARY
}