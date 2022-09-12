/**
 * Copyright MySivana LLC
 *
 * (C) Copyright MySivana LLC   All rights reserved.
 *
 * NOTICE:  All information contained herein or attendant hereto is,
 *          and remains, the property of MySivana LLC.  Many of the
 *          intellectual and technical concepts contained herein are
 *          proprietary to MySivana LLC. Any dissemination of this
 *          information or reproduction of this material is strictly
 *          forbidden unless prior written permission is obtained
 *          from MySivana LLC.
 *
 * ------------------------------------------------------------------------
 *
 * ========================================================================
 * Revision History
 * ========================================================================
 * DATE             : PROGRAMMER  : DESCRIPTION
 * ========================================================================
 * JUNE 06 2018      : BYNDR       : CREATED.
 * ------------------------------------------------------------------------
 *
 * ========================================================================
 */
package com.mysivana.util;

import java.math.BigInteger;

public class RSAObfuscation {
    BigInteger n, e;

    public void setPublic(String B, String A) {
        if (B != null && A != null && B.length() > 0 && A.length() > 0) {
            this.n = new BigInteger(B, 16);
            this.e = new BigInteger(A, 16);
        }
    }

    public String encryptNativeBytes(byte[] B) throws Exception {
        int H = B.length;
        int G = (this.n.bitLength() + 7) >> 3;
        if (H > G) {
            throw new Exception("Invalid 104");
        }
        BigInteger A = new BigInteger(B);
        BigInteger F = this.doPublic(A);
        if (F == null) {
            return null;
        }
        String D = F.toString(16);
        if (D.length() <= (G * 2)) {
            int E = (G * 2) - D.length();
            for (int C = 0; C < E; C++) {
                D = "0" + D;
            }
            return D;
        }
        throw new Exception("Cannot decode");
    }

    public BigInteger doPublic(BigInteger A) {
        return A.modPow(this.e, this.n);
    }

}