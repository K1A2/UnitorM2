package unitor.uni.k1a2.unitor;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by K1A2 on 2017-05-02.
 */

public class FileListViewAdapter extends BaseAdapter {

    private ArrayList<FileListItem> listViewList = new ArrayList<FileListItem>();
    private String title;
    private Drawable icon;
    private String path;

    public FileListViewAdapter() {

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
            convertView = inflater.inflate(R.layout.list_fileselect, parent, false);
        }

        TextView nameView = (TextView)convertView.findViewById(R.id.FragDial_file_name);
        TextView pathView= (TextView)convertView.findViewById(R.id.FragDial_file_path);
        ImageView icoView = (ImageView)convertView.findViewById(R.id.FragDial_file_Image);

        FileListItem listItem = listViewList.get(pos);

        title = listItem.getName();
        path = listItem.getPath();
        icon = listItem.getIco();

        nameView.setText(title);
        pathView.setText(path);
        icoView.setImageDrawable(icon);

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

    public void addItem (Drawable icon, String name, String path) {
        FileListItem listItem = new FileListItem();

        listItem.setName(name);
        listItem.setPath(path);
        listItem.setIco(icon);

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

    public void DataChange () {
        this.notifyDataSetChanged();
    }
}
