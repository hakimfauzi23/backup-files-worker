package com.hakimfauzi23.backupworker.service.impl;

import com.hakimfauzi23.backupworker.dto.FilePathDTO;
import com.hakimfauzi23.backupworker.service.SFTPService;
import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
public class SFTPServiceImpl implements SFTPService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SFTPService.class);

    @Value("${backup.server.hostname}")
    private String hostname;

    @Value("${backup.server.port}")
    private Integer port;

    @Value("${backup.server.username}")
    private String username;

    @Value("${backup.server.password}")
    private String password;

    @Value("${backup.server.directory}")
    private String backupServerBaseDir;

    @Value("${source.server.directory}")
    private String sourceServerBaseDir;

    @Value("${application.backup.today}")
    private Boolean backupMode;

    @Override
    public ChannelSftp conncectSFTP() {
        try {
            JSch jSch = new JSch();
            Session session = jSch.getSession(username, hostname, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            Channel channel = session.openChannel("sftp");
            channel.connect();
            return (ChannelSftp) channel;
        } catch (JSchException e) {
            LOGGER.error(e.getMessage());
            return null;
        }
    }

    @Override
    public void backupFile(FilePathDTO filePathDTO, ChannelSftp channelSftp) {
        String sourceFilePath = sourceServerBaseDir + "/" + filePathDTO.getFilePath();
        String destinationFilePath = backupServerBaseDir + "/" + filePathDTO.getFileDirectoryOnly();

        try {
            channelSftp.put(sourceFilePath, destinationFilePath);
        } catch (SftpException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Path> getFilesList(String srcDirectory) {
        Path directory = Paths.get(srcDirectory);

        try {
            LocalDate today = LocalDate.now();
            LocalDate yesterday = today.minusDays(1);

            LocalDate fileTimeForBackupMode = (backupMode) ? today : yesterday;

            return Files.walk(directory)
                    .filter(Files::isRegularFile)
                    .filter(path -> {
                        try {
                            long l = Files.getLastModifiedTime(path).toMillis();
                            LocalDate lastModifiedDate = LocalDate.ofInstant(Instant.ofEpochMilli(l), ZoneId.systemDefault());

                            return lastModifiedDate.equals(fileTimeForBackupMode);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        return false;
                    }).toList();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public SftpATTRS isDirExists(String dstDirectory, ChannelSftp channelSftp) {
        try {
            return channelSftp.stat(backupServerBaseDir + dstDirectory);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void createRemoteDir(String dstDirectory, ChannelSftp channelSftp) throws SftpException {
        String filePath = backupServerBaseDir + dstDirectory;
        String[] folders = filePath.split("/");
        for (int i = 0; i < folders.length; i++) {
            if (!folders[i].isEmpty()) {
                String folder;
                if (i == 1 || i == 0) {
                    folder = "/" + folders[i];
                } else {
                    folder = folders[i];
                }
                try {
                    channelSftp.cd(folder);
                } catch (SftpException e) {
                    channelSftp.mkdir(folder);
                    channelSftp.cd(folder);
                }

            }
        }
    }
}
