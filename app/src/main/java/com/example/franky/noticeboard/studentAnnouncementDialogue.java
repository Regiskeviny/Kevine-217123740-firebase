package com.example.franky.noticeboard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

public class studentAnnouncementDialogue extends AppCompatDialogFragment {

    private EditText title;
    private EditText body;
    private Spinner dialogSpinner;
    private DatabaseReference mDatabase;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mDatabase = FirebaseDatabase.getInstance().getReference();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.layout_dialog, null);
        builder.setView(view)
                .setTitle("Announcement")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Publish", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String announcementTitle = title.getText().toString();
                        String announcementBody = body.getText().toString();
                        String itemSelected = dialogSpinner.getSelectedItem().toString();
                        addAnnouncement(announcementTitle,announcementBody,itemSelected);

                    }
                });
        title = view.findViewById(R.id.announcementTitle);
        body = view.findViewById(R.id.announcementBody);
        dialogSpinner = view.findViewById(R.id.dialogSpinner);

        List<String> spinnerArray = new ArrayList<>();
        spinnerArray.add("Student");
        spinnerArray.add("Staff");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dialogSpinner.setAdapter(adapter);

        return builder.create();
    }

    private void addAnnouncement(String title, String body,String subscription) {
        studentAnnouncement announcement = new studentAnnouncement(title, body);

        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();

        mDatabase.child(subscription).child(ts).setValue(announcement);
    }

}
@IgnoreExtraProperties
class studentAnnouncement {

    public String title;
    public String body;

    public studentAnnouncement(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public studentAnnouncement() {
    }

}
