package xyz.thewind.music.music.impl;

import org.apache.tomcat.util.codec.binary.Base64;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;
import xyz.thewind.music.util.Base64Util;

import java.nio.charset.StandardCharsets;


public final class KwDES {
    private static final int DES_MODE_DECRYPT = 1;
    private static final int DES_MODE_ENCRYPT = 0;
    private static long L;
    private static long R;
    private static int SOut;
    private static int sbi;
    private static int t;
    public static final byte[] SECRET_KEY = "ylzsxkwm".getBytes();
    public static final int SECRET_KEY_LENGTH = SECRET_KEY.length;
    public static final byte[] KSHOW_SECRET_KEY = "show!#31".getBytes();
    private static final long[] g_arrayMask = {1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384, 32768, 65536, 131072, 262144, 524288, 1048576, 2097152, 4194304, 8388608, 16777216, 33554432, 67108864, 134217728, 268435456, 536870912, 1073741824, 2147483648L, 4294967296L, 8589934592L, 17179869184L, 34359738368L, 68719476736L, 137438953472L, 274877906944L, 549755813888L, 1099511627776L, 2199023255552L, 4398046511104L, 8796093022208L, 17592186044416L, 35184372088832L, 70368744177664L, 140737488355328L, 281474976710656L, 562949953421312L, 1125899906842624L, 2251799813685248L, 4503599627370496L, 9007199254740992L, 18014398509481984L, 36028797018963968L, 72057594037927936L, 144115188075855872L, 288230376151711744L, 576460752303423488L, 1152921504606846976L, 2305843009213693952L, 4611686018427387904L, Long.MIN_VALUE};
    private static final int[] g_arrayIP = {57, 49, 41, 33, 25, 17, 9, 1, 59, 51, 43, 35, 27, 19, 11, 3, 61, 53, 45, 37, 29, 21, 13, 5, 63, 55, 47, 39, 31, 23, 15, 7, 56, 48, 40, 32, 24, 16, 8, 0, 58, 50, 42, 34, 26, 18, 10, 2, 60, 52, 44, 36, 28, 20, 12, 4, 62, 54, 46, 38, 30, 22, 14, 6};
    private static final int[] g_arrayE = {31, 0, 1, 2, 3, 4, -1, -1, 3, 4, 5, 6, 7, 8, -1, -1, 7, 8, 9, 10, 11, 12, -1, -1, 11, 12, 13, 14, 15, 16, -1, -1, 15, 16, 17, 18, 19, 20, -1, -1, 19, 20, 21, 22, 23, 24, -1, -1, 23, 24, 25, 26, 27, 28, -1, -1, 27, 28, 29, 30, 31, 30, -1, -1};
    private static final char[][] g_matrixNSBox = {new char[]{14, 4, 3, 15, 2, '\r', 5, 3, '\r', 14, 6, '\t', 11, 2, 0, 5, 4, 1, '\n', '\f', 15, 6, '\t', '\n', 1, '\b', '\f', 7, '\b', 11, 7, 0, 0, 15, '\n', 5, 14, 4, '\t', '\n', 7, '\b', '\f', 3, '\r', 1, 3, 6, 15, '\f', 6, 11, 2, '\t', 5, 0, 4, 2, 11, 14, 1, 7, '\b', '\r'}, new char[]{15, 0, '\t', 5, 6, '\n', '\f', '\t', '\b', 7, 2, '\f', 3, '\r', 5, 2, 1, 14, 7, '\b', 11, 4, 0, 3, 14, 11, '\r', 6, 4, 1, '\n', 15, 3, '\r', '\f', 11, 15, 3, 6, 0, 4, '\n', 1, 7, '\b', 4, 11, 14, '\r', '\b', 0, 6, 2, 15, '\t', 5, 7, 1, '\n', '\f', 14, 2, 5, '\t'}, new char[]{'\n', '\r', 1, 11, 6, '\b', 11, 5, '\t', 4, '\f', 2, 15, 3, 2, 14, 0, 6, '\r', 1, 3, 15, 4, '\n', 14, '\t', 7, '\f', 5, 0, '\b', 7, '\r', 1, 2, 4, 3, 6, '\f', 11, 0, '\r', 5, 14, 6, '\b', 15, 2, 7, '\n', '\b', 15, 4, '\t', 11, 5, '\t', 0, 14, 3, '\n', 7, 1, '\f'}, new char[]{7, '\n', 1, 15, 0, '\f', 11, 5, 14, '\t', '\b', 3, '\t', 7, 4, '\b', '\r', 6, 2, 1, 6, 11, '\f', 2, 3, 0, 5, 14, '\n', '\r', 15, 4, '\r', 3, 4, '\t', 6, '\n', 1, '\f', 11, 0, 2, 5, 0, '\r', 14, 2, '\b', 15, 7, 4, 15, 1, '\n', 7, 5, 6, '\f', 11, 3, '\b', '\t', 14}, new char[]{2, 4, '\b', 15, 7, '\n', '\r', 6, 4, 1, 3, '\f', 11, 7, 14, 0, '\f', 2, 5, '\t', '\n', '\r', 0, 3, 1, 11, 15, 5, 6, '\b', '\t', 14, 14, 11, 5, 6, 4, 1, 3, '\n', 2, '\f', 15, 0, '\r', 2, '\b', 5, 11, '\b', 0, 15, 7, 14, '\t', 4, '\f', 7, '\n', '\t', 1, '\r', 6, 3}, new char[]{'\f', '\t', 0, 7, '\t', 2, 14, 1, '\n', 15, 3, 4, 6, '\f', 5, 11, 1, 14, '\r', 0, 2, '\b', 7, '\r', 15, 5, 4, '\n', '\b', 3, 11, 6, '\n', 4, 6, 11, 7, '\t', 0, 6, 4, 2, '\r', 1, '\t', 15, 3, '\b', 15, 3, 1, 14, '\f', 5, 11, 0, 2, '\f', 14, 7, 5, '\n', '\b', '\r'}, new char[]{4, 1, 3, '\n', 15, '\f', 5, 0, 2, 11, '\t', 6, '\b', 7, 6, '\t', 11, 4, '\f', 15, 0, 3, '\n', 5, 14, '\r', 7, '\b', '\r', 14, 1, 2, '\r', 6, 14, '\t', 4, 1, 2, 14, 11, '\r', 5, 0, 1, '\n', '\b', 3, 0, 11, 3, 5, '\t', 4, 15, 2, 7, '\b', '\f', 15, '\n', 7, 6, '\f'}, new char[]{'\r', 7, '\n', 0, 6, '\t', 5, 15, '\b', 4, 3, '\n', 11, 14, '\f', 5, 2, 11, '\t', 6, 15, '\f', 0, 3, 4, 1, 14, '\r', 1, 2, 7, '\b', 1, 2, '\f', 15, '\n', 4, 0, 3, '\r', 14, 6, '\t', 7, '\b', '\t', 6, 15, 1, 5, '\f', 3, '\n', 14, 5, '\b', 7, 11, 0, 4, '\r', 2, 11}};
    private static final int[] g_arrayP = {15, 6, 19, 20, 28, 11, 27, 16, 0, 14, 22, 25, 4, 17, 30, 9, 1, 7, 23, 13, 31, 26, 2, 8, 18, 12, 29, 5, 21, 10, 3, 24};
    private static final int[] g_arrayIP_1 = {39, 7, 47, 15, 55, 23, 63, 31, 38, 6, 46, 14, 54, 22, 62, 30, 37, 5, 45, 13, 53, 21, 61, 29, 36, 4, 44, 12, 52, 20, 60, 28, 35, 3, 43, 11, 51, 19, 59, 27, 34, 2, 42, 10, 50, 18, 58, 26, 33, 1, 41, 9, 49, 17, 57, 25, 32, 0, 40, 8, 48, 16, 56, 24};
    private static final int[] g_arrayPC_1 = {56, 48, 40, 32, 24, 16, 8, 0, 57, 49, 41, 33, 25, 17, 9, 1, 58, 50, 42, 34, 26, 18, 10, 2, 59, 51, 43, 35, 62, 54, 46, 38, 30, 22, 14, 6, 61, 53, 45, 37, 29, 21, 13, 5, 60, 52, 44, 36, 28, 20, 12, 4, 27, 19, 11, 3};
    private static final int[] g_arrayPC_2 = {13, 16, 10, 23, 0, 4, -1, -1, 2, 27, 14, 5, 20, 9, -1, -1, 22, 18, 11, 3, 25, 7, -1, -1, 15, 6, 26, 19, 12, 1, -1, -1, 40, 51, 30, 36, 46, 54, -1, -1, 29, 39, 50, 44, 32, 47, -1, -1, 43, 48, 38, 55, 33, 52, -1, -1, 45, 41, 49, 35, 28, 31, -1, -1};
    private static final int[] g_arrayLs = {1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1};
    private static final long[] g_arrayLsMask = {0, 1048577, 3145731};
    private static long out = 0;
    private static int[] pSource = new int[2];
    private static byte[] pR = new byte[8];

