package de.lighti.io;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import de.lighti.DotaPlay;
import de.lighti.DotaPlay.ProgressListener;
import de.lighti.model.AppState;
import de.lighti.parsing.AbilityTracker;
import de.lighti.parsing.CreepHandler;
import de.lighti.parsing.HeroTracker;
import de.lighti.parsing.ItemListener;
import de.lighti.parsing.PlayersListener;

public final class DataImporter {
    public static final FileFilter FILE_FILTER = new FileFilter() {

        @Override
        public boolean accept( File f ) {
            return f.isDirectory() || f.getName().endsWith( ".dem" );
        }

        @Override
        public String getDescription() {
            return "Dota2 Replay files (*.dem)";
        }
    };

    public static void parseFile( AppState appState, File file, ProgressListener... listeners ) {
        DotaPlay.getListeners().clear();
        DotaPlay.addListener( new PlayersListener( appState ) );
        DotaPlay.addListener( new ItemListener( appState ) );
        DotaPlay.addListener( new AbilityTracker( appState ) );
        DotaPlay.addListener( new HeroTracker( appState ) );
        DotaPlay.addListener( new CreepHandler( appState ) );
        DotaPlay.loadFile( file.getAbsolutePath() );
    }

    private DataImporter() {

    }
}
