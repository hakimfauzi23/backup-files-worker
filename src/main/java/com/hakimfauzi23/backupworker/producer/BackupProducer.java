package com.hakimfauzi23.backupworker.producer;

import com.hakimfauzi23.backupworker.dto.FilePathDTO;
import com.hakimfauzi23.backupworker.service.SFTPService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Component
public class BackupProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(BackupProducer.class);

    @Value("${source.server.directory}")
    private String sourceDir;

    @Autowired
    private SFTPService sftpService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.exchange.name}")
    private String exchange;

    @Value("${spring.rabbitmq.routing.key}")
    private String routingKey;

    @Value("${application.backup.scheduler.cron}")
    private String cronExpression;

    private static boolean isToday(FileTime fileTime) {
        LocalDateTime lastModifiedDateTime = fileTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDate today = LocalDate.now();

        return lastModifiedDateTime.toLocalDate().isEqual(today);
    }

    private static boolean isYesterday(FileTime fileTime) {
        LocalDateTime lastModifiedTime = fileTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDate yesterday = LocalDate.now().minusDays(1);

        return lastModifiedTime.toLocalDate().isEqual(yesterday);
    }

    @Scheduled(cron = "${application.backup.scheduler.cron}")
    public void filesPathProduce() throws IOException {

        List<Path> filesList = sftpService.getFilesList(sourceDir);
        LOGGER.info(String.valueOf(filesList.size()));
        filesList.forEach(System.out::println);
        if (filesList.size() > 0) {
            for (Path files : filesList) {
                String strFilePath = String.valueOf(files);
                strFilePath = strFilePath.replace('\\', '/');

                FilePathDTO filePathDTO = new FilePathDTO();
                filePathDTO.setFilePath(strFilePath.substring(sourceDir.length() + 1));

                rabbitTemplate.convertAndSend(exchange, routingKey, filePathDTO);
            }
        } else {
            LocalDate today = LocalDate.now();
            LOGGER.info(String.format("%s There is no files to backup", today));
        }
    }

}
