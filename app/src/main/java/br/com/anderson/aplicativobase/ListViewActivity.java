package br.com.anderson.aplicativobase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ListViewActivity extends AppCompatActivity {
    ListAdapter adapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        adapter = new ListAdapter(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("ListView");
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_black_24px);

        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);


        List<ListModel> list = new ArrayList<>();
        list.add(new ListModel("item 1"));
        list.add(new ListModel("item 2"));
        list.add(new ListModel("item 3"));
        list.add(new ListModel("item 4"));
        list.add(new ListModel("item 5"));
        list.add(new ListModel("item 6"));
        list.add(new ListModel("item 7"));
        list.add(new ListModel("item 8"));
        list.add(new ListModel("item 9"));
        list.add(new ListModel("item 10"));
        list.add(new ListModel("item 11"));
        list.add(new ListModel("item 12"));
        list.add(new ListModel("item 13"));
        list.add(new ListModel("item 14"));

        adapter.addAll(list);

    }
}
