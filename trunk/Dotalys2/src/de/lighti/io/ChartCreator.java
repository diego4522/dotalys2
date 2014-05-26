package de.lighti.io;

import java.awt.Color;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import de.lighti.model.AppState;
import de.lighti.model.game.Hero;
import de.lighti.model.game.Player;
import de.lighti.model.game.Unit;

public final class ChartCreator {
    private final static Logger LOGGER = Logger.getLogger( ChartCreator.class.getName() );

    public static int[] createDeathMap( String name, AppState appState ) {
        final Player p = appState.getPlayerByName( name );

        final Hero hero = p.getHero();
        final Collection<int[]> coords = hero.getDeaths().values();
        final int[] ret = new int[coords.size() * 2];
        int offset = 0;

        for (final int[] e : coords) {
            ret[offset++] = e[0];
            ret[offset++] = e[1];
        }
        return ret;
    }

    public static String[][] createMoveLog( String string, AppState appState ) {
        final Player p = appState.getPlayerByName( string );

        final Unit hero = p.getHero();
        final Map<Long, Integer> x = hero.getX();
        final Map<Long, Integer> y = hero.getY();
        final String[][] ret = new String[x.size()][];
        int i = 0;
        for (final Map.Entry<Long, Integer> e : x.entrySet()) {
            ret[i] = new String[] { e.getKey().toString(), e.getValue().toString(), y.get( e.getKey() ).toString() };
            i++;
        }

        return ret;
    }

    public static int[] createMoveMap( String string, AppState appState ) {

        final Player p = appState.getPlayerByName( string );

        final Unit hero = p.getHero();
        final Map<Long, Integer> x = hero.getX();
        final Map<Long, Integer> y = hero.getY();
        final int[] ret = new int[x.size() * 2];
        int offset = 0;

        for (final Map.Entry<Long, Integer> e : x.entrySet()) {
            ret[offset++] = e.getValue();
            ret[offset++] = y.get( e.getKey() );
        }
        return ret;
    }

    private static XYDataset createPlayerDataSet( String attribute, List<String> players, AppState appState ) {
        final XYSeriesCollection series = new XYSeriesCollection();

        try {
            for (final String player : players) {
                final String id = appState.getPlayerByName( player ).getId();
                final XYSeries series1 = new XYSeries( player );

                for (final Entry<Long, Map<String, Object>> e : appState.gameEventsPerMs.entrySet()) {
                    if (e.getValue().containsKey( attribute + id )) {
                        final Number v = (Number) e.getValue().get( attribute + id );
                        series1.add( e.getKey(), v );
                    }
                }
                series.addSeries( series1 );

            }
        }
        catch (final ClassCastException e) {
            LOGGER.warning( "Selected attribute contained alpha-numeric data" );
        }
        return series;
    }

    public static void createPlayerHistogram( ChartPanel rightPane, String selectedItem, List<String> selectedValuesList, AppState state ) {

        final JFreeChart chart = ChartFactory.createXYLineChart( selectedItem, // chart title
                        "Miliseconds", // x axis label
                        "", // y axis label
                        createPlayerDataSet( selectedItem, selectedValuesList, state ), // data
                        PlotOrientation.VERTICAL, true, // include legend
                        true, // tooltips
                        false // urls
                        );
        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
        chart.setBackgroundPaint( Color.white );

//        final StandardLegend legend = (StandardLegend) chart.getLegend();
        //      legend.setDisplaySeriesShapes(true);

        // get a reference to the plot for further customisation...
        final XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint( Color.lightGray );
        //    plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
        plot.setDomainGridlinePaint( Color.white );
        plot.setRangeGridlinePaint( Color.white );

        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible( 0, false );
        renderer.setSeriesShapesVisible( 1, false );
        plot.setRenderer( renderer );

        // change the auto tick unit selection to integer units only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits( NumberAxis.createIntegerTickUnits() );
        // OPTIONAL CUSTOMISATION COMPLETED.
        rightPane.setChart( chart );

    }

    private ChartCreator() {

    }
}
