package com.lfgit.tasks;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.lfgit.utilites.Constants.BIN_DIR;
import static com.lfgit.utilites.Constants.FILES_DIR;
import static com.lfgit.utilites.Constants.LIB_DIR;

abstract class AbstractExecutor {

    private String mResult;
    String mExeDir;

    AbstractExecutor() {
        mExeDir = BIN_DIR;
    }

    String getResult() {
        return mResult;
    }

    Integer executeBinary(String binary, String destDir, String... strings) {
        String exeBin = mExeDir + binary;
        Integer errCode = 0;

        String dirPath = "";
        dirPath = Environment.getExternalStorageDirectory().toString() + "/" + destDir;
        File f = new File(Environment.getExternalStorageDirectory(), destDir);
        if (strings[0].equals("init")) {
            if (!f.exists()) {
                f.mkdirs();
            }
        }
        List<String> args = new ArrayList<>();
        args.add(exeBin);
        args.addAll(Arrays.asList(strings));

        Log.d("petr", "exe: " + Arrays.toString(args.toArray()));

        ProcessBuilder pb = new ProcessBuilder(args);
        pb.redirectErrorStream(true); // redirect error stream to input stream
        pb.directory(new File(dirPath));
        Map<String, String> env = pb.environment();
        Log.d("petr", "LIB_DIR: " + LIB_DIR);
        env.put("LD_LIBRARY_PATH", LIB_DIR);
        env.put("PATH", BIN_DIR);
        env.put("HOME", FILES_DIR);

        Process javap;
        Buffer buffer;
        try {
            javap = pb.start();
            buffer = new Buffer(javap.getInputStream());
            errCode = javap.waitFor();
            mResult = buffer.getOutput();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        if (errCode == 0) {
            mResult += "\nOperation successful";
        } else {
            mResult += "\nOperation failed";
        }

        return errCode;
    }
    // source https://github.com/jjNford/android-shell/blob/master/src/com/jjnford/android/util/Shell.java
    private static class Buffer extends Thread {
        private InputStream mInputStream;
        private StringBuffer mBuffer;
        private static final String EOL = System.getProperty("line.separator");

        /**
         * @param inputStream Data stream to get shell output from.
         */
        Buffer(InputStream inputStream) {
            mInputStream = inputStream;
            mBuffer = new StringBuffer();
            this.start();
        }

        public String getOutput() {
            try {
                this.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return mBuffer.toString();
        }

        @Override
        public void run() {
            try {
                String line;
                BufferedReader reader = new BufferedReader(new InputStreamReader(mInputStream));
                if((line = reader.readLine()) != null) {
                    mBuffer.append(line);
                    while((line = reader.readLine()) != null) {
                        mBuffer.append(EOL).append(line);
                    }
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}
