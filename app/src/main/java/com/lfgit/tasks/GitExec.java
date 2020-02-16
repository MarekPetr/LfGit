package com.lfgit.tasks;

import static com.lfgit.utilites.Constants.REPOS_DIR;
import static com.lfgit.utilites.Logger.LogAny;

public class GitExec extends AbstractExecutor {

    public void config() {
        envExeForRes("git", "","config", "--global", "user.email", "petr.marek18@gmail.com");
        LogAny(getResult());
        envExeForRes("git", "", "config", "--global", "user.name", "MarekPetr");
        LogAny(getResult());
    }

    public String busybox_echo() {
        envExeForRes("busybox", "", "echo", "ahoj");
        return getResult();
    }


    public String init(String dest) {
        String gitOperation = "init";
        envExeForRes("git", dest, gitOperation);
        return getResult();
    }

    public String commit() {
        String gitOperation = "commit";
        String message = "-m\"newFileToCommit\"";
        String destDir = REPOS_DIR + "clone/test";
        envExeForRes("git", destDir, gitOperation, message);
        return getResult();
    }

    public String clone(String dest, String userName, String password) {
        String gitOperation = "clone";
        String url = "https://" + userName + ":" + password + "@github.com/MarekPetr/test";
        envExeForRes("git", dest, gitOperation, url);
        return getResult();
    }

    public String status() {
        String gitOperation = "status";
        String destDir = REPOS_DIR + "repo/";
        envExeForRes("git", destDir, gitOperation);
        return getResult();
    }

    public String add() {
        String gitOperation = "add";
        String destDir = REPOS_DIR + "clone/test";
        envExeForRes("git", destDir, gitOperation, ".");
        return getResult();
    }

    public String push() {
        String gitOperation = "push";
        String destDir = REPOS_DIR + "clone/test";
         envExeForRes("git", destDir, gitOperation);
        return getResult();
    }
}
