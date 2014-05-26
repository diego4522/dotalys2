package de.lighti.components.player.histogram;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jfree.chart.ChartPanel;

import de.lighti.io.ChartCreator;
import de.lighti.model.AppState;

public class HistogramComponent extends JSplitPane {
    /**
     * 
     */
    private static final long serialVersionUID = 9074089320819436807L;
    private ChartPanel chartPanel;
    private JList<String> playerBox;
    private final AppState appState;
    private JComboBox<String> attributeBox;
    private JPanel selectionPanel;

    public HistogramComponent( AppState appState ) {
        this.appState = appState;

        setOrientation( JSplitPane.HORIZONTAL_SPLIT );
        setOneTouchExpandable( false );
        setDividerLocation( 150 );
        setDividerSize( 0 );
        setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
        setLeftComponent( getSelectionPanel() );
        setRightComponent( getChartPanel() );
    }

    public JComboBox<String> getAttributeBox() {
        if (attributeBox == null) {
            attributeBox = new JComboBox<String>( appState.getAttributeListModel() ) {

                /**
                 * 
                 */
                private static final long serialVersionUID = 7315048556938443236L;

                /** 
                 * @inherited <p>
                 */
                @Override
                public Dimension getMaximumSize() {
                    final Dimension max = super.getMaximumSize();
                    max.height = getPreferredSize().height;
                    return max;
                }

            };

            appState.getAttributeListModel().addListDataListener( new ListDataListener() {

                @Override
                public void contentsChanged( ListDataEvent e ) {

                }

                @Override
                public void intervalAdded( ListDataEvent e ) {
                    getAttributeBox().setEnabled( true );

                    getPlayerBox().setEnabled( true );
                }

                @Override
                public void intervalRemoved( ListDataEvent e ) {

                }
            } );
            attributeBox.setAlignmentX( Component.CENTER_ALIGNMENT );
            attributeBox.setEnabled( false );
            attributeBox.addActionListener( new ActionListener() {

                @Override
                public void actionPerformed( ActionEvent e ) {
                    ChartCreator.createPlayerHistogram( getChartPanel(), (String) attributeBox.getSelectedItem(), playerBox.getSelectedValuesList(), appState );

                }
            } );

        }
        return attributeBox;
    }

    public ChartPanel getChartPanel() {
        if (chartPanel == null) {
            chartPanel = new ChartPanel( null );
            // default size
            chartPanel.setPreferredSize( new java.awt.Dimension( 500, 270 ) );
        }
        return chartPanel;
    }

    public JList<String> getPlayerBox() {
        if (playerBox == null) {
            playerBox = new JList<String>( appState.getPlayerListModel() );
            playerBox.setLayoutOrientation( JList.VERTICAL );
            playerBox.setVisibleRowCount( 10 );
            playerBox.setFixedCellHeight( 12 );
            playerBox.setFixedCellWidth( 200 );
            playerBox.setEnabled( false );
            playerBox.addListSelectionListener( new ListSelectionListener() {

                @Override
                public void valueChanged( ListSelectionEvent e ) {
                    ChartCreator.createPlayerHistogram( getChartPanel(), (String) attributeBox.getSelectedItem(), playerBox.getSelectedValuesList(), appState );

                }
            } );
        }

        return playerBox;
    }

    public JPanel getSelectionPanel() {
        if (selectionPanel == null) {
            selectionPanel = new JPanel();
            selectionPanel.setLayout( new BoxLayout( selectionPanel, BoxLayout.Y_AXIS ) );

            final JScrollPane listScroller = new JScrollPane( getPlayerBox() );
            final Dimension d = new Dimension( 200, 30 + 10 * 12 );

            listScroller.setPreferredSize( d );
            listScroller.setMaximumSize( d );

            listScroller.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED );
            listScroller.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED );

            selectionPanel.add( listScroller );
            selectionPanel.add( Box.createRigidArea( new Dimension( 0, 5 ) ) );

            selectionPanel.add( getAttributeBox() );
            selectionPanel.add( Box.createVerticalGlue() );
            selectionPanel.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
        }
        return selectionPanel;
    }
}
