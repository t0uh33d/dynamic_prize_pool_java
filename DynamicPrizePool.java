import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class DynamicPrizePool {
    public static void main(String[] args) {
        double B = 1500; // prize pool
        double N = 10; // Number of participants
        double E = 100; // entry fee
        double p1 = 225; // player 1 prize usually 15% of the total prize pool
        // B = 100000;
        // N = 10;
        // E = 500;
        // p1 = 15000;
        double up_lim = getUpperLimit(B, N, E, p1);
        double alpha = binarySearch(B, N, E, p1, 0, up_lim);
        double[] afterLeftOverDistribution = generatePrizeList(B, N, E, p1, alpha);
        int count = 0;
        for (int i = 0; i < afterLeftOverDistribution.length; i++) {
            System.out.println(afterLeftOverDistribution[i]);
            count += (int) afterLeftOverDistribution[i];
        }
        System.out.printf("Total %d\n", count);
        System.out.printf("left over : %d\n", ((int) B - count));
        Map<Double, Integer> bucketCount = bucketCount(afterLeftOverDistribution);
        bucketCount.forEach((k, v) -> System.out.printf("%f : %d\n", k, v));
    }

    public static Map<Double, Integer> bucketCount(double[] afterLeftOverDistribution) {
        Map<Double, Integer> res = new TreeMap<Double, Integer>(Collections.reverseOrder());
        for (int i = 0; i < afterLeftOverDistribution.length; i++) {
            res.merge(afterLeftOverDistribution[i], 1, Integer::sum);
        }
        return res;
    }

    public static double getUpperLimit(double B, double N, double E, double p1) {
        double _x = (B - N * E) / (p1 - E);
        double up_lim = 1 - (Math.log(_x) / Math.log(N));
        while (seeError(B, N, E, p1, 0) * seeError(B, N, E, p1, up_lim) > 0)
            up_lim++;
        return up_lim;
    }

    public static double binarySearch(double B, double N, double E, double p1, double a, double b) {
        double c = (a + b) / 2.0;
        while (Math.abs(seeError(B, N, E, p1, c)) > 0.01) {
            if (seeError(B, N, E, p1, a) * seeError(B, N, E, p1, c) < 0)
                b = c;
            else
                a = c;
            c = (a + b) / 2.0;
        }
        return c;
    }

    public static double seeError(double B, double N, double E, double p1, double alpha) {
        double lhs = B - N * E;
        double rhs_1 = p1 - E;
        double rhs_2 = 0;
        for (int i = 1; i <= N; i++) {
            rhs_2 += 1 / Math.pow(i, alpha);
        }
        return lhs - (rhs_1 * rhs_2);
    }

    public static double[] generatePrizeList(double B, double N, double E, double p1, double alpha) {
        double[] prizeList = new double[(int) N];
        double[] niceList = new double[(int) N];
        double count = 0;
        double niceCount = 0;
        for (int i = 1; i <= N; i++) {
            prizeList[i - 1] = E + ((p1 - E) / Math.pow(i, alpha));
            niceList[i - 1] = niceNumber(prizeList[i - 1]);
            count += prizeList[i - 1];
            niceCount += niceList[i - 1];
        }

        System.out.println("Prize list : ");
        for (int i = 1; i <= N; i++)
            System.out.println(prizeList[i - 1]);
        System.out.printf("Total prize = %f\n", count);
        System.out.println("Nice Prize list : ");
        for (int i = 1; i <= N; i++)
            System.out.println(niceList[i - 1]);
        System.out.printf("Total prize = %f\n", niceCount);
        int leftOver = (int) B - (int) niceCount;
        System.out.printf("\n left over : %d\n", leftOver);
        distributeLeftOver(niceList, leftOver, 1, p1, 1);
        return niceList;
    }

    public static double niceNumber(double n) {
        if (n >= 5000) {
            return n - n % 2500;
        } else if (n >= 500) {
            return n - n % 500;
        } else if (n >= 250) {
            return n - n % 50;
        } else if (n >= 100) {
            return n - n % 25;
        } else {
            return n - n % 5;
        }
    }

    public static void distributeLeftOver(double[] niceList, int leftOver, int i, double p1, int distributionCount) {
        if (i == niceList.length && leftOver >= 0 && distributionCount <= 3)
            distributeLeftOver(niceList, leftOver, 1, p1, distributionCount + 1);
        if (i == niceList.length)
            return;
        if (leftOver <= 0)
            return;
        int prev = leftOver;
        while (niceNumber(niceList[i] + leftOver) >= p1)
            leftOver = leftOver - 100;
        int prev_i = (int) niceList[i];
        niceList[i] = niceNumber(niceList[i] + leftOver);
        int _ll = prev - ((int) niceList[i] - prev_i);
        distributeLeftOver(niceList, _ll, i + 1, p1, distributionCount + 1);
    }
}
