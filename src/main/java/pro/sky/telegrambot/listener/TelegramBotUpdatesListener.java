package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;
import pro.sky.telegrambot.service.MessageService;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final TelegramBot telegramBot;
    private final MessageService messageService;
    private final Pattern INCOMING_MESSAGE_PATTERN = Pattern
            .compile("(\\d{2}\\.\\d{2}\\.\\d{4}\\s\\d{2}:\\d{2})(\\s+)(.+)");
    private final NotificationTaskRepository notificationTaskRepository;
    private final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private final String HELLO_TEXT = "Привет!";

    public TelegramBotUpdatesListener(
            TelegramBot telegramBot,
            MessageService messageService,
            NotificationTaskRepository notificationTaskRepository
    ) {
        this.telegramBot = telegramBot;
        this.messageService = messageService;
        this.notificationTaskRepository = notificationTaskRepository;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            String text = update.message().text();
            Long chatId = update.message().chat().id();
            if ("/start".equals(text)) {
                messageService.send(chatId, HELLO_TEXT);
            } else {
                Matcher matcher = INCOMING_MESSAGE_PATTERN.matcher(text);

                if (matcher.matches()) {
                    LocalDateTime notificationDateTime = LocalDateTime.parse(matcher.group(1), DATE_FORMAT);
                    String notificationMessage = matcher.group(3);

                    NotificationTask notificationTask = new NotificationTask();
                    notificationTask.setChildId(chatId);
                    notificationTask.setMessageText(notificationMessage);
                    notificationTask.setNotificationDateTime(notificationDateTime);

                    notificationTaskRepository.save(notificationTask);
                } else {
                    messageService.sendError(chatId);
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

}
