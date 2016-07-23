package ru.edocs_lab.test4;

import android.app.Activity;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class DataActivity extends Activity {
    public static final String EXTRA_JSON_STRING = "JSON_STRING";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        String jsonString = getIntent().getStringExtra(EXTRA_JSON_STRING);
        ArrayList<PointF> pointsArray = GraphicUtils.getPointsFromJson(jsonString);
        PointsAdapter pointsAdapter = new PointsAdapter(pointsArray);
        ListView pointsList = (ListView)findViewById(R.id.pointsList);
        pointsList.setAdapter(pointsAdapter);
        LinearLayout graphLayout = (LinearLayout)findViewById(R.id.graphLayout);
        graphLayout.addView(GraphicUtils.getGraphicView(this, pointsArray));
    }

    private class PointsAdapter extends ArrayAdapter<PointF> {
        public PointsAdapter(ArrayList<PointF> points) {
            super(DataActivity.this, R.layout.list_item, points);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = DataActivity.this.getLayoutInflater().inflate(R.layout.list_item, null);
            }
            TextView tvN = (TextView)convertView.findViewById(R.id.item_n);
            tvN.setText(String.valueOf(position+1));
            PointF p = getItem(position);
            TextView tvX = (TextView)convertView.findViewById(R.id.item_x);
            tvX.setText(String.valueOf(p.x));
            TextView tvY = (TextView)convertView.findViewById(R.id.item_y);
            tvY.setText(String.valueOf(p.y));
            return convertView;
        }
    }
}
