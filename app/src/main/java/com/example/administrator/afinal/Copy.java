package com.example.administrator.afinal;


import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class Copy {
    /**
     * 从Apk本地Assets复制文件到指定文件夹
     *
     * @param context
     * @param fileName
     * @return
     */
    public static boolean copyFileFromAssets(Context context,
                                             String fileName) {
        boolean result = false;
        try {
            // 检查 SQLite 数据库文件是否存在
            String filepath = "data/data/" + context.getPackageName() + "/databases/";
            if ((new File(filepath + fileName)).exists() == false) {
                // 如 SQLite 数据库文件不存在，再检查一下 database 目录是否存在
                File f = new File(filepath);
                // 如 database 目录不存在，新建该目录
                if (!f.exists()) {
                    f.mkdir();
                }
                try {
                    // 得到 assets 目录下我们实现准备好的 SQLite 数据库作为输入流
                    InputStream is = context.getAssets().open(fileName);
                    // 输出流
                    OutputStream os = new FileOutputStream(filepath + fileName);
                    // 文件写入
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = is.read(buffer)) > 0) {
                        os.write(buffer, 0, length);
                    }
                    // 关闭文件流
                    os.flush();
                    os.close();
                    is.close();
                    result = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return result;
    }
}
