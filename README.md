# Back Up Files Spring Boot Apps

## Overview
This Spring Boot app helps you back up files from one computer to another using a secure method called SSH. It also makes sure the folders on the second computer match the original ones. You can set when this backup happens by choosing a schedule, like every day or every week. Plus, it's smart – it only picks files that were changed today or yesterday. And, you can easily change when it considers a file "recently changed."

## Features
- **SSH Backup:** Securely transfer files between computers.
- **Folder Mirroring:** Dynamically create mirrored folder structures.
- **Flexible Scheduler:** Customize backup schedules with cron expressions.
- **Smart File Selection:** Back up only files modified today or yesterday, adjustable as needed.

## Getting Started
1. Clone the repository: `git clone https://github.com/hakimfauzi23/backup-files-worker.git`
2. Navigate to the project directory: `cd backup-files-worker`
3. Configure `application.properties`
```properties
## This is source folder that want to backup
source.server.directory=E:/PERSONAL/TEST-FILE

## This is config for destination Backup Server or VM
backup.server.hostname=
backup.server.port=
backup.server.username=
backup.server.password=
backup.server.directory=

## This is config for Rabbit-MQ 
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
spring.rabbitmq.queue.name=files_backup_queue
spring.rabbitmq.exchange.name=files_backup_queue_exchange
spring.rabbitmq.routing.key=files_backup_queue_routing_key
spring.rabbitmq.listener.simple.acknowledge-mode=manual

## The apps have two option, Backup files that modified today
## or yesterday, if today so make this prop true, and vice versa
application.backup.today=false

## For settings the scheduler, if the scheduler will run every minutes
## Or every 11 PM etc
application.backup.scheduler.cron=*/20 * * * * *
```
4. Start a rabbitMQ Application, here's my command for starting RabbitMQ on docker : `docker run --rm -it -p 15672:15672 -p 5672:5672 rabbitmq:3.10.5-management`
5. Build the application JAR File: `mvn clean package`
6. Run the application by runnning the JAR File : `java -jar backup-files-worker-1.0.0.jar`

## Dependencies

- [Java Development Kit (JDK)](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) - Required for running the application.
- [Spring Boot](https://spring.io/projects/spring-boot) - Framework for creating stand-alone, production-grade Spring-based applications.
- [RabbitMQ](https://www.rabbitmq.com/) - RabbitMQ Message broker for producer and consumer style in sending file information that want to upload
- [Java Secure Channel](http://www.jcraft.com/jsch/) - For SSH Backup the files and also operate SSH command from the app like `put`, `mkdirs`, `cd`, etc.
- [Spring Web](https://spring.io/guides/gs/spring-boot/) - Spring framework for creating and add message converter in Rabbit MQ Purposes.

## Contributing

Contributions are welcome! If you encounter any issues or have suggestions for improvements, please feel free to open an issue or submit a pull request.

## License

This project is licensed under the [MIT License](https://opensource.org/licenses/MIT) - see the [LICENSE.md](LICENSE.md) file for details.
