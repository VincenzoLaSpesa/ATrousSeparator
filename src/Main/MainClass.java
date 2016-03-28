/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import Tools.MultipleImageViewer;
import Algorithms.FloatATrousWavelet;
import Algorithms.HistogramStretchLinear;
import Catalano.Imaging.Concurrent.Filters.Grayscale;
import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.Filters.Subtract;
import Catalano.Imaging.Tools.ImageStatistics;
import Structure.FloatImage;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import sun.awt.image.WritableRasterNative;

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
        final FastBitmap fb = new FastBitmap("M.png");
        
        FloatImage fi= new FloatImage(fb);

        List<FloatImage> livelli = FloatATrousWavelet.applyTransform(fi, 6);
        
        final HistogramStretchLinear hs = new HistogramStretchLinear();
        
        FastBitmap recomposition=FloatATrousWavelet.inverseTransform(livelli).toFastBitmap();
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
        
               
        LinkedList<FastBitmap> livelliFast= new LinkedList<>();
        
        for (FloatImage l : livelli) {
            String path = String.format("L%d.png", n++);
            FastBitmap tmp=l.toFastBitmap();
            tmp.saveAsPNG(path);
            livelliFast.add(tmp);
        }
        livelli.clear();
        
        
        
        livelliFast.stream().parallel().forEach(e ->{
            hs.applyInPlace(e);
        });

              
        //Show the result.
        MultipleImageViewer.show(Arrays.asList(fb,recomposition, delta), Arrays.asList("In", "Out","Delta",info), 3);
        MultipleImageViewer.show(livelliFast, Arrays.asList("Livello 1", "Livello 2", "Livello 3", "Livello 4", "Livello 5", "Residuo"), 3);
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
