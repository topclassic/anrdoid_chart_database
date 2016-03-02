
package com.xxmassdeveloper.mpchartexample;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.xxmassdeveloper.mpchartexample.listviewitems.BarChartItem;
import com.xxmassdeveloper.mpchartexample.listviewitems.ChartItem;
import com.xxmassdeveloper.mpchartexample.listviewitems.LineChartItem;
import com.xxmassdeveloper.mpchartexample.listviewitems.PieChartItem;
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates the use of charts inside a ListView. IMPORTANT: provide a
 * specific height attribute for the chart inside your listview-item
 * 
 * @author Philipp Jahoda
 */
public class DayGraph extends DemoBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_listview_chart);
        
        ListView lv = (ListView) findViewById(R.id.listView1);

        ArrayList<ChartItem> list = new ArrayList<ChartItem>();

        // 30 items
        for (int i = 0; i < 3; i++) {
            
                list.add(new LineChartItem(generateDataLine(i + 1), getApplicationContext()));
          
        }

        ChartDataAdapter cda = new ChartDataAdapter(getApplicationContext(), list);
        lv.setAdapter(cda);
    }

    /** adapter that supports 3 different item types */
    private class ChartDataAdapter extends ArrayAdapter<ChartItem> {
        
        public ChartDataAdapter(Context context, List<ChartItem> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getItem(position).getView(position, convertView, getContext());
        }
        
        @Override
        public int getItemViewType(int position) {           
            // return the views type
            return getItem(position).getItemType();
        }
        
        @Override
        public int getViewTypeCount() {
            return 3; // we have 3 different item-types
        }
    }
    
    /**
     * generates a random ChartData object with just one DataSet
     * 
     * @return
     */
    private LineData generateDataLine(int cnt) {
    	LineData cd; 
    	LineDataSet d1;
        ArrayList<Entry> e1 = new ArrayList<Entry>();

        for (int i = 0; i < 24; i++) {
            e1.add(new Entry((int) (Math.random() * 65) + 40, i));
        }
        if(cnt == 1){
            d1 = new LineDataSet(e1, "Watt ");
            d1.setLineWidth(2.5f);
            d1.setCircleSize(4.5f);
            d1.setHighLightColor(Color.rgb(244, 117, 117));
            d1.setDrawValues(false);
       
            ArrayList<LineDataSet> sets = new ArrayList<LineDataSet>();
            sets.add(d1);

            cd = new LineData(getMonths(), sets);
        	
        }else if(cnt == 2){
            d1 = new LineDataSet(e1, "Unit ");
            d1.setLineWidth(2.5f);
            d1.setCircleSize(4.5f);
            d1.setHighLightColor(Color.rgb(244, 117, 117));
            d1.setDrawValues(false);
       
            ArrayList<LineDataSet> sets = new ArrayList<LineDataSet>();
            sets.add(d1);

            cd = new LineData(getMonths(), sets);
            
        }else

        d1 = new LineDataSet(e1, "Bath ");
        d1.setLineWidth(2.5f);
        d1.setCircleSize(4.5f);
        d1.setHighLightColor(Color.rgb(244, 117, 117));
        d1.setDrawValues(false);
   
        ArrayList<LineDataSet> sets = new ArrayList<LineDataSet>();
        sets.add(d1);

        cd = new LineData(getMonths(), sets);
        
        return cd;
    }
    


    private ArrayList<String> getMonths() {

        ArrayList<String> m = new ArrayList<String>();
        m.add("1");
        m.add("2");
        m.add("3");
        m.add("4");
        m.add("5");
        m.add("6");
        m.add("7");
        m.add("8");
        m.add("9");
        m.add("10");
        m.add("11");
        m.add("12");
        m.add("13");
        m.add("14");
        m.add("15");
        m.add("16");
        m.add("17");
        m.add("18");
        m.add("19");
        m.add("20");
        m.add("21");
        m.add("22");
        m.add("23");
        m.add("24");
        return m;
    }
}
