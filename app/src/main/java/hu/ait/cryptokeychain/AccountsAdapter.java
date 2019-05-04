package hu.ait.cryptokeychain;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import hu.ait.cryptokeychain.data.Account;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AccountsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static String TAG = "AccountsAdapter";
    private LayoutInflater inflater;
    private List<Account> mAccounts;

    /**
     * The adapter populates the data into the RecyclerView by converting an object at a position
     * into a list row item to be inserted. Adapters require the existence of a "ViewHolder"
     * object which describes and provides access to all the views within each item row. In our case,
     * each item row is composed of CardViews.
     *  @param context
     *
     */
    public AccountsAdapter(Context context, List<Account> accounts) {
        inflater = LayoutInflater.from(context);
        this.mAccounts = accounts;
    }

    public static class AccountsViewHolder extends RecyclerView.ViewHolder {
        public TextView mAccountName;
        public TextView mAccountDescription;
        private View mView;
        private Context mContext;

        public AccountsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mContext = mView.getContext();

            mAccountName = mView.findViewById(R.id.account_name);
            mAccountDescription = mView.findViewById(R.id.account_description);

        }

    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflates the XML layout file that will be used for each row within the list
        return new AccountsViewHolder(inflater.inflate(R.layout.account, parent, false));
    }

    /**
     * Sets the view attributes based on the data.
     *
     * @param holder
     * @param position The account whose index is 'position'
     */
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        Account currentAccount = mAccounts.get(position);
        ((AccountsViewHolder)holder).mAccountName.setText(currentAccount.getAccount_name());
        ((AccountsViewHolder)holder).mAccountDescription.setText(currentAccount.getUsername());

        final Bundle bundle = new Bundle();
        bundle.putSerializable("displayAccountInformation", currentAccount);

        ((AccountsViewHolder)holder).mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(((AccountsViewHolder)holder).mView)
                        .navigate(R.id.PasswordInfoFragment, bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAccounts.size();
    }

}
