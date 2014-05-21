package de.lighti.model.game;

import java.util.Map;
import java.util.TreeMap;

public class Unit {
    private final TreeMap<Long, Integer> x;
    private final TreeMap<Long, Integer> y;
    private final String name;

    public Unit( String name ) {
        x = new TreeMap<Long, Integer>();
        x.put( 0l, 0 );
        y = new TreeMap<Long, Integer>();
        y.put( 0l, 0 );
        this.name = name;
    }

    public void addX( long tick, int x ) {
        this.x.put( tick, x );
        if (!y.containsKey( tick )) {
            y.put( tick, y.floorEntry( tick ).getValue() );
        }
    }

    public void addY( long tick, int y ) {
        this.y.put( tick, y );
        if (!x.containsKey( tick )) {
            x.put( tick, x.floorEntry( tick ).getValue() );
        }
    }

    public String getName() {
        return name;
    }

    public Map<Long, Integer> getX() {
        return x;
    }

    public Map<Long, Integer> getY() {
        return y;
    }
}
