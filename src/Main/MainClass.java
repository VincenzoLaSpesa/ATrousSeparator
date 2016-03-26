/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import Tools.MultipleImageViewer;
import Algorithms.ATrousWavelet;
import Algorithms.HistogramStretchLinear;
import Catalano.Imaging.Concurrent.Filters.Grayscale;
import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.Filters.HistogramStretch;
import Catalano.Imaging.Filters.Subtract;
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
        FastBitmap fb = new FastBitmap("M.png");

        List<FastBitmap> livelli = ATrousWavelet.applyTransform(fb, 6);
        
        HistogramStretchLinear hs = new HistogramStretchLinear();
        
        FastBitmap recomposition=ATrousWavelet.inverseTransform(livelli);
        recomposition.saveAsPNG("ricomponi.png");
        
        //calcolo il delta
        FastBitmap delta=new FastBitmap(recomposition);
        Subtract sub= new Subtract(fb);
        sub.applyInPlace(delta);
        FastBitmap buffer=new FastBitmap(delta);
        Grayscale gs= new Grayscale(Grayscale.Algorithm.Average);
        gs.applyInPlace(delta);
        String info= String.format("Max: %s\n Min: %s\n Var: %s", ImageStatistics.Maximum(delta),ImageStatistics.Minimum(delta),ImageStatistics.Variance(delta));
        delta=buffer;
        //
             
        int n = 0;
        StringBuilder sb = new StringBuilder("[");
        for (FastBitmap l : livelli) {
            String path = String.format("L%d.png", n++);
            javafx.geometry.Point2D p = hs.applyInPlaceVerbose(l);
            //float p=1;
            sb.append(String.format("[%f , %f ],", p.getX(), p.getY()));
            System.out.println(p);
            l.saveAsPNG(path);
        }
        char[] json = sb.toString().toCharArray();
        json[json.length - 1] = ']';

        
        /*ExecutorService executor= Executors.newWorkStealingPool();
        executor.awaitTermination(1, TimeUnit.DAYS);*/
        tryWriteFile("levels.json", new String(json));
       

        //Show the result.
        MultipleImageViewer.show(Arrays.asList(fb,recomposition, delta), Arrays.asList("In", "Out","Delta",info), 3);
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
