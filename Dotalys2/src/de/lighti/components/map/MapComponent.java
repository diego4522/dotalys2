package de.lighti.components.map;

import java.io.IOException;
import java.util.Collection;

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

import de.lighti.io.ChartCreator;
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

    private OptionContainer optionContainer;

    private JPanel mapCanvasContainer;

    private int[] markers;

    public MapComponent( AppState state ) throws IOException {
        appState = state;

        setOrientation( JSplitPane.HORIZONTAL_SPLIT );

        setRightComponent( getMapCanvansContainer() );
        setLeftComponent( getAttributeTree() );
        setResizeWeight( 1.0 );
        setOneTouchExpandable( false );
        setDividerSize( 0 );
        setBorder( null );

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

            attributeTree.addTreeSelectionListener( new TreeSelectionListener() {

                @Override
                public void valueChanged( TreeSelectionEvent e ) {

                    final DefaultMutableTreeNode node = (DefaultMutableTreeNode) attributeTree.getLastSelectedPathComponent();
                    if (node != null && node.isLeaf()) {
                        final String selection = (String) node.getUserObject();
                        final Player p = appState.getPlayerByName( selection );
                        if (p != null) {
                            final DefaultMutableTreeNode category = (DefaultMutableTreeNode) node.getParent();
                            final String catName = (String) category.getUserObject();

                            switch (catName) {
                                case CAT_MOVEMENT:
                                    markers = ChartCreator.createMoveMap( p.getName(), appState );
                                    getMapCanvas().setMarkers( markers );
                                    break;
                                case CAT_DEATHS:
                                    final int[] mmarkers = ChartCreator.createDeathMap( p.getName(), appState );
                                    getMapCanvas().setMarkers( mmarkers );
                                    break;
                                default:
                                    getOptionContainer().setEnabled( false );
                                    System.err.println( "Unknown category in tree" );
                                    break;
                            }
                            getOptionContainer().setEnabled( true );
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

    public MapCanvasComponent getMapCanvas() {
        if (mapCanvas == null) {
            mapCanvas = new MapCanvasComponent();
        }
        return mapCanvas;
    }

    public int[] getMarkers() {
        return markers;
    }

    private OptionContainer getOptionContainer() {
        if (optionContainer == null) {
            optionContainer = new OptionContainer( this, appState );
        }
        return optionContainer;
    }
}
