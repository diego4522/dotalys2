package de.lighti.model.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;

public class Hero extends Unit {
    public class ItemEvent {
        public Integer item;
        public int slot;
        public boolean added;
        public long tick;

        private ItemEvent( long tick, Integer item, int slot, boolean added ) {
            super();
            this.tick = tick;
            this.item = item;
            this.slot = slot;
            this.added = added;
        }

    }

    private static int countItem( Integer[] list, Integer n ) {
        int count = 0;
        for (final Integer m : list) {
            if (m != null && m.equals( n )) {
                count++;
            }
        }
        return count;
    }

    private final TreeMap<Long, Integer[]> items;
    private final List<Integer> abilities;

    private final Queue<ItemEvent> itemLog;

    private final TreeMap<Long, int[]> deaths;

    private final static int BAG_SIZE = 12; //two bags of 6. Not sure where courier itmes go

    public Hero( String name ) {
        super( name );

        items = new TreeMap<Long, Integer[]>();
        items.put( 0l, new Integer[BAG_SIZE] );
        itemLog = new LinkedBlockingQueue<ItemEvent>();
        abilities = new ArrayList<Integer>();
        deaths = new TreeMap<>();
    }

    public void addAbility( long tickMs, int slot, int value ) {
        while (abilities.size() < slot + 1) {
            abilities.add( null );
        }

        abilities.set( slot, value );
    }

    public void addDeath( long tickMs, int x, int y ) {
        deaths.put( tickMs, new int[] { x, y } );

    }

    private void generateLogEntries( long tick, Integer[] previous, Integer[] current ) {
        for (int i = 0; i < previous.length; i++) {
            if (previous[i] != current[i]) {
                if (current[i] != null) {

                    if (countItem( previous, current[i] ) == 0) {
                        itemLog.add( new ItemEvent( tick, current[i], i, true ) );
                    }
                }
                else if (previous[i] != null) {
                    itemLog.add( new ItemEvent( tick, previous[i], i, false ) );
                }
            }

        }
    }

    public List<Integer> getAbilities() {
        return abilities;
    }

    public TreeMap<Long, int[]> getDeaths() {
        return deaths;
    }

    public Queue<ItemEvent> getItemLog() {
        return itemLog;
    }

    public void setItem( long tickMs, int slot, Integer newItem ) {
        if (items.containsKey( tickMs )) {
            //Just store the update
            items.get( tickMs )[slot] = newItem;
        }
        else {
            //We advanced. Push the current bag configuration, calculate the diff to the previous one and make
            //a new array for the new tick
            final Entry<Long, Integer[]> current = items.floorEntry( tickMs );
            final Entry<Long, Integer[]> previous = items.floorEntry( current.getKey() - 1 );
            final Integer[] newBag = Arrays.copyOf( current.getValue(), current.getValue().length );
            newBag[slot] = newItem;
            items.put( tickMs, newBag );

            //previous might be null if we actually pulled the 0l entry into current
            if (previous != null) {
                generateLogEntries( current.getKey(), previous.getValue(), current.getValue() );
            }
        }
    }

}
