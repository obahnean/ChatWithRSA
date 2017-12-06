/*
 * CS342_PROJECT5: Networked Chat with RSA Encryption/Decryption
 * 
 * Muna Bist - mbist3
 * Queena Zhang - qzhang85
 * Ovidiu Bahnean - obahne2 
 */
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ServerParts {

    public static void setFileMenu(JMenu file){
        JMenuItem aboutItem = new JMenuItem("About");
        file.add(aboutItem);
        aboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,"<html><h1> Networked Chat</h1>"
                		+ "This is a chat program with RSA Encryption/Decryption\r\n"
                		+ "Multiple clients are allowed to connect to a Central Server\r\n"
                		+ "and they can send the encrypted message over the secure server\r\n",
                        "About" ,JOptionPane.PLAIN_MESSAGE);
                
            }
        });
        
        JMenuItem infoItem = new JMenuItem("Info");
        file.add(infoItem);
        infoItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,"<html><h1> Team Members</h1>"
                		+ "Queena Zhang\t-qzhang85\r\n"
                		+ "Ovidiu Bahnean\t-obahne2\r\n"
                		+ "Muna Bist\t-mbist3\r\n",
                        "Info" ,JOptionPane.PLAIN_MESSAGE);
                
            }
        });

        JMenuItem helpItem = new JMenuItem("Help");
        file.add(helpItem);
        helpItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                
                JOptionPane.showMessageDialog(null, "<html><h1> How to Chat?</h1>"
    					+ "IDE: Intellij\r\n"
    					+ "Have the file.txt file in the src folder.\r\n"
    					+ "Text file contains one prime number per line.\r\n"
    					+ "Choose a unique user name 12 chars max in the Name field. \r\n"
    					+ "Enter the prime numbers in p and q field.\r\n"
    					+ "And then connect to the server.\r\n"
    					+ "Start your chat by sending the messages typed in the message field. \r\n",
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
