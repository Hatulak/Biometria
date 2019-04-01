package filtration;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.Arrays;

public class Filtration {

    public static BufferedImage median3x3Filter(BufferedImage img) {
        BufferedImage copy = deepCopy(img);
        for (int w = 1; w < img.getWidth() - 1; w++) {
            for (int h = 1; h < img.getHeight() - 1; h++) {
                int[] tableRed = new int[9];
                int[] tableGreen = new int[9];
                int[] tableBlue = new int[9];
                int index = 0;
                for (int i = -1; i < 2; i++) {
                    for (int j = -1; j < 2; j++) {
                        Color c = new Color(img.getRGB(w + i, h + j));
                        tableRed[index] = c.getRed();
                        tableGreen[index] = c.getGreen();
                        tableBlue[index] = c.getBlue();
                        index++;
                    }
                }
                Arrays.sort(tableRed);
                Arrays.sort(tableGreen);
                Arrays.sort(tableBlue);
                Color color = new Color(tableRed[4], tableGreen[4], tableBlue[4]);
                copy.setRGB(w, h, color.getRGB());
            }
        }
        return copy;
    }

    public static BufferedImage median5x5Filter(BufferedImage img) {
        BufferedImage copy = deepCopy(img);
        for (int w = 2; w < img.getWidth() - 2; w++) {
            for (int h = 2; h < img.getHeight() - 2; h++) {
                int[] tableRed = new int[25];
                int[] tableGreen = new int[25];
                int[] tableBlue = new int[25];
                int index = 0;
                for (int i = -2; i < 3; i++) {
                    for (int j = -2; j < 3; j++) {
                        Color c = new Color(img.getRGB(w + i, h + j));
                        tableRed[index] = c.getRed();
                        tableGreen[index] = c.getGreen();
                        tableBlue[index] = c.getBlue();
                        index++;
                    }
                }
                Arrays.sort(tableRed);
                Arrays.sort(tableGreen);
                Arrays.sort(tableBlue);
                Color color = new Color(tableRed[12], tableGreen[12], tableGreen[12]);
                copy.setRGB(w, h, color.getRGB());
            }
        }
        return copy;
    }

