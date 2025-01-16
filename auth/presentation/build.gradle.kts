@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.runtracker.android.feature.ui)
}

android {
    namespace = "com.example.auth.presentation"

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(projects.core.domain)
    implementation(projects.auth.domain)
}