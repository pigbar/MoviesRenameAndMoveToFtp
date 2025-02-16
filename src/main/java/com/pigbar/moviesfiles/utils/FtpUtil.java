package com.pigbar.moviesfiles.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class FtpUtil {
    private static final int BUFFER_SIZE = 1024 * 4;
    private static final Logger logger = Logger.getLogger(FtpUtil.class.getName());
    private static final int BASE_PERCENT = 10;

    public static boolean uploadFile(FTPClient ftpClient, String remotePath, File localFile) {
        boolean success = false;
        try {
            String remoteFile = remotePath + File.separator + localFile.getName();
            FileInputStream inputStream = new FileInputStream(localFile);
            logger.info(">> start uploading file " + localFile.getName() + " to " + remotePath);
            OutputStream outputStream = ftpClient.storeFileStream(remoteFile);
            long fileSize = localFile.length();
            byte[] bytesIn = new byte[BUFFER_SIZE];
            int bytesRead = 0;
            long totalRead = 0;
            long timeStamp = System.currentTimeMillis();
            long currentTime = 0;
            int maxDiffTime = (int) (fileSize / (BUFFER_SIZE * BASE_PERCENT));
            maxDiffTime = (maxDiffTime > BUFFER_SIZE)? BUFFER_SIZE: Math.max(maxDiffTime, 2000);
            while ((bytesRead = inputStream.read(bytesIn)) > 0) {
                outputStream.write(bytesIn, 0, bytesRead);
                totalRead += bytesRead;
                long diffTime = currentTime - timeStamp;
                BigDecimal percent = BigDecimal.valueOf((float) totalRead / (float) fileSize * 100.f).setScale(2, RoundingMode.HALF_EVEN);
                if ((diffTime < 0 || diffTime > maxDiffTime) && (percent.intValue() > 0 && percent.remainder(BigDecimal.valueOf(BASE_PERCENT)).intValue() == 0)) {
                    logger.info(" --> uploading " + localFile + " " + percent + " %");
                    timeStamp = System.currentTimeMillis();
                }
                currentTime = System.currentTimeMillis();
            }
            inputStream.close();
            outputStream.close();
            success = ftpClient.completePendingCommand();
            if (success) {
                logger.info(">> file is uploaded successfully.");
            }
        } catch (IOException ex) {
            logger.severe("Error: " + ex.getMessage());
            throw new RuntimeException(ex);
        }
        return success;
    }

    public static FTPClient getConnectedClient(String url, int port, String user, String psw) {
        FTPClient ftp = new FTPClient();
        try {
            int reply;
            ftp.connect(url);//connect to FTP server
            ftp.login(user, psw);
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                throw new RuntimeException("Cannot get valid FtpClient");
            }
            ftp.enterLocalPassiveMode();
            ftp.setFileTransferMode(FTP.STREAM_TRANSFER_MODE);
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
            return ftp;
        } catch (IOException ex) {
            logger.severe("Error: " + ex.getMessage());
            throw new RuntimeException("Error getting valid FtpClient", ex);
        }
    }

    public static boolean isExistPath(String workingPath, FTPClient ftpClient) {
        try {
            return ftpClient.changeWorkingDirectory(workingPath);
        } catch (IOException ex) {
            logger.severe("Error: " + ex.getMessage());
            throw new RuntimeException("Error in isExistPath", ex);
        }
    }

    public static boolean downFile(FTPClient ftp, String remotePath, String fileName, String localPath) {
        boolean success = false;
        try {
            ftp.changeWorkingDirectory(remotePath);
            FTPFile[] fs = ftp.listFiles();
            for (FTPFile ff : fs) {
                if (ff.getName().equals(fileName)) {
                    File localFile = new File(localPath + File.separator + ff.getName());
                    OutputStream is = Files.newOutputStream(localFile.toPath());
                    ftp.retrieveFile(ff.getName(), is);
                    is.close();
                }
            }
            success = true;
        } catch (IOException ex) {
            logger.severe("Error: " + ex.getMessage());
            throw new RuntimeException("Error downloading file", ex);
        }
        return success;
    }

    public static void checkAndRenameFtpFilesInRootDir(String rootDir, List<String> filesExts, FTPClient ftpClient){
        if (isExistPath(rootDir, ftpClient)){
            try {
                for (FTPFile file : ftpClient.listFiles()){
                    if (file.isDirectory()){
                        checkAndRenameFtpFilesInRootDir(rootDir + File.separator + file.getName(), filesExts, ftpClient);
                    } else {
                        String extension = FileNameUtil.getExtFromFileName(file.getName());
                        if (filesExts.contains(extension.toUpperCase())){
                            String newFileName = FileNameUtil.formatFileName(file.getName());
                            if (!newFileName.equalsIgnoreCase(file.getName())){
                                if (ftpClient.rename(file.getName(), newFileName)){
                                    logger.info("File renamed from " + file.getName()
                                    + " to " + newFileName);
                                } else {
                                    logger.info("Cannot rename File from " + file.getName()
                                            + " to " + newFileName);
                                    return;
                                }
                            }
                            newFileName = FileNameUtil.getFileNameWithOutExt(newFileName);
                            if (!FileNameUtil.getLastMemberFromPath(rootDir).equalsIgnoreCase(
                                    newFileName)){
                                String parentPath = FileNameUtil.getParentPath(rootDir);
                                if (ftpClient.rename(rootDir, parentPath + File.separator + newFileName)){
                                    logger.info("Directory renamed from " + rootDir +
                                            " to " + newFileName);
                                    return;
                                } else {
                                    logger.info("Cannot rename directory from " + rootDir +
                                            " to " + newFileName);
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
