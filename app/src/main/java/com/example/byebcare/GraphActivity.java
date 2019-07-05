package com.example.byebcare;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import butterknife.BindView;
import butterknife.ButterKnife;
// org.json.JSONException;
//import org.json.JSONObject;

import com.anychart.APIlib;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.anychart.enums.Orientation;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;
import com.anychart.scales.Linear;
import com.anychart.core.cartesian.series.Base;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class GraphActivity extends AppCompatActivity {

   /* private JSONObject jsonObject;

   public GraphActivity(JSONObject jsonObject){
        this.jsonObject = jsonObject;
        try{
            Number tempAmb = (Number)jsonObject.get("A");
            Number tempB = (Number)jsonObject.get("O");
            Number bpm = (Number) jsonObject.get("B");
        }catch (JSONException e){
            e.printStackTrace();
        }
    }   */
    @BindView(R.id.any_chart_view) AnyChartView anyChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        ButterKnife.bind(this);
        anyChartView.setProgressBar(findViewById(R.id.progress_bar));

        Cartesian cartesian = AnyChart.line();
        Cartesian cartesian2 = AnyChart.column();

        cartesian.animation(true);

        cartesian.padding(5d, 10d, 5d, 10d);

        cartesian.crosshair().enabled(true);
        cartesian.crosshair()
                .yLabel(true)
                // TODO ystroke
                .yStroke((Stroke) null, null, null, (String) null, (String) null);

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.title("Record of your baby condition");
        cartesian.yAxis(0).title("Temperature(celsius)");
        cartesian.yAxis(0).staggerLines(80);


        Linear scalesLinear2 = Linear.instantiate();
        scalesLinear2.minimum(40d);
        scalesLinear2.maximum(100d);
        scalesLinear2.ticks("{interval:10}");

        Linear scalesLinear = Linear.instantiate();
        scalesLinear.minimum(0d);
        scalesLinear.maximum(50d);
        scalesLinear.ticks("{interval:5}");
        
        cartesian.yAxis(0).scale(scalesLinear);

        com.anychart.core.axes.Linear extraYAxis = cartesian.yAxis(1);
        cartesian.yAxis(1).title("Pulse(number/minute)");
        extraYAxis.orientation(Orientation.RIGHT)
                .scale(scalesLinear2);
        extraYAxis.labels()
                .padding(0d,0d,0d,5d);


        cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);

        List<DataEntry> seriesData = new ArrayList<>();
        seriesData.add(new CustomDataEntry("01", 22.5, 33.3, 65));
        seriesData.add(new CustomDataEntry("02", 25.5, 37.3, 77));
        seriesData.add(new CustomDataEntry("03", 26.5, 32.5, 80));
        seriesData.add(new CustomDataEntry("04", 18.5, 28.3, 88));
        seriesData.add(new CustomDataEntry("05", 28.5, 25.7, 50));



        Set set = Set.instantiate();
        set.data(seriesData);
        Mapping tempAmbData = set.mapAs("{ x: 'x', value: 'tempAmb' }");
        Mapping tempBData = set.mapAs("{ x: 'x', value: 'tempB' }");
        Mapping PulseData = set.mapAs("{ x: 'x', value: 'bpm' }" );


        Line seriesA = cartesian.line(tempAmbData);
        seriesA.yScale(scalesLinear);
        seriesA.name("Ambient.t");
        seriesA.hovered().markers().enabled(true);
        seriesA.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(5d);
        seriesA.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);

        Line seriesB = cartesian.line(tempBData);
        seriesB.yScale(scalesLinear);
        seriesB.name("Baby.t");
        seriesB.hovered().markers().enabled(true);
        seriesB.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(5d);
        seriesB.tooltip()
                .position("left")
                .anchor(Anchor.CENTER)
                .offsetX(5d)
                .offsetY(5d);

        Column seriesP = cartesian.column(PulseData);
        seriesP.yScale(scalesLinear2);
        seriesP.name("BPM");
        seriesP.hovered().markers().enabled(true);
        seriesP.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(5d);
        seriesP.tooltip()
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("${%Value}{groupsSeparator:}");

        cartesian.legend().enabled(true);
        cartesian.legend().fontSize(15d);
        cartesian.legend().padding(0d, 0d, 15d, 0d);

        anyChartView.setChart(cartesian);

    }

    private class CustomDataEntry extends ValueDataEntry {

        CustomDataEntry(String x,Number tempAmb, Number tempB, Number bpm) {
            super(x, tempAmb);
            setValue("tempB", tempB);
            setValue("bpm", bpm);
        }

    }
}
