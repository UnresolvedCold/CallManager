package com.schwifty.callmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class Settings extends AppCompatActivity {

    LayoutInflater inflater;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        LinearLayout viewGroup = findViewById(R.id.SettingEntries);

        inflater = LayoutInflater.from(this);

        UpdateView(viewGroup);


    }

    private void UpdateView(LinearLayout viewGroup) {
        viewGroup.removeAllViews();
        //Settings Entries
        PopulateView(viewGroup,"Any Unknown Number","CallManager_Unknown");
        PopulateView(viewGroup,"From your Contacts","CallManager_known");
    }

    private void PopulateView(final LinearLayout viewGroup, String Title, final String PreferenceManagerIdentifier)
    {
        View v =inflater.inflate(R.layout.inflate_settings,null,false);
        TextView vTitle = v.findViewById(R.id.EntryTitle);
        View vEdit = v.findViewById(R.id.EntryEdit);
        View vActivate = v.findViewById(R.id.EntryActivate);
        vTitle.setText(Title);

        try
        {
            final JSONObject pref = new JSONObject( PreferenceManager.getDefaultSharedPreferences(this).getString(PreferenceManagerIdentifier,""));

            //On clicking Edit button
            vEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final EditDialog editDialog = new EditDialog();
                    editDialog.show();

                    try {
                        ((EditText)editDialog.getDialog().findViewById(R.id.Message)).setText(pref.get("message").toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                        ((EditText)editDialog.getDialog().findViewById(R.id.Message)).setText("");

                    }

                    editDialog.getDialog().findViewById(R.id.SaveMessage).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            String message = ((EditText)editDialog.getDialog().findViewById(R.id.Message)).getText().toString();

                            try {
                                pref.put("message",message);
                                pref.put("activate",true);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                                PreferenceManager.getDefaultSharedPreferences(Settings.this).edit()
                                        .putString(PreferenceManagerIdentifier,pref.toString()).apply();

                                editDialog.getDialog().dismiss();
                        }
                    });
               }
            });

            viewGroup.addView(v);

        }
        catch (JSONException e)
        {
            //On clicking Edit button
            vEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final EditDialog editDialog = new EditDialog();
                    editDialog.show();
                    final JSONObject pref = new JSONObject();


                    ((EditText)editDialog.getDialog().findViewById(R.id.Message)).setText("");

                    editDialog.getDialog().findViewById(R.id.SaveMessage).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            String message = ((EditText)editDialog.getDialog().findViewById(R.id.Message)).getText().toString();

                            try {
                                pref.put("message",message);
                                pref.put("activate",true);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            PreferenceManager.getDefaultSharedPreferences(Settings.this).edit()
                                    .putString(PreferenceManagerIdentifier,pref.toString()).apply();

                            editDialog.getDialog().dismiss();

                            UpdateView(viewGroup);
                        }
                    });
                }
            });

            viewGroup.addView(v);
        }
    }

    public class EditDialog
    {

        private Dialog dialog;

        public EditDialog() {
            dialog = new Dialog(Settings.this);
            dialog.setContentView(R.layout.dialog_edit_message);
            dialog.setTitle("");
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(true);
        }

        public Dialog getDialog()
        {
            return dialog;
        }

        public void show()
        {
            dialog.show();
        }

    }
}