    private static long BitTransform(int[] iArr, int i, long j) {
        long j2 = 0;
        for (int i2 = 0; i2 < i; i2++) {
            if (iArr[i2] >= 0) {
                long[] jArr = g_arrayMask;
                if ((jArr[iArr[i2]] & j) != 0) {
                    j2 |= jArr[i2];
                }
            }
        }
        return j2;
    }

    private static void DESSubKeys(long j, long[] jArr, int i) {
        long BitTransform = BitTransform(g_arrayPC_1, 56, j);
        for (int i2 = 0; i2 < 16; i2++) {
            long[] jArr2 = g_arrayLsMask;
            int[] iArr = g_arrayLs;
            BitTransform = ((BitTransform & (~jArr2[iArr[i2]])) >> iArr[i2]) | ((jArr2[iArr[i2]] & BitTransform) << (28 - iArr[i2]));
            jArr[i2] = BitTransform(g_arrayPC_2, 64, BitTransform);
        }
        if (i == 1) {
            for (int i3 = 0; i3 < 8; i3++) {
                long j2 = jArr[i3];
                int i4 = 15 - i3;
                jArr[i3] = jArr[i4];
                jArr[i4] = j2;
            }
        }
    }

    private static long DES64(long[] jArr, long j) {
        out = BitTransform(g_arrayIP, 64, j);
        int[] iArr = pSource;
        long j2 = out;
        iArr[0] = (int) (j2 & 4294967295L);
        iArr[1] = (int) ((j2 & (-4294967296L)) >> 32);
        for (int i = 0; i < 16; i++) {
            R = pSource[1];
            R = BitTransform(g_arrayE, 64, R);
            R ^= jArr[i];
            for (int i2 = 0; i2 < 8; i2++) {
                pR[i2] = (byte) (255 & (R >> (i2 * 8)));
            }
            SOut = 0;
            int i3 = 7;
            while (true) {
                sbi = i3;
                int i4 = sbi;
                if (i4 >= 0) {
                    SOut <<= 4;
                    SOut |= g_matrixNSBox[i4][pR[i4]];
                    i3 = i4 - 1;
                } else {
                    break;
                }
            }
            R = SOut;
            R = BitTransform(g_arrayP, 32, R);
            int[] iArr2 = pSource;
            L = iArr2[0];
            iArr2[0] = iArr2[1];
            iArr2[1] = (int) (L ^ R);
        }
        int[] iArr3 = pSource;
        t = iArr3[0];
        iArr3[0] = iArr3[1];
        iArr3[1] = t;
        out = ((iArr3[1] << 32) & (-4294967296L)) | (4294967295L & iArr3[0]);
        out = BitTransform(g_arrayIP_1, 64, out);
        return out;
    }

