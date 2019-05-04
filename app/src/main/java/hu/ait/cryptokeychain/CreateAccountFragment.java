package hu.ait.cryptokeychain;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import hu.ait.cryptokeychain.data.Account;
import hu.ait.cryptokeychain.data.AppDatabase;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import static android.content.ContentValues.TAG;

public class CreateAccountFragment extends Fragment {
    private static String TAG = "CreateAccountFragment";
    private View mView;
    private SharedViewModel mViewModel;
    private Button mCancel, mDone;
    private EditText website, username, password;
    private SecretKeySpec passwordKey;

    public static CreateAccountFragment newInstance() {
        return new CreateAccountFragment();
    }

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
        mView = inflater.inflate(R.layout.create_account_fragment, container, false);
        ((MainActivity) getActivity()).setNavigationVisibility(false);

        // Initialize create account UI elements
        mCancel = mView.findViewById(R.id.cancel_acct_create);
        mDone = mView.findViewById(R.id.confirm_acct_create);
        website = mView.findViewById(R.id.website);
        username = mView.findViewById(R.id.username);
        password = mView.findViewById(R.id.password);

        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If the user cancels account creation, take them back to the landing page
                Navigation.findNavController(mView).navigate(R.id.AccountsFragment);
            }
        });

        mDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mAccountName = website.getText().toString();
                String mUsername = username.getText().toString();
                String mPassword = password.getText().toString();

                // Retrieve the key derived from the user's master password
                passwordKey = ((MainActivity)getActivity()).passwordKey;

                // Create 16 bytes of random data; package it into an IvParameterSpec object
                SecureRandom ivRandom = new SecureRandom();
                byte[] iv = new byte[16];
                ivRandom.nextBytes(iv);
                IvParameterSpec ivSpec = new IvParameterSpec(iv);

                // Encrypt password using AES-CBC mode and PKCS-7 Padding Scheme
                Cipher cipher = null;
                try {
                    cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                }

                try {
                    cipher.init(Cipher.ENCRYPT_MODE, passwordKey, ivSpec);
                } catch (InvalidAlgorithmParameterException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                }

                byte[] temp = mPassword.getBytes(Charset.defaultCharset());
                mPassword = null;
                try {
                    temp = cipher.doFinal(temp);
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                }

                // Store username, account name, encrypted password, and the iv used to encrypt in the app's database
                String encryptedPassword = android.util.Base64.encodeToString(temp, Base64.DEFAULT);
                String ivString = android.util.Base64.encodeToString(iv, Base64.DEFAULT);
                saveToDatabase(mAccountName, mUsername, encryptedPassword, ivString);
                mAccountName = null;
                mUsername = null;

                // Let the user know their account has been successfully added
                Toast.makeText(getActivity(), "Account securely stored!", Toast.LENGTH_LONG).show();

                // Take the user back to the AccountsFragment where they will see their updated accounts RecyclerView
                Navigation.findNavController(mView).navigate(R.id.AccountsFragment);
            }
        });

    }

    private void saveToDatabase(String mAccountName, String mUsername, String encryptedPassword, String iv) {
        Account newAccount = new Account(null, mAccountName, mUsername, encryptedPassword, iv);
        new AddAccountAsyncTask().execute(newAccount);
    }

    /**
     * AsyncTask to perform database operations. Database cannot be accessed on the
     * main thread as it will lock up the UI.
     */
    private class AddAccountAsyncTask extends AsyncTask<Account, Void, Void> {

        @Override
        protected Void doInBackground(Account... newAccount) {
            Account temp = newAccount[0];
            AppDatabase.Companion.getInstance(getActivity()).AccountDao().insertAccount(temp);
            return null;
        }
    }

}
