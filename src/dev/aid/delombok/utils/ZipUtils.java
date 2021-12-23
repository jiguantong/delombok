package dev.aid.delombok.utils;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 压缩文件内部执行文件
 *
 * @author: 04637@163.com
 * @date: 2020/8/5
 */
public class ZipUtils {

    public static void main(String[] args) {
        URI uri = getJarURI();
        URI uri2 = ZipUtils.getFile(uri, "lombok.jar");
        System.out.println(uri2.getPath());
    }

    public static URI getFile(final URI where,
                              final String fileName) {
        final File location;
        URI fileURI = null;

        location = new File(where);

        // not in a JAR, just return the path on disk
        if (location.isDirectory()) {
            fileURI = URI.create(where.toString() + fileName);
        } else {
            final ZipFile zipFile;
            try {
                zipFile = new ZipFile(location);
                try {
                    fileURI = extract(zipFile, fileName);
                } finally {
                    zipFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return (fileURI);
    }

    public static URI getJarURI() {
        URL classResource = ZipUtils.class.getResource(ZipUtils.class.getSimpleName() + ".class");
        final String url = classResource.toString();
        final String suffix = ZipUtils.class.getCanonicalName().replace('.', '/') + ".class";
        if (!url.endsWith(suffix)) return null; // weird URL

        // strip the class's path from the URL string
        final String base = url.substring(0, url.length() - suffix.length());

        String path = base;

        // remove the "jar:" prefix and "!/" suffix, if present
        if (path.startsWith("jar:")) path = path.substring(4, path.length() - 2);

        try {
            return new URI(path);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static URI extract(final ZipFile zipFile,
                               final String fileName)
            throws IOException {
        final File tempFile;
        final ZipEntry entry;
        final InputStream zipStream;
        OutputStream fileStream;

        tempFile = File.createTempFile("delombok-", fileName);
        tempFile.deleteOnExit();
        entry = zipFile.getEntry(fileName);

        if (entry == null) {
            throw new FileNotFoundException("cannot find file: " + fileName + " in archive: " + zipFile.getName());
        }

        zipStream = zipFile.getInputStream(entry);
        fileStream = null;

        try {
            final byte[] buf;
            int i;

            fileStream = new FileOutputStream(tempFile);
            buf = new byte[1024];
            i = 0;

            while ((i = zipStream.read(buf)) != -1) {
                fileStream.write(buf, 0, i);
            }
        } finally {
            close(zipStream);
            close(fileStream);
        }

        return (tempFile.toURI());
    }

    private static void close(final Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (final IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
