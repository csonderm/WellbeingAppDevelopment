package com.example.natalie.android_wellbeing;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by Natalie on 6/28/2015.
 */
public class PreferenceDialog extends DialogFragment {
    String preferred = "English";
    String email = null;
    //Added this 9/11
    String occupation = "Student";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean emailStored = prefs.getBoolean(getString(R.string.emailStored), false);
        boolean languageStored = prefs.getBoolean(getString(R.string.languageStored), false);
        //Added this 9/11
        boolean occupationStored = prefs.getBoolean("occupationStored", false);

        if(emailStored){
            email = prefs.getString(getString(R.string.user_email), null);
        }

        if(languageStored){
            preferred = prefs.getString(getString(R.string.user_language), "English");
        }
        //Added this 9/11
        if(occupationStored){
            occupation = prefs.getString("user_occupation","Student");
        }

        // Initialize Dialog
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        // Set the dialog message
        LayoutInflater inflater = getActivity().getLayoutInflater();
        alertDialogBuilder
                .setCancelable(false)
                .setView(inflater.inflate(R.layout.activity_preference_dialog, null))
                .setNeutralButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Dialog dialog = PreferenceDialog.this.getDialog();
                        final EditText emailInput = (EditText) dialog.findViewById(R.id.emailEditTxt);
                        final TextView emailMsg = (TextView) dialog.findViewById(R.id.emailTxt);
                        final Spinner spinner = (Spinner) dialog.findViewById(R.id.languageSpinner);
                        //added this 9/11
                        final Spinner spinnerOcc = (Spinner) dialog.findViewById(R.id.occupationSpinner);

                        // Get preferred language
                        preferred = spinner.getSelectedItem().toString();
                        if (!preferred.equals("English")) {
                            String country_code;
                            switch (preferred) {
                                case "English":
                                    country_code = "en_US";
                                    break;
                                case "Español":
                                    country_code = "es_ES";
                                    break;
                                case "Français":
                                    country_code = "fr_FR";
                                    break;
                                default:
                                    country_code = "en_US";
                            }

                            Intent service = new Intent(Intent.ACTION_SYNC, null,
                                    getDialog().getOwnerActivity(), UpdateService.class);

                            UpdateResultReceiver mReceiver = new UpdateResultReceiver(new Handler());
                            mReceiver.setReceiver((UpdateResultReceiver.Receiver) getDialog().getOwnerActivity());
                            service.putExtra("receiver", mReceiver);
                            service.putExtra("requestId", 101);

                            getDialog().getOwnerActivity().startService(service);

                            // Change locale settings in the app.
                            Resources res = getDialog().getOwnerActivity().getApplicationContext().getResources();
                            DisplayMetrics dm = res.getDisplayMetrics();
                            android.content.res.Configuration conf = res.getConfiguration();
                            conf.locale = new Locale(country_code.toLowerCase());
                            res.updateConfiguration(conf, dm);


                            //Added this 9/11
                            // Get preferred occupation
                            occupation = spinnerOcc.getSelectedItem().toString();
                      /*  if (!occupation.equals("Student")){
                            //String occ_code;
                            String country_code;
                            switch(occupation){
                                case "Student":
                                    country_code = "en_US";
                                    break;
                                case "Teacher":
                                    country_code = "es_ES";
                                    break;
                               /* case "Assistant":
                                    occ_code = "As";
                                    break;
                                case "Chef":
                                    occ_code = "Ch";
                                    break;
                                case "Dentist":
                                    occ_code = "De";
                                    break;
                                case "Lawyer":
                                    occ_code = "La";
                                    break;
                                case "Doctor":
                                    country_code = "fr_FR";
                                    break;
                               // case "Unemployed":
                                //    occ_code = "Un";
                                 //   break;
                                default:
                                    country_code = "en_US";
                            }
                            //What do with this
                            Intent service = new Intent(Intent.ACTION_SYNC, null,
                                    getDialog().getOwnerActivity(), UpdateService.class);

                            UpdateResultReceiver mReceiver = new UpdateResultReceiver(new Handler());
                            mReceiver.setReceiver((UpdateResultReceiver.Receiver) getDialog().getOwnerActivity());
                            service.putExtra("receiver", mReceiver);
                            service.putExtra("requestId", 101);

                            getDialog().getOwnerActivity().startService(service);

                            // Change locale settings in the app.
                            Resources res = getDialog().getOwnerActivity().getApplicationContext().getResources();
                            DisplayMetrics dm = res.getDisplayMetrics();
                            android.content.res.Configuration conf = res.getConfiguration();
                            conf.locale = new Locale(country_code.toLowerCase());
                            res.updateConfiguration(conf, dm);

                        }
                        */

                            // Get email address
                            String email = emailInput.getText().toString();
                            Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile( // Standard email regex
                                    "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                                            "\\@" +
                                            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                                            "(" +
                                            "\\." +
                                            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                                            ")+"
                            );

                            // If there's no user input, inform user
                            if (emailInput.getText().toString().trim().length() == 0) {
                                emailMsg.setText("\nYou must enter an email address\n\n");
                                emailMsg.setTextColor(getResources().getColor(android.R.color.holo_red_dark));

                                // If the input string does not follow the standard email regex, inform user
                            } else if (!EMAIL_ADDRESS_PATTERN.matcher(email).matches()) {
                                emailMsg.setText("\nInvalid email address\n\n");
                                emailMsg.setTextColor(getResources().getColor(android.R.color.holo_red_dark));

                                // Otherwise, the input email is valid and is stored in shared preferences
                            } else {
                                final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                                SharedPreferences.Editor edit = prefs.edit();

                                edit.putBoolean(getString(R.string.emailStored), Boolean.TRUE);
                                edit.putString(getString(R.string.user_email), email);
                                edit.commit();

                                edit.putBoolean(getString(R.string.languageStored), Boolean.TRUE);
                                edit.putString(getString(R.string.user_language), preferred);
                                edit.commit();

                                //added this 9/11
                                edit.putBoolean(getString(R.string.occupationStored), Boolean.TRUE);
                                edit.putString(getString(R.string.user_language), occupation);
                                edit.commit();

                                dismiss();
                            }
                        }
                    }
                });

        setCancelable(false);

        // Create alert dialog
        return alertDialogBuilder.create();
    }
}
