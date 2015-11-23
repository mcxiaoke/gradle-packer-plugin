package com.mcxiaoke.modifier;

import java.io.DataOutput;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.zip.ZipFile;

/**
 * User: mcxiaoke
 * Date: 15/11/23
 * Time: 13:12
 */
public final class ZipUtils {
    private static final String UTF_8 = "UTF-8";
    private static final String MAGIC = "MCPK";
    private static final String VERSION = "1";

    private static void writeBytes(byte[] data, DataOutput out) throws IOException {
        out.write(data);
    }

    private static void writeShort(int i, DataOutput out) throws IOException {
        out.write((i >>> 0) & 0xff);
        out.write((i >>> 8) & 0xff);
    }


    private static void writeZipComment(File file, String comment) throws IOException {
        final ZipFile zipFile = new ZipFile(file);
        boolean hasComment = (zipFile.getComment() != null);
        zipFile.close();
        if (hasComment) {
            throw new IllegalStateException("comment already exists, ignore.");
        }
        // {@see java.util.zip.ZipOutputStream.writeEND}
        byte[] data = comment.getBytes(UTF_8);
        final RandomAccessFile raf = new RandomAccessFile(file, "rw");
        raf.seek(file.length() - 2);
        writeShort(data.length, raf);
        writeBytes(data, raf);
        raf.close();
    }

    private static String read(File file) throws IOException {
        return new ZipFile(file).getComment();
    }

    public static boolean writeMarket(final File file, final String market) throws IOException {
        if (market == null || market.length() == 0) {
            return false;
        }
        writeZipComment(file, "market=" + market);
        return true;
    }

    public static void copy(File src, File dest) throws IOException {
        if (!dest.exists()) {
            dest.createNewFile();
        }
        FileChannel source = null;
        FileChannel destination = null;
        try {
            source = new FileInputStream(src).getChannel();
            destination = new FileOutputStream(dest).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

}
