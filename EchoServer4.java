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

    //If multiple threads access a hash map concurrently,
    // and at least one of the threads modifies the map structurally,
    // it must be synchronized externally.
    Map<PrintWriter,String> removeClient = Collections.synchronizedMap(new HashMap<>());


    DefaultListModel model = new DefaultListModel();
    JList list;

    // set up GUI
    public EchoServer4()
    {
        super( "Echo Server" );


        fileMenu = new JMenu("File");


        // set up the shared outStreamList
        outStreamList = new Vector<PrintWriter>();
        nameListVector = new Vector<String>();

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


        //container.add(menuBar);

        setSize( 500, 450 );
        setVisible( true );

    } // end CountDown constructor

    public static void main( String args[] )
    {
        EchoServer4 application = new EchoServer4();
        application.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }

    private void setMenu(){

    }

    // handle button event
    public void doButton( ActionEvent event )
    {
        if (running == false)
        {
            new ConnectionThread (this);
        }
        else
        {
            serverContinue = false;
            ssButton.setText ("Start Listening");
            portInfo.setText (" Not Listening ");
            System.out.println("End of Connection");
            running = false;
            //remove all in the combo list
            model.removeAllElements();
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
                    new CommunicationThread (gui.serverSocket.accept(), gui, gui.outStreamList, gui.nameListVector,gui.removeClient);
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
    private int numOfClient =0;
    private int addRemoveFlag = 0;



    public CommunicationThread (Socket clientSoc, EchoServer4 ec3,
                                Vector<PrintWriter> oSL, Vector<String> names, Map<PrintWriter,String> removeClient )
    {
        clientSocket = clientSoc;
        gui = ec3;
        outStreamList = oSL;
        nameList = names;
        removeClientWhenExit = removeClient;

        gui.history.insert ("Comminucating with Port" + clientSocket.getLocalPort()+"\n", 0);

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
                gui.history.insert (inputLine+"\n", 0);
                //check message type

                //----------------------------------------central server add a user
                if(inputLine.contains("addUserName:")){
                    String userName  = inputLine.substring(inputLine.indexOf(":")+1);
                    gui.model.addElement(userName);
                    nameList.add(userName);
                    removeClientWhenExit.put(out, userName);
                 //   System.out.println("out is what? " + out);
                    addRemoveFlag = 1;

                }
                //----------------------------------------central server remove a user
                if(inputLine.contains("removeUserName:")){
                    String userName  = inputLine.substring(inputLine.indexOf(":")+1);
                    gui.model.removeElement(userName);
                    nameList.remove(userName);
                    addRemoveFlag = 1;

                    //close the socket
                    outStreamList.remove(out);
                    out.close();
                    in.close();
                    clientSocket.close();

                }
                //========================================forward message to a specific client
                if(inputLine.contains("sendMessage:")){
                    String content = inputLine.substring(inputLine.indexOf(":")+1);
                    String target_msg[] = content.split("//:");
                    String targetClient = target_msg[0];
                    int targetIndex = nameList.indexOf(targetClient);
                    System.out.println(targetIndex);

                    inputLine = "getMessage:"; //and from who

                    inputLine+= target_msg[1];
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

            //when client exit
           // System.out.println("user click X to exit");
           /* String removeNameAfterUserClickExit = gui.removeClient.get(out);
            nameList.remove(removeNameAfterUserClickExit);
            gui.model.removeElement(removeNameAfterUserClickExit);*/

            //System.out.println("close the socket");
            outStreamList.remove(out);
           // gui.outStreamList.remove(out);

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
            System.out.println("close the socket");
            try {
                clientSocket.close();
            }
            catch(IOException e){
                System.out.println("error on closing the clientSocket");

            }
        }
    }
}
