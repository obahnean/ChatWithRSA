/*
 * CS342_PROJECT5: Networked Chat with RSA Encryption/Decryption
 * 
 * Muna Bist - mbist3
 * Queena Zhang - qzhang85
 * Ovidiu Bahnean - obahne2 
 */
import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class EchoServer4 extends JFrame {

    // GUI items
    JMenu fileMenu;

    JButton ssButton;
    JLabel machineInfo;
    JLabel portInfo;
    JTextArea history;
    private boolean running;

    // Network Items
    boolean serverContinue;
    ServerSocket serverSocket;
    Vector <PrintWriter> outStreamList;
    Vector<String>nameListVector;
    Vector<String>nameForCheckingDuplicate;

    //If multiple threads access a hash map concurrently,
    // and at least one of the threads modifies the map structurally,
    // it must be synchronized externally.
    Map<PrintWriter,String> removeClient;
    DefaultListModel model;
   //list for client names
    JList list;

    // set up GUI
    public EchoServer4()
    {
        super( "Echo Server" );

        fileMenu = new JMenu("File");

        // set up the shared outStreamList
        outStreamList = new Vector<PrintWriter>();
        nameListVector = new Vector<String>();
        nameForCheckingDuplicate = new Vector<String>();
        removeClient = Collections.synchronizedMap(new HashMap<>());
        model = new DefaultListModel();

        // get content pane and set its layout
        Container container = getContentPane();
        container.setLayout( new FlowLayout() );

        JMenuBar bar = new JMenuBar();
        setJMenuBar(bar);
        bar.add(fileMenu);
        container.add(bar, BorderLayout.NORTH);
        ServerParts.setFileMenu(fileMenu);
        setJMenuBar(bar);

        // create buttons
        running = false;
        ssButton = new JButton( "Start Listening" );
        ssButton.addActionListener( e -> doButton (e) );
        container.add( ssButton );

        String machineAddress = null;
        try
        {
            InetAddress addr = InetAddress.getLocalHost();
            machineAddress = addr.getHostAddress();
        }
        catch (UnknownHostException e)
        {
            machineAddress = "127.0.0.1";
        }
        machineInfo = new JLabel (machineAddress);
        container.add( machineInfo );
        portInfo = new JLabel (" Not Listening ");
        container.add( portInfo );

        //add name list gui
        model.addElement("Client List");
        list = new JList(model);
        list.setPreferredSize(new Dimension(100, 100));
        JScrollPane sp = new JScrollPane(list);
        container.add(sp);


        history = new JTextArea ( 10, 40 );
        history.setEditable(false);
        container.add( new JScrollPane(history) );

        setSize( 500, 450 );
        setVisible( true );

    }

    public static void main( String args[] )
    {
        EchoServer4 application = new EchoServer4();
        application.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }


    // handle button event
    public void doButton( ActionEvent event )
    {
        if (running == false)
        {
            new ConnectionThread (this);
            running = true;
        }
        else
        {
            serverContinue = false;
            ssButton.setText ("Start Listening");
            portInfo.setText (" Not Listening ");
            System.out.println("End of Connection");
            running = false;
            //remove all in the combo list
            String a = model.getElementAt(0).toString();//=>client list
            model.removeAllElements();
            model.addElement(a);
            //close the server
        }
    }


} // end class EchoServer4

class ConnectionThread extends Thread
{
    EchoServer4 gui;

    public ConnectionThread (EchoServer4 es3)
    {
        gui = es3;
        start();
    }

    public void run()
    {
        gui.serverContinue = true;

        try
        {
            gui.serverSocket = new ServerSocket(0);
            gui.portInfo.setText("Listening on Port: " + gui.serverSocket.getLocalPort());
            System.out.println ("Connection Socket Created");
            try {
                while (gui.serverContinue)
                {
                    System.out.println ("Waiting for Connection");
                    gui.ssButton.setText("Stop Listening");
                    new CommunicationThread (gui.serverSocket.accept(), gui, gui.outStreamList, gui.nameListVector,gui.removeClient,gui.nameForCheckingDuplicate);
                }
            }
            catch (IOException e)
            {
                System.err.println("Accept failed.");
                System.exit(1);
            }
        }
        catch (IOException e)
        {
            System.err.println("Could not listen on port: 10008.");
            System.exit(1);
        }
        finally
        {
            try {
                gui.serverSocket.close();
            }
            catch (IOException e)
            {
                System.err.println("Could not close port: 10008.");
                System.exit(1);
            }
        }
    }
}

//====================================================communication thread
class CommunicationThread extends Thread
{
    //private boolean serverContinue = true;
    private Socket clientSocket;
    private EchoServer4 gui;
    private Vector<PrintWriter> outStreamList;
    private Vector<String>nameList;
    private Map<PrintWriter, String> removeClientWhenExit;
    private int addRemoveFlag = 0;
    private Vector<String> nameCheckDuplicate;



