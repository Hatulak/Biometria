/*
 * Created by JFormDesigner on Sun Mar 17 19:11:56 CET 2019
 */

package gui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.DefaultIntervalXYDataset;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * @author asd
 */
public class HistogramsFrame extends JFrame {
    public HistogramsFrame(List<int[]> histograms) {
        initComponents();
        generateHistograms(histograms);
        this.setVisible(true);
    }

    private void generateHistograms(List<int[]> histograms) {
        String[] names = new String[]{"Red", "Green", "Blue", "Average"};
        for (int j = 0; j < histograms.size(); j++) {
            int[] h = histograms.get(j);

            DefaultIntervalXYDataset xyDataset = new DefaultIntervalXYDataset();
            double[] x = new double[256];
            double[] startx = new double[256];
            double[] endx = new double[256];
            double[] y = new double[256];
            double[] starty = new double[256];
            double[] endy = new double[256];

            for (int i = 0; i < x.length; i++) {
                x[i] = i;
                startx[i] = i - 0.2;
                endx[i] = i + 0.2;
                y[i] = h[i];
                starty[i] = i;
                endy[i] = i;
            }

            double[][] datasetDouble = new double[][]{x, startx, endx, y, starty, endy};
            xyDataset.addSeries("", datasetDouble);

            JFreeChart chart = ChartFactory.createXYBarChart("Histogram" + names[j], "", false, "", xyDataset, PlotOrientation.VERTICAL, false, false, false);

            XYPlot xyPlot = chart.getXYPlot();
            ValueAxis categoryAxis = xyPlot.getDomainAxis();
            categoryAxis.setLowerMargin(0.0);
            categoryAxis.setUpperMargin(0.0);

            XYItemRenderer barRenderer = xyPlot.getRenderer();
            switch (j) {
                case 0:
                    barRenderer.setSeriesPaint(0, Color.red);
                    break;
                case 1:
                    barRenderer.setSeriesPaint(0, Color.green);
                    break;
                case 2:
                    barRenderer.setSeriesPaint(0, Color.blue);
                    break;
                case 3:
                    barRenderer.setSeriesPaint(0, Color.gray);
                    break;
            }
            ChartPanel chartPanel = new ChartPanel(chart);


            this.add(chartPanel);
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - asd

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new GridLayout(2, 2));
        setSize(1190, 880);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - asd
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
