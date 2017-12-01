import javax.swing.*;
import java.awt.*;

public class ServerMenu extends EchoServer4 {
    private JPanel jpanel;
    public Menu menuBar;
    private EchoServer4 server;

    public ServerMenu()
    {
        menuBar = new Menu();
        jpanel = new JPanel();
        server = new EchoServer4();

        jpanel.add(server, BorderLayout.NORTH);
    }

    public void createFrameMenu()
    {
        JFrame f = new JFrame("Server");
        f.add(jpanel);
        f.pack();
        f.setSize(400,400);
        f.setVisible(true);
        f.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public Menu returnMenu()
    {
        return menuBar;
    }

}