    public static synchronized byte[] encrypt(byte[] bArr, int i, byte[] bArr2, int i2) {
        byte[] bArr3;
        byte[] bArr4;
        synchronized (KwDES.class) {
            long j = 0;
            for (int i3 = 0; i3 < 8; i3++) {
                try {
                    j |= bArr2[i3] << (i3 * 8);
                } catch (Throwable th) {
                    throw th;
                }
            }
            int i4 = i / 8;
            long[] jArr = new long[16];
            for (int i5 = 0; i5 < 16; i5++) {
                jArr[i5] = 0;
            }
            long[] jArr2 = new long[i4];
            for (int i6 = 0; i6 < i4; i6++) {
                for (int i7 = 0; i7 < 8; i7++) {
                    jArr2[i6] |= ((long) bArr[(i6 * 8) + i7] << (i7 * 8));
                }
            }
            long[] jArr3 = new long[(((i4 + 1) * 8) + 1) / 8];
            DESSubKeys(j, jArr, 0);
            for (int i8 = 0; i8 < i4; i8++) {
                jArr3[i8] = DES64(jArr, jArr2[i8]);
            }
            int i9 = i % 8;
            int i10 = i4 * 8;
            int i11 = i - i10;
            bArr3 = new byte[i11];
            System.arraycopy(bArr, i10, new byte[i11], 0, i11);
            long j2 = 0;
            for (int i12 = 0; i12 < i9; i12++) {
                j2 |= bArr3[i12] << (i12 * 8);
            }
            jArr3[i4] = DES64(jArr, j2);
            bArr4 = new byte[jArr3.length * 8];
            int i13 = 0;
            int i14 = 0;
            while (i13 < jArr3.length) {
                int i15 = i14;
                for (int i16 = 0; i16 < 8; i16++) {
                    bArr4[i15] = (byte) (255 & (jArr3[i13] >> (i16 * 8)));
                    i15++;
                }
                i13++;
                i14 = i15;
            }
        }
        return bArr4;
    }

