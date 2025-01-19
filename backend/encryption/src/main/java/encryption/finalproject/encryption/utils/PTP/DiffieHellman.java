package encryption.finalproject.encryption.utils.PTP;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DiffieHellman {

    // List of pre-generated large prime numbers (replace with actual prime numbers)
    private static final BigInteger[] PRE_GENERATED_PRIMES = new BigInteger[]{
        new BigInteger("104395303741223797938632238015220276426099436130893551404881159405665674204779576283790168146387233235447209643139391338401913441177407579960877716410813597157462609733248017463057667247380016300323225592749993154053267232478953905927057"),
        new BigInteger("157317040010223734438474561736038289739694755964471141862147724248672569272444984042501911299889785348205758343443702387701942552567353247693586298013194813631925591466365144022291525329234524496228166603284657596499379337699027982939905")
        // Add more primes as needed
    };

    public static void main(String[] args) {
        try {
            // Select a random prime number from the pre-generated list
            BigInteger p = getRandomPrime();
            BigInteger g = findPrimitiveRoot(p);

            System.out.println("Prime (p): " + p);
            System.out.println("Primitive Root (g): " + g);

            // Each peer generates their private and public keys
            BigInteger privateKey1 = generateSecureRandomKey();
            BigInteger publicKey1 = g.modPow(privateKey1, p);

            BigInteger privateKey2 = generateSecureRandomKey();
            BigInteger publicKey2 = g.modPow(privateKey2, p);

            System.out.println("Peer 1 Public Key: " + publicKey1);
            System.out.println("Peer 2 Public Key: " + publicKey2);

            // Exchange public keys and compute shared secrets
            BigInteger sharedSecret1 = publicKey2.modPow(privateKey1, p);
            BigInteger sharedSecret2 = publicKey1.modPow(privateKey2, p);

            System.out.println("Peer 1 Shared Secret: " + sharedSecret1);
            System.out.println("Peer 2 Shared Secret: " + sharedSecret2);

            if (sharedSecret1.equals(sharedSecret2)) {
                System.out.println("Diffie-Hellman Key Exchange Successful!");
            } else {
                System.out.println("Key Exchange Failed.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error");
        }
    }

    // Generate a secure random private key (simpler version)
    static BigInteger generateSecureRandomKey() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(512, random); // Use 512 bits for the private key
    }

    // Select a random prime number from the pre-generated list
    static BigInteger getRandomPrime() {
        Random rand = new Random();
        int index = rand.nextInt(PRE_GENERATED_PRIMES.length); // Random index
        return PRE_GENERATED_PRIMES[index];
    }

    // Find a primitive root modulo p using more efficient methods
    static BigInteger findPrimitiveRoot(BigInteger p) {
        BigInteger one = BigInteger.ONE;
        BigInteger two = BigInteger.TWO;
        BigInteger pMinusOne = p.subtract(one);

        // Check small prime numbers for efficiency
        for (BigInteger g = two; g.compareTo(p) < 0; g = g.add(one)) {
            if (isPrimitiveRoot(g, p, pMinusOne)) {
                return g;
            }
        }
        throw new IllegalStateException("Failed to find a primitive root for p.");
    }

    // Efficient check for primitive root
    public static boolean isPrimitiveRoot(BigInteger g, BigInteger p, BigInteger pMinusOne) {
        for (BigInteger factor : factorize(pMinusOne)) {
            if (g.modPow(pMinusOne.divide(factor), p).equals(BigInteger.ONE)) {
                return false;
            }
        }
        return true;
    }

    // Efficient factorization method for large numbers
    public static BigInteger[] factorize(BigInteger n) {
        List<BigInteger> factorsList = new ArrayList<>();
        BigInteger two = BigInteger.TWO;

        while (n.mod(two).equals(BigInteger.ZERO)) {
            factorsList.add(two);
            n = n.divide(two);
        }

        BigInteger factor = BigInteger.valueOf(3);
        while (factor.compareTo(n.sqrt()) <= 0) {
            while (n.mod(factor).equals(BigInteger.ZERO)) {
                factorsList.add(factor);
                n = n.divide(factor);
            }
            factor = factor.add(BigInteger.TWO);
        }

        if (n.compareTo(BigInteger.ONE) > 0) {
            factorsList.add(n);
        }

        return factorsList.toArray(new BigInteger[0]);
    }
}
