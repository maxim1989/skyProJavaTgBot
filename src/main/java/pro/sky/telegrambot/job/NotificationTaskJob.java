package pro.sky.telegrambot.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;
import pro.sky.telegrambot.service.MessageService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static java.util.concurrent.TimeUnit.MINUTES;

@Service
public class NotificationTaskJob {
    private final NotificationTaskRepository notificationTaskRepository;
    private final MessageService messageService;

    public NotificationTaskJob(
            NotificationTaskRepository notificationTaskRepository,
            MessageService messageService
    ) {
        this.notificationTaskRepository = notificationTaskRepository;
        this.messageService = messageService;
    }

    @Scheduled(fixedRate = 1, timeUnit = MINUTES)
    public void sendNotifications() {
        LocalDateTime currentDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

        List<NotificationTask> notificationTasks = notificationTaskRepository
                .findAllByNotificationDateTime(currentDateTime);

        for (NotificationTask notificationTask : notificationTasks) {
            messageService.send(notificationTask.getChildId(), notificationTask.getMessageText());
        }
    }
}
