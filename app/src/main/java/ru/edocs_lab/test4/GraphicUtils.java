package ru.edocs_lab.test4;

import android.app.Activity;
import android.graphics.PointF;
import android.view.View;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class GraphicUtils {

    public static ArrayList<PointF> getPointsFromJson(String json) {
        ArrayList<PointF> pointsArr = new ArrayList<>();
        PointF point;
        JSONObject obj;
        try {
            JSONArray jsonArray = new JSONArray(json);
            for(int i=0; i<jsonArray.length();i++){
                obj = jsonArray.getJSONObject(i);
                point = new PointF(Float.valueOf(obj.getString("x")), Float.valueOf(obj.getString("y")));
                pointsArr.add(point);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return pointsArr;
    }

    public static View getGraphicView(Activity activity, ArrayList<PointF> pointsArray) {
        GraphView graph = new GraphView(activity);
        DataPoint dpArray[] = getGraphDataPoints(pointsArray);
        graph.getGridLabelRenderer().setHorizontalLabelsAngle(45);
        graph.getGridLabelRenderer().setTextSize(12);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(dpArray[0].getX());
        graph.getViewport().setMaxX(dpArray[dpArray.length-1].getX());
        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dpArray);
        series.setThickness(2);
        graph.addSeries(series);
        return graph;
    }

    private static DataPoint[] getGraphDataPoints(ArrayList<PointF> pointsArray) {
        DataPoint dpArray[] = new DataPoint[pointsArray.size()];
        for(int i=0; i<pointsArray.size(); i++) {
            dpArray[i] = new DataPoint(pointsArray.get(i).x, pointsArray.get(i).y);
        }
        Arrays.sort(dpArray, new Comparator<DataPoint>() {
            @Override
            public int compare(DataPoint lhs, DataPoint rhs) {
                if (rhs.getX() == lhs.getX()) {
                    return 0;
                } else {
                    return rhs.getX() < lhs.getX()? 1: -1;
                }
            }
        });
        return dpArray;
    }
}
