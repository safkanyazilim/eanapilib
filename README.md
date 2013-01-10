#ean-android

This project intends to:

- Provide a java library to ease implementation of the EAN API within a native android app.
- Provide a sample android app demonstrating use of this library.

##Building both the Library and the App
It is possible to build the library and the app in a single step using the build.xml at the root of ean-android. This will build the library, publish it to a local artifactory repository, then resolve the dependencies of the android project and fix up the local android files and build the debug configuration of sample-app.

###Requirements
- Java JDK (1.6+)
- Apache Ant (1.7+)
- Apache Ivy (2.2.0+)
- Android sdk on path, with api at least level 14 (Android 4.0) installed

###Steps
1. Download the source code via whatever method you choose
    - With a git client:

            git clone git://ExpediaInc/ean-android/ean-android.git
2. cd to the newly cloned directory (ean-android)
3. Now run the ant target to build the api-lib and sample-app

        ant
    - The library has now been built, published, and the app has had its dependencies resolved and has been built in the debug configuration.
4. The output apk can be found at

        ean-android/sample-app/bin/sample-app-debug.apk
    and can be installed using

        adb install ean-android/sample-app/bin/sample-app-debug.apk

##Building The Library

###Requirements

- Java JDK (1.6+)
- Apache Ant (1.7+)
- Apache Ivy (2.2.0+)

###Steps
1. Download the source code via whatever method you choose (this setup is redudant if you completed in the previous section).

    - With a git client:

            git clone git://ExpediaInc/ean-android/ean-android.git
2. In the same directory as build.xml (ean-android/api-lib/) run ant.

        cd /path/to/ean-android
        cd api-lib
        ant publish
3. You have now built the api-lib and published it to the local repository.

The build (assuming it worked properly) will have resolved dependencies (creating and populating a lib/ folder in the process), checked the project for style issues, run junit tests, built the javadoc, and built jars to the build/ directory.
        
        ean-android/
            api-lib/
                build.xml
                build/
                    ean-api-lib.jar
                    ean-api-lib-all.jar
                lib/
                    some jars
                    
ean-api-lib.jar contains the minimal set of classes to use the library, whereas ean-api-lib-full.jar contains not only the classes, but their sources and the generated javadoc as well.

To build without publishing, just run the default ant target:

        ant
##Building The Sample App

###Requirements

- Java JDK (1.6+)
- Apache Ant (1.7+)
- Android sdk on path, with api at least level 14 (Android 4.0) installed

###Steps
1. Download the source code via whatever method you choose (this setup is redudant if you completed either of the previous sections).

    - With a git client:

            git clone git://ExpediaInc/ean-android/ean-android.git
    - Ensure the library has been published as described above.

3. In a terminal, cd to the sample app's main directory (ean-android/sample-app).

        cd ean-android/sample-app
4. Create the local files required for using ant on the sample-app, as specified by the android sdk.

        android update project -p ./
    - This will set up the environment-specific files for the project.
5. Now sample-app can be built using

        ant debug
    the built project will be output to

        ean-android/sample-app/bin/sample-app-debug.apk
    and can then be pushed to an android device or vm with

        adb install ean-android/sample-app/bin/sample-app-debug.apk


##IDE Setup Tips

###api-lib
- Do not include android.jar on the classpath. The unit tests will be unable to run due to stubbing issues.
- Include src/app and src/stubs as project files. 
    - src/stubs provides functionality that allows us to not need android.jar during development and testing, but does not need to be included with the output jar.

###sample-app
- run ant resolve.dependencies before setting up the project in your ide, as it will include all dependencies necessary.
- there is no need to include the libs/ivy folder, or the libs/check folder in the ide. These are only used by the build to manage dependencies and perform checkstyle checks, respectively.
