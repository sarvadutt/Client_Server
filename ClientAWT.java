
import java.net.Socket;
import java.util.Scanner;
import java.io.FileOutputStream;
import java.io.File;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.*;
import java.awt.event.*;

class ClientAWT extends Frame implements ActionListener {

    private TextField ipField, portField, savePathField;
    private TextArea logArea;
    private Button connectButton;

    public ClientAWT() {
        setLayout(new FlowLayout());

        add(new Label("Server IP:"));
        ipField = new TextField("127.0.0.1", 15);
        add(ipField);

        add(new Label("Port:"));
        portField = new TextField("3456", 5);
        add(portField);

        add(new Label("Save Path:"));
        savePathField = new TextField("/Users/akavas/Desktop/Client_server_caM/client_result.png", 30);
        add(savePathField);

        connectButton = new Button("Connect to Server");
        add(connectButton);
        connectButton.addActionListener(this);

        logArea = new TextArea(10, 50);
        add(logArea);

        setTitle("Client AWT");
        setSize(600, 400);
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                dispose();
            }
        });
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == connectButton) {
            String serverIp = ipField.getText();
            int port = Integer.parseInt(portField.getText());
            String savePath = savePathField.getText();

            logArea.append("Connecting to server...
");

            try {
                Socket socket = new Socket(serverIp, port);
                Scanner s = new Scanner(socket.getInputStream());
                String str = s.nextLine();

                logArea.append("Connected to server.
");

                // Set paths to temporary files for image channels
                File f1 = File.createTempFile("r_channel", ".tmp");
                File f2 = File.createTempFile("g_channel", ".tmp");
                File f3 = File.createTempFile("b_channel", ".tmp");

                int temp;

                FileOutputStream fosr = new FileOutputStream(f1);
                FileOutputStream fosg = new FileOutputStream(f2);
                FileOutputStream fosb = new FileOutputStream(f3);

                Scanner sc1 = new Scanner(socket.getInputStream());

                temp = sc1.nextInt();
                while (temp != -1) {
                    fosr.write((byte) temp);
                    temp = sc1.nextInt();
                }
                fosr.flush();
                fosr.close();

                temp = sc1.nextInt();
                while (temp != -1) {
                    fosg.write((byte) temp);
                    temp = sc1.nextInt();
                }
                fosg.flush();
                fosg.close();

                temp = sc1.nextInt();
                while (temp != -1) {
                    fosb.write((byte) temp);
                    temp = sc1.nextInt();
                }
                fosb.flush();
                fosb.close();

                BufferedImage imgr = ImageIO.read(f1);
                BufferedImage imgg = ImageIO.read(f2);
                BufferedImage imgb = ImageIO.read(f3);

                BufferedImage grscl = new BufferedImage(imgr.getWidth(), imgr.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);

                int height, width;

                height = imgr.getHeight();
                width = imgr.getWidth();

                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {

                        Color cr = new Color(imgr.getRGB(j, i));
                        Color cg = new Color(imgg.getRGB(j, i));
                        Color cb = new Color(imgb.getRGB(j, i));

                        int red = cr.getRed();
                        int green = cg.getGreen();
                        int blue = cb.getBlue();

                        int a1 = cr.getAlpha();
                        int a2 = cg.getAlpha();
                        int a3 = cb.getAlpha();

                        int a = (a1 + a2 + a3) / 3;

                        Color co = new Color(red, green, blue, a);

                        grscl.setRGB(j, i, co.getRGB());

                    }
                }

                ImageIO.write(grscl, "png", new File(savePath));

                logArea.append("Image saved at " + savePath + "
");

                f1.delete();  // Clean up temp files
                f2.delete();
                f3.delete();

            } catch (Exception ae1) {
                logArea.append("Error: " + ae1.getMessage() + "
");
            }
        }
    }

    public static void main(String[] args) {
        new ClientAWT();
    }
}
