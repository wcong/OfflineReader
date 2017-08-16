package org.wcong.or.offline;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CacheFileInputStream extends FilterInputStream {

    private final OutputStream out;

    private Runnable notifyOnClose;


    CacheFileInputStream(InputStream in, OutputStream out) {
        super(in);
        this.out = out;
    }

    @Override
    public void close() throws IOException {
        super.close();
        if (null != out) {
            out.flush();
            out.close();
        }
        if (null != notifyOnClose) {
            notifyOnClose.run();
        }
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        final int numBytes = super.read(b, off, len);
        if (null != out && numBytes > 0) {
            out.write(b, off, numBytes);
        }
        return numBytes;
    }

    void onInputStreamClose(Runnable r) {
        notifyOnClose = r;
    }
}
