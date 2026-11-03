
package com.example.poster1019;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Displays the Contact Us page.
 * Allows users to open their email application and
 * view a personal introduction when a button is clicked.
 */
public class ContactFragment extends Fragment {

    /**
     * Creates the Contact Us fragment using its XML layout.
     */
    public ContactFragment() {
        super(R.layout.fragment_contact);
    }

    /**
     * Initializes the Contact Us page and sets up
     * click listeners for the email section and
     * the personal introduction button.
     */
    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        // Find the description TextView and introduction button.
        TextView contactDescription =
                view.findViewById(R.id.contactDescription);

        Button btnIntroduction =
                view.findViewById(R.id.btnIntroduction);

        // Change the description when the button is clicked.
        btnIntroduction.setOnClickListener(v -> {

            String introduction =
                    "Hi! I'm a Computer Science student at " +
                            "Central Washington University. " +
                            "I enjoy developing software, exploring " +
                            "new technologies, and building applications " +
                            "that solve real-world problems.";

            contactDescription.setText(introduction);
        });

        // Open the user's email app when the email row is clicked.
        view.findViewById(R.id.layoutEmail)
                .setOnClickListener(v -> {
                    openEmailApp(getString(R.string.contact_email));
                });
    }

    /**
     * Opens an email application with the contact email
     * address already filled in.
     *
     * @param emailAddress The recipient's email address.
     */
    private void openEmailApp(String emailAddress) {

        Intent intent = new Intent(
                Intent.ACTION_SENDTO,
                Uri.parse("mailto:" + emailAddress)
        );

        try {
            startActivity(intent);

        } catch (ActivityNotFoundException e) {

            Toast.makeText(
                    requireContext(),
                    R.string.no_email_app,
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}
