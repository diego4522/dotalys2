package de.lighti.components.map;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import de.lighti.io.ChartCreator;
import de.lighti.io.DataExporter;
import de.lighti.model.AppState;
import de.lighti.model.game.Player;

public class MapComponent extends JSplitPane {
    /**
     * 
     */
    private static final long serialVersionUID = 1045770296903996356L;

    private final AppState appState;

    private JTree attributeTree;

    public final static String CAT_MOVEMENT = "Movement";
    public final static String CAT_DEATHS = "Deaths";
    private MapCanvasComponent mapCanvas;

    private JComponent optionContainer;

    private JComponent mapCanvasContainer;

    public MapComponent( AppState state ) throws IOException {
        appState = state;

        setOrientation( JSplitPane.HORIZONTAL_SPLIT );

        setRightComponent( getMapCanvansContainer() );
        setLeftComponent( getAttributeTree() );
        setResizeWeight( 1.0 );
        setOneTouchExpandable( false );

        appState.getPlayerComboModel().addListDataListener( new ListDataListener() {

            @Override
            public void contentsChanged( ListDataEvent e ) {

            }

            @Override
            public void intervalAdded( ListDataEvent e ) {
                final Collection<Player> p = appState.getPlayers().values();

                final String[] temp = new String[p.size()];
                int i = 0;
                for (final Player q : p) {
                    temp[i++] = q.getName();
                }
                buildTreeNodes( temp );

            }

            @Override
            public void intervalRemoved( ListDataEvent e ) {
                // TODO Auto-generated method stub

            }
        } );

    }

    private void buildTreeNodes( String[] playerNames ) {

        final DefaultMutableTreeNode movement = new DefaultMutableTreeNode( CAT_MOVEMENT );
        for (final String p : playerNames) {
            movement.add( new DefaultMutableTreeNode( p ) );
        }
        final DefaultMutableTreeNode deaths = new DefaultMutableTreeNode( CAT_DEATHS );
        for (final String p : playerNames) {
            deaths.add( new DefaultMutableTreeNode( p ) );
        }

        final DefaultTreeModel model = (DefaultTreeModel) attributeTree.getModel();
        final DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        root.removeAllChildren();
        root.add( movement );
        root.add( deaths );

        model.reload( root );
    }

    private ActionListener createExportButtonActionListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent e ) {
                final DefaultMutableTreeNode node = (DefaultMutableTreeNode) getAttributeTree().getLastSelectedPathComponent();
                if ((node != null) && node.isLeaf()) {
                    final String selection = (String) node.getUserObject();
                    final Player p = appState.getPlayerByName( selection );
                    if (p != null) {
                        final DefaultMutableTreeNode category = (DefaultMutableTreeNode) node.getParent();
                        final String catName = (String) category.getUserObject();
                        switch (catName) {
                            case CAT_MOVEMENT:
                                final String[][] log = ChartCreator.createMoveLog( p.getName(), appState );
                                MapComponent.this.doSaveDialog( catName, log );
                                break;
                            default:
                                JOptionPane.showMessageDialog( getMapCanvas(), "Exporting " + catName + " is not implemented", "We're terribly sorry",
                                                JOptionPane.ERROR_MESSAGE );
                        }
                    }
                }

            }
        };
    }

    private void doSaveDialog( String category, String[][] data ) {
        //Create a file chooser
        final JFileChooser fc = new JFileChooser( "." );
        fc.setFileFilter( new FileFilter() {

            @Override
            public boolean accept( File f ) {
                return f.isDirectory() || f.getName().endsWith( ".csv" );
            }

            @Override
            public String getDescription() {
                return "comma-separated values (*.csv)";
            }
        } );

        final int returnVal = fc.showSaveDialog( this );
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                String header = "# <unknown data>";
                switch (category) {
                    case CAT_MOVEMENT:
                        header = "#tickms, x , y";
                        break;
                    default:
                        break;
                }
                DataExporter.exportCSV( fc.getSelectedFile(), header, data );
            }
            catch (final IOException e) {
                JOptionPane.showMessageDialog( getMapCanvas(), e.getLocalizedMessage(), "We're terribly sorry", JOptionPane.ERROR_MESSAGE );
            }
        }
    }

    private JTree getAttributeTree() {
        if (attributeTree == null) {
            attributeTree = new JTree( new DefaultMutableTreeNode( "Attributes" ) );
            attributeTree.addTreeSelectionListener( new TreeSelectionListener() {

                @Override
                public void valueChanged( TreeSelectionEvent e ) {

                    final DefaultMutableTreeNode node = (DefaultMutableTreeNode) attributeTree.getLastSelectedPathComponent();
                    if ((node != null) && node.isLeaf()) {
                        final String selection = (String) node.getUserObject();
                        final Player p = appState.getPlayerByName( selection );
                        if (p != null) {
                            final DefaultMutableTreeNode category = (DefaultMutableTreeNode) node.getParent();
                            final String catName = (String) category.getUserObject();
                            getOptionContainer().setEnabled( true );
                            switch (catName) {
                                case CAT_MOVEMENT:
                                    final int[] mmarkers = ChartCreator.createMoveMap( p.getName(), appState );
                                    getMapCanvas().setMarkers( mmarkers );
                                    break;
                                case CAT_DEATHS:
                                    final int[] dmarkers = ChartCreator.createDeathMap( p.getName(), appState );
                                    getMapCanvas().setMarkers( dmarkers );
                                    break;
                                default:
                                    getOptionContainer().setEnabled( false );
                                    System.err.println( "Unknown category in tree" );
                                    break;
                            }
                        }
                    }

                }
            } );
        }
        return attributeTree;
    }

    private JComponent getMapCanvansContainer() {
        if (mapCanvasContainer == null) {
            mapCanvasContainer = new JPanel();
            mapCanvasContainer.add( getMapCanvas() );
            mapCanvasContainer.add( getOptionContainer() );
        }

        return mapCanvasContainer;
    }

    private MapCanvasComponent getMapCanvas() {
        if (mapCanvas == null) {
            mapCanvas = new MapCanvasComponent();
        }
        return mapCanvas;
    }

    private Component getOptionContainer() {
        if (optionContainer == null) {
            final JButton button = new JButton( "Export" );
            button.addActionListener( createExportButtonActionListener() );
            button.setEnabled( false );

            final JCheckBox all = new JCheckBox( "All" );
            all.setEnabled( false );

            final JSlider slider = new JSlider();
            slider.setEnabled( false );

            optionContainer = new JPanel( new FlowLayout() ) {

                /**
                 * 
                 */
                private static final long serialVersionUID = 1098640225534974782L;

                @Override
                public void setEnabled( boolean enabled ) {
                    super.setEnabled( enabled );

                    button.setEnabled( enabled );
                    slider.setEnabled( enabled );
                    all.setEnabled( enabled );
                }

            };
            optionContainer.add( all );
            optionContainer.add( button );
            optionContainer.add( slider );

        }
        return optionContainer;
    }
}