    public static BufferedImage kuwaharaFilter(BufferedImage img) {
        BufferedImage copy = deepCopy(img);
        for (int w = 2; w < img.getWidth() - 2; w++) {
            for (int h = 2; h < img.getHeight() - 2; h++) {

                //1 region - lewa góro
                int regionLG1RedAvg = 0;
                int regionLG1GreenAvg = 0;
                int regionLG1BlueAvg = 0;
                for (int i = -2; i <= 0; i++) {
                    for (int j = -2; j <= 0; j++) {
                        Color c = new Color(img.getRGB(w + i, h + j));
                        regionLG1RedAvg += c.getRed();
                        regionLG1GreenAvg += c.getGreen();
                        regionLG1BlueAvg += c.getBlue();
                    }
                }
                regionLG1RedAvg = regionLG1RedAvg / 9;
                regionLG1GreenAvg = regionLG1GreenAvg / 9;
                regionLG1BlueAvg = regionLG1BlueAvg / 9;
                int regionLG1RedWar = 0;
                int regionLG1GreenWar = 0;
                int regionLG1BlueWar = 0;
                for (int i = -2; i <= 0; i++) {
                    for (int j = -2; j <= 0; j++) {
                        Color c = new Color(img.getRGB(w + i, h + j));
                        regionLG1RedWar += Math.pow(regionLG1RedAvg - c.getRed(), 2);
                        regionLG1GreenWar += Math.pow(regionLG1GreenAvg - c.getGreen(), 2);
                        regionLG1BlueWar += Math.pow(regionLG1BlueAvg - c.getBlue(), 2);
                    }
                }
                regionLG1RedWar = regionLG1RedWar / 9;
                regionLG1GreenWar = regionLG1GreenWar / 9;
                regionLG1BlueWar = regionLG1BlueWar / 9;

                //2 region - prawa góro
                int regionLG2RedAvg = 0;
                int regionLG2GreenAvg = 0;
                int regionLG2BlueAvg = 0;
                for (int i = 0; i <= 2; i++) {
                    for (int j = -2; j <= 0; j++) {
                        Color c = new Color(img.getRGB(w + i, h + j));
                        regionLG2RedAvg += c.getRed();
                        regionLG2GreenAvg += c.getGreen();
                        regionLG2BlueAvg += c.getBlue();
                    }
                }
                regionLG2RedAvg = regionLG2RedAvg / 9;
                regionLG2GreenAvg = regionLG2GreenAvg / 9;
                regionLG2BlueAvg = regionLG2BlueAvg / 9;
                int regionLG2RedWar = 0;
                int regionLG2GreenWar = 0;
                int regionLG2BlueWar = 0;
                for (int i = 0; i <= 2; i++) {
                    for (int j = -2; j <= 0; j++) {
                        Color c = new Color(img.getRGB(w + i, h + j));
                        regionLG2RedWar += Math.pow(regionLG2RedAvg - c.getRed(), 2);
                        regionLG2GreenWar += Math.pow(regionLG2GreenAvg - c.getGreen(), 2);
                        regionLG2BlueWar += Math.pow(regionLG2BlueAvg - c.getBlue(), 2);
                    }
                }
                regionLG2RedWar = regionLG2RedWar / 9;
                regionLG2GreenWar = regionLG2GreenWar / 9;
                regionLG2BlueWar = regionLG2BlueWar / 9;

                //3 region - lewa dol
                int regionLG3RedAvg = 0;
                int regionLG3GreenAvg = 0;
                int regionLG3BlueAvg = 0;
                for (int i = -2; i <= 0; i++) {
                    for (int j = 0; j <= 2; j++) {
                        Color c = new Color(img.getRGB(w + i, h + j));
                        regionLG3RedAvg += c.getRed();
                        regionLG3GreenAvg += c.getGreen();
                        regionLG3BlueAvg += c.getBlue();
                    }
                }
                regionLG3RedAvg = regionLG3RedAvg / 9;
                regionLG3GreenAvg = regionLG3GreenAvg / 9;
                regionLG3BlueAvg = regionLG3BlueAvg / 9;
                int regionLG3RedWar = 0;
                int regionLG3GreenWar = 0;
                int regionLG3BlueWar = 0;
                for (int i = -2; i <= 0; i++) {
                    for (int j = 0; j <= 2; j++) {
                        Color c = new Color(img.getRGB(w + i, h + j));
                        regionLG3RedWar += Math.pow(regionLG3RedAvg - c.getRed(), 2);
                        regionLG3GreenWar += Math.pow(regionLG3GreenAvg - c.getGreen(), 2);
                        regionLG3BlueWar += Math.pow(regionLG3BlueAvg - c.getBlue(), 2);
                    }
                }
                regionLG3RedWar = regionLG3RedWar / 9;
                regionLG3GreenWar = regionLG3GreenWar / 9;
                regionLG3BlueWar = regionLG3BlueWar / 9;


                //4 region - prawa dol
                int regionLG4RedAvg = 0;
                int regionLG4GreenAvg = 0;
                int regionLG4BlueAvg = 0;
                for (int i = 0; i <= 2; i++) {
                    for (int j = 0; j <= 2; j++) {
                        Color c = new Color(img.getRGB(w + i, h + j));
                        regionLG4RedAvg += c.getRed();
                        regionLG4GreenAvg += c.getGreen();
                        regionLG4BlueAvg += c.getBlue();
                    }
                }
                regionLG4RedAvg = regionLG4RedAvg / 9;
                regionLG4GreenAvg = regionLG4GreenAvg / 9;
                regionLG4BlueAvg = regionLG4BlueAvg / 9;
                int regionLG4RedWar = 0;
                int regionLG4GreenWar = 0;
                int regionLG4BlueWar = 0;
                for (int i = 0; i <= 2; i++) {
                    for (int j = 0; j <= 2; j++) {
                        Color c = new Color(img.getRGB(w + i, h + j));
                        regionLG4RedWar += Math.pow(regionLG4RedAvg - c.getRed(), 2);
                        regionLG4GreenWar += Math.pow(regionLG4GreenAvg - c.getGreen(), 2);
                        regionLG4BlueWar += Math.pow(regionLG4BlueAvg - c.getBlue(), 2);
                    }
                }
                regionLG4RedWar = regionLG4RedWar / 9;
                regionLG4GreenWar = regionLG4GreenWar / 9;
                regionLG4BlueWar = regionLG4BlueWar / 9;

                int[] redRegions = new int[]{regionLG1RedWar, regionLG2RedWar, regionLG3RedWar, regionLG4RedWar};
                int[] greenRegions = new int[]{regionLG1GreenWar, regionLG2GreenWar, regionLG3GreenWar, regionLG4GreenWar};
                int[] blueRegions = new int[]{regionLG1BlueWar, regionLG2BlueWar, regionLG3BlueWar, regionLG4BlueWar};

                int[] redAvgs = new int[]{regionLG1RedAvg, regionLG2RedAvg, regionLG3RedAvg, regionLG4RedAvg};
                int[] greenAvgs = new int[]{regionLG1GreenAvg, regionLG2GreenAvg, regionLG3GreenAvg, regionLG4GreenAvg};
                int[] blueAvgs = new int[]{regionLG1BlueAvg, regionLG2BlueAvg, regionLG3BlueAvg, regionLG4BlueAvg};

                int redMin = regionLG1RedWar, greenMin = regionLG1GreenWar, blueMin = regionLG1BlueWar;
                int redAvg = regionLG1RedAvg, greenAvg = regionLG1GreenAvg, blueAvg = regionLG1BlueAvg;
                for (int i = 0; i < redRegions.length; i++) {
                    if (redRegions[i] < redMin) {
                        redMin = redRegions[i];
                        redAvg = redAvgs[i];
                    }
                    if (greenRegions[i] < greenMin) {
                        greenMin = greenRegions[i];
                        greenAvg = greenAvgs[i];
                    }
                    if (blueRegions[i] < blueMin) {
                        blueMin = blueRegions[i];
                        blueAvg = blueAvgs[i];
                    }
                }

                if (redAvg > 255) redAvg = 255;
                if (greenAvg > 255) greenAvg = 255;
                if (blueAvg > 255) blueAvg = 255;
                if (redAvg < 0) redAvg = 0;
                if (greenAvg < 0) greenAvg = 0;
                if (blueAvg < 0) blueAvg = 0;

                Color c = new Color(redAvg, greenAvg, blueAvg);
                copy.setRGB(w, h, c.getRGB());

            }
        }

        return copy;
    }


