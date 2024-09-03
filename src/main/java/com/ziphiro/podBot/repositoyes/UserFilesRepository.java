package com.ziphiro.podBot.repositoyes;

import com.ziphiro.podBot.entityes.UserFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserFilesRepository extends JpaRepository<UserFile, Long> {


}
