<<<<<<< HEAD
//import com.sun.org.apache.xpath.internal.SourceTree;

=======
>>>>>>> 57d551555aead255761220bb05d01e184ea1cc37
import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;
import javax.swing.*;


/*
name of each client must be unique
 */
public class EchoClient extends JFrame implements ActionListener
{
    // GUI items
    JButton sendButton;
    JButton connectButton;
    JTextField machineInfo;
    JTextField portInfo;
    JTextField message;
    JTextArea history;


    JButton enterPrime;
    JButton generatePrime;

    JTextField userInputP;
    JTextField userInputq;

    int blockingSize = 2;

    JTextField name;
    int name_length_restricted_to = 12;
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

    //RSA encrypt and decrypt
    primeNumbers PrimeClass = new primeNumbers();
    //public key (n ,e)
    int n =0;
    int e =0;
    //private key(d, n)
    long d =0;
    Random rand = new Random();




    // set up GUIp
    public EchoClient()
    {
        super( "Echo Client" );
        // get content pane and set its layout
        Container container = getContentPane();
        container.setLayout (new BorderLayout ());

        // set up the North panel
        upperPanel = new JPanel ();
        upperPanel.setLayout (new GridLayout (10,2));
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


        machineInfo = new JTextField ("127.0.0.1");

        portInfo = new JTextField ("");



        //prime numbers
        enterPrime = new JButton("Enter Your Primes");
        enterPrime.addActionListener(this);
        generatePrime = new JButton("or Generate Primes Randomly");
        generatePrime.addActionListener(this);


        userInputP = new JTextField("p");
        userInputq = new JTextField("q");


        name = new JTextField("Name");

        connectButton = new JButton( "Connect to Server" );
        connectButton.addActionListener( this );
        connectButton.setEnabled(false);

        message = new JTextField ("");
        message.addActionListener( this );


        sendButton = new JButton( "Send Message" );
        sendButton.addActionListener( this );
        sendButton.setEnabled (false);


        listOfClient = new JComboBox(comboModel);

        //---------------------------port
        upperPanel.add ( new JLabel ("Server Address: ", JLabel.RIGHT) );
        upperPanel.add( machineInfo );
        upperPanel.add ( new JLabel ("Server Port: ", JLabel.RIGHT) );
        upperPanel.add( portInfo );
        //---------------------------primes
        upperPanel.add(enterPrime);
        upperPanel.add(generatePrime);
        upperPanel.add(userInputP);
        upperPanel.add(userInputq);


        upperPanel.add(name);
        upperPanel.add( connectButton );
        upperPanel.add ( new JLabel ("Message: ", JLabel.RIGHT) );
        upperPanel.add( message );
        upperPanel.add( sendButton );
        upperPanel.add(listOfClient);



    }

