package pro.sky.telegrambot;

import com.pengrad.telegrambot.BotUtils;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import pro.sky.telegrambot.entities.NotificationTask;
import pro.sky.telegrambot.listener.TelegramBotUpdatesListener;
import pro.sky.telegrambot.repositories.NotificationTasksRepository;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class TelegramBotApplicationTests {

    @Mock
    TelegramBot telegramBot = Mockito.mock(TelegramBot.class);
    @Mock
    NotificationTasksRepository notificationTasksRepository = Mockito.mock(NotificationTasksRepository.class);

    @InjectMocks
    TelegramBotUpdatesListener out;


    @Test
    void contextLoads() {
    }

    @Test
    void sendActualTasksTest() {
        List<NotificationTask> expectedTasks = new ArrayList<>();
        NotificationTask testTasks1 = new NotificationTask();
        testTasks1.setNotificationDate(LocalDateTime.parse("19.08.2023 03:20",
                DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
        testTasks1.setChatId(123);
        testTasks1.setText("рандомная задача");
        expectedTasks.add(testTasks1);
        out.sendActualTasks(expectedTasks);

        ArgumentCaptor<SendMessage> sendMessageArgumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        Mockito.verify(telegramBot).execute(sendMessageArgumentCaptor.capture());
        SendMessage actualSendMessage = sendMessageArgumentCaptor.getValue();

        assertThat(actualSendMessage.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actualSendMessage.getParameters().get("text")).isEqualTo("19.08.2023 03:20 рандомная задача");
    }

    @Test
    void findActualTasksTest() {


    }



    @Test
    void createNotificationPositiveTest() throws URISyntaxException, IOException {
        List<NotificationTask> expectedTasks = new ArrayList<>();
        NotificationTask testTasks1 = new NotificationTask();
        testTasks1.setNotificationDate(LocalDateTime.parse("19.08.2023 03:20",
                DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
        testTasks1.setChatId(123);
        testTasks1.setText("рандомная задача");
        expectedTasks.add(testTasks1);
        ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
        out.process(initUpdatesList());
        Mockito.verify(telegramBot).execute(argumentCaptor.capture());
        SendMessage actual = argumentCaptor.getValue();
        out.createNotification(initUpdatesList());

        assertThat(out.createNotification(initUpdatesList()).get(0).getText())
                .isEqualTo(expectedTasks.get(0).getText());
        assertThat(out.createNotification(initUpdatesList()).get(0).getNotificationDate())
                .isEqualTo(expectedTasks.get(0).getNotificationDate());
        assertThat(actual.getParameters().get("chat_id")).isEqualTo(123L);
        assertThat(actual.getParameters().get("text")).isEqualTo("Задача создана");

    }


    private List<Update> initUpdatesList() throws URISyntaxException, IOException {
        String json = Files.readString(
                Paths.get(TelegramBotApplicationTests.class.getResource("upd_test.json").toURI()));
        Update update1 = getUpdate(json, "19.08.2023 03:20 рандомная задача");
        List<Update> updates = new ArrayList<>();
        updates.add(update1);
        return updates;
    }

    private Update getUpdate(String json, String replaced) {
        return BotUtils.fromJson(json.replace("%text%", replaced), Update.class);
    }
}
