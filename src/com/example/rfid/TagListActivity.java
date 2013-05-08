package com.example.rfid;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class TagListActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tag_list);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        // �A�C�e����ǉ����܂�
        adapter.add("red");
        adapter.add("green");
        adapter.add("blue");
        ListView listView = (ListView) findViewById(R.id.listview);
        // �A�_�v�^�[��ݒ肵�܂�
        listView.setAdapter(adapter);
        // ���X�g�r���[�̃A�C�e�����N���b�N���ꂽ���ɌĂяo�����R�[���o�b�N���X�i�[��o�^���܂�
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                ListView listView = (ListView) parent;
                // �N���b�N���ꂽ�A�C�e�����擾���܂�
                String item = (String) listView.getItemAtPosition(position);
                Toast.makeText(TagListActivity.this, item, Toast.LENGTH_LONG).show();
            }
        });
        // ���X�g�r���[�̃A�C�e�����I�����ꂽ���ɌĂяo�����R�[���o�b�N���X�i�[��o�^���܂�
        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
                ListView listView = (ListView) parent;
                // �I�����ꂽ�A�C�e�����擾���܂�
                String item = (String) listView.getSelectedItem();
                Toast.makeText(TagListActivity.this, item, Toast.LENGTH_LONG).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}