    public static BufferedImage filterImageCustomMask(BufferedImage img, int[][] mask) {

        BufferedImage copy = deepCopy(img);
        for (int w = 1; w < img.getWidth() - 1; w++) {
            for (int h = 1; h < img.getHeight() - 1; h++) {
                copy.setRGB(w, h, calculateNewPixelValue(img, w, h, mask));
            }
        }
        return copy;
    }

    public static int calculateNewPixelValue(BufferedImage img, int w, int h, int[][] mask) {
        double sumRed = 0, sumGreen = 0, sumBlue = 0;
        double sumaWag = 0;
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                Color c = new Color(img.getRGB(w + i, h + j));
                sumRed += c.getRed() * mask[i + 1][j + 1];
                sumBlue += c.getBlue() * mask[i + 1][j + 1];
                sumGreen += c.getGreen() * mask[i + 1][j + 1];
                sumaWag += mask[i + 1][j + 1];
            }
        }

        if (sumaWag != 0) {
            sumRed = sumRed / sumaWag;
            sumGreen = sumGreen / sumaWag;
            sumBlue = sumBlue / sumaWag;
        }

        if (sumRed > 255) sumRed = 255;
        if (sumGreen > 255) sumGreen = 255;
        if (sumBlue > 255) sumBlue = 255;
        if (sumRed < 0) sumRed = 0;
        if (sumGreen < 0) sumGreen = 0;
        if (sumBlue < 0) sumBlue = 0;

        Color c = new Color((int) sumRed, (int) sumGreen, (int) sumBlue);
        return c.getRGB();
    }


    public static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(bi.getRaster().createCompatibleWritableRaster());
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

}
