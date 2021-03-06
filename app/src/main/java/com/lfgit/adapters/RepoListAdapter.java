package com.lfgit.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;

import com.lfgit.R;
import com.lfgit.activities.BasicAbstractActivity;
import com.lfgit.activities.RepoTasksActivity;
import com.lfgit.database.model.Repo;
import com.lfgit.view_models.RepoListViewModel;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Bind repositories to the view.
 */
public class RepoListAdapter extends ArrayAdapter<Repo> implements AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener {

    private BasicAbstractActivity mContext;
    private RepoListViewModel mRepoListViewModel;
    private List<Repo> mLastRepoList;

    public RepoListAdapter(@NonNull BasicAbstractActivity context, RepoListViewModel viewModel) {
        super(context, 0);
        mContext = context;
        mRepoListViewModel = viewModel;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent intent = new Intent(mContext, RepoTasksActivity.class);
        Repo repo = getItem(position);
        if (mRepoListViewModel.repoDirExists(repo)) {
            intent.putExtra(Repo.TAG, repo);
            mContext.startActivity(intent);
        } else {
            mContext.showToastMsg(mContext.getResources().getString(R.string.repo_not_found));
            if (mLastRepoList != null) {
                List<Repo> repoList = mLastRepoList;
                repoList.remove(repo);
                setRepos(repoList);
                removeRepoDB(repo);
            }
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
        BasicAbstractActivity.onOptionClicked[] dialog = new BasicAbstractActivity.onOptionClicked[]{
                () -> {
                    deleteRepo(position);
                },
        };

        mContext.showOptionsDialog(
                R.string.dialog_choose_option,
                R.array.repo_options,
                dialog
        );

        return true;
    }

    @NotNull
    @Override
    public View getView(int position, View convertView, @NotNull ViewGroup parent) {
        if (convertView == null) {
            convertView = newView(getContext(), parent);
        }
        bindView(convertView, position);
        return convertView;
    }

    private View newView(Context context, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.repo_list_item, parent, false);
        RepoListItemHolder holder = new RepoListItemHolder();
        holder.title = view.findViewById(R.id.title);
        holder.remoteURL = view.findViewById(R.id.remoteURL);
        holder.localPath = view.findViewById(R.id.localPath);
        view.setTag(holder);
        return view;
    }

    private void bindView(View view, int position) {
        RepoListItemHolder holder = (RepoListItemHolder) view.getTag();
        final Repo repo = getItem(position);
        if (repo != null) {
            holder.title.setText(repo.getDisplayName());
            holder.localPath.setText(repo.getLocalPath());

            // set remote URL if known
            if (!StringUtils.isBlank(repo.getRemoteURL())) {
                holder.remoteURL.setText(repo.getRemoteURL());
            } else {
                holder.remoteURL.setText(mContext.getResources().getString(R.string.unknown_remote));
            }
        }
    }

    public void setRepos(List<Repo> repos) {
        if (repos == null) return;
        mLastRepoList = repos;
        setExistingRepos(repos);
    }

    private void setExistingRepos(List<Repo> repos) {
        if (repos == null) return;

        // if repository directory exists add it to the list
        List<Repo> validRepos = new ArrayList<>();
        for (Repo repo:repos) {
            if (mRepoListViewModel.repoDirExists(repo)) {
                validRepos.add(repo);
            } else {
                removeRepoDB(repo);
            }
        }

        clear();
        addAll(validRepos);
        notifyDataSetChanged();
    }

    public void refreshRepos() {
        if (mLastRepoList != null) {
            setExistingRepos(mLastRepoList);
        }
    }

    private void deleteRepo(int position) {
        final Repo repo = getItem(position);
        assert repo != null;
        removeRepoDB(repo);
    }

    private void removeRepoDB(Repo repo) {
        remove(repo);
        notifyDataSetChanged();
        mRepoListViewModel.deleteRepoById(repo.getId());
    }

    private class RepoListItemHolder {
        TextView title;
        TextView localPath;
        TextView remoteURL;
    }
}
