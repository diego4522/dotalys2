package de.lighti.model.game;

import java.util.TreeMap;

public class Ability {
    private String name;

    private final TreeMap<Long, Integer> level;

    public Ability( String name ) {
        super();
        this.name = name;
        level = new TreeMap<Long, Integer>();
    }

    public TreeMap<Long, Integer> getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }

    public void setLevel( long tickMs, int level ) {
        if (this.level.isEmpty() || (this.level.floorEntry( tickMs ).getValue() < level)) {
            this.level.put( tickMs, level );
        }
    }

    public void setName( String name ) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Ability [name=" + name + "]";
    }

}
