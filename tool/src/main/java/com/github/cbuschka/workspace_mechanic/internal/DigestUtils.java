package com.github.cbuschka.workspace_mechanic.internal;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.UndeclaredThrowableException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DigestUtils {

    public static BigInteger getChecksum(File file) {
        try (InputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            byte[] buffer = new byte[8192];
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            ;
            int count;
            while ((count = bis.read(buffer)) > 0) {
                digest.update(buffer, 0, count);
            }
            byte[] hash = digest.digest();
            return new BigInteger(hash);
        } catch (IOException | NoSuchAlgorithmException ex) {
            throw new UndeclaredThrowableException(ex);
        }
    }
}