    public CommunicationThread (Socket clientSoc, EchoServer4 ec3,
                                Vector<PrintWriter> oSL, Vector<String> names,
                                Map<PrintWriter,String> removeClient, Vector<String>nameCheck )
    {
        clientSocket = clientSoc;
        gui = ec3;
        outStreamList = oSL;
        nameList = names;
        removeClientWhenExit = removeClient;

        nameCheckDuplicate = nameCheck;
        //gui.history.insert ("Communicating with Port" + clientSocket.getLocalPort()+"\n", 0);

        start();
    }
    //-----------------------------------------------add remove functions
    public String addClients(){
        String sendMultipleNames = "addUserName:";
        for (String name : nameList) {
            sendMultipleNames += name;
            sendMultipleNames += "//:";
        }
        return sendMultipleNames;
    }
    public String removeClient(){
        String sendMultipleNames = "removeUserName:";
        for (String name : nameList) {
            sendMultipleNames += name;
            sendMultipleNames += "//:";
        }
        return sendMultipleNames;
    }
    //===================================================run threads
    public void run()
    {
        System.out.println ("New Communication Thread Started");
        try {

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),
                    true);

            outStreamList.add(out);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader( clientSocket.getInputStream()));

            String inputLine="";

            while ((inputLine = in.readLine()) != null)
            {
                System.out.println ("Server: " + inputLine);
                //server gui insert

                //check message type

                //----------------------------------------central server add a user
                if(inputLine.contains("addUserName:")){
                    //user name + public key
                    String userName_and_publicKey  = inputLine.substring(inputLine.indexOf(":")+1);
                    int getUserNameIndex = userName_and_publicKey.indexOf("(");
                    String userName = userName_and_publicKey.substring(0,getUserNameIndex);

                    //need another list for username
                    if(nameCheckDuplicate.contains(userName)){
                        //---------------check if the user name is unique or not
                        out.println("NameNotUniqueAlert:name is already used");
                    }
                    else {
                        gui.history.insert (inputLine+"\n", 0);
                        gui.model.addElement(userName_and_publicKey);
                        nameList.add(userName_and_publicKey);
                        removeClientWhenExit.put(out, userName_and_publicKey);
                        nameCheckDuplicate.add(userName);
                        //   System.out.println("out is what? " + out);
                        addRemoveFlag = 1;//=>update new client to all existing clients
                    }

                }
                //----------------------------------------central server remove a user
                if(inputLine.contains("removeUserName:")){
                    gui.history.insert (inputLine+"\n", 0);
                    String userName  = inputLine.substring(inputLine.indexOf(":")+1);
                    gui.model.removeElement(userName);//=>user name with public key
                    nameList.remove(userName);
                    int getUserNameIndex = userName.indexOf("(");
                    String removeuserName = userName.substring(0,getUserNameIndex);
                    nameCheckDuplicate.remove(removeuserName);


                    addRemoveFlag = 1;

                    //close the socket
                    outStreamList.remove(out);
                    out.close();
                    in.close();
                    clientSocket.close();

                }
                //========================================forward message to a specific client
                if(inputLine.contains("sendMessage:")){
                    gui.history.insert (inputLine+"\n", 0);
                    String content = inputLine.substring(inputLine.indexOf(":")+1);
                    String target_msg[] = content.split("//:");

                    //forward the encrypted message to the target client
                    String fromClient = target_msg[0];
                    String targetClient = target_msg[1];
                    int targetIndex = nameList.indexOf(targetClient);

                    inputLine = "getMessage:" + "From " + fromClient + ": " + target_msg[2]; //and from who
                    PrintWriter targetOut = outStreamList.get(targetIndex);
                    targetOut.println(inputLine);
                }
                //==============================================================================
                //central server Loop through the outStreamList and send to all "active" streams
                if(addRemoveFlag == 1) {
                    for (PrintWriter out1 : outStreamList) {
                        if (inputLine.contains("addUserName:")) {
                            inputLine = addClients();
                        }
                        if (inputLine.contains("removeUserName:")) {
                            inputLine = removeClient();
                        }
                        System.out.println("Sending Message");
                        out1.println(inputLine); //send Messages to clients
                    }
                    addRemoveFlag =0;
                }
                //======================================================send message to target client

                if (inputLine.equals("Bye."))
                    break;

                if (inputLine.equals("End Server."))
                    gui.serverContinue = false;
            }

            System.out.println("close the socket");
            outStreamList.remove(out);

            out.close();
            in.close();
            clientSocket.close();
        }

        catch (IOException e)
        {

            System.out.println("num of clients: " + outStreamList.size());
            System.err.println("Problem with Communication Server");
            //System.exit(1);
        }
        finally{

            try {
                clientSocket.close();
                System.out.println("close the socket at the end");

            }
            catch(IOException e){
                System.out.println("error on closing the clientSocket");

            }
        }
    }
}
