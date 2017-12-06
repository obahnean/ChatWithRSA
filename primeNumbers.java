/*
 * CS342_PROJECT5: Networked Chat with RSA Encryption/Decryption
 * 
 * Muna Bist - mbist3
 * Queena Zhang - qzhang85
 * Ovidiu Bahnean - obahne2 
 */
import java.util.Random;

public class primeNumbers {
    private int n = 0;
    private int p = 0;
    private int q = 0;
    private int phi = 0;
    private int e = 0;
    Random rand = new Random();
    private int k = 0;
    private double d = 0 ;

    public primeNumbers(){

    }

    public boolean isPrime(int num) {
        for (int i = 2; i < num; i++) {
            if (num % i == 0) {
                return false;
            }
        }
        return true;
    }

    public int getN(int inputP, int inputQ){
        p = inputP;
        q = inputQ;
        n = p*q;
        return n;
    }
    public int getPhi(){
        phi = (p-1)*(q-1);
        return phi;
    }
    public int getE(){
        /*must be an integer
        not a factor of phi
        must be "relatively-prime" with phi
        realatively_prime: if the only positive integer (factor) that divides both of them is 1*/
        //1 < e< (prime1 -1)*(prime2-1)
        //if( phi % e != 0) => e is valid
        //lower bound inclusive
        //upper bound exclusive
        //if e is too small, then the value for d will be too big,
        for(int i =2;i<phi;i++){
            if( (gcd(i,phi) == 1)){
                e = i;
                break;
            }
        }
        return e;
    }
    //====================================
    //used gcd function from: http://www.geeksforgeeks.org/rsa-algorithm-cryptography/
    int gcd(int a, int h)
    {
        int temp;
        while (true)
        {
            temp = a%h;
            if (temp == 0)
                return h;
            a = h;
            h = temp;
        }
    }
    //===================================
    public void getPublicKey(){
        //public key is n and e
        getPhi();//=>for generating e
        getE();
    }
    //=====================================private key
    public void generatePrivateKey(){
        getD_and_k();

    }
    public void getD_and_k(){
      /*Determine/Select dm
                d = (k*phi + 1) / e for some integer k
        where e must divide (k*phi + 1) evenly
                ((k*phi + 1) must be a multiple of e)*/

        for(int i=2;i<phi;i++){
            if((i*(double)phi+1)%e == 0){
                k = i;
                break;
            }
        }
       // System.out.println("count is: "+ count);
        double kandphi = (double)k*(double)phi +1;
        d = (kandphi)/ (double)e;
        System.out.println("k is: "+k);
        //System.out.println("phi is: " + phi);
       // System.out.println("k and phi is: " + kandphi);
        System.out.println("e is: " + e);
        System.out.println("d is " + (int)d);
    }
    //--------------------need to call generate public and private key first
    public int returnE(){
        return e;
    }
    public int returnN(){
        return n;
    }
    public int returnD(){
        return (int)d;
    }
    //=================================================


}
