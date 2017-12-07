/*
 * CS342_PROJECT5: Networked Chat with RSA Encryption/Decryption
 * 
 * Muna Bist - mbist3
 * Queena Zhang - qzhang85
 * Ovidiu Bahnean - obahne2 
 */
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class RSA{
    private BigInteger p;
    private BigInteger q;
    private BigInteger n;
    private BigInteger phi;
    private BigInteger e;
    private BigInteger d;
    private Random r;

    public RSA(){

    }

    public void getPublicPrivateKey(BigInteger inputP, BigInteger inputQ){
        p = inputP;
        q = inputQ;
        n = p.multiply(q);
        phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        getE();
        d = e.modInverse(phi);
    }

    public int check_n_is_large_than_blocking_pack(int a, int b){
        return a*b;

    }
    /*e:
    not a factor of phi
    must be "relatively-prime" with phi
    realatively_prime: if the only positive integer (factor) that divides both of them is 1*/
    //1 < e< (prime1 -1)*(prime2-1)*/
    public void getE(){
        e = BigInteger.valueOf(2);
        while(e.compareTo(phi) < 0){
            if(phi.gcd(e).compareTo(BigInteger.ONE) == 0){
                break;
            }
            e = e.add(BigInteger.ONE);
        }
    }
    public BigInteger returnE(){
        return e;
    }
    public boolean isPrime(int num) {
        for (int i = 2; i < num; i++) {
            if (num % i == 0) {
                return false;
            }
        }
        return true;
    }

    // Encrypt message
    public byte[] encrypt(byte[] message, BigInteger targetE, BigInteger targetN)
    {
       // System.out.println("when encrypt e: " + targetE);
       // System.out.println("when target encrypt n: " + targetN);
        return (new BigInteger(message)).modPow(targetE, targetN).toByteArray();
    }

    // Decrypt message
    public byte[] decrypt(byte[] message)
    {
      //  System.out.println("when decryt d is:" + d);
        //System.out.println("when decrpt n is:" + n);
        return (new BigInteger(message)).modPow(d, n).toByteArray();
    }
    //for checking purpose
    public String bytesToString(byte[] encrypted)
    {
        String message = "";
        for (byte b : encrypted)
        {
            message += Byte.toString(b);
        }
        return message;
    }

}
