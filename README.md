#ean-android

This project intends to:
- Provide a java library to ease implementation of the EAN API within a native android app.
- Provide a sample android app demonstrating use of this library.

##To Build ean-android

###Requirements

- Java JDK (or equivalent)
- Gradle (to use build scripts)

1. Download the source code via whatever method you choose
2. In a shell in the same directory as a gradle.build run

        gradle build
3. You have now built ean-android

##To Setup IDE
Do not include android.jar on the classpath. The unit tests will be unable to run due to stubbing issues.
Include src/app and src/stubs as project files. 
src/stubs provides functionality that allows us to not use android.jar during test development, but does not need to be included with the output jar.