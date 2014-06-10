package de.lighti.model.game;

public class Player {
    private String name;
    private final int id;

    private int totalEarnedGold;

    private int totalXpEarned;

    private Hero hero;

    private boolean isRadiant;

    public Player( int id ) {

        this.id = id;
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
        return totalXpEarned;
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

    public void setTotalXP( int value ) {
        totalXpEarned = value;
    }

}
