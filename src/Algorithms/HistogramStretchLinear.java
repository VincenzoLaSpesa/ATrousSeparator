/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Algorithms;


import Catalano.Imaging.FastBitmap;
import Catalano.Imaging.Filters.HistogramStretch;

/**
 * Stretch histogram across the entire gray level range, which has the effect of increasing the contrast of a low contrast image.
 * <br /> If a stretch is desired over a smaller range,  different MAX and MIN values can be specified.
 * @see Computer Imaging - Scott E. Umbaugh - Chapter 8 - p. 353
 * @author Diego Catalano
 */
public class HistogramStretchLinear extends HistogramStretch{
    private int max, min;

    public HistogramStretchLinear(){
        this.max = 255;
        this.min = 0;
    }
    
    public float applyInPlaceVerbose(FastBitmap fastBitmap){
        int size = fastBitmap.getSize();
        
        if (fastBitmap.isGrayscale()) {
            float grayMax = getMaxGray(fastBitmap);
            float grayMin = getMinGray(fastBitmap);

            float gray; 
            float stretch;
            for (int x = 0; x < size; x++) {
                gray = fastBitmap.getGray(x);
                stretch = (((gray - grayMin)/(grayMax - grayMin)) * (max - min)) + min;
                fastBitmap.setGray(x, (int)stretch);
            }
            return grayMax;
        }
        else{
            float[] colorMax = getMaxRGB(fastBitmap);
            float[] colorMin = getMinRGB(fastBitmap);
            float globalMax=Math.max(colorMax[0], Math.max(colorMax[1], colorMax[2]));
            float globalMin=Math.min(colorMin[0], Math.min(colorMin[1], colorMin[2]));
            float delta=globalMax-globalMin;

            float r,g,b; 
            float stretchRed,stretchGreen,stretchBlue;
            for (int x = 0; x < size; x++) {
                r = fastBitmap.getRed(x);
                g = fastBitmap.getGreen(x);
                b = fastBitmap.getBlue(x);

                stretchRed = (((r - globalMin)/delta) * (max - min)) + min;
                stretchGreen = (((g - globalMin)/delta) * (max - min)) + min;
                stretchBlue = (((b - globalMin)/delta) * (max - min)) + min;

                fastBitmap.setRGB(x, (int)stretchRed, (int)stretchGreen, (int)stretchBlue);
                
            }
            return globalMax;
        }
    }
    
    @Override
    public void applyInPlace(FastBitmap fastBitmap){
        applyInPlaceVerbose(fastBitmap);       
    }
    
    private int getMaxGray(FastBitmap fb){
        
        int size = fb.getSize();
        
        int max = 0;
        for (int i = 0; i < size; i++)
            if (fb.getGray(i) > max) max = fb.getGray(i);
        
        return max;
    }
    
    private int getMinGray(FastBitmap fb){
        
        int size = fb.getSize();
        
        int min = 255;
        for (int i = 0; i < size; i++)
            if (fb.getGray(i) < min) min = fb.getGray(i);
        
        return min;
    }
    
    private float[] getMaxRGB(FastBitmap fb){
        float[] color = new float[3];
        int size = fb.getSize();
        
        int maxR = 0, maxG = 0, maxB = 0;
        for (int i = 0; i < size; i++){
            if (fb.getRed(i) > maxR) maxR = fb.getRed(i);
            if (fb.getGreen(i) > maxG) maxG = fb.getGreen(i);
            if (fb.getBlue(i) > maxB) maxB = fb.getBlue(i);
        }
        color[0] = (float)maxR;
        color[1] = (float)maxG;
        color[2] = (float)maxB;
        
        return color;
    }
    
    private float[] getMinRGB(FastBitmap fb){
        float[] color = new float[3];
        int size = fb.getSize();
        
        int minR = 255, minG = 255, minB = 255;
        for (int i = 0; i < size; i++){
            if (fb.getRed(i) < minR) minR = fb.getRed(i);
            if (fb.getGreen(i) < minG) minG = fb.getGreen(i);
            if (fb.getBlue(i) < minB) minB = fb.getBlue(i);
        }
        color[0] = (float)minR;
        color[1] = (float)minG;
        color[2] = (float)minB;
        
        return color;
    }
}