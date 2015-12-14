package com.example.contact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MainActivity extends BaseActivity {

    private ListView listview;
    private ImageView add;
    private SimpleAdapter adapter;
    private Cursor result;
    private List<Map<String, Object>> allContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listview = (ListView) super.findViewById(R.id.listview);
        add = (ImageView) super.findViewById(R.id.add);
        // 得到联系人数据放入链表中
        result = super.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        allContacts = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = null;

        for (result.moveToFirst(); !result.isAfterLast(); result.moveToNext()) {
            map = new HashMap<String, Object>();
            map.put("contact_id", result.getInt(result.getColumnIndex(ContactsContract.Contacts._ID)));
            map.put("contact_name", result.getString(result.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
            allContacts.add(map);
        }
        adapter = new SimpleAdapter(this, allContacts, R.layout.contact_item, new String[] {
            "contact_id",
            "contact_name"
        }, new int[] {
            R.id.contact_id,
            R.id.contact_name
        });
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new OnItemClickListener() {

            // 通过id得到号码，调到详情界面
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub

                long contactsId = Long.parseLong(allContacts.get(position).get("contact_id").toString());
                String name = allContacts.get(position).get("contact_name").toString();

                String number = "";
                String phoneSelection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?";
                String[] phoneSelectionArgs = {
                    String.valueOf(contactsId)
                };
                Cursor c = MainActivity.this.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, phoneSelection, phoneSelectionArgs, null);
                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                    number = number + c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                }

                Intent intent = new Intent(MainActivity.this, ContactsEdit.class);
                intent.putExtra("id", contactsId);
                intent.putExtra("name", name);
                intent.putExtra("number", number);
                startActivity(intent);
            }

        });
        add.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                startActivity(new Intent(MainActivity.this, ContactsEdit.class));
            }
        });
    }

    // 利用广播关闭所有activity
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        Intent it = new Intent();
        it.setAction("finish");
        sendBroadcast(it);
        return super.onKeyDown(keyCode, event);
    }

}
