import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Formatter;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class Main {

    private static int computerMoveIndex;
    private static String computerMove;
    private static int userMoveIndex;
    private static String userMove;
    private static int indexOfMidElement;
    private static String hmac;
    private static String[] shiftedArray;

    public static void main(String[] args) {
        verifyInputParameters(args);

        //get index of middle element(it's equal to number of stronger and weaker moves)
        indexOfMidElement = args.length / 2;

        //generate key for HMAC
        byte[] key = generateKey();

        //computer makes move
        String move = getComputersMove(args);

        try {
            //generate HMAC for computer's move with already generated key
            hmac = generateHMAC(move, key);
            System.out.println("HMAC: " + hmac);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }

        //sout menu
        for (int i = 0; i < args.length; i++) {
            System.out.println((i + 1) + " - " + args[i]);
        }
        System.out.println("0 - Exit");
        System.out.print("Enter your move: ");

        //get user's move
        Scanner keyboard = new Scanner(System.in);
        int userInput = keyboard.nextInt();
        if (userInput != 0) {
            userMoveIndex = userInput - 1;
            userMove = args[userMoveIndex];
        } else {
            System.exit(0);
        }

        System.out.println("Your move: " + userMove);
        System.out.println("Computers move: " + move);

        //define winner
        //get number of shifts and direction of shift
        int shiftProperty = indexOfMidElement - userMoveIndex;
        if (shiftProperty != 0) {
            //put user move to middle of array
            int numberOfShifts = Math.abs(shiftProperty);

            if (shiftProperty > 0) {
                //right shifts
                for (int i = 0; i < numberOfShifts; i++) {
                    shiftedArray = rightShift(args);
                }
            } else {
                //left shifts
                for (int i = 0; i < numberOfShifts; i++) {
                    shiftedArray = leftShift(args);
                }
            }

            //get new pos of computer's move
            int newComputerMovePos = 0;
            for (int i = 0; i < shiftedArray.length; i++) {
                if (shiftedArray[i].equals(computerMove)) {
                    newComputerMovePos = i;
                }
            }

            if (indexOfMidElement < newComputerMovePos) {
                System.out.println("You loose :(");
                System.out.println("HMAC key: " + toHexString(key));
            } else if (indexOfMidElement == newComputerMovePos) {
                System.out.println("Draw XD");
                System.out.println("HMAC key: " + toHexString(key));
                System.exit(0);
            } else {
                System.out.println("You win :)");
                System.out.println("HMAC key: " + toHexString(key));
                System.exit(0);
            }
        } else {
            if (userMoveIndex == indexOfMidElement) {
                if (userMoveIndex < computerMoveIndex) {
                    System.out.println("You loose :(");
                    System.out.println("HMAC key: " + toHexString(key));
                    System.exit(0);
                } else if (userMoveIndex == computerMoveIndex) {

                } else {
                    System.out.println("You win :)");
                    System.out.println("HMAC key: " + toHexString(key));
                    System.exit(0);
                }
            }
        }
    }

    private static String[] leftShift(String[] arr) {
        String tmp = arr[0];
        for (int j = 0; j < arr.length - 1; j++) {
            arr[j] = arr[j + 1];
        }
        arr[arr.length - 1] = tmp;
        return arr;
    }

    private static String[] rightShift(String[] arr) {
        String tmp = arr[arr.length - 1];
        for (int i = arr.length - 2; i >= 0; i--) {
            arr[i + 1] = arr[i];
        }
        arr[0] = tmp;
        return arr;
    }

    private static void verifyInputParameters(String[] params) {

        if (params.length == 1) {
            System.out.println("You have entered only one parameter, required number of parameters: >=3");
            System.exit(0);
        }

        if (params.length == 0) {
            System.out.println("You haven't pass any parameter, required number of parameters: >=3");
            System.exit(0);
        }

        if ((params.length) % 2 == 0) {
            System.out.println("You have entered even number of parameters!!! Only odd number of parameters allowed)");
            System.exit(0);
        }

        Set<String> mySet = new HashSet<String>();
        for (int i = 0; i < params.length; i++) {
            if (!mySet.add(params[i])) {
                System.out.println("Duplicate found: " + params[i]);
                System.exit(0);
            }
        }
    }

    private static byte[] generateKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[16]; // 128 bits are converted to 16 bytes;
        random.nextBytes(bytes);
        return bytes;
    }

    private static String generateHMAC(String message, byte[] key) throws NoSuchAlgorithmException, InvalidKeyException {
        String HMAC_SHA512 = "HmacSHA512";
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, HMAC_SHA512);
        Mac mac = Mac.getInstance(HMAC_SHA512);
        mac.init(secretKeySpec);
        return toHexString(mac.doFinal(message.getBytes()));
    }

    private static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }

    private static String getComputersMove(String[] moves) {
        computerMoveIndex = ThreadLocalRandom.current().nextInt(moves.length);
        computerMove = moves[computerMoveIndex];
        return computerMove;
    }

}
