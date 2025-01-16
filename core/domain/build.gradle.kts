@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.runtracker.jvm.library)

}

//java {
//    sourceCompatibility = JavaVersion.VERSION_1_8
//    targetCompatibility = JavaVersion.VERSION_1_8
//}
dependencies {
    implementation(libs.kotlinx.coroutines.core)

}