    public static synchronized byte[] encrypt2(byte[] bArr, int i, byte[] bArr2, int i2) {
        byte[] bArr3;
        byte[] bArr4;
        synchronized (KwDES.class) {
            long j = 0;
            for (int i3 = 0; i3 < 8; i3++) {
                try {
                    j |= bArr2[i3] << (i3 * 8);
                } catch (Throwable th) {
                    throw th;
                }
            }
            int i4 = i / 8;
            long[] jArr = new long[16];
            for (int i5 = 0; i5 < 16; i5++) {
                jArr[i5] = 0;
            }
            long[] jArr2 = new long[i4];
            for (int i6 = 0; i6 < i4; i6++) {
                for (int i7 = 0; i7 < 8; i7++) {
                    jArr2[i6] = ((bArr[(i6 * 8) + i7] & 255) << (i7 * 8)) | jArr2[i6];
                }
            }
            long[] jArr3 = new long[(((i4 + 1) * 8) + 1) / 8];
            DESSubKeys(j, jArr, 0);
            for (int i8 = 0; i8 < i4; i8++) {
                jArr3[i8] = DES64(jArr, jArr2[i8]);
            }
            int i9 = i % 8;
            int i10 = i4 * 8;
            int i11 = i - i10;
            bArr3 = new byte[i11];
            System.arraycopy(bArr, i10, new byte[i11], 0, i11);
            long j2 = 0;
            for (int i12 = 0; i12 < i9; i12++) {
                j2 |= (long) (bArr3[i12] & 255) << (i12 * 8);
            }
            jArr3[i4] = DES64(jArr, j2);
            bArr4 = new byte[jArr3.length * 8];
            int i13 = 0;
            int i14 = 0;
            while (i13 < jArr3.length) {
                int i15 = i14;
                for (int i16 = 0; i16 < 8; i16++) {
                    bArr4[i15] = (byte) (255 & (jArr3[i13] >> (i16 * 8)));
                    i15++;
                }
                i13++;
                i14 = i15;
            }
        }
        return bArr4;
    }

