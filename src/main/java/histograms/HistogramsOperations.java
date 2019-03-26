package histograms;

import shared.ImageSharedOperations;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class HistogramsOperations {

    public static List<int[]> createHistograms(Image image) {
        List<int[]> histogramsList = new ArrayList<>();

        int[] histogramRed = new int[256];
        int[] histogramGreen = new int[256];
        int[] histogramBlue = new int[256];
        int[] histogramAvg = new int[256];

        BufferedImage imageToCreateHistograms = ImageSharedOperations.toBufferedImage(image);

        for (int i = 0; i < imageToCreateHistograms.getWidth(); i++) {
            for (int j = 0; j < imageToCreateHistograms.getHeight(); j++) {
                Color color = new Color(imageToCreateHistograms.getRGB(i, j));
                histogramRed[color.getRed()]++;
                histogramGreen[color.getGreen()]++;
                histogramBlue[color.getBlue()]++;
                histogramAvg[(color.getBlue() + color.getRed() + color.getGreen()) / 3]++;
            }
        }
        histogramsList.add(histogramRed);
        histogramsList.add(histogramGreen);
        histogramsList.add(histogramBlue);
        histogramsList.add(histogramAvg);

        return histogramsList;
    }

    public static Image enlightenImageByPower(Image image) {
        double C = 1.1;
        double P = 1.3;
        int[] LUT = new int[256];
        for (int i = 0; i < LUT.length; i++) {
            LUT[i] = (int) Math.min(255, C * (Math.pow(i, P)));
        }

        BufferedImage bufferedImage = ImageSharedOperations.toBufferedImage(image);
        for (int i = 0; i < bufferedImage.getWidth(); i++) {
            for (int j = 0; j < bufferedImage.getHeight(); j++) {
                Color color = new Color(bufferedImage.getRGB(i, j));
                Color colorAfterChange = new Color(LUT[color.getRed()], LUT[color.getGreen()], LUT[color.getBlue()]);
                bufferedImage.setRGB(i, j, colorAfterChange.getRGB());
            }
        }
        return bufferedImage;
    }

    public static Image enlightenImageByLogarithm(Image image) {
        double C = 50;
        int[] LUT = new int[256];
        for (int i = 0; i < LUT.length; i++) {
            LUT[i] = (int) Math.min(255, C * Math.log(i + 1.0));
        }

        BufferedImage bufferedImage = ImageSharedOperations.toBufferedImage(image);
        for (int i = 0; i < bufferedImage.getWidth(); i++) {
            for (int j = 0; j < bufferedImage.getHeight(); j++) {
                Color color = new Color(bufferedImage.getRGB(i, j));
                Color colorAfterChange = new Color(LUT[color.getRed()], LUT[color.getGreen()], LUT[color.getBlue()]);
                bufferedImage.setRGB(i, j, colorAfterChange.getRGB());
            }
        }
        return bufferedImage;
    }

    public static Image dimImageByPower(Image image) {
        double C = 0.9;
        double P = 0.9;
        int[] LUT = new int[256];
        for (int i = 0; i < LUT.length; i++) {
            LUT[i] = (int) Math.min(255, C * (Math.pow(i, P)));
        }

        BufferedImage bufferedImage = ImageSharedOperations.toBufferedImage(image);
        for (int i = 0; i < bufferedImage.getWidth(); i++) {
            for (int j = 0; j < bufferedImage.getHeight(); j++) {
                Color color = new Color(bufferedImage.getRGB(i, j));
                Color colorAfterChange = new Color(LUT[color.getRed()], LUT[color.getGreen()], LUT[color.getBlue()]);
                bufferedImage.setRGB(i, j, colorAfterChange.getRGB());
            }
        }
        return bufferedImage;
    }

    public static Image dimImageByLogarithm(Image image) {
        double C = 15;
        int[] LUT = new int[256];
        for (int i = 0; i < LUT.length; i++) {
            LUT[i] = (int) Math.min(255, C * Math.log(i + 1.0));
        }

        BufferedImage bufferedImage = ImageSharedOperations.toBufferedImage(image);
        for (int i = 0; i < bufferedImage.getWidth(); i++) {
            for (int j = 0; j < bufferedImage.getHeight(); j++) {
                Color color = new Color(bufferedImage.getRGB(i, j));
                Color colorAfterChange = new Color(LUT[color.getRed()], LUT[color.getGreen()], LUT[color.getBlue()]);
                bufferedImage.setRGB(i, j, colorAfterChange.getRGB());
            }
        }
        return bufferedImage;
    }

    public static Image strechHistograms(Image image, List<int[]> histograms) {
        int twofivefive = 255;
        int[] max = new int[]{255, 255, 255, 255};
        int[] min = new int[]{0, 0, 0, 0};

        for (int i = 0; i < histograms.size(); i++) {
            int[] h = histograms.get(i);
            //wyznaczanie max składowej
            for (int j = 255; j > 0; j--) {
                if (h[j] == 0) {
                    max[i] = j - 1;
                } else break;
            }
            //wyznaczanie min składowej
            for (int j = 0; j < 256; j++) {
                if (h[j] == 0) {
                    min[i] = j + 1;
                } else break;
            }
        }

        List<int[]> LUTList = new LinkedList<>();
        LUTList.add(new int[256]);
        LUTList.add(new int[256]);
        LUTList.add(new int[256]);
        LUTList.add(new int[256]);

        for (int i = 0; i < histograms.size(); i++) {
            int[] h = histograms.get(i);
            int[] LUT = LUTList.get(i);
            for (int j = 0; j < 256; j++) {
                LUT[j] = (int) ((j - min[i]) * ((float) twofivefive) / ((float) (max[i] - min[i])));
            }
        }

        BufferedImage bufferedImage = ImageSharedOperations.toBufferedImage(image);
        for (int i = 0; i < bufferedImage.getWidth(); i++) {
            for (int j = 0; j < bufferedImage.getHeight(); j++) {
                Color color = new Color(bufferedImage.getRGB(i, j));

                Color colorAfterChange = new Color(LUTList.get(0)[color.getRed()], LUTList.get(1)[color.getGreen()], LUTList.get(2)[color.getBlue()]);
                bufferedImage.setRGB(i, j, colorAfterChange.getRGB());
            }
        }
        return bufferedImage;
    }

    public static Image equalizeHistograms(Image image, List<int[]> histograms) {

        List<double[]> DList = new LinkedList<>();
        DList.add(new double[256]);
        DList.add(new double[256]);
        DList.add(new double[256]);
        DList.add(new double[256]);

        int[] pixelSum = new int[]{0, 0, 0, 0};
        for (int i = 0; i < 256; i++) {
            pixelSum[0] += histograms.get(0)[i];
            pixelSum[1] += histograms.get(1)[i];
            pixelSum[2] += histograms.get(2)[i];
            pixelSum[3] += histograms.get(3)[i];
        }

        for (int i = 0; i < histograms.size(); i++) {
            int[] h = histograms.get(i);
            double[] D = DList.get(i);
            D[0] = h[0];
            for (int j = 1; j < 256; j++) {
                D[j] = D[j - 1] + h[j];
            }
        }

        double[] d_zero = new double[]{0, 0, 0, 0};
        for (int i = 0; i < histograms.size(); i++) {
            double[] D = DList.get(i);
            for (int j = 0; j < 256; j++) {
                if (D[j] != 0) {
                    d_zero[i] = D[j];
                    break;
                }
            }
        }

        int k = 256;

        List<int[]> LUTList = new LinkedList<>();
        LUTList.add(new int[256]);
        LUTList.add(new int[256]);
        LUTList.add(new int[256]);
        LUTList.add(new int[256]);

        for (int i = 0; i < histograms.size(); i++) {
            int[] h = histograms.get(i);
            double[] D = DList.get(i);
            int[] LUT = LUTList.get(i);
            for (int j = 0; j < 256; j++) {
                LUT[j] = (int) (((D[j] / (double) pixelSum[i] - d_zero[i] / (double) pixelSum[i]) / (1.0 - d_zero[i] / (double) pixelSum[i])) * (k - 1));
            }
        }

        BufferedImage bufferedImage = ImageSharedOperations.toBufferedImage(image);
        for (int i = 0; i < bufferedImage.getWidth(); i++) {
            for (int j = 0; j < bufferedImage.getHeight(); j++) {
                Color color = new Color(bufferedImage.getRGB(i, j));
                Color colorAfterChange = new Color(LUTList.get(0)[color.getRed()], LUTList.get(1)[color.getGreen()], LUTList.get(2)[color.getBlue()]);
                bufferedImage.setRGB(i, j, colorAfterChange.getRGB());
            }
        }
        return bufferedImage;
    }

    public static double roundAvoid(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }

}
