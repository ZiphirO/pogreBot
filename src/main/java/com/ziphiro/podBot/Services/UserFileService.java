package com.ziphiro.podBot.Services;

import com.ziphiro.podBot.entityes.UserFile;
import com.ziphiro.podBot.repositoyes.UserFilesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserFileService {

    @Autowired
    private UserFilesRepository userFilesRepository;

    public UserFile initUserFile(UserFile file){
        return userFilesRepository.save(file);
    }

}
