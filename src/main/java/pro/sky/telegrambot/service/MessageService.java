package pro.sky.telegrambot.service;

public interface MessageService {
    public void send(Long chatId, String message);

    public void sendError(Long chatId);
}
