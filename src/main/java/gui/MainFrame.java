/*
 * Created by JFormDesigner on Mon Mar 11 16:32:38 CET 2019
 */

package gui;

import binarization.Binarization;
import filtration.Filtration;
import histograms.HistogramsOperations;
import shared.ImageSharedOperations;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * @author asd
 */
public class MainFrame extends JFrame {

    BufferedImage originalImage;
    Image resizedImage;
    Image clonedImage;

    List<int[]> histogramsList = new LinkedList<>();

    private boolean isMouseMotionListenerWorking = false;
    private MouseMotionListener mouseMotionListener = new MouseMotionListener() {
        @Override
        public void mouseDragged(MouseEvent e) {

        }

        @Override
        public void mouseMoved(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            if (resizedImage == null) return;
            BufferedImage tmpImage = ImageSharedOperations.toBufferedImage(resizedImage);
            if (x >= tmpImage.getWidth() || y >= tmpImage.getHeight()) {
                mouseXPositionLabel.setText("");
                mouseYPositionLabel.setText("");
                colorRTextField.setText("");
                colorGTextField.setText("");
                colorBTextField.setText("");
                colorRTextField.setEnabled(false);
                colorGTextField.setEnabled(false);
                colorBTextField.setEnabled(false);
                return;
            }
            mouseXPositionLabel.setText("" + x);
            mouseYPositionLabel.setText("" + y);
            int rgb = tmpImage.getRGB(x, y);
            int red = (rgb >> 16) & 0xFF;
            int green = (rgb >> 8) & 0xFF;
            int blue = rgb & 0xFF;
            colorRTextField.setEnabled(true);
            colorGTextField.setEnabled(true);
            colorBTextField.setEnabled(true);
            colorRTextField.setText("" + red);
            colorGTextField.setText("" + green);
            colorBTextField.setText("" + blue);

        }
    };

    private double zoomLevel = 1.0;
    private int readedImageWidth;
    private int readedImageHeight;


