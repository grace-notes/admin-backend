package com.gracenotes.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by adam on 4/9/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/applicationContext.xml" })
public class PasswordEncryptionHelperTest {

    @Test
    public void testEncryption() {
        try {
            //System.out.println(orderExporterService.getOrderData());
            String plainText = "carnagey.adam@gmail.com:418130d184a2b1d0dc0b6d62b7c52a7a0fd3c837";
            System.out.println("plain: " + plainText);
            String encrypted = PasswordEncryptionHelper.encrypt(plainText);
            System.out.println("encrypted: " + encrypted);
            String decrypted = PasswordEncryptionHelper.decrypt(encrypted);
            System.out.println("decrypted: " + decrypted);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
