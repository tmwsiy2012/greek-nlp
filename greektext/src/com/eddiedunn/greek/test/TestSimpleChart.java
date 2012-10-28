package com.eddiedunn.greek.test;

import com.googlecode.charts4j.Data;
import com.googlecode.charts4j.GCharts;
import com.googlecode.charts4j.Plot;
import com.googlecode.charts4j.Plots;
import com.googlecode.charts4j.ScatterPlot;

public class TestSimpleChart {

    /**
     * @param args
     */
    public static void main(String[] args) {
	
        Plot plot = Plots.newPlot(Data.newData(0, 66.6, 33.3, 100));
        ScatterPlot chart = GCharts.newScatterPlot(plot);
        String url = chart.toURLString();
        System.out.println(url);

    }

}
