package de.lighti;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import de.lighti.components.DotalysMenuBar;
import de.lighti.components.map.MapComponent;
import de.lighti.components.match.GameStatisticsComponent;
import de.lighti.components.player.histogram.HistogramComponent;
import de.lighti.components.player.statistics.PlayerComponent;
import de.lighti.io.DataImporter;
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
            UIManager.put( "SplitPane.background", Color.WHITE );
            UIManager.put( "OptionPane.background", Color.WHITE );
        }
        catch (final Exception e) {
            // Don't care
        }
    }

    private JTabbedPane mainView;

    public Dotalys2App() {
        super( "Dotalys2" );

        appState = new AppState();
        parseLocalisedHeroNames();

        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        setSize( new Dimension( 900, 700 ) );
        setResizable( false );

        final JComponent com = getMainView();
        com.setPreferredSize( getContentPane().getPreferredSize() );

        getContentPane().add( com, BorderLayout.CENTER );
        // pack();
        setJMenuBar( new DotalysMenuBar( this ) );

    }

    public AppState getAppState() {
        return appState;
    }

    public JComponent getMainView() {
        if (mainView == null) {
            mainView = new JTabbedPane();
            mainView.addTab( "Player Histograms", new HistogramComponent( appState ) );

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

            final GameStatisticsComponent gsc = new GameStatisticsComponent( getAppState() );
            mainView.addTab( "Match Analysis", gsc );

            mainView.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
        }

        return mainView;
    }

    private void parseLocalisedHeroNames() {
        DataImporter.readLocalisedHeroNames( getClass().getResourceAsStream( "heroes.xml" ), appState );

    }
}
