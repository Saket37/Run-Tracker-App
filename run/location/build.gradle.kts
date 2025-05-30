plugins {
    alias(libs.plugins.runtracker.android.library)

}

android {
    namespace = "com.example.run.location"

}

dependencies {
    //implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.core.ktx)

//    implementation(libs.androidx.compose.ui)
//    implementation(libs.androidx.compose.ui.tooling.preview)
//    implementation(libs.androidx.compose.ui.graphics)
//    implementation(libs.androidx.compose.material3)
//    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.google.android.gms.play.services.location)

    implementation(projects.core.domain)
    implementation(projects.run.domain)
    implementation(libs.bundles.koin)
}