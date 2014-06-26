package de.lighti.model.game;

import java.util.TreeMap;

public class Player {
    private String name;
    private final int id;

    private int totalEarnedGold;

    private Hero hero;

    private boolean isRadiant;

    private final TreeMap<Long, Integer> xp;

    public Player( int id ) {

        this.id = id;
        xp = new TreeMap<Long, Integer>();
        xp.put( 0l, 0 );
    }

    public Hero getHero() {
        return hero;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getTotalEarnedGold() {
        return totalEarnedGold;
    }

    public int getTotalXP() {
        return xp.lastEntry().getValue();
    }

    public int getXP( long time ) {
        return xp.floorEntry( time ).getValue();
    }

    public boolean isRadiant() {
        return isRadiant;
    }

    public void setHero( Hero hero ) {
        this.hero = hero;
    }

    public void setName( String value ) {
        name = value;
    }

    public void setRadiant( boolean isRadiant ) {
        this.isRadiant = isRadiant;
    }

    public void setTotalEarnedGold( int totalEarnedGold ) {
        this.totalEarnedGold = totalEarnedGold;
    }

    public void setTotalXP( long time, int value ) {
        xp.put( time, value );
    }

}
