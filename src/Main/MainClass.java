/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import Tools.MultipleImageViewer;
import Algorithms.ATrousWavelet;
import Algorithms.HistogramStretchLinear;
import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.Filters.HistogramStretch;
import Catalano.Imaging.Tools.ImageHistogram;
import Catalano.Imaging.Tools.ImageStatistics;
import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
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
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {

        //Loads an image.
        FastBitmap fb = new FastBitmap("pp.png");

        List<FastBitmap> livelli = ATrousWavelet.applyTransform(fb, 8);
        final HistogramStretchLinear hs = new HistogramStretchLinear();
             
        int n = 0;
        StringBuilder sb = new StringBuilder("[");
        for (FastBitmap l : livelli) {
            String path = String.format("L%d.png", n++);
            float p = hs.applyInPlaceVerbose(l);
            sb.append(p).append(",");
            System.out.println(p);
            l.saveAsPNG(path);
        }
        char[] json = sb.toString().toCharArray();
        json[json.length - 1] = ']';

        
        /*ExecutorService executor= Executors.newWorkStealingPool();
        executor.awaitTermination(1, TimeUnit.DAYS);*/
        tryWriteFile("levels.json", json.toString());

        //Show the result.
        MultipleImageViewer.show(livelli, Arrays.asList("Livello 1", "Livello 2", "Livello 3", "Livello 4", "Livello 5", "Residuo"), 3);
    }

    public static boolean tryWriteFile(String filename, String data) {
        try (PrintWriter out = new PrintWriter(filename)) {
            out.println(data);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainClass.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }
}
