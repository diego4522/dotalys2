package de.lighti.components.player.statistics;

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

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import de.lighti.model.AppState;
import de.lighti.model.Statics;
import de.lighti.model.game.Ability;
import de.lighti.model.game.Dota2Item;
import de.lighti.model.game.Hero;
import de.lighti.model.game.Player;

public class PlayerComponent extends JSplitPane {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final AppState appState;
    private JComboBox<String> playerBox;

    public PlayerComponent( AppState appState ) {
        super( JSplitPane.HORIZONTAL_SPLIT, null, null );

        this.appState = appState;

        setOneTouchExpandable( false );
        setDividerLocation( 150 );
        setDividerSize( 0 );
        setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
        setLeftComponent( createLeftComponent() );
        setRightComponent( createRightComponent() );
    }

    private Component createLeftComponent() {

        final JPanel leftPane = new JPanel();
        leftPane.setLayout( new BoxLayout( leftPane, BoxLayout.Y_AXIS ) );

        leftPane.add( getPlayerBox() );
        leftPane.add( Box.createVerticalGlue() );

        leftPane.setBorder( BorderFactory.createTitledBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ), "Player" ) );
        return leftPane;
    }

    private JComponent createPlayerBuildOrderTab() {
        final BuildOrderComponent bc = new BuildOrderComponent();
        getPlayerBox().addItemListener( new java.awt.event.ItemListener() {

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

    private JComponent createPlayerSkillTreeTab() {
        final SkillTreecomponent c = new SkillTreecomponent();
        getPlayerBox().addItemListener( new java.awt.event.ItemListener() {

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

    private JComponent createPlayerStatisticsTab() {

        final JPanel rightPane = new JPanel( new GridLayout( 7, 4 ) );

        final JLabel nameLabel = new JLabel( "Name:" );
        final JLabel nameValue = new JLabel();
        rightPane.add( nameLabel );
        rightPane.add( nameValue );
        final JLabel teamLabel = new JLabel( "Team:" );
        final JLabel teamValue = new JLabel();
        rightPane.add( teamLabel );
        rightPane.add( teamValue );
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

        getPlayerBox().addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                final String id = (String) getPlayerBox().getSelectedItem();
                final Player p = appState.getPlayerByName( id );
                if (p != null) {
                    final long ms = appState.getGameLength();

                    final double minutes = ms / 60000.0;
                    nameValue.setText( p.getName() );
//                final int heroId = appState.getSelectedHero( p.getId() );
//                final Hero hero = appState.getHero( heroId );
                    final String team = p.isRadiant() ? Statics.RADIANT : Statics.DIRE;
                    teamLabel.setText( team );
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

    private Component createRightComponent() {
        final JTabbedPane rightPane = new JTabbedPane();
        rightPane.addTab( "Statistics", createPlayerStatisticsTab() );
        rightPane.addTab( "Build Order", createPlayerBuildOrderTab() );
        rightPane.addTab( "Skill Tree", createPlayerSkillTreeTab() );
        rightPane.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
        return rightPane;
    }

    public JComboBox<String> getPlayerBox() {
        if (playerBox == null) {
            playerBox = new JComboBox<String>( appState.getPlayerComboModel() ) {

                /**
                 * 
                 */
                private static final long serialVersionUID = 1L;

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

            playerBox.setAlignmentX( Component.CENTER_ALIGNMENT );
        }
        return playerBox;
    }

}
