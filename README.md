#ean-android

This project intends to:
- Provide a java library to ease implementation of the EAN API within a native android app.
- Provide a sample android app demonstrating use of this library.

##Building both the Library and the App
It is possible to build the library and the app in a single step using the parent build.xml at the root of ean-android. This will build the library, publish it to a local repository, then resolve the dependencies of the android project. Once those steps have completed, the final step of updating the project for your local environment will still need to occur.

These steps assume familiarity with the command line, ant, and file paths.
###Requirements
- Java JDK (1.6+)
- Apache Ant (1.7+)
- Android sdk on path, with api at least level 14 installed

###Steps
1. Clone the repository
2. cd to the newly cloned directory (ean-android)
3. Now run the ant target to build the api-lib and resolve the dependencies for sample-app

        ant resolve
    - The library has now been built, published, and the app has had its dependencies resolved.
4. cd to sample-app
5. Create the local files required for using ant on the sample-app, as specified by the android sdk.

        android update project -p ./
    - This will set up the environment-specific files for the project.
6. Now sample-app can be built using

        ant debug
    the built project will be output to

        ean-android/sample-app/bin/sample-app-debug.apk
    and can then be pushed to an android device or vm with

        adb install ean-android/sample-app/bin/sample-app-debug.apk


##Building The Library

###Requirements

- Java JDK (1.6+)
- Apache Ant (1.7+)

###Steps
1. Download the source code via whatever method you choose
    - With a git client:
    
            git clone git://github.com/aaronklor/ean-android.git
2. In the same directory as build.xml (ean-android/api-lib/) run ant.

        cd /path/to/ean-android
        cd api-lib
        ant
3. You have now built the api-lib.

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

To publish the newly built jars to a local repository so that the sample-app can access them in its build script run

        ant publish
instead of

        ant
to build, test, and deploy the newly made jars to a local repository where it can be accessed by the sample-app build script.

##Building The Sample App

###Requirements

- Java JDK (1.6+)
- Apache Ant (1.7+)
- Android sdk on path, with api at least level 14 installed

###Steps
1. Download the source code
    - If you downloaded the libarary, you already have the source
    - Otherwise, follow the same download instructions as the libarary above.
2. In a terminal, cd to the sample app's main directory (ean-android/sample-app).

3. Follow the instructions from steps 5 and 6 of the "Building the Library and the App" section above.


##IDE Setup Tips

###api-lib
- Do not include android.jar on the classpath. The unit tests will be unable to run due to stubbing issues.
- Include src/app and src/stubs as project files. 
    - src/stubs provides functionality that allows us to not need android.jar during development and testing, but does not need to be included with the output jar.

###sample-app
- run ant resolve.dependencies before setting up the project in your ide, as it will include all dependencies necessary.
- there is no need to include the libs/ivy folder, or the libs/check folder in the ide. These are only used by the build to manage dependencies and perform checkstyle checks, respectively.