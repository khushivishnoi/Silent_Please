package com.vishnoikhushi.silentplease;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.vishnoikhushi.silentplease.data.DbHandler;
import com.vishnoikhushi.silentplease.data.model.Contact;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    private boolean mPermissionGranted = false;
    private final int PERMISSION_REQUEST_CODE = 1234;
    private static final int RESULT_PICK_CONTACT = 1;

    private FloatingActionButton add;
    private RecyclerView recyclerView;
    private DbHandler db;
    private RecyclerViewAdpater recyclerViewAdpater;
    private ArrayList<Contact> contactsArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        db = new DbHandler(MainActivity.this);
        add = findViewById(R.id.btn_add);
        //Recyclerview initialization
        recyclerView = findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(in, RESULT_PICK_CONTACT);
            }
        });
        contactsArrayList = new ArrayList<>();
        List<Contact> allContacts = db.getAllContacts();
        for (Contact cont : allContacts) {
            Log.d("dataBase", "ID: " + cont.getId() + "\t" + "Name: " + cont.getName()
                    + "\t" + "Number: " + cont.getNumber() + "\n");
            contactsArrayList.add(cont);
        }
        recyclerViewAdpater = new RecyclerViewAdpater(MainActivity.this, contactsArrayList);
        recyclerView.setAdapter(recyclerViewAdpater);
        Log.d("dataBase", "You have " + db.getCount() + " contacts in your database");

    }
    /*
   This method is used to get all the permission
    */
    private void getPermission() {
        String permission[] = {Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CALL_LOG,
                Manifest.permission.SEND_SMS};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                        Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                            Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED)
                        mPermissionGranted = true;
                }
            }
        } else
            ActivityCompat.requestPermissions(this, permission, PERMISSION_REQUEST_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length > 0) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    mPermissionGranted = false;
                    Log.d(TAG, "Permission not granted");
                    return;
                }
            }
            mPermissionGranted = true;
            Log.d(TAG, "Permission  Granted");
        }
    }

    private void init() {
        getPermission();
        if (mPermissionGranted) {
            Toast.makeText(this, "Permission to read Contact Granted", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESULT_PICK_CONTACT: {
                    contactPicked(data);
                    break;
                }
            }
        } else {
            Toast.makeText(this, "Failed To pick contact", Toast.LENGTH_SHORT).show();
        }
    }

    private void contactPicked(Intent data) {
        Cursor cursor = null;
        try {
            String phoneNo = null;
            String phoneName = null;
            Uri uri = data.getData();
            cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            phoneNo = cursor.getString(phoneIndex);
            phoneName = cursor.getString(nameIndex);

            Contact name = new Contact();
            name.setNumber(phoneNo);
            name.setName(phoneName);
            db.addContact(name);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}