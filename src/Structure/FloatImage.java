/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Structure;

import Catalano.Imaging.FastBitmap;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Darshan
 */
public class FloatImage {

    protected float[][][] RGB;
    protected float[][] GRAY;
    protected boolean isGray;
    public int Y, X;

    public int length() {
        if (isGray) {
            return GRAY.length;
        } else {
            return RGB.length;
        }
    }

    public FloatImage(FastBitmap fb) {
        Y = fb.getWidth();
        X = fb.getHeight();
        if (fb.isGrayscale()) {
            this.GRAY = new float[X][Y];
            fb.toArrayGray(GRAY);
            isGray = true;
        } else if (fb.isRGB()) {
            this.RGB = new float[X][Y][3];
            fb.toArrayRGB(RGB);
            isGray = false;
        }
    }

    private int CalcLines(float[][] kernel) {
        int lines = (kernel[0].length - 1) / 2;
        return lines;
    }

    /**
     * TODO: se le due immagini sono di tipo diverso non funzionerà
     *
     * @param fi
     */
    public void addInPlace(FloatImage fi) {
        if (isGray) {
            for (int x = 0; x < X; x++) {
                for (int y = 0; y < Y; y++) {
                    this.GRAY[x][y] += fi.GRAY[x][y];
                }
            }
        } else {
            for (int x = 0; x < X; x++) {
                for (int y = 0; y < Y; y++) {
                    for (int i = 0; i < 3; i++) {
                        this.RGB[x][y][i] += fi.RGB[x][y][i];
                    }
                }
            }

        }
    }

    /**
     * TODO: se le due immagini sono di tipo diverso non funzionerà
     *
     * @param k
     */
    public void shift(int k) {
        if (isGray) {
            for (int x = 0; x < X; x++) {
                for (int y = 0; y < Y; y++) {
                    this.GRAY[x][y] += k;
                }
            }
        } else {
            for (int x = 0; x < X; x++) {
                for (int y = 0; y < Y; y++) {
                    for (int i = 0; i < 3; i++) {
                        this.RGB[x][y][i] += k;
                    }
                }
            }

        }
    }

    public List<FastBitmap> toPositiveFastBitmap() {
        if (isGray) {
            int bufferGrayP[][] = new int[X][Y];
            int bufferGrayN[][] = new int[X][Y];
            for (int x = 0; x < X; x++) {
                for (int y = 0; y < Y; y++) {
                    if (GRAY[x][y] > 0) {
                        bufferGrayP[x][y] = Math.round(GRAY[x][y]);
                    } else {
                        bufferGrayN[x][y] = -Math.round(GRAY[x][y]);
                    }

                }
            }
            return Arrays.asList(new FastBitmap(bufferGrayN), new FastBitmap(bufferGrayP));
        } else {
            int bufferRGB_P[][][] = new int[X][Y][3];
            int bufferRGB_N[][][] = new int[X][Y][3];
            for (int x = 0; x < X; x++) {
                for (int y = 0; y < Y; y++) {
                    for (int i = 0; y < 3; i++) {
                        if (RGB[x][y][i] > 0) {
                            bufferRGB_P[x][y][i] = Math.round(RGB[x][y][i]);
                        } else {
                            bufferRGB_N[x][y][i] = -Math.round(RGB[x][y][i]);
                        }
                    }
                }
            }

            return Arrays.asList(new FastBitmap(bufferRGB_N), new FastBitmap(bufferRGB_P));
        }

    }

    public FastBitmap toFastBitmap() {
        int p;
        if (isGray) {
            int bufferGray[][] = new int[X][Y];
            for (int x = 0; x < X; x++) {
                for (int y = 0; y < Y; y++) {
                    bufferGray[x][y] = Math.round(GRAY[x][y]);
                }
            }
            return new FastBitmap(bufferGray);
        } else {
            int bufferRGB[][][] = new int[X][Y][3];
            for (int x = 0; x < X; x++) {
                for (int y = 0; y < Y; y++) {
                    for (int i = 0; i < 3; i++) {
                        p = (Math.round(RGB[x][y][i]));
                        if(p>0)
                            bufferRGB[x][y][i] = p;
                    }
                }
            }
            return new FastBitmap(bufferRGB);
        }
    }

