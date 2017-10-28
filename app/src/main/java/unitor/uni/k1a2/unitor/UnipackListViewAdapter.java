package unitor.uni.k1a2.unitor;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by K1A2 on 2017-05-02.
 */

public class UnipackListViewAdapter extends BaseAdapter {

    private ArrayList<UnipackListItem> listViewList = new ArrayList<UnipackListItem>();
    private String title;
    private String producer;
    private String path;
    private String chain;
    private String land;

    public UnipackListViewAdapter () {

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
            convertView = inflater.inflate(R.layout.list_unipack, parent, false);
        }

        TextView titleView = (TextView)convertView.findViewById(R.id.list_unipack_title);
        TextView produceView = (TextView)convertView.findViewById(R.id.list_unipack_producer);
        TextView pathView= (TextView)convertView.findViewById(R.id.list_unipack_path);
        TextView chainView = (TextView)convertView.findViewById(R.id.list_unipack_chain);
        TextView landView = (TextView)convertView.findViewById(R.id.list_unipack_land);

        final SharedPreferences theme = PreferenceManager.getDefaultSharedPreferences(context);
        final String th = theme.getString("setting_theme", "0");
        if (th.equals("1")) {
            convertView.setBackgroundResource(R.drawable.list_background_dark);
            titleView.setTextColor(context.getResources().getColor(R.color.list_title_dark));
            produceView.setTextColor(context.getResources().getColor(R.color.list_producer_dark));
            pathView.setTextColor(context.getResources().getColor(R.color.list_producer_dark));
        }

        UnipackListItem listItem = listViewList.get(pos);

        title = listItem.getTitle();
        producer = listItem.getProducer();
        path = listItem.getPath();
        chain = listItem.getChain();
        land = listItem.getLand();

        titleView.setText(title);
        produceView.setText(producer);
        pathView.setText(path);
        chainView.setText(chain);
        landView.setText(land);

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

    public void addItem (String title, String producer, String path, String chain, String land) {
        UnipackListItem listItem = new UnipackListItem();

        listItem.setTitle(title);
        listItem.setProducer(producer);
        listItem.setPath(path);
        listItem.setChain(chain);
        listItem.setLand(land);

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
