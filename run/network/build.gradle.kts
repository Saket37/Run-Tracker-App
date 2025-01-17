@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.runtracker.jvm.library)

    alias(libs.plugins.runtracker.jvm.ktor)

}

//java {
//    sourceCompatibility = JavaVersion.VERSION_1_7
//    targetCompatibility = JavaVersion.VERSION_1_7
//}

dependencies {
//    implementation(projects.core.domain)
//    implementation(projects.core.data)
}

