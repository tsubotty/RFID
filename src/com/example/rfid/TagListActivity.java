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
        // アイテムを追加します
        adapter.add("red");
        adapter.add("green");
        adapter.add("blue");
        ListView listView = (ListView) findViewById(R.id.listview);
        // アダプターを設定します
        listView.setAdapter(adapter);
        // リストビューのアイテムがクリックされた時に呼び出されるコールバックリスナーを登録します
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                ListView listView = (ListView) parent;
                // クリックされたアイテムを取得します
                String item = (String) listView.getItemAtPosition(position);
                Toast.makeText(TagListActivity.this, item, Toast.LENGTH_LONG).show();
            }
        });
        // リストビューのアイテムが選択された時に呼び出されるコールバックリスナーを登録します
        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
                ListView listView = (ListView) parent;
                // 選択されたアイテムを取得します
                String item = (String) listView.getSelectedItem();
                Toast.makeText(TagListActivity.this, item, Toast.LENGTH_LONG).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}