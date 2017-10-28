package unitor.uni.k1a2.unitor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by K1A2 on 2017-03-01.
 */

public class SpinnerColordapter extends BaseAdapter {


    Context context;
    List<String> data;
    List<Integer> coclor;
    LayoutInflater inflater;

    public SpinnerColordapter(Context context, List<String> data, List<Integer> coclor){
        this.context = context;
        this.data = data;
        this.coclor = coclor;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null) {
            convertView = inflater.inflate(R.layout.color_spinner_normar, parent, false);
        }

        if(data!=null&&coclor!=null){
            //데이터세팅
            String text = data.get(position);
            int color = coclor.get(position);
            ((TextView)convertView.findViewById(R.id.txt_color_name)).setText(text);
            ((FrameLayout)convertView.findViewById(R.id.color_frame)).setBackgroundColor(color);
        }

        return convertView;
    }

    @Override
    public int getCount() {
        if(data!=null) return data.size();
        return 0;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = inflater.inflate(R.layout.color_spinner_down, parent, false);
        }

        //데이터세팅
        String text = data.get(position);
        int color = coclor.get(position);
        ((TextView)convertView.findViewById(R.id.txt_color_name)).setText(text);
        ((FrameLayout)convertView.findViewById(R.id.color_frame)).setBackgroundColor(color);

        return convertView;
    }

    @Override
    public Object[] getItem(int position) {
        Object[] r = new Object[] {data.get(position), coclor.get(position)};
        return r;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
