package hu.ait.cryptokeychain;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import hu.ait.cryptokeychain.data.Account;

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

import static android.content.ContentValues.TAG;

public class PasswordInfoFragment extends Fragment {
    private static String TAG = "PasswordInfoFragment";
    private View mView;
    private SharedViewModel mViewModel;
    private Account currentAccount;
    private TextView mAccountName, mUsername, mPassword;
    private Button mDecrypt;
    private SecretKeySpec passwordKey;

    public static PasswordInfoFragment newInstance() {
        return new PasswordInfoFragment();
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
        mView = inflater.inflate(R.layout.password_info_fragment, container, false);
        ((MainActivity)getActivity()).setNavigationVisibility(false);

        currentAccount = (Account)getArguments().getSerializable("displayAccountInformation");

        // Initialize UI elements
        mAccountName = mView.findViewById(R.id.display_website);
        mUsername = mView.findViewById(R.id.display_username);
        mPassword = mView.findViewById(R.id.display_password);
        mDecrypt = mView.findViewById(R.id.decrypt_password);

        return mView;

    }

    /**
     * We display the account information by accessing the properties of currentAccount.
     *
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);

        if(currentAccount != null) {

            String accountName = currentAccount.getAccount_name();
            String username = currentAccount.getUsername();
            final byte[] encryptedPassword = android.util.Base64.decode(currentAccount.getEncrypted_password(), Base64.DEFAULT);
            final byte[] iv = android.util.Base64.decode(currentAccount.getIv(), Base64.DEFAULT);

            // Display account information for the account that was tapped on
            mAccountName.setText(accountName);
            mUsername.setText(username);
            mPassword.setText(new String(encryptedPassword));

            // If 'Decrypt Password' button is tapped, display the account's decrypted password
            mDecrypt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Cipher cipher = null;
                    try {
                        cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (NoSuchPaddingException e) {
                        e.printStackTrace();
                    }

                    // Retrieve the key derived from the user's master password
                    passwordKey = ((MainActivity) getActivity()).passwordKey;

                    // Create an IvParameterSpec object from the IV used to encrypt the account information
                    IvParameterSpec ivSpec = new IvParameterSpec(iv);

                    // Create Cipher object, decrypt encrypted password
                    try {
                        cipher.init(Cipher.DECRYPT_MODE, passwordKey, ivSpec);
                        byte[] decrypted = cipher.doFinal(encryptedPassword);
                        String temp = new String(decrypted);
                        mPassword.setText(temp);
                    } catch (InvalidAlgorithmParameterException e) {
                        e.printStackTrace();
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    } catch (BadPaddingException e) {
                        e.printStackTrace();
                    } catch (IllegalBlockSizeException e) {
                        e.printStackTrace();
                    }

                }
            });

        }

    }

}