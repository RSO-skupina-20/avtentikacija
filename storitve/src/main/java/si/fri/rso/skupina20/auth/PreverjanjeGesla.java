package si.fri.rso.skupina20.auth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;



public class PreverjanjeGesla {

    // Funkcija za generiranje soli
    public static String generirajSol(){
        SecureRandom secureRandom = new SecureRandom();
        byte[] sol = new byte[16];
        secureRandom.nextBytes(sol);
        return Base64.getEncoder().encodeToString(sol);
    }

    // Funkcija za generiranje hash-a
    public static String generirajHash(String geslo, String sol) throws NoSuchAlgorithmException{
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
        messageDigest.update(sol.getBytes());
        byte[] hash = messageDigest.digest(geslo.getBytes());

        return Base64.getEncoder().encodeToString(hash);
    }

    // Funkcija za preverjanje gesla
    public static boolean preveriGeslo(String geslo, String sol, String hash) throws NoSuchAlgorithmException{
        String novHash = generirajHash(geslo, sol);
        return novHash.equals(hash);
    }
}
