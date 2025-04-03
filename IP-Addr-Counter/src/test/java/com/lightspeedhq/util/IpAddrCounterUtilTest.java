package com.lightspeedhq.util;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class IpAddrCounterUtilTest {

    @Test
    void shouldReturnZeroForEmptyInput() throws IOException {
        StringReader stringReader = new StringReader("");
        assertEquals(0, IpAddrCounterUtil.countIpAddressesFromReader(stringReader));
    }

    @Test
    void shouldReturnTenForTenDifferentAddressesFromInput() throws IOException {
        StringReader stringReader = new StringReader("""
                97.71.174.4
                97.71.173.241
                97.71.173.235
                161.71.174.27
                215.10.61.107
                161.71.174.29
                227.215.10.99
                161.71.174.30
                215.10.61.63
                161.71.174.31
                """);
        assertEquals(10, IpAddrCounterUtil.countIpAddressesFromReader(stringReader));
    }

    @Test
    void shouldReturnOneForTenIdenticalAddressesFromInput() throws IOException {
        StringReader stringReader = new StringReader("""
                97.71.174.4
                97.71.174.4
                97.71.174.4
                97.71.174.4
                97.71.174.4
                97.71.174.4
                97.71.174.4
                97.71.174.4
                97.71.174.4
                97.71.174.4
                """);
        assertEquals(1, IpAddrCounterUtil.countIpAddressesFromReader(stringReader));
    }

    @Test
    void shouldReturnZeroIfStreamDoesntContainProperIpAddresses() throws IOException {
        StringReader stringReader = new StringReader("""
                a.b.c.d
                256.256.256.256
                string
                
                
                """);
        assertEquals(0, IpAddrCounterUtil.countIpAddressesFromReader(stringReader));
    }
}
