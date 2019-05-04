package hu.ait.cryptokeychain;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import hu.ait.cryptokeychain.data.Account;
import hu.ait.cryptokeychain.data.AppDatabase;

import java.util.List;

public class AccountsFragment extends Fragment {
    private static String TAG = "AccountsFragment";
    private View mView;
    private SharedViewModel mViewModel;
    private List<Account> mAccounts;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FloatingActionButton mFAB;

    public static AccountsFragment newInstance() { return new AccountsFragment(); }

    /**
     * This initializes the UI variables once the fragment starts up, and returns the view
     * to its parent.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Get the view from fragment XML
        mView = inflater.inflate(R.layout.accounts_fragment, container, false);
        ((MainActivity)getActivity()).setNavigationVisibility(true);

        new LoadAccountsAsyncTask().execute();

        mFAB = mView.findViewById(R.id.add_account);
        mFAB.show();

        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);

        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate the user to the CreateAccountFragment
                Navigation.findNavController(mView).navigate(R.id.CreateAccountFragment);
            }
        });
    }


    /**
     * AsyncTask to perform database operations. Database cannot be accessed on the
     * main thread as it will lock up the UI.
     */
    private class LoadAccountsAsyncTask extends AsyncTask<Void, Void, List<Account>> {

        @Override
        protected List<Account> doInBackground(Void... voids) {
            // Retrieve information from AppDatabase
            mAccounts = AppDatabase.Companion.getInstance(getActivity()).AccountDao().getAllAccounts();
            return mAccounts;
        }

        @Override
        protected void onPostExecute(List<Account> mAccounts) {
            // Initialize RecyclerView with data from AppDatabase
            mRecyclerView = mView.findViewById(R.id.accounts_recycler_view);
            mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setHasFixedSize(true);
            mAdapter = new AccountsAdapter(getActivity(), mAccounts);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

}
