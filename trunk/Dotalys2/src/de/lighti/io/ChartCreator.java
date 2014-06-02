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

import de.lighti.components.match.GameStatisticsComponent;
import de.lighti.model.AppState;
import de.lighti.model.Statics;
import de.lighti.model.game.Hero;
import de.lighti.model.game.Player;
import de.lighti.model.game.Unit;

public final class ChartCreator {
    private final static Logger LOGGER = Logger.getLogger( ChartCreator.class.getName() );

    /**
     * Helper method that assigns a chart to a chart panel and sets up the layout.
     * Centralizing it here helps us make all graphs use the same layout.
     * 
     * @param panel the chart panel to send the data to
     * @param chart the chart to be formatted
     */
    public static void assignChart( ChartPanel panel, JFreeChart chart ) {

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
        plot.setRenderer( renderer );

        // change the auto tick unit selection to integer units only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits( NumberAxis.createIntegerTickUnits() );
        // OPTIONAL CUSTOMISATION COMPLETED.
        panel.setChart( chart );

    }

    /**
     * This method creates a data set with two series(one radiant, one dire) representing the average
     * distance between all members of that team. For each timestep, the symmetrical half of
     * and Euclidian distance matrix is calculated, and the entries for players of the same team are
     * added to the sum. Each sum is then divided by 5.
     * @param appState the current app state containing player data
     * @return a dat set containing two data series
     */
    private static XYDataset createAverageTeamDistanceDataSet( AppState appState ) {
        final XYSeriesCollection series = new XYSeriesCollection();
        final XYSeries goodGuys = new XYSeries( Statics.RADIANT );
        final XYSeries badGuys = new XYSeries( Statics.DIRE );

        for (long seconds = 0l; seconds < appState.getGameLength(); seconds += appState.getMsPerTick() * 1000) {
            long baddyDistance = 0l;
            long goodDistance = 0l;

            rowLoop: for (final Player p : appState.getPlayers().values()) {
                for (final Player q : appState.getPlayers().values()) {
                    if (p == q) {
                        continue rowLoop;
                    }
                    if (p.isRadiant() == q.isRadiant() == true) {
                        //Two radiant players
                        final int x1 = p.getHero().getX( seconds );
                        final int x2 = q.getHero().getX( seconds );
                        final int y1 = p.getHero().getY( seconds );
                        final int y2 = q.getHero().getY( seconds );
                        goodDistance += Math.sqrt( Math.pow( x1 - x2, 2 ) + Math.pow( y1 - y2, 2 ) );
                    }
                    else if (p.isRadiant() == q.isRadiant() == false) {
                        //Two dire players
                        final int x1 = p.getHero().getX( seconds );
                        final int x2 = q.getHero().getX( seconds );
                        final int y1 = p.getHero().getY( seconds );
                        final int y2 = q.getHero().getY( seconds );
                        baddyDistance += Math.sqrt( Math.pow( x1 - x2, 2 ) + Math.pow( y1 - y2, 2 ) );
                    }

                }
                //We should never be here
            }
            //Average
            goodDistance /= 5l;
            baddyDistance /= 5l;

            goodGuys.add( seconds, goodDistance );
            badGuys.add( seconds, baddyDistance );

        }
        series.addSeries( badGuys );
        series.addSeries( goodGuys );
        return series;
    }

    public static JFreeChart createAverageTeamDistanceGraph( AppState state ) {
        return ChartFactory.createXYLineChart( GameStatisticsComponent.AVERAGE_TEAM_DISTANCE, // chart title
                        Statics.MILISECONDS, // x axis label
                        "", // y axis label
                        createAverageTeamDistanceDataSet( state ), // data
                        PlotOrientation.VERTICAL, true, // include legend
                        true, // tooltips
                        false // urls
                        );
    }

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

    public static JFreeChart createPlayerHistogram( String selectedItem, List<String> selectedValuesList, AppState state ) {
        return ChartFactory.createXYLineChart( selectedItem, // chart title
                        Statics.MILISECONDS, // x axis label
                        "", // y axis label
                        createPlayerDataSet( selectedItem, selectedValuesList, state ), // data
                        PlotOrientation.VERTICAL, true, // include legend
                        true, // tooltips
                        false // urls
                        );
    }

    /**
     * Default constructor to prevent instantiation.
     */
    private ChartCreator() {

    }
}
