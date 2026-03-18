package com.saki.crtrs.utils;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

/**
 * 文件上传工具类：生成唯一文件名、创建目录
 */
@Component
public class FileUploadUtil {
    // ========== 注入yml中的FTP配置 ==========
    @Value("${ftp.host}")
    private String ftpHost;
    @Value("${ftp.port}")
    private Integer ftpPort;
    @Value("${ftp.username}")
    private String ftpUsername;
    @Value("${ftp.password}")
    private String ftpPassword;
    @Value("${ftp.basePath}")
    private String ftpBasePath;

    public static String getFilenameExtension(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    public static void mkdirs(String filePath) {
        java.io.File file = new java.io.File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public boolean uploadFile(String fileName, InputStream inputStream) throws IOException {
        FTPClient ftpClient = new FTPClient();
        try {
            // 1. 连接宝塔FTP服务器
            ftpClient.connect(ftpHost, ftpPort);
            // 2. 登录FTP账号密码
            boolean loginSuccess = ftpClient.login(ftpUsername, ftpPassword);
            if (!loginSuccess) {
                return false;
            }

            // ========== 被动模式 ==========
            ftpClient.enterLocalPassiveMode();
            // ========== 设置编码+二进制传输，防止图片乱码/损坏 ==========
            ftpClient.setControlEncoding("UTF-8");
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            // ========== 自动创建宝塔的目录（如果目录不存在），宝塔必加 ==========
            if (!ftpClient.changeWorkingDirectory(ftpBasePath)) {
                ftpClient.makeDirectory(ftpBasePath);
                ftpClient.changeWorkingDirectory(ftpBasePath);
            }

            // ========== 上传图片到宝塔服务器 指定目录 ==========
            boolean uploadSuccess = ftpClient.storeFile(fileName, inputStream);

            // ========== 上传完成：关闭流+登出 ==========
            inputStream.close();
            ftpClient.logout();

            return uploadSuccess;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            // ========== 无论成功失败，都断开FTP连接，防止连接泄露 ==========
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 删除FTP服务器图片（兼容空URL、无效URL）
     * @param filePath 图片URL/相对路径（允许为null/空字符串）
     * @return true=删除成功/无文件无需删；false=有文件但删除失败
     */
    public boolean deleteFile(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return true;
        }
        String realFilePath = filePath.trim();

        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(ftpHost, ftpPort);
            boolean loginSuccess = ftpClient.login(ftpUsername, ftpPassword);
            int reply = ftpClient.getReplyCode();
            if (!loginSuccess || !FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                return false;
            }

            // 从URL中提取纯文件名（去掉所有路径前缀）
            if (realFilePath.startsWith("http")) {
                // 从 http://xxx/assets/CRTRS/xxx.jpg 中提取出 xxx.jpg
                realFilePath = realFilePath.substring(realFilePath.lastIndexOf("/") + 1);
            }
            // FTP根目录已经是CRTRS，直接用文件名，不再拼接任何路径
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();

            boolean fileExists = ftpClient.listFiles(realFilePath).length > 0;
            boolean deleteSuccess = false;
            if (fileExists) {
                deleteSuccess = ftpClient.deleteFile(realFilePath);
            } else {
                deleteSuccess = true; // 文件不存在，视为删除成功
            }

            ftpClient.logout();
            return deleteSuccess;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
