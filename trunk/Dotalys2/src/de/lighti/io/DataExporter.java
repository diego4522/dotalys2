package de.lighti.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DataExporter {
    public static void exportCSV( File file, String header, String[][] data ) throws IOException {
        final BufferedWriter fo = new BufferedWriter( new FileWriter( file ) );
        fo.write( header );
        fo.newLine();
        for (final String[] line : data) {
            for (int i = 0; i < line.length; i++) {
                fo.write( line[i] );
                if (i < line.length - 1) {
                    fo.write( ", " );
                }
            }
            fo.newLine();

        }
        fo.close();

    }
}