    private MouseWheelListener mouseWheelListener = new MouseAdapter() {
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            super.mouseWheelMoved(e);
            zoomLevel += 0.05 * e.getPreciseWheelRotation();
            int newImageWidth = (int) (readedImageWidth * zoomLevel);
            int newImageHeight = (int) (readedImageHeight * zoomLevel);
            resizedImage = originalImage.getScaledInstance(newImageWidth, newImageHeight, Image.SCALE_SMOOTH);
            imageLabel.setSize(resizedImage.getWidth(null), resizedImage.getHeight(null));
            imageLabel.setIcon(new ImageIcon(resizedImage));
        }
    };

    public MainFrame() {
        initComponents();
        scrollPane1.setPreferredSize(new Dimension(mainPanel.getWidth(), mainPanel.getHeight()));

        this.loadImage.addActionListener((ActionEvent e) -> {
            JFileChooser imageOpener = new JFileChooser();
            imageOpener.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    String fileName = f.getName().toLowerCase();
                    if (fileName.endsWith(".jpg") || fileName.endsWith(".png")
                            || fileName.endsWith(".tiff") || fileName.endsWith(".bmp") || fileName.endsWith(".svg") || f.isDirectory()) {
                        return true;
                    } else return false;
                }

                @Override
                public String getDescription() {
                    return "Image files (.jpg, .png, .tiff, .bmp, .svg)";
                }
            });

            int returnValue = imageOpener.showDialog(null, "Select originalImage");
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                originalImage = ImageSharedOperations.loadImage(imageOpener.getSelectedFile().getPath());
                readedImageWidth = originalImage.getWidth();
                readedImageHeight = originalImage.getHeight();
                imageLabel.setSize(originalImage.getWidth(), originalImage.getHeight());
                resizedImage = originalImage;
                imageLabel.setIcon(new ImageIcon(resizedImage));
                clonedImage = Binarization.cloneImage(originalImage);
                zoomLevel = 1.0;
            }
        });
        this.saveImage.addActionListener((ActionEvent e) -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Specify a file to save");

            int userSelection = fileChooser.showSaveDialog(null);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                String path = fileToSave.getAbsolutePath();
//                BufferedImage img = ImageSharedOperations.toBufferedImage(resizedImage);
                BufferedImage img = ImageSharedOperations.convertIconToImage((ImageIcon) this.imageLabel.getIcon());
                ImageSharedOperations.saveImage(img, path);
            }
        });
        imageLabel.addMouseListener(new MouseListener() {
            boolean isMotionListenerWorking = false;

            @Override
            public void mouseClicked(MouseEvent e) {
                if (isMotionListenerWorking) {
                    imageLabel.removeMouseMotionListener(mouseMotionListener);
                    imageLabel.removeMouseWheelListener(mouseWheelListener);
                    isMotionListenerWorking = false;
                } else {
                    imageLabel.addMouseMotionListener(mouseMotionListener);
                    imageLabel.addMouseWheelListener(mouseWheelListener);
                    isMotionListenerWorking = true;
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (imageLabel.getMouseMotionListeners().length == 0) {
                    imageLabel.addMouseMotionListener(mouseMotionListener);
                    imageLabel.addMouseWheelListener(mouseWheelListener);
                    isMotionListenerWorking = true;
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        changeColorButton.addActionListener((ActionEvent e) -> {
            int red = Integer.parseInt(colorRTextField.getText());
            int green = Integer.parseInt(colorGTextField.getText());
            int blue = Integer.parseInt(colorBTextField.getText());
            int rgb = red;
            if (red < 0 || red > 255 || green < 0 || green > 255 || blue < 0 || blue > 255) {
                JOptionPane.showMessageDialog(this, "Bad value", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            rgb = (rgb << 8) + green;
            rgb = (rgb << 8) + blue;
            int x = Integer.parseInt(mouseXPositionLabel.getText());
            int y = Integer.parseInt(mouseYPositionLabel.getText());
            originalImage = ImageSharedOperations.toBufferedImage(resizedImage);
            originalImage.setRGB(x, y, rgb);
            resizedImage = originalImage;
            imageLabel.setIcon(new ImageIcon(resizedImage));
        });
        createHistogram.addActionListener((ActionEvent e) -> {
            histogramsList.clear();
            histogramsList.addAll(HistogramsOperations.createHistograms(resizedImage));
            new HistogramsFrame(histogramsList);
        });
        strechHistograms.addActionListener((ActionEvent e) -> {
            resizedImage = HistogramsOperations.strechHistograms(resizedImage, histogramsList);
            imageLabel.setIcon(new ImageIcon(resizedImage));
        });
        equalizeHistograms.addActionListener((ActionEvent e) -> {
            resizedImage = HistogramsOperations.equalizeHistograms(resizedImage, histogramsList);
            imageLabel.setIcon(new ImageIcon(resizedImage));
        });
        enlightenButton.addActionListener((ActionEvent e) -> {
            resizedImage = HistogramsOperations.enlightenImageByPower(resizedImage);
            imageLabel.setIcon(new ImageIcon(resizedImage));
        });
        enlightenByLogarithmButton.addActionListener((ActionEvent e) -> {
            resizedImage = HistogramsOperations.enlightenImageByLogarithm(resizedImage);
            imageLabel.setIcon(new ImageIcon(resizedImage));
        });
        dimByPowerButton.addActionListener((ActionEvent e) -> {
            resizedImage = HistogramsOperations.dimImageByPower(resizedImage);
            imageLabel.setIcon(new ImageIcon(resizedImage));
        });
        dimByLogarithmButton.addActionListener((ActionEvent e) -> {
            resizedImage = HistogramsOperations.dimImageByLogarithm(resizedImage);
            imageLabel.setIcon(new ImageIcon(resizedImage));
        });
        backToOriginalImageButton.addActionListener((ActionEvent e) -> {
            originalImage = ImageSharedOperations.toBufferedImage(clonedImage);
            resizedImage = clonedImage;
            imageLabel.setIcon(new ImageIcon(resizedImage));
            zoomLevel = 1.0;
            clonedImage = Binarization.cloneImage(originalImage);
        });
        this.grayScaleFromRed.addActionListener((ActionEvent e) -> {
            resizedImage = Binarization.changeImageToGrayScale(resizedImage, Binarization.RED);
            imageLabel.setIcon(new ImageIcon(resizedImage));
        });
        this.grayScaleFromGreen.addActionListener((ActionEvent e) -> {
            resizedImage = Binarization.changeImageToGrayScale(resizedImage, Binarization.GREEN);
            imageLabel.setIcon(new ImageIcon(resizedImage));
        });
        this.grayScaleFromBlue.addActionListener((ActionEvent e) -> {
            resizedImage = Binarization.changeImageToGrayScale(resizedImage, Binarization.BLUE);
            imageLabel.setIcon(new ImageIcon(resizedImage));
        });
        this.grayScaleFromAvg.addActionListener((ActionEvent e) -> {
            resizedImage = Binarization.changeImageToGrayScale(resizedImage, Binarization.AVERAGE);
            imageLabel.setIcon(new ImageIcon(resizedImage));
        });
        manualBinarization.addActionListener((ActionEvent) -> {
            String threshold = JOptionPane.showInputDialog("Enter threshold");
            int th = Integer.parseInt(threshold);
            resizedImage = Binarization.manualBinarization(ImageSharedOperations.toBufferedImage(resizedImage), th);
            originalImage = ImageSharedOperations.toBufferedImage(resizedImage);
            imageLabel.setIcon(new ImageIcon(resizedImage));
        });
        otsuMethod.addActionListener((ActionEvent e) -> {
            resizedImage = Binarization.otsuBinarization(ImageSharedOperations.toBufferedImage(resizedImage));
            originalImage = ImageSharedOperations.toBufferedImage(resizedImage);
            imageLabel.setIcon(new ImageIcon(resizedImage));
        });
        niblackMethod.addActionListener((ActionEvent e) -> {
            String parameter = JOptionPane.showInputDialog("Enter threshold parameter");
            double param = Double.parseDouble(parameter);
            String windowSize = JOptionPane.showInputDialog("Enter window size");
            int window = Integer.parseInt(windowSize);
            resizedImage = Binarization.niblackBinarization(ImageSharedOperations.toBufferedImage(resizedImage), param, window);
            originalImage = ImageSharedOperations.toBufferedImage(resizedImage);
            imageLabel.setIcon(new ImageIcon(resizedImage));
        });
        bernsenMethod.addActionListener((ActionEvent e) -> {
            String contrastThresholdParameter = JOptionPane.showInputDialog("Enter contrast threshold parameter");
            int contrast = Integer.parseInt(contrastThresholdParameter);
            String pixelThresholdParameter = JOptionPane.showInputDialog("Enter pixel threshold parameter");
            int set_threshold = Integer.parseInt(pixelThresholdParameter);
            String windowSize = JOptionPane.showInputDialog("Enter window size");
            int window = Integer.parseInt(windowSize);
            resizedImage = Binarization.bernsenBinarization(ImageSharedOperations.toBufferedImage(resizedImage), contrast, set_threshold, window);
            originalImage = ImageSharedOperations.toBufferedImage(resizedImage);
            imageLabel.setIcon(new ImageIcon(resizedImage));
        });
        customMaskFiltration.addActionListener((ActionEvent e) -> {

            int[][] mask = new int[3][3];
            mask[0][0] = Integer.parseInt(maskTF00.getText());
            mask[0][1] = Integer.parseInt(maskTF01.getText());
            mask[0][2] = Integer.parseInt(maskTF02.getText());
            mask[1][0] = Integer.parseInt(maskTF10.getText());
            mask[1][1] = Integer.parseInt(maskTF11.getText());
            mask[1][2] = Integer.parseInt(maskTF12.getText());
            mask[2][0] = Integer.parseInt(maskTF20.getText());
            mask[2][1] = Integer.parseInt(maskTF21.getText());
            mask[2][2] = Integer.parseInt(maskTF22.getText());

            resizedImage = Filtration.filterImageCustomMask(ImageSharedOperations.toBufferedImage(resizedImage), mask);

            originalImage = ImageSharedOperations.toBufferedImage(resizedImage);
            imageLabel.setIcon(new ImageIcon(resizedImage));
        });

        kuwaharaFiltration.addActionListener((ActionEvent e) -> {
            resizedImage = Filtration.kuwaharaFilter(ImageSharedOperations.toBufferedImage(resizedImage));
            originalImage = ImageSharedOperations.toBufferedImage(resizedImage);
            imageLabel.setIcon(new ImageIcon(resizedImage));
        });
        median3x3Filtration.addActionListener((ActionEvent e) -> {
            resizedImage = Filtration.median3x3Filter(ImageSharedOperations.toBufferedImage(resizedImage));
            originalImage = ImageSharedOperations.toBufferedImage(resizedImage);
            imageLabel.setIcon(new ImageIcon(resizedImage));
        });
        median5x5Filtration.addActionListener((ActionEvent e) -> {
            resizedImage = Filtration.median5x5Filter(ImageSharedOperations.toBufferedImage(resizedImage));
            originalImage = ImageSharedOperations.toBufferedImage(resizedImage);
            imageLabel.setIcon(new ImageIcon(resizedImage));
        });
    }

    private void createUIComponents() {
        // TODO: add custom component creation code here
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - asd
        menuBar = new JMenuBar();
        files = new JMenu();
        loadImage = new JMenuItem();
        saveImage = new JMenuItem();
        histograms = new JMenu();
        createHistogram = new JMenuItem();
        strechHistograms = new JMenuItem();
        equalizeHistograms = new JMenuItem();
        menu1 = new JMenu();
        grayScaleFromRed = new JMenuItem();
        grayScaleFromGreen = new JMenuItem();
        grayScaleFromBlue = new JMenuItem();
        grayScaleFromAvg = new JMenuItem();
        menu2 = new JMenu();
        bernsenMethod = new JMenuItem();
        manualBinarization = new JMenuItem();
        otsuMethod = new JMenuItem();
        niblackMethod = new JMenuItem();
        menu3 = new JMenu();
        customMaskFiltration = new JMenuItem();
        kuwaharaFiltration = new JMenuItem();
        median3x3Filtration = new JMenuItem();
        median5x5Filtration = new JMenuItem();
        mainPanel = new JPanel();
        changeColorPanel = new JPanel();
        label9 = new JLabel();
        label10 = new JLabel();
        mouseXPositionLabel = new JLabel();
        label11 = new JLabel();
        mouseYPositionLabel = new JLabel();
        label12 = new JLabel();
        label13 = new JLabel();
        colorRTextField = new JTextField();
        label14 = new JLabel();
        colorGTextField = new JTextField();
        label15 = new JLabel();
        colorBTextField = new JTextField();
        changeColorButton = new JButton();
        enlightenButton = new JButton();
        enlightenByLogarithmButton = new JButton();
        dimByPowerButton = new JButton();
        dimByLogarithmButton = new JButton();
        backToOriginalImageButton = new JButton();
        maskTF00 = new JTextField();
        maskTF10 = new JTextField();
        maskTF20 = new JTextField();
        maskTF01 = new JTextField();
        maskTF11 = new JTextField();
        maskTF21 = new JTextField();
        maskTF02 = new JTextField();
        maskTF12 = new JTextField();
        maskTF22 = new JTextField();
        scrollPane1 = new JScrollPane();
        imageLabel = new JLabel();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== menuBar ========
        {

            //======== files ========
            {
                files.setText("File");

                //---- loadImage ----
                loadImage.setText("Load Image");
                files.add(loadImage);

                //---- saveImage ----
                saveImage.setText("Save Image");
                files.add(saveImage);
            }
            menuBar.add(files);

            //======== histograms ========
            {
                histograms.setText("Histograms");

                //---- createHistogram ----
                createHistogram.setText("Create Histograms");
                histograms.add(createHistogram);

                //---- strechHistograms ----
                strechHistograms.setText("Strech Histograms");
                histograms.add(strechHistograms);

                //---- equalizeHistograms ----
                equalizeHistograms.setText("Equalize Histograms");
                histograms.add(equalizeHistograms);
            }
            menuBar.add(histograms);

            //======== menu1 ========
            {
                menu1.setText("Image");

                //---- grayScaleFromRed ----
                grayScaleFromRed.setText("Gray Scale from Red");
                menu1.add(grayScaleFromRed);

                //---- grayScaleFromGreen ----
                grayScaleFromGreen.setText("Gray Scale from Green");
                menu1.add(grayScaleFromGreen);

                //---- grayScaleFromBlue ----
                grayScaleFromBlue.setText("Gray Scale from Blue");
                menu1.add(grayScaleFromBlue);

                //---- grayScaleFromAvg ----
                grayScaleFromAvg.setText("Gray Scale from Avg");
                menu1.add(grayScaleFromAvg);
            }
            menuBar.add(menu1);

            //======== menu2 ========
            {
                menu2.setText("Binarization");

                //---- bernsenMethod ----
                bernsenMethod.setText("Bernsen method");
                menu2.add(bernsenMethod);

                //---- manualBinarization ----
                manualBinarization.setText("Manual Binarization");
                menu2.add(manualBinarization);

                //---- otsuMethod ----
                otsuMethod.setText("Otsu method");
                menu2.add(otsuMethod);

                //---- niblackMethod ----
                niblackMethod.setText("Niblack method");
                menu2.add(niblackMethod);
            }
            menuBar.add(menu2);

            //======== menu3 ========
            {
                menu3.setText("Filtration");

                //---- customMaskFiltration ----
                customMaskFiltration.setText("Custom Mask");
                menu3.add(customMaskFiltration);

                //---- kuwaharaFiltration ----
                kuwaharaFiltration.setText("Kuwahara");
                menu3.add(kuwaharaFiltration);

                //---- median3x3Filtration ----
                median3x3Filtration.setText("Median 3x3");
                menu3.add(median3x3Filtration);

                //---- median5x5Filtration ----
                median5x5Filtration.setText("Median 5x5");
                menu3.add(median5x5Filtration);
            }
            menuBar.add(menu3);
        }
        setJMenuBar(menuBar);

        //======== mainPanel ========
        {

            // JFormDesigner evaluation mark
            mainPanel.setBorder(new javax.swing.border.CompoundBorder(
                    new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
                            "JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
                            javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
                            java.awt.Color.red), mainPanel.getBorder()));
            mainPanel.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
                public void propertyChange(java.beans.PropertyChangeEvent e) {
                    if ("border".equals(e.getPropertyName())) throw new RuntimeException();
                }
            });


            //======== changeColorPanel ========
            {
                changeColorPanel.setPreferredSize(new Dimension(100, 0));

                //---- label9 ----
                label9.setText("Mouse Position:");

                //---- label10 ----
                label10.setText("X:");

                //---- mouseXPositionLabel ----
                mouseXPositionLabel.setText("text");

                //---- label11 ----
                label11.setText("Y:");

                //---- mouseYPositionLabel ----
                mouseYPositionLabel.setText("text");

                //---- label12 ----
                label12.setText("Pixel Properties");

                //---- label13 ----
                label13.setText("R:");

                //---- label14 ----
                label14.setText("G:");

                //---- label15 ----
                label15.setText("B:");

                //---- changeColorButton ----
                changeColorButton.setText("Change");

                //---- enlightenButton ----
                enlightenButton.setText("Enlighten");

                //---- enlightenByLogarithmButton ----
                enlightenByLogarithmButton.setText("Enli by Log");

                //---- dimByPowerButton ----
                dimByPowerButton.setText("Dim");

                //---- dimByLogarithmButton ----
                dimByLogarithmButton.setText("Dim by Log");

                //---- backToOriginalImageButton ----
                backToOriginalImageButton.setText("Original");

                GroupLayout changeColorPanelLayout = new GroupLayout(changeColorPanel);
                changeColorPanel.setLayout(changeColorPanelLayout);
                changeColorPanelLayout.setHorizontalGroup(
                        changeColorPanelLayout.createParallelGroup()
                                .addGroup(changeColorPanelLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(changeColorPanelLayout.createParallelGroup()
                                                .addGroup(changeColorPanelLayout.createSequentialGroup()
                                                        .addGap(6, 6, 6)
                                                        .addGroup(changeColorPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                                .addComponent(label9)
                                                                .addGroup(changeColorPanelLayout.createSequentialGroup()
                                                                        .addComponent(label14)
                                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                        .addComponent(colorGTextField, GroupLayout.DEFAULT_SIZE, 1, Short.MAX_VALUE))
                                                                .addGroup(changeColorPanelLayout.createSequentialGroup()
                                                                        .addComponent(label15)
                                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                        .addComponent(colorBTextField, GroupLayout.DEFAULT_SIZE, 1, Short.MAX_VALUE))
                                                                .addGroup(changeColorPanelLayout.createSequentialGroup()
                                                                        .addComponent(label13)
                                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                        .addComponent(colorRTextField, GroupLayout.DEFAULT_SIZE, 1, Short.MAX_VALUE))
                                                                .addGroup(changeColorPanelLayout.createSequentialGroup()
                                                                        .addComponent(label10)
                                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                        .addComponent(mouseXPositionLabel, GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE))
                                                                .addGroup(changeColorPanelLayout.createSequentialGroup()
                                                                        .addComponent(label11)
                                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                        .addComponent(mouseYPositionLabel, GroupLayout.DEFAULT_SIZE, 0, Short.MAX_VALUE))
                                                                .addComponent(label12)))
                                                .addComponent(changeColorButton, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(enlightenButton, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(enlightenByLogarithmButton, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(dimByPowerButton, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(dimByLogarithmButton)
                                                .addComponent(backToOriginalImageButton, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE)
                                                .addGroup(changeColorPanelLayout.createSequentialGroup()
                                                        .addComponent(maskTF00, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(maskTF10, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(maskTF20, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE))
                                                .addGroup(changeColorPanelLayout.createSequentialGroup()
                                                        .addComponent(maskTF01, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(maskTF11, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(maskTF21, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE))
                                                .addGroup(changeColorPanelLayout.createSequentialGroup()
                                                        .addComponent(maskTF02, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(maskTF12, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(maskTF22, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)))
                                        .addContainerGap(9, Short.MAX_VALUE))
                );
                changeColorPanelLayout.setVerticalGroup(
                        changeColorPanelLayout.createParallelGroup()
                                .addGroup(changeColorPanelLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(label9)
                                        .addGroup(changeColorPanelLayout.createParallelGroup()
                                                .addGroup(changeColorPanelLayout.createSequentialGroup()
                                                        .addGap(8, 8, 8)
                                                        .addComponent(label10))
                                                .addGroup(changeColorPanelLayout.createSequentialGroup()
                                                        .addGap(6, 6, 6)
                                                        .addComponent(mouseXPositionLabel)))
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(changeColorPanelLayout.createParallelGroup()
                                                .addComponent(label11)
                                                .addComponent(mouseYPositionLabel))
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(label12)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(changeColorPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(label13)
                                                .addComponent(colorRTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(changeColorPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(label14)
                                                .addComponent(colorGTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(changeColorPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(label15)
                                                .addComponent(colorBTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(changeColorButton)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(enlightenButton)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(enlightenByLogarithmButton)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(dimByPowerButton)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(dimByLogarithmButton)
                                        .addGap(18, 18, 18)
                                        .addComponent(backToOriginalImageButton)
                                        .addGap(51, 51, 51)
                                        .addGroup(changeColorPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(maskTF00, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(maskTF10, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(maskTF20, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(changeColorPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(maskTF01, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(maskTF11, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(maskTF21, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(changeColorPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(maskTF02, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(maskTF12, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(maskTF22, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addContainerGap(250, Short.MAX_VALUE))
                );
            }

            //======== scrollPane1 ========
            {

                //---- imageLabel ----
                imageLabel.setVerticalAlignment(SwingConstants.TOP);
                imageLabel.setAlignmentY(0.0F);
                scrollPane1.setViewportView(imageLabel);
            }

            GroupLayout mainPanelLayout = new GroupLayout(mainPanel);
            mainPanel.setLayout(mainPanelLayout);
            mainPanelLayout.setHorizontalGroup(
                    mainPanelLayout.createParallelGroup()
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                    .addComponent(changeColorPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 1016, Short.MAX_VALUE)
                                    .addContainerGap())
            );
            mainPanelLayout.setVerticalGroup(
                    mainPanelLayout.createParallelGroup()
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                    .addGroup(mainPanelLayout.createParallelGroup()
                                            .addComponent(changeColorPanel, GroupLayout.PREFERRED_SIZE, 731, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 753, GroupLayout.PREFERRED_SIZE))
                                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            );
        }
        contentPane.add(mainPanel, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - asd
    private JMenuBar menuBar;
    private JMenu files;
    private JMenuItem loadImage;
    private JMenuItem saveImage;
    private JMenu histograms;
    private JMenuItem createHistogram;
    private JMenuItem strechHistograms;
    private JMenuItem equalizeHistograms;
    private JMenu menu1;
    private JMenuItem grayScaleFromRed;
    private JMenuItem grayScaleFromGreen;
    private JMenuItem grayScaleFromBlue;
    private JMenuItem grayScaleFromAvg;
    private JMenu menu2;
    private JMenuItem bernsenMethod;
    private JMenuItem manualBinarization;
    private JMenuItem otsuMethod;
    private JMenuItem niblackMethod;
    private JMenu menu3;
    private JMenuItem customMaskFiltration;
    private JMenuItem kuwaharaFiltration;
    private JMenuItem median3x3Filtration;
    private JMenuItem median5x5Filtration;
    private JPanel mainPanel;
    private JPanel changeColorPanel;
    private JLabel label9;
    private JLabel label10;
    private JLabel mouseXPositionLabel;
    private JLabel label11;
    private JLabel mouseYPositionLabel;
    private JLabel label12;
    private JLabel label13;
    private JTextField colorRTextField;
    private JLabel label14;
    private JTextField colorGTextField;
    private JLabel label15;
    private JTextField colorBTextField;
    private JButton changeColorButton;
    private JButton enlightenButton;
    private JButton enlightenByLogarithmButton;
    private JButton dimByPowerButton;
    private JButton dimByLogarithmButton;
    private JButton backToOriginalImageButton;
    private JTextField maskTF00;
    private JTextField maskTF10;
    private JTextField maskTF20;
    private JTextField maskTF01;
    private JTextField maskTF11;
    private JTextField maskTF21;
    private JTextField maskTF02;
    private JTextField maskTF12;
    private JTextField maskTF22;
    private JScrollPane scrollPane1;
    private JLabel imageLabel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
