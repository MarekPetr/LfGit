package com.lfgit.executors;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.lfgit.utilites.Constants.BIN_DIR;
import static com.lfgit.utilites.Constants.FILES_DIR;
import static com.lfgit.utilites.Constants.LIB_DIR;
import static com.lfgit.utilites.Logger.LogMsg;

class BinaryExecutor {

    private Process mProcess = null;
    private String mExeDir;
    private static final String EOL = System.getProperty("line.separator");
    private ExecListener mCallback;

    BinaryExecutor(ExecListener callback) {
        mExeDir = BIN_DIR;
        mCallback = callback;
    }

    void run(String binary, String destDir, String... strings) {
        String exeBin = mExeDir + "/" + binary;
        File f = new File(destDir);
        if (binary.equals("git") &&
                (strings[0].equals("init") || strings[0].equals("clone"))) {
            if (!f.exists()) {
                f.mkdirs();
            }
        }
        List<String> args = new ArrayList<>();
        args.add(exeBin);
        args.addAll(Arrays.asList(strings));

        LogMsg("exe: " + Arrays.toString(args.toArray()));

        ProcessBuilder pb = new ProcessBuilder(args);
        pb.redirectErrorStream(true); // redirect error stream to input stream
        pb.directory(new File(destDir));
        Map<String, String> env = pb.environment();
        env.put("LD_LIBRARY_PATH", LIB_DIR + ":" + BIN_DIR);
        env.put("PATH", BIN_DIR);
        env.put("HOME", FILES_DIR);
        env.put("XDG_CONFIG_HOME",FILES_DIR);

        Process javap = null;
        try {
            javap = pb.start();
        } catch (IOException e) {
            mProcess = null;
            e.printStackTrace();
        }
        mProcess = javap;

        new Thread() {
            @Override
            public void run() {
                StringBuilder mOutBuffer = new StringBuilder();
                String result = "";
                int errCode;
                mCallback.onExecStarted();
                String line;
                try {
                    InputStream stdout = mProcess.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
                    /*while((line = reader.readLine()) != null) {
                        mOutBuffer.append(line).append(EOL);
                    }*/

                    File targetFile = new File("/storage/emulated/0/LfGit/strace_log.txt");
                    OutputStream outStream = new FileOutputStream(targetFile);

                    byte[] buffer = new byte[8 * 1024];
                    int bytesRead;
                    while ((bytesRead = stdout.read(buffer)) != -1) {
                        outStream.write(buffer, 0, bytesRead);
                    }

                } catch(IOException e) {
                    // ignore
                }

                try {
                    errCode = mProcess.waitFor();
                    //result = mOutBuffer.toString();
                    mCallback.onExecFinished(result, errCode);
                } catch (InterruptedException e) {
                    // ignore
                }
            }
        }.start();
    }
}


