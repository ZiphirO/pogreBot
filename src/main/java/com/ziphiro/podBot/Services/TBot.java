package com.ziphiro.podBot.Services;

import com.ziphiro.podBot.DTO.UserDTO;
import com.ziphiro.podBot.entityes.User;
import com.ziphiro.podBot.entityes.UserFile;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@PropertySource("classpath:props.yml")
public class TBot extends TelegramLongPollingBot {
    private final String storagePath = "/home/ziphiro/myBotStorage/";
    @Value("${bot-token}")
    private String botToken;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy:HH.mm.ss");
    @Autowired
    private UserService userService;
    @Autowired
    private UserFileService fileService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @SneakyThrows
    @Override
    @Async
    public void onUpdateReceived(Update update) {
        String userName = update.getMessage().getFrom().getUserName();
            Path userDirPath = Path.of(storagePath + userName);
            long chatId = update.getMessage().getChatId();
        if (update.hasMessage() && update.getMessage().hasText()){

            String messageText = update.getMessage().getText();

            switch (messageText){
                case "/start" -> startCommandReceived(chatId, userName);
                //case "/register" -> ;
                case "/storage" -> sendMessage(chatId, "you can use your browser version of storage if follow " +
                        "this link: http://localhost:8080");
                default -> defaultCase(chatId, userName, messageText);
            }
        }

           if (update.hasMessage() &&
                   update.getMessage().hasDocument() &&
                   userService.checkUser(userName)){
                var docId = update.getMessage().getDocument().getFileId();
                var docName = LocalDateTime.now().format(formatter) + update.getMessage().getDocument().getFileName();
                var docPath = storagePath + userName + "/" + docName;

                fileService.initUserFile(UserFile.builder().fileName(docName).filePath(docPath)
                        .creator(userName).build());
                uploadFile(userName, docName, docId);
            }
            if (update.hasMessage() && update.getMessage().hasPhoto()
            && userService.checkUser(userName)){
                var photoName = LocalDateTime.now().format(formatter) +
                        update.getMessage().getPhoto().getLast().getFileUniqueId() + ".jpg";
                var photoId = update.getMessage().getPhoto().getLast().getFileId();
                var photoPath = storagePath + userName + "/" + photoName;

                fileService.initUserFile(UserFile.builder().fileName(photoName)
                        .filePath(photoPath).creator(userName).build());
                uploadFile(userName, photoName, photoId);
            }
            if (update.hasMessage() && update.getMessage().hasVideo()
            && userService.checkUser(userName)){
                var videoName = LocalDateTime.now().format(formatter) + update.getMessage().getVideo().getFileName();
                var videoId = update.getMessage().getVideo().getFileId();
                var videoPath = storagePath + userName + "/" + videoName;

                fileService.initUserFile(UserFile.builder().fileName(videoName)
                        .filePath(videoPath).creator(userName).build());
                uploadFile(userName, videoName, videoId);
            }
        }

    @Override
    public String getBotUsername() {
        return "podBot";
    }
    public TBot(DefaultBotOptions options, String botToken){
        super(options, botToken);
    }
    private void startCommandReceived(long chatId, String name){
        if (userService.checkUser(name)){
            sendMessage(chatId, "have a nice time " + name);
        } else  {
            String request = "Welcome to pogreb_OK, " + name + "!" + "i'm a simple bot for data operations " +
                    "you are not registered in pogreb_OK, please Enter your 1)email and 2)password" +
                    " separated by a space for your new account. You can log in to browser version of pogreb_OK " +
                    "with your email or telegram user name and password";
            sendMessage(chatId, request);
        }
    }
    private void registerCommandReceived(String userName, String email, String pass){
        userService.initUser(new User(null, userName, email, passwordEncoder.encode(pass)));
    }

    private void defaultCase (long chatId, String name, String text) throws IOException {
        if (!userService.checkUser(name)) {
            String[] userReg = text.split(" ");
            userService.initUser(User.builder().name(name).pass(passwordEncoder.encode(userReg[1])).email(userReg[0]).build());
            Path userDirPath = Path.of(storagePath + name);
            Files.createDirectory(userDirPath);
            sendMessage(chatId, "nice, you are registered now");
        }
    }
    private void sendMessage(long chatId, String text){
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), text);
        try {
            sendApiMethod(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void uploadFile(String userName, String fileName, String fileId) throws IOException {
        URL url = new URL("https://api.telegram.org/bot" + botToken
                + "/getFile?file_id=" + fileId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        String res = reader.readLine();
        JSONObject jsonObject = new JSONObject(res);
        JSONObject path = jsonObject.getJSONObject("result");
        String file_path = path.getString("file_path");

        URL downoload = new URL("https://api.telegram.org/file/bot" + botToken
                + "/" + file_path);
        FileOutputStream fos = new FileOutputStream(storagePath + userName
                + "/" + fileName);
        System.out.println(userName + " Start upload");
        ReadableByteChannel rbc = Channels.newChannel(downoload.openStream());
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
        System.out.println(userName + " upload complete");
    }

    }





