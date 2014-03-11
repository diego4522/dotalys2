package de.lighti.model.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;

public class Hero extends Unit {
    private static int countItem( List<String> list, String n ) {
        int count = 0;
        for (final String m : list) {
            if ((m != null) && m.equals( n )) {
                count++;
            }
        }
        return count;
    }

    private final TreeMap<Long, List<String>> items;
    private final List<Integer> abilities;

    private final Queue<String> itemLog;

    private final TreeMap<Long, int[]> deaths;

    public Hero( String name ) {
        super( name );

        items = new TreeMap<Long, List<String>>();
        items.put( 0l, new ArrayList<String>() );
        itemLog = new LinkedBlockingQueue<String>();
        abilities = new ArrayList<Integer>();
        deaths = new TreeMap<>();
    }

    public void addAbility( long tickMs, int slot, int value ) {
        while (abilities.size() < (slot + 1)) {
            abilities.add( null );
        }

        abilities.set( slot, value );
    }

    public void addDeath( long tickMs, int x, int y ) {
        deaths.put( tickMs, new int[] { x, y } );

    }

    public List<Integer> getAbilities() {
        return abilities;
    }

    public TreeMap<Long, int[]> getDeaths() {
        return deaths;
    }

    public Queue<String> getItemLog() {
        return itemLog;
    }

    public void setItem( long tickMs, int slot, String item ) {
        List<String> tickItems = items.get( tickMs );
        if (tickItems == null) {
            tickItems = new ArrayList<String>( items.floorEntry( tickMs ).getValue() );
            items.put( tickMs, tickItems );
        }
        while (tickItems.size() < (slot + 1)) {
            tickItems.add( null );
        }
        final String oldItem = tickItems.get( slot );
        if ((oldItem != null) && oldItem.equals( item )) {
            //We have received the same item we already have in that slot
            return;
        }
        else if ((item == null) && (oldItem == null)) {
            //We are setting null where already is null .. boring
            return;
        }
        else {
            tickItems.set( slot, item );
            final List<String> previous = items.lowerEntry( tickMs ).getValue();
            if (item != null) {
                //Now we have actually a new item in that slot. Let's find out if it has been moved from another slot               
                final int previousCount = countItem( previous, item );
                final int count = countItem( tickItems, item );
                if (previousCount < count) {
                    itemLog.add( "+" + item );
                }
            }

            if (oldItem != null) {
                //Check what happened to the oldItem
                final int previousCount = countItem( previous, oldItem );
                final int count = countItem( tickItems, oldItem );
                if (previousCount > count) {
                    itemLog.add( "-" + oldItem );
                }
            }
        }

    }
}
