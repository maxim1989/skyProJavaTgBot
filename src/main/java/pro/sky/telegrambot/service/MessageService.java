package pro.sky.telegrambot.service;

public interface MessageService {
    public void send(Long chatId, String message, String userName);

    public void sendError(Long chatId);
}