    /**
     * TODO: se le due immagini sono di tipo diverso non funzionerà
     *
     * @param fi
     */
    public void subtractInPlace(FloatImage fi) {
        if (isGray) {
            for (int x = 0; x < X; x++) {
                for (int y = 0; y < Y; y++) {
                    this.GRAY[x][y] -= fi.GRAY[x][y];
                }
            }
        } else {
            for (int x = 0; x < X; x++) {
                for (int y = 0; y < Y; y++) {
                    for (int i = 0; i < 3; i++) {
                        this.RGB[x][y][i] -= fi.RGB[x][y][i];
                    }
                }
            }

        }
    }

    public void convolveInPlace(float[][] kernel, float divisione, boolean replicate) {
        float div;

        int Xline, Yline, x, y;
        int lines = CalcLines(kernel);

        if (divisione != 1) {
            for (x = 0; x < kernel.length; x++) {
                for (y = 0; y < kernel[0].length; y++) {
                    kernel[x][y] /= divisione;
                }
            }
        }

        FloatImage copy = new FloatImage(this);

        if (isGray) {
            float gray;
            for (x = 0; x < X; x++) {
                for (y = 0; y < Y; y++) {
                    gray = div = 0;
                    for (int i = 0; i < kernel.length; i++) {
                        Xline = x + (i - lines);
                        for (int j = 0; j < kernel[0].length; j++) {
                            Yline = y + (j - lines);
                            if ((Xline >= 0) && (Xline < X) && (Yline >= 0) && (Yline < Y)) {
                                gray += kernel[i][j] * copy.GRAY[Xline][Yline];
                                div += kernel[i][j];
                            } else if (replicate) {

                                int r = x + i - lines;
                                int c = y + j - lines;

                                if (r < 0) {
                                    r = 0;
                                }
                                if (r >= X) {
                                    r = X - 1;
                                }

                                if (c < 0) {
                                    c = 0;
                                }
                                if (c >= Y) {
                                    c = Y - 1;
                                }

                                gray += kernel[i][j] * copy.GRAY[r][c];
                                div += kernel[i][j];
                            }
                        }
                    }

                    if (div != 0) {
                        gray /= div;
                    }
                    this.GRAY[x][y] = gray;
                }
            }
        } else {
            float r, g, b;
            for (x = 0; x < X; x++) {
                for (y = 0; y < Y; y++) {
                    r = g = b = div = 0;
                    for (int i = 0; i < kernel.length; i++) {
                        Xline = x + (i - lines);
                        for (int j = 0; j < kernel[0].length; j++) {
                            Yline = y + (j - lines);
                            if ((Xline >= 0) && (Xline < X) && (Yline >= 0) && (Yline < Y)) {
                                r += kernel[i][j] * copy.RGB[Xline][Yline][0];
                                g += kernel[i][j] * copy.RGB[Xline][Yline][1];
                                b += kernel[i][j] * copy.RGB[Xline][Yline][2];
                                div += kernel[i][j];
                            } else if (replicate) {

                                int rr = x + i - lines;
                                int cc = y + j - lines;

                                if (rr < 0) {
                                    rr = 0;
                                }
                                if (rr >= X) {
                                    rr = X - 1;
                                }

                                if (cc < 0) {
                                    cc = 0;
                                }
                                if (cc >= Y) {
                                    cc = Y - 1;
                                }

                                r += kernel[i][j] * copy.RGB[rr][cc][0];
                                g += kernel[i][j] * copy.RGB[rr][cc][0];
                                b += kernel[i][j] * copy.RGB[rr][cc][0];
                                div += kernel[i][j];
                            }
                        }
                    }

                    if (div != 0) {
                        r /= div;
                        g /= div;
                        b /= div;
                    }

                    this.RGB[x][y][0] = r;
                    this.RGB[x][y][1] = g;
                    this.RGB[x][y][2] = b;
                }
            }
        }

    }

    public FloatImage(FloatImage fb) {
        Y = fb.X;
        X = fb.Y;

        if (isGray) {
            //this.GRAY = fb.GRAY.clone();
            this.GRAY = new float[fb.GRAY.length][fb.GRAY[0].length];
            for (int i = 0; i < fb.GRAY.length; i++) {
                this.GRAY[i] = Arrays.copyOf(fb.GRAY[i], fb.GRAY[i].length);
            }
        } else {
            int A=fb.RGB.length,B=fb.RGB[0].length,a,b,c;
            this.RGB = new float[A][B][3];
            for (int i = 0; i < fb.RGB.length; i++) 
                for(a=0;a<A;a++)
                    for(b=0;b<B;b++)
                        for(c=0;c<3;c++)
                            RGB[a][b][c]=fb.RGB[a][b][c];
            
        }
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

}
