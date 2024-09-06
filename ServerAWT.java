
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.net.Socket;
import java.net.ServerSocket;
import java.io.PrintStream;
import java.util.Scanner;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.Color;
import java.awt.*;
import java.awt.event.*;

class ServerAWT extends Frame implements ActionListener {

    private TextField pathField, outputPathField, redField, greenField, blueField;
    private TextArea logArea;
    private Button startButton;

    public ServerAWT() {
        setLayout(new FlowLayout());

        add(new Label("Image Path:"));
        pathField = new TextField(30);
        add(pathField);

        add(new Label("Output Directory:"));
        outputPathField = new TextField(30);
        add(outputPathField);

        add(new Label("Red Channel Name:"));
        redField = new TextField(10);
        add(redField);

        add(new Label("Green Channel Name:"));
        greenField = new TextField(10);
        add(greenField);

        add(new Label("Blue Channel Name:"));
        blueField = new TextField(10);
        add(blueField);

        startButton = new Button("Start Server");
        add(startButton);
        startButton.addActionListener(this);

        logArea = new TextArea(10, 50);
        add(logArea);

        setTitle("Server AWT");
        setSize(600, 400);
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                dispose();
            }
        });
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == startButton) {
            String path = pathField.getText();
            String outputPath = outputPathField.getText();
            String redName = redField.getText();
            String greenName = greenField.getText();
            String blueName = blueField.getText();

            logArea.append("Starting server...
");

            try {
                ServerSocket server = new ServerSocket(3456);
                Socket socket = server.accept();
                logArea.append("Client connected.
");

                File f = new File(path);
                BufferedImage img = ImageIO.read(f);
                int width = img.getWidth();
                int height = img.getHeight();

                BufferedImage grsclimr = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE);
                BufferedImage grsclimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE);
                BufferedImage grsclimb = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE);

                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        Color c = new Color(img.getRGB(j, i));
                        int r = c.getRed();
                        int g = c.getGreen();
                        int b = c.getBlue();
                        int a = c.getAlpha();
                        Color re = new Color(r, r, r, a);
                        Color ge = new Color(g, g, g, a);
                        Color bl = new Color(b, b, b, a);
                        grsclimr.setRGB(j, i, re.getRGB());
                        grsclimg.setRGB(j, i, ge.getRGB());
                        grsclimb.setRGB(j, i, bl.getRGB());
                    }
                }

                String PATH1 = outputPath + redName + ".png";
                String PATH2 = outputPath + greenName + ".png";
                String PATH3 = outputPath + blueName + ".png";

                ImageIO.write(grsclimr, "png", new File(PATH1));
                ImageIO.write(grsclimg, "png", new File(PATH2));
                ImageIO.write(grsclimb, "png", new File(PATH3));

                logArea.append("Channel images saved.
");

                PrintStream p = new PrintStream(socket.getOutputStream());
                FileInputStream fisr = new FileInputStream(PATH1);
                FileInputStream fisg = new FileInputStream(PATH2);
                FileInputStream fisb = new FileInputStream(PATH3);

                sendData(fisr, p);
                sendData(fisg, p);
                sendData(fisb, p);

                logArea.append("Data sent to client.
");

            } catch (IOException e) {
                logArea.append("Error: " + e.getMessage() + "
");
            }
        }
    }

    private void sendData(FileInputStream fis, PrintStream p) throws IOException {
        int data = fis.read();
        while (data != -1) {
            p.println(data);
            data = fis.read();
        }
        p.println(-1);
        fis.close();
    }

    public static void main(String[] args) {
        new ServerAWT();
    }
}
