package com.lightspeedhq.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

/**
 * This utility class provides functionality to count IP addresses from a given reader.
 */
public final class IpAddrCounterUtil {

    /**
     * Counts the number of valid IPv4 addresses from the provided reader.
     *
     * @param reader The reader providing lines containing IP addresses.
     * @return The total number of valid IP addresses found in the reader.
     * @throws IOException If an I/O error occurs while reading from the reader.
     */
    public static long countIpAddressesFromReader(Reader reader) throws IOException {
        try (BufferedReader br = new BufferedReader(reader)) {

            BitSet firstHalf = new BitSet();
            BitSet secondHalf = new BitSet();

            long counter = 0;

            String str;

            while ((str = br.readLine()) != null) {
                List<Integer> ipParts;
                try {
                    ipParts = Arrays.stream(str.split("\\."))
                            .map(Integer::valueOf)
                            .filter(n -> n > -1 && n < 256)
                            .toList();
                } catch (NumberFormatException e) {
                    continue;
                }

                if (ipParts.size() != 4) continue;

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
            return counter;
        } catch (IOException e) {
            throw e;
        }
    }

    private static int countIpNumber(int ip0, int ip1, int ip2, int ip3) {
        return (ip0 << 24) + (ip1 << 16) + (ip2 << 8) + ip3;
    }
}
