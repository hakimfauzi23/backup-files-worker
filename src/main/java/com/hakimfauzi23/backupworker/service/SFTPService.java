package com.hakimfauzi23.backupworker.service;

import com.hakimfauzi23.backupworker.dto.FilePathDTO;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

import java.nio.file.Path;
import java.util.List;

public interface SFTPService {

    ChannelSftp conncectSFTP();

    void backupFile(FilePathDTO filePathDTO, ChannelSftp channelSftp);

    List<Path> getFilesList(String srcDirectory);

    SftpATTRS isDirExists(String dstDirectory, ChannelSftp channelSftp);

    void createRemoteDir(String dstDirectory, ChannelSftp channelSftp) throws SftpException;

}
