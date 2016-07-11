package br.com.anderson.aplicativobase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DevMaker on 7/11/16.
 */
public class ListAdapter extends BaseAdapter {

    List<ListModel> list = new ArrayList<>();
    Context context;
    LayoutInflater inflater;
    public ListAdapter(Context context){
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void add(ListModel model){
        list.add(model);
        notifyDataSetChanged();
    }

    public void addAll(List<ListModel> list){
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void clear(){
        list.clear();
        notifyDataSetChanged();
    }

    public void remove(int position){
        list.remove(position);
        notifyDataSetChanged();
    }

    public void remove(ListModel model){
        list.remove(model);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public ListModel getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView==null) {
            convertView = inflater.inflate(R.layout.list_view_adapter, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.textView = (TextView) convertView.findViewById(R.id.textView3);
            convertView.setTag(viewHolder);

        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textView.setText(getItem(position).getName());

        return convertView;
    }

    static class ViewHolder{
        TextView textView;
    }
}
