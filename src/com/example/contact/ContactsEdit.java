package com.example.contact;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/*
 * 编辑界面，详情界面，新建联系人界面，通用一个布局，根据标志，更改组件设置
 */
public class ContactsEdit extends BaseActivity implements OnClickListener {

    EditText edit_name;
    EditText edit_number;
    TextView left;
    TextView right;
    TextView middle;
    Button call;
    long id;
    String name;
    String number;
    boolean isEdit = false;
    boolean isNewContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_edit);
        edit_name = (EditText) super.findViewById(R.id.edit_name);
        edit_number = (EditText) super.findViewById(R.id.edit_number);
        left = (TextView) super.findViewById(R.id.left);
        middle = (TextView) super.findViewById(R.id.middle);
        right = (TextView) super.findViewById(R.id.right);
        call = (Button) super.findViewById(R.id.call);
        left.setOnClickListener(this);
        right.setOnClickListener(this);
        call.setOnClickListener(this);

        // 得到入口标志，是新建联系人入口，还是详情入口
        Intent it = getIntent();
        id = it.getLongExtra("id", -1);
        isNewContact = (id == -1 ? true : false);
        name = it.getStringExtra("name");
        number = it.getStringExtra("number");

        // 详情入口则设置各组件
        if (!isNewContact) {
            edit_name.setText(name);
            edit_number.setText(number);
            edit_name.setEnabled(false);
            edit_number.setEnabled(false);

            // 新建联系人入口设置各组件
        } else {
            left.setText("取消");
            right.setText("保存");
            middle.setText("新建联系人");
            call.setVisibility(8);
        }
    }

    // 各个监听处理
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
        case R.id.left:
            onBackPressed();
            break;

        // 不是新建入口，则分是详情入口还是编辑接口
        case R.id.right:
            if (!isNewContact) {
                if (!isEdit) {
                    right.setText("保存");
                    left.setText("取消");
                    middle.setText("编辑联系人");
                    call.setText("删除联系人");
                    edit_name.setEnabled(true);
                    edit_number.setEnabled(true);
                    isEdit = true;

                } else {
                    Toast.makeText(ContactsEdit.this, "保存成功！", Toast.LENGTH_SHORT).show();
                    insert(edit_name.getText().toString(), edit_number.getText().toString());
                    startActivity(new Intent(ContactsEdit.this, MainActivity.class));
                }
            } else {
                // 插入数据
                Toast.makeText(ContactsEdit.this, "新建成功！", Toast.LENGTH_SHORT).show();
                insert(edit_name.getText().toString(), edit_number.getText().toString());
                startActivity(new Intent(ContactsEdit.this, MainActivity.class));

            }
            break;

        // 是详情入口拨打电话还是编辑入口删除联系人
        case R.id.call:
            if (isEdit) {
                getContentResolver().delete(Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(id)), null, null);
                Toast.makeText(ContactsEdit.this, "该联系人已删除！", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ContactsEdit.this, MainActivity.class));
            } else {

                Toast.makeText(ContactsEdit.this, "拨打电话！", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number.trim()));

                startActivity(intent);
            }

            break;
        }

    }

    // 手机联系人插入
    public void insert(String name, String number) {
        ContentValues value = new ContentValues();
        Uri rawContactUri = getContentResolver().insert(RawContacts.CONTENT_URI, value);
        long rawContactId = ContentUris.parseId(rawContactUri);
        value.clear();
        value.put(Data.RAW_CONTACT_ID, rawContactId);
        value.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
        value.put(StructuredName.GIVEN_NAME, name);
        getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, value);

        value.clear();
        value.put(Data.RAW_CONTACT_ID, rawContactId);
        value.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
        value.put(Phone.NUMBER, number);
        value.put(Phone.TYPE, Phone.TYPE_MOBILE);
        getContentResolver().insert(android.provider.ContactsContract.Data.CONTENT_URI, value);

    }
}
