/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Algorithms;

import Catalano.Imaging.FastBitmap;
import Structure.FloatImage;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Darshan
 */
@SuppressWarnings("FieldMayBeFinal")
public class FloatATrousWavelet {

    public static final float[][] kernel = {{1f/16, 2f/16, 1f/16}, {2f/16, 4f/16, 2f/16}, {1f/16, 2f/16, 1f/16}};

    
    private int width, height;
    private final FloatImage img; //immagine da processare
    private float[][] K;

    /**
     * Initialize a new instance of the ATrousWavelet class.
     *
     * @param fastBitmap Image.
     */
    public FloatATrousWavelet(FloatImage fastBitmap) {
        this.width = fastBitmap.X;
        this.height = fastBitmap.Y;
        img = new FloatImage(fastBitmap);
        K = kernel.clone();
    }

    /**
     * Applies forward Wavelet transformation to an image.
     *
     * @return a new level
     */
    public FloatImage Forward() {
        //Ok, it's not very smart to use a normal convolution with a tensorial and punched kernel. But it works.
        FloatImage t = new FloatImage(img);
        img.convolveInPlace(kernel, 1, true);       
        K = pierceKernel(K);
        t.subtractInPlace(img);
        return t;
    }

    public FloatImage getResidual() {
        return new FloatImage(img);
    }
    
    public FastBitmap getResidualasFastBitmap() {
        return img.toFastBitmap();
    }
    
    
    public static float[][] pierceKernel(float[][] kernel) {
        int n = kernel.length;
        int N = n * 2 - 1;
        float[][] k = new float[N][N];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                k[i * 2][j * 2] = kernel[i][j];
            }
        }
        return k;
    }

    public static List<FloatImage> applyTransform(FloatImage source, int levels) {
        FloatATrousWavelet transformer = new FloatATrousWavelet(source);
        LinkedList<FloatImage> out = new LinkedList<>();
        for (int i = 0; i < levels; i++) {
            System.out.println(i);
            out.add(transformer.Forward());
        }
        out.add(transformer.getResidual());
        return out;
    }
    
    public static FloatImage inverseTransform(List<FloatImage> levels) {
        FloatImage out=null;
        for (FloatImage l : levels) {
            if(out==null){
                out=new FloatImage(l);                
            }else{
                out.addInPlace(l);
            }
        }
        return out;
    }
}
