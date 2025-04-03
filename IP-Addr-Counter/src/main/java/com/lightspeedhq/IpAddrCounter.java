package com.lightspeedhq;

import com.lightspeedhq.util.IpAddrCounterUtil;

import java.io.FileReader;
import java.io.IOException;

public class IpAddrCounter {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java -jar IpAddrCounter.jar [filename]");
            return;
        }

        try (FileReader fileReader = new FileReader(args[0])) {
            System.out.println(IpAddrCounterUtil.countIpAddressesFromReader(fileReader));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
