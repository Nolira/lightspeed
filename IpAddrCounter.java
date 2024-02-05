import java.io.*;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

public class IpAddrCounter {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("File not specified");
            return;
        }

        BitSet firstHalf = new BitSet();
        BitSet secondHalf = new BitSet();
        long counter = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(args[0]))) {
            String str;

            while ((str = reader.readLine()) != null) {
                List<Integer> ipParts = Arrays.stream(str.split("\\."))
                        .map(Integer::valueOf)
                        .toList();

                int ip0 = ipParts.get(0);
                int ip1 = ipParts.get(1);
                int ip2 = ipParts.get(2);
                int ip3 = ipParts.get(3);

                if (ip0 < 128) {
                    int index = countIpNumber(ip0, ip1, ip2, ip3);
                    if (!firstHalf.get(index)) {
                        firstHalf.set(index);
                        counter++;
                    }
                } else {
                    ip0 = ip0 - 128;
                    int index = countIpNumber(ip0, ip1, ip2, ip3);
                    if (!secondHalf.get(index)) {
                        secondHalf.set(index);
                        counter++;
                    }
                }
            }
            System.out.println(counter);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private static int countIpNumber(int ip0, int ip1, int ip2, int ip3) {
        return (ip0 << 24) + (ip1 << 16) + (ip2 << 8) + ip3;
    }
}