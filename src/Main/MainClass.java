/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import Tools.MultipleImageViewer;
import Algorithms.ATrousWavelet;
import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.Filters.HistogramStretch;
import java.util.Arrays;
import java.util.List;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

public class MainClass {

    static OptionParser parser;
    static OptionSet options;
    static boolean debugMode = true;

    private static OptionParser inizializzaOptionParser() {
        OptionParser p = new OptionParser();
        p.accepts("image", "specifica la path dell'immagine in input").withRequiredArg();
        p.accepts("levels", "il numero di livelli da estrarre").withRequiredArg();
        return p;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        //Loads an image.
        FastBitmap fb = new FastBitmap("lena_std.png");
              
        List<FastBitmap> livelli=ATrousWavelet.applyTransform(fb, 5);
        
       
        HistogramStretch hs= new HistogramStretch();
        livelli.parallelStream().forEach( e -> { hs.applyInPlace(e); } );
        //Show the result.
        MultipleImageViewer.show(livelli, Arrays.asList("Livello 1","Livello 2","Livello 3","Livello 4","Livello 5", "Residuo"),3);
    }
}
