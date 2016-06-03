# An Android library for downloading files

## Features:
* Support add, pause, resume and delete of download task.
* Support continue downloading from where interrupted.
* All tasks will be recorded in sqlite db.
* Multi downloading threads.
* Auto retry on fail.

## Project description:

Module tugcore is the library for downloading, and app is a demo.

## Usage:
    // initialization
    int logLevel = BuildConfig.DEBUG ? Log.VERBOSE : Log.INFO;
    Tug tug = new Tug.Builder(this)
            .setNeedLog(true)
            .setLogLevel(logLevel)
            .setThreads(2)
            .build();
    Tug.setInstance(tug);
    Tug.getInstance().start();

    // add task to download
    String url = "http://..."; // file url to download
    String filePath = "/sdcard/..."; // local file path to save
    DownloadListener downloadListener = ...; // download listener to receive callback events
    Tug.getInstance().addTask(url, TugTask.FileType.FILE, filePath, downloadListener);

## Using Tug in your application

If you are building with Gradle, simply add the following line to the `dependencies` section of your `build.gradle` file:

```groovy
compile 'me.chatgame.mobilecg:tugcore:1.1.1'
```

And add `mavenCentral()` to `repositories` section in `build.gradle`