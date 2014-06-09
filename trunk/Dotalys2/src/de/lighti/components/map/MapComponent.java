package de.lighti.components.map;

import java.io.IOException;
import java.util.Collection;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import de.lighti.io.ChartCreator;
import de.lighti.model.AppState;
import de.lighti.model.Statics;
import de.lighti.model.game.Player;

public class MapComponent extends JSplitPane {
    /**
     * 
     */
    private static final long serialVersionUID = 1045770296903996356L;

    private final AppState appState;

    private JTree attributeTree;

    public final static String CAT_MOVEMENT = Statics.MOVEMENT;
    public final static String CAT_DEATHS = Statics.DEATHS;
    private MapCanvasComponent mapCanvas;

    private OptionContainer optionContainer;

    private JPanel mapCanvasContainer;

    private final static Logger LOGGER = Logger.getLogger( MapCanvasComponent.class.getName() );

    public MapComponent( AppState state ) throws IOException {
        appState = state;

        setOrientation( JSplitPane.HORIZONTAL_SPLIT );

        setRightComponent( getMapCanvansContainer() );
        setLeftComponent( getAttributeTree() );
        setResizeWeight( 1.0 );
        setOneTouchExpandable( false );
        setDividerSize( 0 );
        setDividerLocation( 150 );
        setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );

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
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
        if (root != null) {
            root.removeAllChildren();
        }
        else {
            root = new DefaultMutableTreeNode();
            model.setRoot( root );
        }
        root.add( movement );
        root.add( deaths );

        model.reload( root );
    }

    public JTree getAttributeTree() {
        if (attributeTree == null) {
            final TreeModel model = new DefaultTreeModel( null );
            attributeTree = new JTree( model );
            final TreeSelectionModel selectionModel = attributeTree.getSelectionModel();
            selectionModel.setSelectionMode( TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION );
            attributeTree.addTreeSelectionListener( new TreeSelectionListener() {

                @Override
                public void valueChanged( TreeSelectionEvent e ) {
                    final XYSeriesCollection series = getMapCanvas().getMarkers();
                    final TreePath[] paths = e.getPaths();
                    for (int i = 0; i < paths.length; i++) {
                        final TreePath p = paths[i];
                        final DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) p.getLastPathComponent();
                        final String selectionName = (String) selectedNode.getUserObject();
                        final DefaultMutableTreeNode category = (DefaultMutableTreeNode) selectedNode.getParent();
                        final String catName = (String) category.getUserObject();
                        if (selectedNode.isLeaf()) {
                            if (e.isAddedPath( i )) {
                                final Player player = appState.getPlayerByName( selectionName );

                                switch (catName) {
                                    case CAT_MOVEMENT: {
                                        final XYSeries s = ChartCreator.createMoveMap( player.getName(), appState );
                                        s.setKey( player.getName() + catName );
                                        series.addSeries( s );
                                    }
                                        break;
                                    case CAT_DEATHS: {
                                        final XYSeries s = ChartCreator.createDeathMap( player.getName(), appState );
                                        s.setKey( player.getName() + catName );
                                        series.addSeries( s );
                                    }
                                        break;
                                    default:
                                        LOGGER.warning( "Unknown category in tree" );
                                        break;
                                }
                                getOptionContainer().setEnabled( true );
                                getOptionContainer().getStepSlider().setMaximum( series.getItemCount( 0 ) );
                            }
                            else {
                                series.removeSeries( series.getSeries( selectionName + catName ) );
                            }
                        }
                    }
                    getMapCanvas().repaint();
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

    public MapCanvasComponent getMapCanvas() {
        if (mapCanvas == null) {
            mapCanvas = new MapCanvasComponent();
        }
        return mapCanvas;
    }

    private OptionContainer getOptionContainer() {
        if (optionContainer == null) {
            optionContainer = new OptionContainer( this, appState );
        }
        return optionContainer;
    }
}
