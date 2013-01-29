#ean-android

This project contains two components:

- api-lib: An Android optimized java library to handle making calls to Expedia Affiliate Network API.
- sample-app: A sample Android app that uses the api-lib to make all of it's calls.

##Building both the Library and the App
It is possible to build the library and the app in a single step using the build.xml at the root of the project. This will build the library, publish it to a local artifactory repository, then resolve the dependencies of the Android project and fix up the local Android files and build the debug configuration of sample-app. 

###Requirements

- [Java JDK (1.6+)] [java]
- [Apache Ant (1.7+)] [apache-ant]
- [Android SDK] [android-sdk] needs to be installed and both the tools and platform-tools directories are assumed to be on the path. Additionally you'll need to have at least Android version 14 setup (4.0 Ice Cream Sandwich).

###Steps

1. Download the source code from git: `git clone git://ExpediaInc/ean-android/ean-android.git`
            
2. Open a command line in the cloned directory: `cd ean-android`

3. Now run the ant target to build the api-lib and sample-app: `ant`
    - The library has now been built, published to a local artifactory repository, the app has had its dependencies resolved and has been built in the debug configuration.
    
4. The output apk can be found at `ean-android/sample-app/bin/sample-app-debug.apk` and can be installed using the command line function `adb install ean-android/sample-app/bin/sample-app-debug.apk`.

##Building The Only Library

###Requirements

- [Java JDK (1.6+)] [java]
- [Apache Ant (1.7+)] [apache-ant]

###Steps
Command line versions of each step are in parenthesis.

1. Download the source code from git: `git clone git://ExpediaInc/ean-android/ean-android.git`

2. Open a command line in the cloned directory: `cd ean-android/api-lib`

3. Run the ant command publish: `ant publish`

3. You should now have built the api-lib and published it to the local artifactory repository.

The build (assuming it worked properly) will have resolved dependencies (creating and populating a lib/ folder in the process), checked the project for style issues, run junit tests, built the javadoc, and built jars to the build/ directory.
        
        ean-android/
            api-lib/
                build.xml
                build/
                    ean-api-lib.jar
                    ean-api-lib-all.jar
                lib/
                    dependencies
                    
ean-api-lib.jar contains the minimal set of classes to use the library, whereas ean-api-lib-full.jar contains not only the classes, but their sources and the generated javadoc as well.

To build without publishing, just run the default ant target `ant`.

##Building  Just The Sample App

###Requirements

- [Java JDK (1.6+)] [java]
- [Apache Ant (1.7+)] [apache-ant]
- [Android SDK] [android-sdk] needs to be installed and both the tools and platform-tools directories are assumed to be on the path. Additionally you'll need to have at least Android version 14 setup (4.0 Ice Cream Sandwich).

###Steps
1. Download the source code from git: `git clone git://ExpediaInc/ean-android/ean-android.git`
    - Ensure the library has already been published as described above.

2. Open a command line in the cloned directory: `cd ean-android/sample-app`

4. Next you will need to use the android command line tool to setup your environment for the project: `android update project -p ./`. This will set up the environment-specific files for the project.

5. Now sample-app can be built using `ant debug` the built project will be output to `ean-android/sample-app/bin/sample-app-debug.apk` and can then be pushed to an Android device or vm with `adb install ean-android/sample-app/bin/sample-app-debug.apk`

##IDE Setup Tips

###api-lib
- Do not include the android.jar on your classpath. The unit tests will be unable to run due to stubbing issues.
- Include src/app and src/stubs as project files. 
    - src/stubs provides stub functionality from android.jar for use during development and testing, but does not need to be included with the output jar.
- Add .json as a resource file type. This will allow certain IDEs (Intellij) to include the .json resources in the classpath for the tests that need them.

###sample-app
- run ant resolve.dependencies before setting up the project in your ide, as it will include all dependencies necessary.
- there is no need to include the libs/ivy folder, or the libs/check folder in the ide. These are only used by the build to manage dependencies and perform checkstyle checks, respectively.

[git-project]: http://ExpediaInc/ean-android/ean-android.git "ean-android project"
[java]: http://www.oracle.com/technetwork/java/javase/downloads/index.html "Java"
[apache-ant]: http://ant.apache.org/bindownload.cgi "Apache Ant"
[android-sdk]: http://developer.android.com/sdk/index.html "Android SDK"