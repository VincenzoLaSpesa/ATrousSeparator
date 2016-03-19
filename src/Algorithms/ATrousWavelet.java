/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Algorithms;

import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.Filters.Convolution;
import Catalano.Imaging.Filters.Subtract;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Darshan
 */
@SuppressWarnings("FieldMayBeFinal")
public class ATrousWavelet {

    public static final int[][] kernel = {{1, 2, 1}, {2, 4, 2}, {1, 2, 1}};

    
    private int width, height;
    private FastBitmap img; //immagine da processare
    private int[][] K;

    /**
     * Initialize a new instance of the ATrousWavelet class.
     *
     * @param fastBitmap Image.
     */
    public ATrousWavelet(FastBitmap fastBitmap) {
        this.width = fastBitmap.getWidth();
        this.height = fastBitmap.getHeight();
        img = new FastBitmap(fastBitmap);
        K = kernel.clone();
    }

    /**
     * Applies forward Wavelet transformation to an image.
     *
     * @return a new level
     */
    public FastBitmap Forward() {
        //Ok, it's not very smart to use a normal convolution with a tensorial and punched kernel. But it works.
        FastBitmap t = new FastBitmap(img);
        Convolution c = new Convolution(K, 16, true);
        c.applyInPlace(img); // img=img © K    
        K = pierceKernel(K);
        Subtract s = new Subtract(img);
        s.applyInPlace(t);
        return t; // img - img © K
    }

    public FastBitmap getResidual() {
        return new FastBitmap(img);
    }

    public static int[][] pierceKernel(int[][] kernel) {
        int n = kernel.length;
        int N = n * 2 - 1;
        int[][] k = new int[N][N];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                k[i * 2][j * 2] = kernel[i][j];
            }
        }
        return k;
    }

    public static List<FastBitmap> applyTransform(FastBitmap source, int levels) {
        ATrousWavelet transformer = new ATrousWavelet(source);
        LinkedList<FastBitmap> out = new LinkedList<>();
        for (int i = 0; i < levels; i++) {
            out.add(transformer.Forward());
        }
        out.add(transformer.getResidual());
        return out;
    }

}