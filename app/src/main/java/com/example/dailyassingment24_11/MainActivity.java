package com.example.dailyassingment24_11;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private static final int MAX_PICK_CONTACT = 15;
    private static final int REQUEST_CODE_PICK_CONTACT = 0;
    private static final String TAG = "Main Activity" ;
    private static final int REQUEST_CODE_PICK_CONTACTS = 1;
    RecyclerView recyclerView;
    Button btnGetContacts;
    ArrayList<ContactsInfo> contactsInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnGetContacts = findViewById(R.id.btnGetContacts);
        recyclerView = findViewById(R.id.recyclerView);


        btnGetContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestContactPermission();
            }
        });
    }

    private void getContacts(){
        Intent phoneBookIntent = new Intent("intent.action.INTERACTION_TOPMENU");
        phoneBookIntent.putExtra(Intent.CATEGORY_APP_CONTACTS, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(phoneBookIntent, REQUEST_CODE_PICK_CONTACT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK) {
            if(requestCode == REQUEST_CODE_PICK_CONTACT ) {
                Log.d(TAG, "Response: " + data.toString());
                Uri uriContact = data.getData();
                ContentResolver contentResolver = getContentResolver();
                String contactId = null;
                String displayName = null;
                contactsInfoList = new ArrayList<ContactsInfo>();

                Cursor cursor = getContentResolver().query(uriContact, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                        if (hasPhoneNumber > 0) {

                            ContactsInfo contactsInfo = new ContactsInfo();
                            contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                            displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                            contactsInfo.setContactId(contactId);
                            contactsInfo.setDisplayName(displayName);

                            Cursor phoneCursor = getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                    new String[]{contactId},
                                    null);

                            if (phoneCursor.moveToNext()) {
                                String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                                contactsInfo.setPhoneNumber(phoneNumber);
                            }

                            phoneCursor.close();

                            contactsInfoList.add(contactsInfo);
                        }
                    }
                }
                cursor.close();
                recyclerView.setAdapter(new MyCustomAdapter(this,contactsInfoList));
                recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));


                Log.i(TAG, "onActivityResult: " + uriContact.toString());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void requestContactPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.READ_CONTACTS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Read contacts access needed");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("Please enable access to contacts.");
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {android.Manifest.permission.READ_CONTACTS}
                                    , PERMISSIONS_REQUEST_READ_CONTACTS);
                        }
                    });
                    builder.show();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.READ_CONTACTS},
                            PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            } else {
                getContacts();
            }
        } else {
            getContacts();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContacts();
                } else {
                    Toast.makeText(this, "You have disabled a contacts permission", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
}