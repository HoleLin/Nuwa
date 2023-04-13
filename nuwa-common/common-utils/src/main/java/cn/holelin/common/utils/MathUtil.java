package cn.holelin.common.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @Description:
 * @Author: HoleLin
 * @CreateDate: 2022/10/20 10:58
 * @UpdateUser: HoleLin
 * @UpdateDate: 2022/10/20 10:58
 * @UpdateRemark: 修改内容
 * @Version: 1.0
 */
public class MathUtil {

    public static int fibonacci(int n) {
        if (n <= 1) {
            return n;
        } else {
            return fibonacci(n - 1) + fibonacci(n - 2);
        }
    }

    public static int factorial(int number) {
        int result = 1;
        for (int factor = 2; factor <= number; factor++) {
            result *= factor;
        }
        return result;
    }

    // Radius of sphere on which the points are, in this case Earth.
    private static final double SPHERE_RADIUS_IN_KM = 6372.8;

    public static double findHaversineDistance(double latA, double longA, double latB, double longB) {
        if (!isValidLatitude(latA)
                || !isValidLatitude(latB)
                || !isValidLongitude(longA)
                || !isValidLongitude(longB)) {
            throw new IllegalArgumentException();
        }

        // Calculate the latitude and longitude differences
        double latitudeDiff = Math.toRadians(latB - latA);
        double longitudeDiff = Math.toRadians(longB - longA);

        double latitudeA = Math.toRadians(latA);
        double latitudeB = Math.toRadians(latB);

        // Calculating the distance as per haversine formula
        double a = Math.pow(Math.sin(latitudeDiff / 2), 2)
                + Math.pow(Math.sin(longitudeDiff / 2), 2) * Math.cos(latitudeA) * Math.cos(latitudeB);
        double c = 2 * Math.asin(Math.sqrt(a));
        return SPHERE_RADIUS_IN_KM * c;
    }

    // Check for valid latitude value
    private static boolean isValidLatitude(double latitude) {
        return latitude >= -90 && latitude <= 90;
    }

    // Check for valid longitude value
    private static boolean isValidLongitude(double longitude) {
        return longitude >= -180 && longitude <= 180;
    }

    public static Integer[] performLottery(int numNumbers, int numbersToPick) {
        ArrayList<Integer> numbers = new ArrayList<Integer>();
        for (int i = 0; i < numNumbers; i++) {
            numbers.add(i + 1);
        }

        Collections.shuffle(numbers);
        return numbers.subList(0, numbersToPick).toArray(new Integer[numbersToPick]);
    }

    public static int calculateLuhnChecksum(long num) {
        if (num < 0) {
            throw new IllegalArgumentException("Non-negative numbers only.");
        }
        final String numStr = String.valueOf(num);

        int sum = 0;
        boolean isOddPosition = true;
        // We loop on digits in numStr from right to left.
        for (int i = numStr.length() - 1; i >= 0; i--) {
            final int digit = Integer.parseInt(Character.toString(numStr.charAt(i)));
            final int substituteDigit = (isOddPosition ? 2 : 1) * digit;

            final int tensPlaceDigit = substituteDigit / 10;
            final int onesPlaceDigit = substituteDigit % 10;
            sum += tensPlaceDigit + onesPlaceDigit;

            isOddPosition = !isOddPosition;
        }
        final int checksumDigit = (10 - (sum % 10)) % 10;
        // Outermost modulus handles edge case `num = 0`.
        return checksumDigit;
    }

    public static int gcd(int a, int b) {
        if (b == 0) {
            return a;
        }
        return gcd(b, a % b);
    }

    public static boolean isPrime(int number) {
        if (number < 3) {
            return true;
        }

        // check if n is a multiple of 2
        if (number % 2 == 0) {
            return false;
        }

        // if not, then just check the odds
        for (int i = 3; i * i <= number; i += 2) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }

    public static String toBinary(long naturalNumber) {
        if (naturalNumber < 0) {
            throw new NumberFormatException("Negative Integer, this snippet only accepts "
                    + "positive integers");
        }
        if (naturalNumber == 0) {
            return "0";
        }
        final Stack<Long> binaryBits =
                Stream.iterate(naturalNumber, n -> n > 0, n -> n / 2).map(n -> n % 2)
                        .collect(Stack::new, Stack::push, Stack::addAll);
        return Stream.generate(binaryBits::pop)
                .limit(binaryBits.size()).map(String::valueOf).collect(Collectors.joining());
    }

    public static Long fromBinary(String binary) {
        binary.chars().filter(c -> c != '0' && c != '1').findFirst().ifPresent(in -> {
            throw new NumberFormatException(
                    "Binary string contains values other than '0' and '1'");
        });
        return IntStream.range(0, binary.length())
                .filter(in -> binary.charAt(binary.length() - 1 - in) == '1')
                .mapToLong(in -> ((long) 0b1) << in).sum();
    }


}
