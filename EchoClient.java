import com.sun.org.apache.xpath.internal.SourceTree;

import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import javax.swing.*;

public class EchoClient extends JFrame implements ActionListener
{
    // GUI items
    JButton sendButton;
    JButton connectButton;
    JTextField machineInfo;
    JTextField portInfo;
    JTextField message;
    JTextArea history;

    private JTextField name;
   // private Vector<String> listOfClientNames;
    DefaultComboBoxModel comboModel = new DefaultComboBoxModel();
    //private JComboBox<String> listOfClient;
    JComboBox listOfClient;

    JPanel upperPanel;

    // Network Items
    boolean connected;
    Socket echoSocket;
    PrintWriter out;
    BufferedReader in;

    // set up GUIp
    public EchoClient()
    {
        super( "Echo Client" );
        // get content pane and set its layout
        Container container = getContentPane();
        container.setLayout (new BorderLayout ());

        // set up the North panel
        upperPanel = new JPanel ();
        upperPanel.setLayout (new GridLayout (5,2));
        container.add (upperPanel, BorderLayout.NORTH);

        // create buttons
        connected = false;

        setUpperPanel();

        history = new JTextArea ( 10, 40 );
        history.setEditable(false);
        container.add( new JScrollPane(history) ,  BorderLayout.CENTER);

        setSize( 500, 500 );
        setVisible( true );

    } // end CountDown constructor
    public void setUpperPanel(){

        upperPanel.add ( new JLabel ("Server Address: ", JLabel.RIGHT) );
        machineInfo = new JTextField ("127.0.0.1");
        upperPanel.add( machineInfo );
        upperPanel.add ( new JLabel ("Server Port: ", JLabel.RIGHT) );
        portInfo = new JTextField ("");
        upperPanel.add( portInfo );

        name = new JTextField("Name");
        upperPanel.add(name);

        connectButton = new JButton( "Connect to Server" );
        connectButton.addActionListener( this );
        upperPanel.add( connectButton );

        upperPanel.add ( new JLabel ("Message: ", JLabel.RIGHT) );
        message = new JTextField ("");
        message.addActionListener( this );
        upperPanel.add( message );

        sendButton = new JButton( "Send Message" );
        sendButton.addActionListener( this );
        sendButton.setEnabled (false);
        upperPanel.add( sendButton );

        //listOfClientNames = new Vector<>();
        //listOfClientNames.add("");
        //listOfClient = new JComboBox<>(new Vector<>(listOfClientNames));
        listOfClient = new JComboBox(comboModel);

        upperPanel.add(listOfClient);



    }

    public static void main( String args[] )
    {
        EchoClient application = new EchoClient();
        application.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }

    // handle button event
    public void actionPerformed( ActionEvent event )
    {
        if ( connected &&
                (event.getSource() == sendButton ||
                        event.getSource() == message ) )
        {
            doSendMessage();
        }
        else if (event.getSource() == connectButton)
        {
            doManageConnection();
        }
    }

    public void doSendMessage()
    {
        try
        {

            String sendMessageToClient = "sendMessage:";
            String targetClient = comboModel.getSelectedItem().toString();
            String getMessage = message.getText();

            sendMessageToClient = sendMessageToClient + targetClient + "//:" + getMessage;

            out.println(sendMessageToClient);
            //history.insert ("From Server: " + in.readLine() + "\n" , 0);

        }
        catch (Exception e)
        {
            history.insert ("Error in processing message ", 0);
        }
    }

    public void doManageConnection()
    {
        if (connected == false)
        {
            String machineName = null;
            int portNum = -1;
            try {
                machineName = machineInfo.getText();
                portNum = Integer.parseInt(portInfo.getText());
                echoSocket = new Socket(machineName, portNum );
                out = new PrintWriter(echoSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(
                        echoSocket.getInputStream()));

                //send the name out to the server
                String userName = name.getText();
                System.out.println(userName);
                //send out the new user name to the server
                out.println("addUserName:" + userName);

                // start a new thread to read from the socket
                new CommunicationReadThread (in, this);

                sendButton.setEnabled(true);
                connected = true;
                connectButton.setText("Disconnect from Server");
                //set username not editable
                name.setEnabled(false);

            } catch (NumberFormatException e) {
                history.insert ( "Server Port must be an integer\n", 0);
            } catch (UnknownHostException e) {
                history.insert("Don't know about host: " + machineName , 0);
            } catch (IOException e) {
                history.insert ("Couldn't get I/O for "
                        + "the connection to: " + machineName , 0);
            }

        }
        else
        {
            try
            {

                String userName = name.getText();
                System.out.println(userName);
                //send out the disconnect user name to the server
                out.println("removeUserName:" + userName);


                out.close();
                in.close();
                echoSocket.close();
                sendButton.setEnabled(false);
                connected = false;
                connectButton.setText("Connect to Server");
                name.setEnabled(true);
            }
            catch (IOException e)
            {
                history.insert ("Error in closing down Socket ", 0);
            }
        }


    }

} // end class EchoServer3

// Class to handle socket reads
//   THis class is NOT written as a nested class, but perhaps it should
//==========================================================communication thread
class CommunicationReadThread extends Thread
{
    //private Socket clientSocket;
    private EchoClient gui;
    private BufferedReader in;


    public CommunicationReadThread (BufferedReader inparam, EchoClient ec3)
    {
        in = inparam;
        gui = ec3;
        start();
        gui.history.insert ("Starting Communicating\n", 0);

    }

    public void run()
    {
        System.out.println ("New Communication Thread Started");

        try {
            String inputLine;

            while ((inputLine = in.readLine()) != null)
            {
                System.out.println ("From Server: " + inputLine);
               // gui.history.insert ("From Server: " + inputLine + "\n", 0);

                if(inputLine.contains("addUserName:") || inputLine.contains("removeUserName:")){
                    //then send the new user over to all client

                    String newClient  = inputLine.substring(inputLine.indexOf(":")+1);
                    //add to client list
                    //clear the model, then add all active clients
                    gui.comboModel.removeAllElements();
                    String[] names= newClient.split("//:");
                    System.out.println(names.length);
                    for(int i =0;i<names.length;i++){
                        System.out.println("newly added client add list: " + names[i]);
                        gui.comboModel.addElement(names[i]);
                    }

                }

                //send message is handled in send message button
                //handle getMessage here + from who:
                if(inputLine.contains("getMessage:")) {
                    String content = inputLine.substring(inputLine.indexOf(":") + 1);
                    //String target_msg[] = content.split("//:");
                    gui.history.insert(content + "\n", 0);
                }



                if(inputLine.equals("NameNotUniqueAlert")){
                    gui.history.insert("Please choose another name",0);

                    break;
                }

                if (inputLine.equals("Bye."))
                    break;

            }

            in.close();
            //clientSocket.close();
        }
        catch (IOException e)
        {
            //close the socket
            try {
                in.close();
            }
            catch(IOException except){
                System.out.println("close the socket");
            }

            System.err.println("Problem with Client Read");
            //System.exit(1);
        }
    }
}
