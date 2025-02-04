package com.pigbar.moviesfiles;

import com.pigbar.moviesfiles.utils.FileNameUtil;
import com.pigbar.moviesfiles.utils.FtpUtil;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class FileHandler {
    public final static String MOVIES_DIR = "/sda2/data/Movies";
    public final static String USER = "anonymous";
    public final static String PSW = "";
    public final static String HOST_NAME = "192.168.50.1";
    public final static int DEFAULT_PORT = 0;
    public final static String DOWNLOAD_DIR = "/home/pigbar/Torrents/Downloaded";
    public final static List<String> moviesExt = new ArrayList<>();
    public final static Logger logger = Logger.getLogger(FileHandler.class.getName());

    public FileHandler() {
        moviesExt.add(".MP4");
        moviesExt.add(".MOV");
        moviesExt.add(".AVI");
        moviesExt.add(".AVCHD");
        moviesExt.add(".WEBM");
        moviesExt.add(".FLV");
    }

    public void processDownloadedMovies() {
        renameFilesInDirectory(DOWNLOAD_DIR);
        moveFilesToRemoteFtp(HOST_NAME, DEFAULT_PORT, USER, PSW, DOWNLOAD_DIR, MOVIES_DIR);
    }

    public void moveFilesToRemoteFtp(String hostName, int defaultPort, String user, String psw, String localDir, String remoteDir) {
        FTPClient ftpClient = null;
        try {
            ftpClient = FtpUtil.getConnectedClient(hostName, defaultPort, user, psw);
            moveFilesToRemoteFtp(ftpClient, localDir, remoteDir);
        } catch (Exception ex) {
            logger.severe("Error: " + ex.getMessage());
            throw new RuntimeException("Error in getConnectedClient", ex);
        } finally {
            if (ftpClient != null && ftpClient.isConnected()) {
                try {
                    ftpClient.quit();
                    ftpClient.disconnect();
                } catch (IOException ex) {
                    logger.severe("Error: " + ex.getMessage());
                }
            }
        }
    }

    public void moveFilesToRemoteFtp(FTPClient ftpClient, String localDir, String remoteDir) {
        File rootDir = new File(localDir);
        if (rootDir.isDirectory()) {
            logger.info("Moving Dir : " + rootDir.getName());
            String localDirPath = rootDir.getAbsolutePath();
            if (!localDirPath.equalsIgnoreCase(DOWNLOAD_DIR)) {
                if (!FtpUtil.isExistPath(remoteDir, ftpClient)) {
                    try {
                        if (ftpClient.makeDirectory(remoteDir)) {
                            logger.info(" Directory created : " + remoteDir);
                        } else {
                            logger.severe(" Error, cannot create Directory : " + remoteDir);
                            return;
                        }
                    } catch (IOException ex) {
                        logger.severe("Error: " + ex.getMessage());
                        throw new RuntimeException("Error in existPath", ex);
                    }
                } else {
                    logger.info("Directory already exist, doing nothing");
                    return;
                }
            }
            File[] filesInDir = rootDir.listFiles();
            assert filesInDir != null;
            for (File fileInDir : filesInDir) {
                if (fileInDir.isDirectory()) {
                    moveFilesToRemoteFtp(ftpClient, fileInDir.getAbsolutePath(),
                            remoteDir + File.separator + fileInDir.getName());
                    fileInDir.delete();
                } else {
                    if (FtpUtil.uploadFile(ftpClient, remoteDir, fileInDir)) {
                        logger.info("    File moved to ftp : " + remoteDir + File.separator + fileInDir.getName());
                    } else {
                        logger.severe("    Error moving file to ftp : " + remoteDir + File.separator +
                                fileInDir.getName());
                    }
                    fileInDir.delete();
                }
            }

        } else {
            throw new RuntimeException("Not valid root dir for moving files");
        }
    }

    public void renameFilesInDirectory(String directory) {
        File rootDir = new File(directory);
        if (rootDir.isDirectory()) {
            logger.info("Processing Dir : " + rootDir.getName());
            File[] filesInDir = rootDir.listFiles();
            assert filesInDir != null;
            String newParentName = null;
            for (File fileInDir : filesInDir) {
                if (fileInDir.isDirectory()) {
                    renameFilesInDirectory(fileInDir.getAbsolutePath());
                } else {
                    String fileExt = FileNameUtil.getExtFromFileName(fileInDir.getName()).toUpperCase();
                    if (moviesExt.contains(fileExt)) {
                        logger.info("Processing file : " + fileInDir.getName());
                        String newFileName = fileInDir.getParent() + File.separator + FileNameUtil.formatFileName(fileInDir.getName());
                        newParentName = rootDir.getParent() + File.separator + FileNameUtil.getFileNameWithOutExt(FileNameUtil.formatFileName(fileInDir.getName()));
                        File newFile = new File(newFileName);
                        if (fileInDir.renameTo(newFile)) {
                            logger.info("file renamed to : " + newFile.getAbsolutePath());
                        }
                        break;
                    }
                }
            }
            if (newParentName != null && !rootDir.getAbsolutePath().equalsIgnoreCase(DOWNLOAD_DIR) &&
                    !newParentName.equalsIgnoreCase(rootDir.getAbsolutePath())) {
                File newDir = new File(newParentName);
                if (rootDir.renameTo(newDir)) {
                    logger.info("Dir renamed to : " + rootDir.getAbsolutePath());
                }
            }
        } else {
            throw new RuntimeException("Not valid root dir");
        }
    }
}