    public static synchronized byte[] decryptKSing(byte[] bArr, byte[] bArr2) {
        byte[] bArr3;
        synchronized (KwDES.class) {
            try {
                int length = bArr.length;
                int length2 = bArr2.length;
                long[] jArr = new long[16];
                long j = 0;
                for (int i = 0; i < 8; i++) {
                    j |= bArr2[i] << (i * 8);
                }
                for (int i2 = 0; i2 < 16; i2++) {
                    jArr[i2] = 0;
                }
                DESSubKeys(j, jArr, 1);
                int i3 = length / 8;
                long[] jArr2 = new long[i3];
                for (int i4 = 0; i4 < i3; i4++) {
                    for (int i5 = 0; i5 < 8; i5++) {
                        jArr2[i4] = jArr2[i4] | ((bArr[(i4 * 8) + i5] & 255) << (i5 * 8));
                    }
                }
                long[] jArr3 = new long[i3];
                for (int i6 = 0; i6 < i3; i6++) {
                    jArr3[i6] = DES64(jArr, jArr2[i6]);
                }
                bArr3 = new byte[i3 * 8];
                for (int i7 = 0; i7 < i3; i7++) {
                    for (int i8 = 0; i8 < 8; i8++) {
                        bArr3[(i7 * 8) + i8] = (byte) (255 & (jArr3[i7] >> (i8 * 8)));
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return bArr3;
    }

    public static byte[] encrypt(byte[] bArr) {
        byte[] encryptKSing = encryptKSing(bArr, KSHOW_SECRET_KEY);
        return encryptKSing;
    }

    public static byte[] enc(byte[] bytes) {
        return encryptKSing(bytes, KSHOW_SECRET_KEY);
    }

    public static byte[] dec(byte[] bytes) {
        return decryptKSing(bytes, KSHOW_SECRET_KEY);
    }

    public static String encrypt(String str, byte[] bArr) {
        byte[] encryptKSing = encryptKSing(bArr, str.getBytes());
        return new String(Base64Coder.encode(encryptKSing, encryptKSing.length));
    }

    public static synchronized byte[] encryptKSing(byte[] bArr, byte[] bArr2) {
        long j;
        byte[] bArr3;
        byte[] bArr4;
        synchronized (KwDES.class) {
            try {
                int length = bArr.length;
                int length2 = bArr2.length;
                long[] jArr = new long[16];
                int i = 0;
                while (true) {
                    j = 0;
                    if (i >= 16) {
                        break;
                    }
                    jArr[i] = 0;
                    i++;
                }
                long j2 = 0;
                for (int i2 = 0; i2 < bArr2.length; i2++) {
                    j2 |= (long) bArr2[i2] << (i2 * 8);
                }
                DESSubKeys(j2, jArr, 0);
                int i3 = length / 8;
                long[] jArr2 = new long[i3];
                for (int i4 = 0; i4 < i3; i4++) {
                    for (int i5 = 0; i5 < 8; i5++) {
                        jArr2[i4] = jArr2[i4] | ((bArr[(i4 * 8) + i5] & 255) << (i5 * 8));
                    }
                }
                long[] jArr3 = new long[(((i3 + 1) * 8) + 1) / 8];
                for (int i6 = 0; i6 < i3; i6++) {
                    jArr3[i6] = DES64(jArr, jArr2[i6]);
                }
                int i7 = length % 8;
                int i8 = i3 * 8;
                int i9 = length - i8;
                bArr3 = new byte[i9];
                System.arraycopy(bArr, i8, new byte[i9], 0, i9);
                for (int i10 = 0; i10 < i7; i10++) {
                    j |= (long) (bArr3[i10] & 255) << (i10 * 8);
                }
                jArr3[i3] = DES64(jArr, j);
                bArr4 = new byte[jArr3.length * 8];
                int i11 = 0;
                int i12 = 0;
                while (i11 < jArr3.length) {
                    int i13 = i12;
                    for (int i14 = 0; i14 < 8; i14++) {
                        bArr4[i13] = (byte) (255 & (jArr3[i11] >> (i14 * 8)));
                        i13++;
                    }
                    i11++;
                    i12 = i13;
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return bArr4;
    }

    public static String decodeKSing(String str) {
        return decodeKSing(str, strToByte(ksingSecretKey));
    }


    public static String decodeKSing(String str, byte[] bArr) {
        try {
            return byteToStr(decryptKSing(Base64Coder.decode(str), bArr)).trim();
        } catch (Exception e) {
            return "";
        }
    }

    private static final String ksingSecretKey = (System.currentTimeMillis() + "12345678").substring(0, 8);

    public static byte[] strToByte(String str) {
        return str.getBytes(StandardCharsets.UTF_8);
    }

    public static String byteToStr(byte[] bArr) {
        return new String(bArr, StandardCharsets.UTF_8);
    }
}