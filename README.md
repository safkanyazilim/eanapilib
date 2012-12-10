#ean-android

This project intends to:
- Provide a java library to ease implementation of the EAN API within a native android app.
- Provide a sample android app demonstrating use of this library.

##Building

###Requirements

- Java JDK
- Apache Ant 1.7+

1. Download the source code via whatever method you choose
    - With a github client:
    
            git clone git://github.com/aaronklor/ean-android.git
2. In the same directory as build.xml (ean-android/api-lib/) run ant. For example:

        cd /path/to/ean-android
        cd api-lib
        ant
3. You have now built ean-android

The build (assuming it worked properly) will have resolved dependencies (creating and populating a lib/ folder in the process), checked the project for style issues, run junit tests, built the javadoc, and built jars to the build/ directory.
        
        ean-android/
            api-lib/
                build.xml
                build/
                    ean-api-lib.jar
                    ean-api-lib-all.jar
                lib/
                    some jars
                    
ean-api-lib.jar contains the minimal set of classes to use the library, whereas ean-api-lib-all.jar contains not only the classes, but their sources and the generated javadoc as well.
##IDE Setup Tips
- Do not include android.jar on the classpath. The unit tests will be unable to run due to stubbing issues.
- Include src/app and src/stubs as project files. 
    - src/stubs provides functionality that allows us to not need android.jar during development and testing, but does not need to be included with the output jar.