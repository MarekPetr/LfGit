package com.lfgit.database;
import android.app.Application;
import androidx.lifecycle.LiveData;
import com.lfgit.database.model.Repo;

import java.io.File;
import java.util.List;

public class RepoRepository {
    private RepoDao mRepoDao;
    private LiveData<List<Repo>> mAllRepos;

    public RepoRepository(Application application) {
        RepoDatabase db = RepoDatabase.getInstance(application);
        mRepoDao = db.repoDao();
        mAllRepos = mRepoDao.getAllRepos();
    }

    public void insertRepo(Repo repo) {
        RepoDatabase.databaseWriteExecutor.execute(() -> mRepoDao.insertRepo(repo));
    }

    public void insertList(List<Repo> repos) {
        RepoDatabase.databaseWriteExecutor.execute(() -> mRepoDao.insertList(repos));
    }

    public LiveData<List<Repo>> getAllRepos() {
        return mAllRepos;
    }

    public void deleteByID(int repoId) {
        RepoDatabase.databaseWriteExecutor.execute(() -> mRepoDao.deleteByRepoId(repoId));
    }

    public void updateCredentials(Repo repo) {
        String username = repo.getUsername();
        String password = repo.getPassword();
        int id = repo.getId();
        RepoDatabase.databaseWriteExecutor.execute(() -> mRepoDao.updateCredentials(username, password, id));
    }

    public void updateRemoteURL(Repo repo) {
        String remoteURL = repo.getRemoteURL();
        int id = repo.getId();
        RepoDatabase.databaseWriteExecutor.execute(() -> mRepoDao.updateRemoteURL(remoteURL, id));
    }

    public Boolean repoDirExists(Repo repo) {
        String path = repo.getLocalPath();
        File file = new File(path);
        return file.exists();
    }
}
