package de.lighti.components.player;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import de.lighti.model.AppState;
import de.lighti.model.game.Ability;
import de.lighti.model.game.Dota2Item;
import de.lighti.model.game.Hero;
import de.lighti.model.game.Player;

public class PlayerComponent extends JSplitPane {
    private final AppState appState;

    public PlayerComponent( AppState appState ) {
        super();
        this.appState = appState;

        final JComboBox<String> playerBox = new JComboBox<String>( appState.getPlayerComboModel() );

        playerBox.setAlignmentX( Component.CENTER_ALIGNMENT );
        final JPanel leftPane = new JPanel();
        leftPane.setLayout( new BoxLayout( leftPane, BoxLayout.Y_AXIS ) );
        leftPane.add( playerBox );

        final JTabbedPane rightPane = new JTabbedPane();
        rightPane.addTab( "Statistics", createPlayerStatisticsTab( playerBox ) );
        rightPane.addTab( "Build Order", createPlayerBuildOrderTab( playerBox ) );
        rightPane.addTab( "Skill Tree", createPlayerSkillTreeTab( playerBox ) );

        setOrientation( JSplitPane.HORIZONTAL_SPLIT );

        setOneTouchExpandable( false );
        setDividerLocation( 150 );

        //Provide minimum sizes for the two components in the split pane
        final Dimension minimumSize = new Dimension( 100, 100 );
        leftPane.setMinimumSize( minimumSize );
        rightPane.setMinimumSize( minimumSize );
        setLeftComponent( leftPane );
        setRightComponent( rightPane );
    }

    private JComponent createPlayerBuildOrderTab( final JComboBox<String> playerBox ) {
        final BuildOrderComponent bc = new BuildOrderComponent();
        playerBox.addItemListener( new java.awt.event.ItemListener() {

            @Override
            public void itemStateChanged( ItemEvent e ) {
                final String id = (String) playerBox.getSelectedItem();
                if (id == null) {
                    return;
                }

                final Player p = appState.getPlayerByName( id );
                final Queue<Dota2Item> buildOrder = new LinkedBlockingQueue<Dota2Item>();
//                final int heroId = appState.getSelectedHero( p.getId() );
//                final Hero hero = appState.getHero( heroId );
                final Hero hero = p.getHero();
                if (hero != null) {
                    final Queue<String> completeLog = hero.getItemLog();
                    for (final String n : completeLog) {
                        if (n.startsWith( "+" )) {
                            buildOrder.add( appState.getItemByName( n.substring( 1 ) ) );
                        }
                    }
                    bc.setItems( buildOrder );
                }

            }
        } );
        return bc;
    }

    private JComponent createPlayerSkillTreeTab( final JComboBox<String> playerBox ) {
        final SkillTreecomponent c = new SkillTreecomponent();
        playerBox.addItemListener( new java.awt.event.ItemListener() {

            @Override
            public void itemStateChanged( ItemEvent e ) {
                final String id = (String) playerBox.getSelectedItem();
                if (id == null) {
                    return;
                }
                final Player p = appState.getPlayerByName( id );
                final Hero hero = p.getHero();

                if (hero != null) {
                    final List<Ability> abilities = new ArrayList<Ability>();
                    for (final Integer i : hero.getAbilities()) {
                        abilities.add( appState.getAbility( i ) );
                    }
                    c.setAbilities( abilities );
                    c.repaint();
                }
            }
        } );
        return c;
    }

    private JComponent createPlayerStatisticsTab( final JComboBox<String> playerBox ) {

        final JPanel rightPane = new JPanel( new GridLayout( 10, 4 ) );
        final JLabel nameLabel = new JLabel( "Name:" );
        final JLabel nameValue = new JLabel();
        rightPane.add( nameLabel );
        rightPane.add( nameValue );
        final JLabel heroNameLabel = new JLabel( "Hero:" );
        final JLabel heroNameValue = new JLabel();
        rightPane.add( heroNameLabel );
        rightPane.add( heroNameValue );
        final JLabel goldLabel = new JLabel( "Total gold:" );
        final JLabel goldValue = new JLabel();
        rightPane.add( goldLabel );
        rightPane.add( goldValue );
        final JLabel gpmLabel = new JLabel( "Gold per min:" );
        final JLabel gpmValue = new JLabel();
        rightPane.add( gpmLabel );
        rightPane.add( gpmValue );
        final JLabel xpLabel = new JLabel( "Total XP:" );
        final JLabel xpValue = new JLabel();
        rightPane.add( xpLabel );
        rightPane.add( xpValue );
        final JLabel xpmLabel = new JLabel( "XP per min:" );
        final JLabel xpmValue = new JLabel();
        rightPane.add( xpmLabel );
        rightPane.add( xpmValue );

        playerBox.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                final String id = (String) playerBox.getSelectedItem();
                final Player p = appState.getPlayerByName( id );
                if (p != null) {
                    final long ms = appState.getGameLength();

                    final double minutes = ms / 60000.0;
                    nameValue.setText( p.getName() );
//                final int heroId = appState.getSelectedHero( p.getId() );
//                final Hero hero = appState.getHero( heroId );
                    final Hero hero = p.getHero();
                    final String name = hero != null ? hero.getName() : "<unknown>";
                    heroNameValue.setText( name );
                    final int gold = p.getTotalEarnedGold();
                    goldValue.setText( "" + gold );
                    final double gpm = gold / minutes;
                    gpmValue.setText( "" + gpm );
                    final int toalXp = p.getTotalXP();
                    final double xpm = toalXp / minutes;
                    xpValue.setText( "" + toalXp );
                    xpmValue.setText( "" + xpm );
                }
            }
        } );
        return rightPane;
    }

}
