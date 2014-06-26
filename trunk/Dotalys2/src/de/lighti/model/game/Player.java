package de.lighti.model.game;

import java.util.TreeMap;

public class Player {
    private String name;
    private final int id;

    private Hero hero;

    private boolean isRadiant;

    private final TreeMap<Long, Integer> xp;
    private final TreeMap<Long, Integer> gold;

    public Player( int id ) {

        this.id = id;
        xp = new TreeMap<Long, Integer>();
        xp.put( 0l, 0 );
        gold = new TreeMap<Long, Integer>();
        gold.put( 0l, 0 );
    }

    public int getEarnedGold( long time ) {
        return gold.floorEntry( time ).getValue();
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
        return gold.lastEntry().getValue();
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

    public void setTotalEarnedGold( long time, int totalEarnedGold ) {
        gold.put( time, totalEarnedGold );
    }

    public void setTotalXP( long time, int value ) {
        xp.put( time, value );
    }

}
