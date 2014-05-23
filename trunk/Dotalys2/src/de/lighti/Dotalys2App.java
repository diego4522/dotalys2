package de.lighti;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.jfree.chart.ChartPanel;

import de.lighti.components.DotalysMenuBar;
import de.lighti.components.map.MapComponent;
import de.lighti.components.player.PlayerComponent;
import de.lighti.io.ChartCreator;
import de.lighti.model.AppState;

public class Dotalys2App extends JFrame {
    private final AppState appState;

    /**
     * 
     */
    private static final long serialVersionUID = -5920990846685808741L;
    static {
        try {
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
            UIManager.put( "Panel.background", Color.WHITE );
            UIManager.put( "Slider.background", Color.WHITE );
        }
        catch (final Exception e) {
            // Don't care
        }
    }

    private JTabbedPane mainView;

    public Dotalys2App() {
        super( "Dotalys2" );

        appState = new AppState();

        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        setSize( new Dimension( 900, 700 ) );

        final JComponent com = getMainView();
        com.setPreferredSize( getContentPane().getPreferredSize() );

        getContentPane().add( com, BorderLayout.CENTER );
        // pack();
        setJMenuBar( new DotalysMenuBar( this ) );

    }

    /**
     * @return
     */
    private ChartPanel createChartComponent() {
        // This will create the dataset
        // final PieDataset dataset = chartCreator.createDataset();
        // // based on the dataset we create the chart
        // final JFreeChart chart = chartCreator.createChart( dataset, "Dummy"
        // );
        // we put the chart into a panel
        final ChartPanel chartPanel = new ChartPanel( null );
        // default size
        chartPanel.setPreferredSize( new java.awt.Dimension( 500, 270 ) );
        // add it to our application
        return chartPanel;
    }

    private JComponent createHistogramTab() {
        final ChartPanel rightPane = createChartComponent();
        final JList<String> playerBox = new JList<String>( appState.getPlayerListModel() );
        playerBox.setVisibleRowCount( 10 );
        playerBox.setFixedCellHeight( 12 );
        playerBox.setFixedCellWidth( 200 );
        playerBox.setAlignmentX( Component.CENTER_ALIGNMENT );

        final JComboBox<String> attributeBox = new JComboBox<String>( appState.getAttributeListModel() );
        attributeBox.setAlignmentX( Component.CENTER_ALIGNMENT );
        attributeBox.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                ChartCreator.createPlayerHistogram( rightPane, (String) attributeBox.getSelectedItem(), playerBox.getSelectedValuesList(), appState );

            }
        } );

        final JPanel leftPane = new JPanel();
        leftPane.setLayout( new BoxLayout( leftPane, BoxLayout.Y_AXIS ) );

        leftPane.add( playerBox );
        leftPane.add( attributeBox );
        leftPane.add( Box.createVerticalGlue() );
        // leftPane.setBackground( Color.red );

        final JSplitPane playerTabPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, leftPane, rightPane );
        playerTabPane.setOneTouchExpandable( false );
        playerTabPane.setDividerLocation( 150 );
        // Provide minimum sizes for the two components in the split pane
        final Dimension minimumSize = new Dimension( 100, 100 );
        leftPane.setMinimumSize( minimumSize );
        rightPane.setMinimumSize( minimumSize );
        return playerTabPane;
    }

    public AppState getAppState() {
        return appState;
    }

    public JComponent getMainView() {
        if (mainView == null) {
            mainView = new JTabbedPane();
            mainView.addTab( "Player Histograms", createHistogramTab() );

            final PlayerComponent pc = new PlayerComponent( appState );
            mainView.addTab( "Player Statistics", pc );
            try {
                final MapComponent mc = new MapComponent( appState );
                mainView.addTab( "Map Events", mc );
            }
            catch (final IOException e) {
                JOptionPane.showMessageDialog( this, e.getLocalizedMessage() );
            }
            mainView.setEnabled( false );
            appState.getPlayerComboModel().addListDataListener( new ListDataListener() {

                @Override
                public void contentsChanged( ListDataEvent e ) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void intervalAdded( ListDataEvent e ) {
                    mainView.setEnabled( true );

                }

                @Override
                public void intervalRemoved( ListDataEvent e ) {
                    // TODO Auto-generated method stub

                }

            } );
        }
        return mainView;
    }
}
