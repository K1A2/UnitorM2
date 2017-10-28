package unitor.uni.k1a2.unitor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by K1A2 on 2017-05-02.
 */

public class SoundListViewAdapter extends BaseAdapter {

    private ArrayList<SoundListItem> listViewList = new ArrayList<SoundListItem>();
    private String title;
    private String producer;
    private String path;

    public SoundListViewAdapter() {

    }

    @Override
    public int getCount () {
        return listViewList.size();
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_sound, parent, false);
        }

        TextView nameView = (TextView)convertView.findViewById(R.id.Sound_list_title);
        TextView pathView= (TextView)convertView.findViewById(R.id.Sound_list_path);

        SoundListItem listItem = listViewList.get(pos);

        title = listItem.getName();
        path = listItem.getPath();

        nameView.setText(title);
        pathView.setText(path);

        return convertView;
    }

    @Override
    public long getItemId (int position) {
        return position;
    }

    @Override
    public Object getItem (int position) {
        return listViewList.get(position);
    }

    public void addItem (String name, String path) {
        SoundListItem listItem = new SoundListItem();

        listItem.setName(name);
        listItem.setPath(path);

        listViewList.add(listItem);
    }

    public void remove (int position) {
        listViewList.remove(position);
        DataChange();
    }

    public void clear () {
        listViewList.clear();
        DataChange();
    }

    public void set (int position, String content) {
        SoundListItem soundListItem = new SoundListItem();
        soundListItem.setName("Frame");
        soundListItem.setPath(content);
        listViewList.set(position, soundListItem);
        DataChange();
    }

    public void setDelay (int position, String content) {
        SoundListItem soundListItem = new SoundListItem();
        soundListItem.setName("Delay");
        soundListItem.setPath(content);
        listViewList.set(position, soundListItem);
        DataChange();
    }

    public void DataChange () {
        this.notifyDataSetChanged();
    }
}
