This app is designed to communicate with Arduino via Bluetooth, display Google Maps on the UI using the Google Map SDK, and send data calculated within the app to a Cloud server. 

The app counts the time in seconds from when it receives the Bluetooth data string "hello" until Arduino stops sending data within 3 seconds and then sends this data to a Cloud server, Firebase.

The architecture follows the MVVM pattern, utilizing only the UI layer and consisting of two modules: `app` and `ui`. 

The `app` module is the entry point, while the `ui` module is a library module. 

My initial goal was to adhere to the architecture recommended by Android (UI - domain - data), similar to Clean Architecture, although Android officially states that the two are different. 

However, I first created only the `ui` module as a prototype.

The following list outlines the core frameworks, libraries, and APIs used in this app:

1. **Dagger-Hilt** for Dependency Injection (DI).
2. **KSP** instead of Kapt for faster builds.
3. **Google Services** for Google Map SDK in Google Cloud.
4. **Jetpack Compose** for UI Design with Material3 and Navigation.

The app requires permissions for Bluetooth communication, Internet access, device location, and notifications. 

These permissions are specified in the manifest file in the `app` module as requirements.

I injected the ViewModel into the MainActivity to receive data from the Bluetooth adapter. 

As I built the code, I encountered the biggest problem in this project. 

The first issue was that I omitted the package name in the ViewModel file, so MainActivity didn't recognize the ViewModel. 

However, I didn't realize this and added "androidx.hilt:hilt-lifecycle-viewmodel", which is no longer required for the updated version of Hilt since 2.31. 

This caused serious conflicts with other dependencies, resulting in a compile error. 

Even after discovering the initial issue of omitting the package name, I could not identify the second problem, spending 5 hours before finally finding it.
