import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ServerParts {

    public static void setFileMenu(JMenu file){
        JMenuItem aboutItem = new JMenuItem("About");
        file.add(aboutItem);
        aboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,"To do",
                        "About" ,JOptionPane.PLAIN_MESSAGE);
            }
        });

        JMenuItem helpItem = new JMenuItem("Help");
        file.add(helpItem);
        helpItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                JOptionPane.showMessageDialog(null,"To do",
                        "Help", JOptionPane.PLAIN_MESSAGE);
            }
        });

        JMenuItem exitItem = new JMenuItem("Exit");
        file.add(exitItem);
        exitItem.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        System.exit(0);
                    }
                }
        );

    }
}
