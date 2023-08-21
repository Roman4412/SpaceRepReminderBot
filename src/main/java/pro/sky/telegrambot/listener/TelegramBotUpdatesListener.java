package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entities.NotificationTask;
import pro.sky.telegrambot.repositories.NotificationTasksRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {
    private final Pattern pattern = Pattern.compile("([0-9.:\\s]{16})(\\s)([\\W+]+)");
    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private final NotificationTasksRepository repository;

    @Autowired
    private TelegramBot telegramBot;

    public TelegramBotUpdatesListener(NotificationTasksRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> logger.info("Processing update: {}", update.message().text()));
        createNotification(updates);
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    public List<NotificationTask> createNotification(List<Update> updates) {
        List<NotificationTask> tasks = new ArrayList<>();
        for (Update update : updates) {
            Matcher matcher = pattern.matcher(update.message().text());
            Long chatId = update.message().chat().id();
            if (matcher.matches()) {
                NotificationTask task = new NotificationTask();
                task.setChatId(chatId);
                task.setNotificationDate(LocalDateTime.parse(parseDateFromMessage(matcher),
                        DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
                task.setText(parseTextFromMessage(matcher));
                logger.info("create task: created " + task);
                repository.save(task);
                tasks.add(task);
                telegramBot.execute(new SendMessage(update.message().chat().id(), "Задача создана"));
            } else {
                telegramBot.execute(new SendMessage(update.message().chat().id(), "Некорректный формат"));
            }
        }
        return tasks;
    }

    public String parseDateFromMessage(Matcher matcher) {
        String date = matcher.group(1);
        logger.info("parse_date " + date);
        return date;
    }

    public String parseTextFromMessage(Matcher matcher) {
        String text = matcher.group(3);
        logger.info("parse_text " + text);
        return text;
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void findActualTasks() {
        LocalDateTime time = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        logger.info("findActualTasks started " + time);
        List<NotificationTask> tasks = repository.findActualTasks(time);
        if (tasks.size() > 0) {
            sendActualTasks(tasks);
            tasks.clear();
            logger.info("list of tasks cleared");
        }
    }

    public void sendActualTasks(List<NotificationTask> tasks) {
        for (NotificationTask t : tasks) {
            telegramBot.execute(new SendMessage(t.getChatId(),
                    t.getNotificationDate().toString() + " " + t.getText() + "\n"));
        }
        logger.info("sendActualTasks: tasks sent");
    }

}
