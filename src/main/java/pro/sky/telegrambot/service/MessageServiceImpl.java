package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl implements MessageService {
    private final TelegramBot telegramBot;
    private Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

    public MessageServiceImpl(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @Override
    public void send(Long chatId, String messageText, String userName) {
        SendMessage message = new SendMessage(
                chatId,
                "Привет, " + userName + "!"
        );
        SendResponse response = telegramBot.execute(message);
        checkResponse(response);
    }

    @Override
    public void sendError(Long chatId) {
        SendMessage message = new SendMessage(
                chatId,
                "Неверный формат сообщения :("
        );
        SendResponse response = telegramBot.execute(message);
        checkResponse(response);
    }

    private void checkResponse(SendResponse response) {
        if (response.isOk()) {
            logger.info("Message was sent successfully");
        } else {
            logger.error("Message was not sent");
        }
    }
}
