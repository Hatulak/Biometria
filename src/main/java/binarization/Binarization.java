package binarization;

import shared.ImageSharedOperations;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Binarization {

    public static final int RED = 1;
    public static final int GREEN = 2;
    public static final int BLUE = 3;
    public static final int AVERAGE = 4;

    public static BufferedImage manualBinarization(BufferedImage img, int threshold) {
        for (int w = 0; w < img.getWidth(); w++) {
            for (int h = 0; h < img.getHeight(); h++) {
                Color c = new Color(img.getRGB(w, h));
                img.setRGB(w, h,
                        c.getRed() >= threshold ? Color.WHITE.getRGB() : Color.BLACK.getRGB());
            }
        }
        return img;
    }

    public static Image changeImageToGrayScale(Image image, int channel) {
        BufferedImage bufferedImage = ImageSharedOperations.toBufferedImage(image);
        switch (channel) {
            case RED:
                for (int i = 0; i < bufferedImage.getWidth(); i++) {
                    for (int j = 0; j < bufferedImage.getHeight(); j++) {
                        Color color = new Color(bufferedImage.getRGB(i, j));
                        color = new Color(color.getRed(), color.getRed(), color.getRed());
                        bufferedImage.setRGB(i, j, color.getRGB());
                    }
                }
                break;
            case GREEN:
                for (int i = 0; i < bufferedImage.getWidth(); i++) {
                    for (int j = 0; j < bufferedImage.getHeight(); j++) {
                        Color color = new Color(bufferedImage.getRGB(i, j));
                        color = new Color(color.getGreen(), color.getGreen(), color.getGreen());
                        bufferedImage.setRGB(i, j, color.getRGB());
                    }
                }
                break;
            case BLUE:
                for (int i = 0; i < bufferedImage.getWidth(); i++) {
                    for (int j = 0; j < bufferedImage.getHeight(); j++) {
                        Color color = new Color(bufferedImage.getRGB(i, j));
                        color = new Color(color.getBlue(), color.getBlue(), color.getBlue());
                        bufferedImage.setRGB(i, j, color.getRGB());
                    }
                }
                break;
            case AVERAGE:
                for (int i = 0; i < bufferedImage.getWidth(); i++) {
                    for (int j = 0; j < bufferedImage.getHeight(); j++) {
                        Color color = new Color(bufferedImage.getRGB(i, j));
                        int avg = (color.getRed() + color.getBlue() + color.getGreen()) / 3;
                        color = new Color(avg, avg, avg);
                        bufferedImage.setRGB(i, j, color.getRGB());
                    }
                }
                break;
        }

        return bufferedImage;
    }

    public static BufferedImage cloneImage(BufferedImage image) {
        BufferedImage copyOfImage =
                new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = copyOfImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        return copyOfImage;
    }

    public static BufferedImage otsuBinarization(BufferedImage img) {
        int[] histogram = new int[256];
        double sumaPikseliWObrazie = 0;

        for (int i = 0; i < img.getWidth(); i++)
            for (int j = 0; j < img.getHeight(); j++) {
                Color color = new Color(img.getRGB(i, j));
                histogram[color.getRed()]++;
            }
        sumaPikseliWObrazie = img.getHeight() * img.getWidth();
        double[] eta = new double[256];

        for (int t = 1; t < eta.length; t++) {
            double P0 = 0;
            double P1 = 0;
            double mi0 = 0;
            double mi1 = 0;
            double sumaHistogramOdZeraDoT = 0;
            double sumaHistogramOdTdo255 = 0;
            double sumaHistogramOdZeraDoTRazyI = 0;
            double sumaHistogramOdTDo255RazyI = 0;

            for (int i = 0; i < t - 1; i++)
                sumaHistogramOdZeraDoT += histogram[i];
            for (int i = t; i < 256; i++)
                sumaHistogramOdTdo255 += histogram[i];

            P0 = sumaHistogramOdZeraDoT / sumaPikseliWObrazie;
            P1 = sumaHistogramOdTdo255 / sumaPikseliWObrazie;

            for (int i = 0; i < t - 1; i++)
                sumaHistogramOdZeraDoTRazyI += histogram[i] * i;
            for (int i = t; i < 256; i++)
                sumaHistogramOdTDo255RazyI += histogram[i] * i;

            mi0 = sumaHistogramOdZeraDoTRazyI / sumaHistogramOdZeraDoT;
            mi1 = sumaHistogramOdTDo255RazyI / sumaHistogramOdTdo255;

            eta[t] = P0 * P1 * Math.pow(mi0 - mi1, 2);
        }

        int eta_max = 0;
        for (int i = 0; i < eta.length; i++)
            if (eta[i] > eta[eta_max])
                eta_max = i;

        return manualBinarization(img, eta_max);
    }


    public static BufferedImage niblackBinarization(BufferedImage img, double parameter, int windowSize) {
        BufferedImage clonedImage = Binarization.cloneImage(img);
        for (int i = 0; i < clonedImage.getWidth(); i++) {
            for (int j = 0; j < clonedImage.getHeight(); j++) {

                double srednia = 0;
                double odchylenie = 0;
                double liczbaPixeli = 0;
                for (int k = i - windowSize / 2; k <= i + windowSize / 2; k++) {
                    if (k < 0 || k >= clonedImage.getWidth()) continue;
                    for (int l = j - windowSize / 2; l <= j + windowSize / 2; l++) {
                        if (l < 0 || l >= clonedImage.getHeight()) continue;
                        Color color = new Color(img.getRGB(k, l));
                        srednia += color.getRed();
                        liczbaPixeli++;
                    }
                }
                srednia = srednia / liczbaPixeli;

                for (int k = i - windowSize / 2; k < i + windowSize / 2; k++) {
                    if (k < 0 || k >= clonedImage.getWidth()) continue;
                    for (int l = j - windowSize / 2; l < j + windowSize / 2; l++) {
                        if (l < 0 || l >= clonedImage.getHeight()) continue;
                        Color color = new Color(img.getRGB(k, l));
                        odchylenie += Math.pow(color.getRed() - srednia, 2);
                    }
                }
                odchylenie = Math.sqrt(odchylenie / liczbaPixeli);
                int T = (int) (srednia + parameter * odchylenie);
                Color c = new Color(clonedImage.getRGB(i, j));
                clonedImage.setRGB(i, j,
                        c.getRed() >= T ? Color.WHITE.getRGB() : Color.BLACK.getRGB());
            }
        }
        return clonedImage;
    }

    public static BufferedImage bernsenBinarization(BufferedImage img, int contrast_threshold, int set_threshold, int windowSize) {
        BufferedImage clonedImage = Binarization.cloneImage(img);
        for (int i = 0; i < clonedImage.getWidth(); i++) {
            for (int j = 0; j < clonedImage.getHeight(); j++) {

                Color max = new Color(0, 0, 0);
                Color min = new Color(255, 255, 255);
                for (int k = i - windowSize / 2; k <= i + windowSize / 2; k++) {
                    if (k < 0 || k >= clonedImage.getWidth()) continue;
                    for (int l = j - windowSize / 2; l <= j + windowSize / 2; l++) {
                        if (l < 0 || l >= clonedImage.getHeight()) continue;
                        Color color = new Color(img.getRGB(k, l));
                        if (color.getRed() > max.getRed())
                            max = new Color(color.getRGB());
                        if (color.getRed() < min.getRed())
                            min = new Color(color.getRGB());
                    }
                }
                int mid_gray = (min.getRed() + max.getRed()) / 2;
                int local_contrast = max.getRed() - min.getRed();
                Color c = new Color(img.getRGB(i, j));
                if (local_contrast < contrast_threshold)
                    clonedImage.setRGB(i, j, mid_gray >= set_threshold ? Color.WHITE.getRGB() : Color.BLACK.getRGB());
                else clonedImage.setRGB(i, j, c.getRed() >= mid_gray ? Color.WHITE.getRGB() : Color.BLACK.getRGB());


            }
        }
        return clonedImage;
    }

}
