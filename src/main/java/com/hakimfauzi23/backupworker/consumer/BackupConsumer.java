package com.hakimfauzi23.backupworker.consumer;

import com.hakimfauzi23.backupworker.dto.FilePathDTO;
import com.hakimfauzi23.backupworker.service.SFTPService;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class BackupConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(BackupConsumer.class);

    @Autowired
    private SFTPService sftpService;


    @RabbitListener(queues = "${spring.rabbitmq.queue.name}")
    public void backupFile(
            FilePathDTO filePathDTO,
            Channel channelMQ,
            @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {

        ChannelSftp channelSftp = sftpService.conncectSFTP();

        if (channelSftp != null) {
            SftpATTRS dirExists = sftpService.isDirExists(filePathDTO.getFileDirectoryOnly(), channelSftp);
            if (dirExists == null) {
                try {
                    sftpService.createRemoteDir(filePathDTO.getFileDirectoryOnly(), channelSftp);
                } catch (SftpException e) {
                    throw new RuntimeException(e);
                }
            }
            sftpService.backupFile(filePathDTO, channelSftp);
            channelMQ.basicAck(tag, false);
            channelSftp.disconnect();
            LOGGER.info(String.format("Successfully Backup : %s", filePathDTO.getFilePath()));
        } else {
            channelMQ.basicNack(tag, false, true);
            LOGGER.info("SFTP FAILURE - REQUEUE MESSAGE...");
        }

    }

}
