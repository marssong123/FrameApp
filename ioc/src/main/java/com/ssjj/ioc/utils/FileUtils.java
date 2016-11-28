package com.ssjj.ioc.utils;

import android.content.Context;
import android.os.Environment;

import com.ssjj.ioc.iocvalue.IocValue;
import com.ssjj.ioc.log.L;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by GZ1581 on 2016/5/13.
 */
public final class FileUtils {
    protected static final String TAG = "FileUtils";

    public static boolean isStorageExist(Context context) {
        File cacheDir = getCacheDir(context);
        File sdCardDir = getSDCardDir();

        return null != cacheDir || null != sdCardDir;
    }

    public static File getRootDir(Context context) {
        File sdCardDir = getSDCardDir();
        if (null != sdCardDir) {
            return sdCardDir;
        }

        return getCacheDir(context);
    }


    public static File zipFiles(String dstFullFilePath, List<File> files) {
        if (0 >= files.size()) {
            AdaUtils.crashIfDebug("try zip null file");
            return null;
        }

        try {
            File zipFile = new File(dstFullFilePath);
            if (zipFile.exists()) {
                zipFile.delete();
            }

            zipFile.createNewFile();

            FileOutputStream fos = new FileOutputStream(dstFullFilePath);
            ZipOutputStream zos = new ZipOutputStream(fos);

            byte[] buffer = new byte[1024];
            for (File item : files) {
                if (null == item || !item.exists()) {
                    continue;
                }

                ZipEntry zip = new ZipEntry(item.getName());
                zos.putNextEntry(zip);

                FileInputStream in = new FileInputStream(item);

                int len;
                while ((len = in.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }

                in.close();
            }

            zos.closeEntry();
            zos.close();

            return zipFile;
        } catch (Exception e) {
            L.error(TAG, "zip file to %s error %s", dstFullFilePath, e.toString());
            return null;
        }
    }

    public static String fileMd5(File file) {
        if (file == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            byte[] buffer = new byte[8192];
            int readCount = 0;
            java.security.MessageDigest md5 = java.security.MessageDigest
                    .getInstance("MD5");
            while ((readCount = in.read(buffer)) != -1) {
                md5.update(buffer, 0, readCount);
            }
            sb.append(AdaUtils.bytesToHexString(md5.digest()));
        } catch (Throwable throwable) {
            L.error(TAG,"fileMd5"+throwable.toString());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    L.error(TAG,"fileMd5"+ e.toString());
                }
            }
        }
        return sb.toString();
    }

    private static File getCacheDir(Context context) {
        File dir = context.getCacheDir();
        return dirAvailable(dir) ? dir : null;
    }

    private static File getSDCardDir() {
        File root;
        try {
            root = Environment.getExternalStorageDirectory();
        } catch (Exception e) {
            return null;
        }

        File adaDir = createDirIfNoExist(root, IocValue.gTag);
        if (dirAvailable(adaDir)) {
            return adaDir;
        }

        root = new File(root.getAbsolutePath().replace("0", "1"));
        adaDir = createDirIfNoExist(root, IocValue.gTag);
        if (dirAvailable(adaDir)) {
            return adaDir;
        }

        return null;
    }

    private static boolean dirAvailable(File dir) {
        return null != dir && dir.isDirectory() && dir.canRead() && dir.canWrite();
    }

    public static File createDirIfNoExist(File rootDir, String dir) {
        File currentDir = rootDir;
        currentDir = new File(currentDir, dir);
        if ((currentDir.exists() && !currentDir.isDirectory())
                || (!currentDir.exists() && !currentDir.mkdir())) {
            return null;
        }

        return currentDir;
    }

    public static File createFileOnSD(String dir, String name) {
        File file = new File(dir);
        if (!file.exists() && !file.mkdir()) {
            return null;
        }

        file = new File(dir + "/" + name);
        try {
            if (!file.exists() && !file.createNewFile()) {
                file = null;
            }
        } catch (IOException e) {
            L.error(TAG,"createFileOnSD"+ " can not create file on SD card");
            file = null;
        }


        return file;
    }

    public static File createNewFile(String path) {
        File file = new File(path);
        try {
            if (!file.exists() && !file.createNewFile()) {
                file = null;
            }
        } catch (Exception e) {
            L.error(TAG,"createNewFile",  "can not create file on SD card");
            file = null;
        }

        return file;
    }
}
