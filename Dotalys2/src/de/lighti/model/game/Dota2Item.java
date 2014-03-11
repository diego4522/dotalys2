package de.lighti.model.game;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.lighti.io.ImageCache;

public class Dota2Item {
    private final String name;

    public final static Dota2Item UNKNOWN_ITEM = new Dota2Item( "<Unknown>", false );

    private static Map<String, BufferedImage> images = new HashMap<String, BufferedImage>();

    private final boolean fetchImage;

    public Dota2Item( String name ) {
        this( name, true );
    }

    public Dota2Item( String name, boolean fetchImage ) {
        super();
        if ((name == null) || name.isEmpty()) {
            throw new IllegalArgumentException( "name must not be null" );
        }

        this.name = name;
        this.fetchImage = fetchImage;

    }

    public BufferedImage getImage() throws IOException {
        if (fetchImage) {
            BufferedImage i = images.get( name );
            if ((i == null) && !images.containsKey( name )) {
                i = ImageCache.getItemImage( name );
                images.put( name, i );
            }
            return i;
        }
        return null;
    }

    public String getName() {
        return name;
    }

}
