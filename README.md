ean-android
===========
===========
This project intends to:
- Provide a java library to ease implementation of the EAN API within a native android app.
- Provide a sample android app demonstrating use of this library.

To Setup IDE
============
Do not include android.jar on the classpath. The unit tests will be unable to run due to stubbing issues.
Include src/app and src/stubs as project files. 
src/stubs provides functionality that allows us to not use android.jar during test development, but does not need to be included with the output jar.