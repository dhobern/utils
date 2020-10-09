/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.dhobern.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Platyptilia
 */
public class ZipUtilsTest {
    
    public ZipUtilsTest() {
    }

    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of zipFolder method, of class ZipUtils.
     */
    @org.junit.jupiter.api.Test
    public void testZipFolder() throws Exception {
        ZipUtils.zipFolder("mockdata", "mockdata-NOW.zip");
        // TODO review the generated test code and remove the default call to fail.
    }
    
}