    public static void main( String args[] )
    {
        EchoClient application = new EchoClient();
        //when close, remove the client list
        //application.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        application.setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
        application.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        application.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (JOptionPane.showConfirmDialog(application,
                        "Are you sure to close this window?", "Confirmation",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
                    //remove the client when exit
                    String userName = application.name.getText();
                    System.out.println(userName);

                    //send out the disconnect user name to the server
                    if(application.connected == true) {
                        application.out.println("removeUserName:" + userName);
                        application.out.close();

                        try {
                            application.in.close();
                        } catch (IOException e) {
                            System.out.println("error on closing in");
                        }
                        try {
                            application.echoSocket.close();
                        } catch (IOException e2) {
                            System.out.println("error on closing down the socket");
                        }
                    }
                    System.exit(0);
                }
            }
        });
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
        else if(event.getSource() == enterPrime){
            enterPrimeFunction();
        }
        else if(event.getSource() == generatePrime){
            generatePrimeFunction();

        }
    }
    //===============================================================enter prime numbers action
    public void enterPrimeFunction(){
        generatePrime.setEnabled(false);
        //get the value of q and p
        //check if they are prime, then check if they are greater than blocking size
        String p = userInputP.getText().toString();
        String q = userInputq.getText().toString();
        int checkP = 0;
        int checkQ = 0;
        int numberFlag = 0;
        try {
            checkP = Integer.parseInt(p);
            checkQ = Integer.parseInt(q);
            numberFlag = 1;

        }
        catch(NumberFormatException e){
            JOptionPane.showMessageDialog(null,"Please enter Prime Numbers for p and q");
        }
        if(numberFlag == 1) {
            if (PrimeClass.isPrime(checkP) && PrimeClass.isPrime(checkQ)) {
                //check if their p*q is greater than 128^blocking size
                int enteredN = PrimeClass.getN(checkP, checkQ);
                if(enteredN <= Math.pow(128, blockingSize)){
                    JOptionPane.showMessageDialog(null,"Please enter larger Prime Numbers");
                }
                else{
                    System.out.println("value for n: " + enteredN);
                    //now get the public key and priate
                    n = enteredN;
                    getPublicKey_getPrivateKey();
                    connectButton.setEnabled(true);
                }
            }
            else{
                JOptionPane.showMessageDialog(null,"Please enter Prime Numbers");
            }
        }
    }
    //=====================================================get public key and private key
    public void getPublicKey_getPrivateKey(){
        PrimeClass.getPublicKey();
        PrimeClass.generatePrivateKey();
        e = PrimeClass.returnE();
        d = PrimeClass.returnD();
        System.out.println("e is: " + e);
        System.out.println("d is: " + d);
    }
    //====================================================generate Prime number from a file
    public HashMap<Integer, String> storePrimeFromFile(){
        File file = new File("file.txt");
        HashMap<Integer, String> chooseNumberFromFileMap = new HashMap<>();
        int mapKey = 0;
        String fromFile = "";
        BufferedReader readFromFile = null;
        try{
            readFromFile = new BufferedReader(new FileReader(file));
            String text = "";
            while( (text = readFromFile.readLine())!= null){
                chooseNumberFromFileMap.put(mapKey, text);
                mapKey+=1;
            }
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            try{
                if(readFromFile != null){
                    readFromFile.close();
                }
            }catch(IOException e){
                System.out.println("Failed to close the file");
            }
        }
        return chooseNumberFromFileMap;
    }
    //====================================================================select prime # from file
    public int selectAPrimeNumberFromMap(HashMap<Integer, String> map){
        int mapSizeFromFile = map.size();
        int mapKey = rand.nextInt(mapSizeFromFile);
        String pFromFile =  map.get(mapKey);
        int prime_from_file = Integer.parseInt(pFromFile);
        return prime_from_file;
    }
    //-----------------------------------------------------generate prime action
    public void generatePrimeFunction(){
        enterPrime.setEnabled(false);
        userInputP.setEnabled(false);
        userInputq.setEnabled(false);
        int valid_flag = 1;

        //select 2 prime number from a file
        HashMap<Integer, String > primeMap = storePrimeFromFile();
        int mapSizeFromFile = primeMap.size();

        int p_from_file = selectAPrimeNumberFromMap(primeMap);
        int q_from_file = selectAPrimeNumberFromMap(primeMap);

        //check if these 2 values are larger than 128^blockingsize
        n = PrimeClass.getN(p_from_file, q_from_file);
        int check_if_the_file_contains_valid_numbers = 0;
        while(n <= Math.pow(128, blockingSize)){
            //select 2 prime numbers from map randomly
            p_from_file = selectAPrimeNumberFromMap(primeMap);
            q_from_file = selectAPrimeNumberFromMap(primeMap);
            n = PrimeClass.getN(p_from_file, q_from_file);
            check_if_the_file_contains_valid_numbers+=1;

            if(check_if_the_file_contains_valid_numbers == (Math.pow(mapSizeFromFile, mapSizeFromFile))){
                System.out.println("Please use larger prime numbers");
                valid_flag = 0;
                break;
            }
        }
        // get public key n,e  private key d, n
        if(valid_flag == 1){
            getPublicKey_getPrivateKey();
        }
        System.out.println("p from file is: " + p_from_file);
        System.out.println("q from file is: " + q_from_file);
        System.out.println("n is: " + n);

        generatePrime.setEnabled(false);
        connectButton.setEnabled(true);


    }
    //========================================================================send message
    public void doSendMessage()
    {
        try
        {

            String sendMessageToClient = "sendMessage:";
            String fromWho = name.getText();
            String targetClient = comboModel.getSelectedItem().toString();
            String getMessage = message.getText();

            sendMessageToClient = sendMessageToClient + fromWho + "//:" + targetClient + "//:" + getMessage;

            out.println(sendMessageToClient);
            //history.insert ("From Server: " + in.readLine() + "\n" , 0);

        }
        catch (Exception e)
        {
            history.insert ("Error in processing message ", 0);
        }
    }
    //==========================================================================make connection
    public void doManageConnection()
    {
        if (connected == false)
        {
            String machineName = null;
            int portNum = -1;
            try {

                //send the name out to the server
                String userName = name.getText();
                if(userName.length() > name_length_restricted_to)
                {
                    JOptionPane.showMessageDialog(null, "Please enter a shorter name");
                }
                else{
                    machineName = machineInfo.getText();
                    portNum = Integer.parseInt(portInfo.getText());
                    echoSocket = new Socket(machineName, portNum );
                    out = new PrintWriter(echoSocket.getOutputStream(), true);
                    in = new BufferedReader(new InputStreamReader(
                            echoSocket.getInputStream()));


                    System.out.println(userName);
                    //send out the new user name to the server
                    //AND Public key
                    //todo
                    out.println("addUserName:" + userName);

                    //generate public and private key
                   /* PrimeClass.getPublicKey();
                    PrimeClass.generatePrivateKey();*/
                    /*System.out.println("d is: " + PrimeClass.returnD());
                    System.out.println("e is: " + PrimeClass.returnE());
                    System.out.println("n is: " + PrimeClass.returnN());*/

                    // start a new thread to read from the socket
                    new CommunicationReadThread(in, this);

                    sendButton.setEnabled(true);
                    connected = true;
                    connectButton.setText("Disconnect from Server");
                    //set username not editable
                    name.setEnabled(false);
                }

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


                if(inputLine.contains("NameNotUniqueAlert:")){
                    String content = inputLine.substring(inputLine.indexOf(":") + 1);
                    gui.history.insert("Please choose another name: "+ content+"\n",0);
                    gui.sendButton.setEnabled(false);
                    gui.connectButton.setText("C");

                    gui.echoSocket.close();
                    gui.connected = false;
                    gui.connectButton.setText("Connect to Server");
                    gui.name.setEnabled(true);

                    break;
                }

                if (inputLine.equals("Bye."))
                    break;

            }
            System.out.println("in closed?");
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
