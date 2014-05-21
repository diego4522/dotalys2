package de.lighti.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ListModel;

import de.lighti.model.game.Ability;
import de.lighti.model.game.Dota2Item;
import de.lighti.model.game.Hero;
import de.lighti.model.game.Player;

public class AppState {
    public TreeMap<Long, Map<String, Object>> gameEventsPerMs = new TreeMap<Long, Map<String, Object>>();
    private final Map<String, Player> players = new HashMap<String, Player>();

    private final DefaultListModel<String> playerListModel;

    private final Set<String> playerVariables;
    private final DefaultComboBoxModel<String> attributeBoxModel;
    private final DefaultComboBoxModel<String> playerComboModel;
    private final Map<Integer, Hero> heroes;
    private final Map<Integer, Dota2Item> items;
    private final Map<Integer, Ability> abilities;

    public AppState() {
        playerListModel = new DefaultListModel<String>();
        attributeBoxModel = new DefaultComboBoxModel<String>();
        playerComboModel = new DefaultComboBoxModel<>();
        playerVariables = new HashSet<String>();
        heroes = new HashMap<Integer, Hero>();
        items = new HashMap<Integer, Dota2Item>();
        abilities = new HashMap<Integer, Ability>();
    }

    public void addAbility( int id, Ability ability ) {
        abilities.put( id, ability );

    }

    public void addItem( int id, String value ) {
        items.put( id, new Dota2Item( value ) );

    }

    public void addPlayer( String id, Player p ) {
        players.put( id, p );
        playerListModel.addElement( p.getName() );
        playerComboModel.addElement( p.getName() );
    }

    public void addPlayerVariable( String n ) {
        if (!playerVariables.contains( n )) {
            playerVariables.add( n );
            attributeBoxModel.addElement( n );
        }
    }

    public void clear() {
        playerListModel.clear();
        attributeBoxModel.removeAllElements();
        playerComboModel.removeAllElements();
        playerVariables.clear();
        heroes.clear();
        items.clear();
        abilities.clear();
        players.clear();
    }

    public Ability getAbility( int id ) {
        return abilities.get( id );
    }

    public ComboBoxModel<String> getAttributeListModel() {
        return attributeBoxModel;
    }

    public long getGameLength() {
        return gameEventsPerMs.lastKey();
    }

    public Hero getHero( int value ) {
        return heroes.get( value );
    }

    public Hero getHeroForPlayer( String playerName ) {
        final Player p = getPlayerByName( playerName );
        return p.getHero();
    }

    public Dota2Item getItem( int value ) {
        if (items.containsKey( value )) {
            return items.get( value );
        }
        else {
            return Dota2Item.UNKNOWN_ITEM;
        }
    }

    public Dota2Item getItemByName( String name ) {
        for (final Dota2Item d : items.values()) {
            if (d.getName().equals( name )) {
                return d;
            }
        }
        return Dota2Item.UNKNOWN_ITEM;
    }

    public Object getLastKnownValue( long tick, String name ) {
        Map.Entry<Long, Map<String, Object>> e = gameEventsPerMs.floorEntry( tick );
        while (e != null) {
            if (e.getValue().containsKey( name )) {
                return e.getValue().get( name );

            }
            else {
                e = gameEventsPerMs.lowerEntry( e.getKey() );
            }

        }
        return null;
    }

    public Player getPlayer( String id ) {
        return players.get( id );
    }

    public Player getPlayerByName( String player ) {
        for (final Player p : players.values()) {
            if (p.getName().equals( player )) {
                return p;
            }
        }
        return null;
    }

    public ComboBoxModel<String> getPlayerComboModel() {
        return playerComboModel;
    }

    public ListModel<String> getPlayerListModel() {
        return playerListModel;
    }

    public Map<String, Player> getPlayers() {
        return players;
    }

    public void setHero( int id, Hero hero ) {
        heroes.put( id, hero );
    }

}
