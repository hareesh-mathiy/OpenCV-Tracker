package tracker;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.DataBufferByte;
import java.util.concurrent.TimeUnit;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.*;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Tracker {

    public ImageCapture imagecap;
    public ArrayList<Rect> rects = new ArrayList<>();
    public ArrayList<Rect> lastrects = new ArrayList<>();
    public boolean showbwimage = false;
    public boolean showcontours = false;
    public boolean showboxes = true;

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        new Tracker();
    }

    public Tracker() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                    ex.printStackTrace();
                }

                JFrame frame = new JFrame("Tracker");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                JButton showbwimageButton = new JButton("Show/Hide BW Image");
                JButton showcontourimageButton = new JButton("Show/Hide Contours");
                JButton showboxesButton = new JButton("Show/Hide Boxes");

                try {
                    frame.add(new TestPane(), BorderLayout.PAGE_START);
                    frame.add(showbwimageButton, BorderLayout.EAST);
                    frame.add(showcontourimageButton, BorderLayout.WEST);
                    frame.add(showboxesButton, BorderLayout.SOUTH);
                } catch (AWTException ex) {
                    Logger.getLogger(Tracker.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Tracker.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Tracker.class.getName()).log(Level.SEVERE, null, ex);
                }
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

                showbwimageButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (showbwimage) {
                            showbwimage = false;
                        } else {
                            showbwimage = true;
                        }
                    }
                });

                showcontourimageButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (showcontours) {
                            showcontours = false;
                        } else {
                            showcontours = true;
                        }
                    }
                });

                showboxesButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (showboxes) {
                            showboxes = false;
                        } else {
                            showboxes = true;
                        }
                    }
                });
            }
        });
    }

    public class TestPane extends JPanel {

        private BufferedImage currentFrame;

        public TestPane() throws AWTException, IOException, InterruptedException {
            imagecap = new ImageCapture();
            Timer timer = new Timer(30, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        currentFrame = imagecap.getImage();

                        if (imagecap.getRects().size() > 0) {
                            rects = imagecap.getRects();
                            lastrects.clear();
                            for (Rect r : rects) {
                                lastrects.add(r);
                            }
                        } else {
                            rects = lastrects;
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(Tracker.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    repaint();
                    try {
                        imagecap.getMat();
                    } catch (AWTException ex) {
                        Logger.getLogger(Tracker.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(Tracker.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Tracker.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            timer.start();
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(500, 300);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (currentFrame != null) {
                Graphics2D g2d = (Graphics2D) g.create();
                int x = (getWidth() - currentFrame.getWidth()) / 2;
                int y = (getHeight() - currentFrame.getHeight()) / 2;
                if (showbwimage) {
                    g2d.drawImage(currentFrame, x, y, this);
                }
                if (showboxes) {
                    for (Rect r : rects) {
                        g2d.drawRect(r.x, r.y, 50, 50);
                        g2d.setColor(Color.RED);
                        g2d.fillRect(r.x, r.y, 50, 50);
                    }
                }
                g2d.dispose();
            }
        }

    }

    public class ImageCapture {

        public BufferedImage image;
        public ArrayList<Rect> rects = new ArrayList<>();

        public ImageCapture() throws AWTException, IOException, InterruptedException {
            getMat();
        }

        public void getMat() throws AWTException, IOException, InterruptedException {
            //-----------GET TWO SCREENSHOTS-------------
            Rectangle screenRect = new Rectangle(600, 400, 500, 300);
            //Rectangle screenRect = new Rectangle(100, 100, 1000, 700);
            BufferedImage capture1 = new Robot().createScreenCapture(screenRect);
            TimeUnit.MILLISECONDS.sleep(40);
            BufferedImage capture2 = new Robot().createScreenCapture(screenRect);
            //ImageIO.write(capture1, "bmp", new File("C:/Users/Hareesh/Desktop/tracker/capture1.bmp"));
            //ImageIO.write(capture2, "bmp", new File("C:/Users/Hareesh/Desktop/tracker/capture2.bmp"));

            //-----------GET DIFFERENCE BETWEEN THE SCREENSHOTS-------------
            BufferedImage difference = new BufferedImage(capture1.getWidth(), capture1.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
            for (int x = 0; x < capture1.getWidth(); x++) {
                for (int y = 0; y < capture1.getHeight(); y++) {
                    if ((Math.abs(capture2.getRGB(x, y) - capture1.getRGB(x, y))) != 0) {
                        difference.setRGB(x, y, (new Color(255, 255, 255)).getRGB());
                    } else {
                        difference.setRGB(x, y, 0);
                    }
                }
            }

            //ImageIO.write(difference, "bmp", new File("C:/Users/Hareesh/Desktop/tracker/difference.bmp"));
            //-----------DILATE AND ERODE TO GET RID OF NOISE-------------
            try {
                System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
                int erosion_size = 15;
                int dilation_size = 5;

                Mat src = new Mat(difference.getHeight(), difference.getWidth(), CvType.CV_8UC1);
                byte[] pixels = ((DataBufferByte) difference.getRaster().getDataBuffer()).getData();
                src.put(0, 0, pixels);
                Mat dst = new Mat();
                //Convert mask to binary values
                double threshold = Imgproc.threshold(src, dst, 0, 255, Imgproc.THRESH_TRIANGLE);

                //Dilate
                Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2 * dilation_size + 1, 2 * dilation_size + 1));
                Imgproc.dilate(src, dst, element);
                //Imgcodecs.imwrite("C:/Users/Hareesh/Desktop/tracker/differenceDilated.bmp", dst);

                //Erode
                Mat element1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2 * dilation_size + 1, 2 * dilation_size + 1));
                Imgproc.erode(dst, dst, element1);
                //Imgcodecs.imwrite("C:/Users/Hareesh/Desktop/tracker/differenceEroded.bmp", dst);

                //OutlineObjects
                ArrayList<MatOfPoint> contours = new ArrayList<>();
                Mat hierarchy = new Mat();
                Imgproc.findContours(dst, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
                Scalar c = new Scalar(255, 255, 0);
                if (hierarchy.size().height > 0 && hierarchy.size().width > 0) {
                    rects.clear();
                    for (int idx = 0; idx >= 0; idx = (int) hierarchy.get(0, idx)[0]) {
                        Rect r = Imgproc.boundingRect(contours.get(idx));
                        if (showcontours) {
                            Imgproc.rectangle(dst, new org.opencv.core.Point(r.x, r.y + r.height), new org.opencv.core.Point(r.x + r.width, r.y), c, 3);
                        }
                        if (r.area() > 1000) {
                            rects.add(r);
                        }

                    }
                    //Imgcodecs.imwrite("C:/Users/Hareesh/Desktop/tracker/contours.bmp", dst);
                }

                image = mat2Img(dst);

            } catch (Exception e) {
                System.out.println("error: " + e.getMessage());
            }
        }

        public ArrayList<Rect> getRects() {
            return rects;
        }

        public BufferedImage mat2Img(Mat in) {
            BufferedImage out = new BufferedImage(in.width(), in.height(), BufferedImage.TYPE_BYTE_GRAY);
            byte[] data = ((DataBufferByte) out.getRaster().getDataBuffer()).getData();
            in.get(0, 0, data);
            return out;
        }

        public BufferedImage getImage() throws IOException {
            //ImageIO.write(image, "bmp", new File("C:/Users/Hareesh/Desktop/tracker/image.bmp"));
            return image;
        }
    }
}
