package de.lighti.components.map;

import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.jfree.data.xy.XYSeries;

import de.lighti.io.ChartCreator;
import de.lighti.model.AppState;
import de.lighti.model.Statics;
import de.lighti.model.game.Ability;
import de.lighti.model.game.Hero;
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
    public final static String CAT_ZONES = Statics.ZONES;
    public final static String CAT_ABILITIES = Statics.ABILITIES;

    private MapCanvasComponent mapCanvas;

    private OptionContainer optionContainer;

    private JPanel mapCanvasContainer;

    private final static Logger LOGGER = Logger.getLogger( MapCanvasComponent.class.getName() );

    public MapComponent( AppState state ) {
        appState = state;

        setOrientation( JSplitPane.HORIZONTAL_SPLIT );

        setRightComponent( getMapCanvansContainer() );
        setLeftComponent( new JScrollPane( getAttributeTree() ) );
        setResizeWeight( 1.0 );
        setOneTouchExpandable( false );
        setDividerSize( 0 );
        setDividerLocation( 250 );
        setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );

    }

    public void buildTreeNodes( Iterable<Player> players ) {

        final DefaultMutableTreeNode movement = new DefaultMutableTreeNode( CAT_MOVEMENT );
        for (final Player p : players) {
            movement.add( new DefaultMutableTreeNode( p.getName() ) );
        }
        final DefaultMutableTreeNode deaths = new DefaultMutableTreeNode( CAT_DEATHS );
        for (final Player p : players) {
            deaths.add( new DefaultMutableTreeNode( p.getName() ) );
        }

        final DefaultMutableTreeNode abilities = new DefaultMutableTreeNode( CAT_ABILITIES );
        for (final Player p : players) {
            final DefaultMutableTreeNode playerNode = new DefaultMutableTreeNode( p.getName() );
            final Hero h = p.getHero();
            for (final Ability a : h.getAbilities()) {
                if (!a.getKey().equals( "attribute_bonus" )) {
                    playerNode.add( new DefaultMutableTreeNode( a ) );
                }
            }
            abilities.add( playerNode );
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
        root.add( abilities );

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
                    final TreePath[] paths = e.getPaths();
                    for (int i = 0; i < paths.length; i++) {
                        final TreePath p = paths[i];
                        final DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) p.getLastPathComponent();

                        if (selectedNode.isLeaf()) {
                            final Object selection = selectedNode.getUserObject();

                            String catName = null;
                            DefaultMutableTreeNode category = selectedNode;
                            while (catName == null) {
                                category = (DefaultMutableTreeNode) category.getParent();
                                final String value = (String) category.getUserObject();

                                switch (value) {
                                    case CAT_MOVEMENT:
                                    case CAT_DEATHS:
                                    case CAT_ABILITIES:
                                        catName = value;
                                        break;
                                }
                            }
                            if (e.isAddedPath( i )) {

                                switch (catName) {
                                    case CAT_MOVEMENT: {
                                        final Player player = appState.getPlayerByName( (String) selection );

                                        final XYSeries s = ChartCreator.createMoveMap( player.getName(), appState );
                                        s.setKey( player.getName() + catName );
                                        getMapCanvas().addSeries( s, player.getId() );
                                    }
                                    break;
                                    case CAT_DEATHS: {
                                        final Player player = appState.getPlayerByName( (String) selection );

                                        final XYSeries s = ChartCreator.createDeathMap( player.getName(), appState );
                                        s.setKey( player.getName() + catName );
                                        getMapCanvas().addSeries( s, player.getId() );
                                    }
                                    break;
                                    case CAT_ABILITIES: {
                                        final String playerName = (String) ((DefaultMutableTreeNode) selectedNode.getParent()).getUserObject();
                                        final Player player = appState.getPlayerByName( playerName );
                                        final XYSeries s = ChartCreator.createAbilityMap( player.getHero(), ((Ability) selection).getKey() );
                                        getMapCanvas().addSeries( s, player.getId() );
                                    }
                                        break;
                                    default:
                                        LOGGER.warning( "Unknown category in tree" );
                                        break;
                                }
                                getOptionContainer().setEnabled( true );
                                getOptionContainer().getStepSlider().setMaximum( getMapCanvas().getItemCount() );
                            }
                            else {
                                getMapCanvas().removeSeries( ((Ability) selection).getKey() + catName );
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
