

/*
RSA algorithm in cryptography
the public key consists of 2 numbers
1: multiplication of 2 prime numbers: max 28 bits (int 2^27)for this project
2: an int, not  a factor of n
 */

import java.util.HashMap;
import java.util.Random;

import static java.lang.Math.abs;
import static java.lang.Math.random;

public class RSA {
    //generate public key
    HashMap<Integer, Integer>primeMap = new HashMap<>();
    Random rand = new Random();
    int maxPrime;
    int mapSize;
    int p = 0;
    int q = 0;
    int n = 0;
    int e = 0;
    int phi = 0;
    int d =0;
    int k=0;


    public RSA(int maxPrime){
        this.maxPrime = maxPrime;
        storePrimes();
        mapSize = primeMap.size();
    }

    public static void main(String[] args) {
        RSA public_private_keys = new RSA(1000);
       // public_private_keys.printPrimes();
        public_private_keys.generatePublicKey();
    }

    public void getPrimeNumber(){
        // n  has to be larger than 127
       //from the stored prime numbers, get 2 prime numbers randomly
       //
        //mapsize => max exclusive
        n = 0;
        while( n <= 127) {
            int randomKey = rand.nextInt(mapSize);
            int randomKey2 = rand.nextInt(mapSize);

            p = primeMap.get(randomKey);
            q = primeMap.get(randomKey2);
            //n is the part 1 of public key
            System.out.println("p is "+ p);
            System.out.println("q is " + q);
            n = p * q;
            System.out.println("n is: " + n);
        }
        //second number e: must be integer, not a factor of n, and


    }
    public void getPublicKeyE(){
        //Random rand = new Random();
        /*Calculate/Select e
        must be an integer
        must be "relatively-prime" with phi
        realatively_prime: if the only positive integer (factor) that divides both of them is 1*/

        //1 < e< (prime1 -1)*(prime2-1)
        phi = (p-1)*(q-1);
        System.out.println("phi is: "+ phi);
        //lower bound inclusive
        //upper bound exclusive
        //must be in range 1 < e < phi
       // e  = rand.nextInt(phi)+2;
        //choose a prime number, then
        //if( phi % e != 0) => e is valid
        int randomEKey = rand.nextInt(mapSize);


        int val = primeMap.get(randomEKey);

        while(val >  phi || (val ==p) || (val ==q)){
            randomEKey = rand.nextInt(mapSize);

            val = primeMap.get(randomEKey);
            System.out.println("val for random e is: " + val);

        }
        e = val;
        System.out.println("e is " + e);
    }
    //=================================================prime numbers
    public void storePrimes(){
        //maxPrime has to be positive
        maxPrime = abs(maxPrime);
        int key = 0;
        for(int i = 2; i< maxPrime; i++){
            if( i == 2 || i == 3 || i == 5 || i==7){
                primeMap.put(key,i);
                key++;
                continue;
            }
            if(i %2 == 0 || i%3 ==0 || i %5 == 0 || i%7 == 0){
                //not a prime number, then go to next round
                continue;
            }
            if(isPrime(i)){
                primeMap.put(key, i);
                key++;
            }
        }
    }
    public void printPrimes(){
        int numsPerLine = 0;
        for(Integer key : primeMap.keySet()){
            System.out.print(primeMap.get(key) + " ");
            numsPerLine++;
            if(numsPerLine % 10 == 0){
                System.out.println();
            }
        }
    }
    public boolean isPrime(int num){
        for(int i = 2;i<num;i++){
            if(num % i == 0){
               return false;
            }
        }
        return true;
    }
    //============================================public key
    public void generatePublicKey(){
        getPrimeNumber();
        getPublicKeyE();
        //getD_and_k();
    }
    //==============================================private key
    public void generatePrivateKey(){
        getD_and_k();

    }
    public void getD_and_k(){
      /*Determine/Select dm
                d = (k*phi + 1) / e for some integer k
        where e must divide (k*phi + 1) evenly
                ((k*phi + 1) must be a multiple of e)*/
        k = rand.nextInt(phi);

        int count =0;
        while( (k*phi+1)% e != 0){
            k = rand.nextInt(phi);
            count +=1;
        }
        System.out.println("k is: "+k);
        d = (k*phi+1)/ e;
        System.out.println("count is: "+ count);
        System.out.println("d is " + d);


    }
    //================================================Encrypt
    public void encryptMessage(String Message){
        //Encrypt Message M :   M^e % n  ===> M'
    }
    public void decryptMessage(String Message){
        //Decrypt M'        :   M'^d % n ===> M
    }



}
