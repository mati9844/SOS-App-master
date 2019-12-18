package com.example.sos_app_ui.ui.configuration;
import com.example.sos_app_ui.R;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ContactsList extends AppCompatActivity {

    private ListView lista;
    private ListView finalLista;
    ArrayList<Android_Contact> arrayList_Android_Contacts;
    ArrayList<Android_Contact> selectedContacts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_list);

        finalLista = findViewById(R.id.listView1);

        selectedContacts = new ArrayList<Android_Contact>();
        lista = findViewById(R.id.contacts);
        Button bot = findViewById(R.id.loadContacts);
        bot.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                fp_get_Android_Contacts();
            }
        });

        Button bot2 = findViewById(R.id.addBtn);
        bot2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putSerializable("ARRAYLIST",(Serializable)selectedContacts);


                Intent resultIntent = new Intent(v.getContext(),
                        WarningTargets.class);
                resultIntent.putExtra("selectedContacts",args);
                setResult(RESULT_OK,resultIntent);

                finish();
            }
        });

        lista.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //for (int j = 0; j < adapterView.getChildCount(); j++)
                    //adapterView.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);

                // change the background color of the selected element
                selectedContacts.add(arrayList_Android_Contacts.get(i));
                System.out.println("ID " + i);
                view.setBackgroundColor(Color.LTGRAY);

            }
        });
    }


    public void fp_get_Android_Contacts(){
        arrayList_Android_Contacts = new ArrayList<Android_Contact>();

        //--< get all Contacts >--
        Cursor cursor_Android_Contacts = null;
        ContentResolver contentResolver = getContentResolver();
        try {
            cursor_Android_Contacts = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        } catch (Exception ex) {
            Log.e("Error on contact", ex.getMessage());
        }

        if (cursor_Android_Contacts != null) {
            if (cursor_Android_Contacts.getCount() > 0) {

                while (cursor_Android_Contacts.moveToNext())
                {
                    Android_Contact android_contact = new Android_Contact();
                    String contact_id = cursor_Android_Contacts.getString(cursor_Android_Contacts.getColumnIndex(ContactsContract.Contacts._ID));
                    String contact_display_name = cursor_Android_Contacts.getString(cursor_Android_Contacts.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    System.out.println(contact_display_name);

                    android_contact.android_contact_Name = contact_display_name;

                    int hasPhoneNumber = Integer.parseInt(cursor_Android_Contacts.getString(cursor_Android_Contacts.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                    if (hasPhoneNumber > 0) {

                        Cursor phoneCursor = contentResolver.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                                , null
                                , ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?"
                                , new String[]{contact_id}
                                , null);

                        while (phoneCursor.moveToNext()) {
                            String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            System.out.println(phoneNumber);
                            android_contact.android_contact_TelefonNr = phoneNumber;
                        }
                        phoneCursor.close();
                    }

                    arrayList_Android_Contacts.add(android_contact);
                }

                Adapter_for_Android_Contacts adapter = new Adapter_for_Android_Contacts(this, arrayList_Android_Contacts);
                lista.setAdapter(adapter);
            }
        }
    }
}

class Android_Contact implements Serializable{
    public String android_contact_Name = "";
    public String android_contact_TelefonNr = "";
    public int android_contact_ID=0;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Android_Contact c1 = (Android_Contact)obj;

        if (!this.android_contact_Name.equals(c1.android_contact_Name))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        return this.android_contact_Name.hashCode();
    }
}

    class Adapter_for_Android_Contacts extends BaseAdapter {
        Context mContext;
        List<Android_Contact> mList_Android_Contacts;

        public Adapter_for_Android_Contacts(Context mContext, List<Android_Contact> mContact) {
            this.mContext = mContext;
            this.mList_Android_Contacts = mContact;
        }

        @Override
        public int getCount() {
            return mList_Android_Contacts.size();
        }

        @Override
        public Object getItem(int position) {
            return mList_Android_Contacts.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view=View.inflate(mContext,R.layout.contactlist_items,null);

            TextView textview_contact_Name= view.findViewById(R.id.textview_android_contact_name);
            TextView textview_contact_TelefonNr= view.findViewById(R.id.textview_android_contact_phoneNr);

            textview_contact_Name.setText(mList_Android_Contacts.get(position).android_contact_Name);
            textview_contact_TelefonNr.setText(mList_Android_Contacts.get(position).android_contact_TelefonNr);


            view.setTag(mList_Android_Contacts.get(position).android_contact_Name);
            return view;
        }



    }

