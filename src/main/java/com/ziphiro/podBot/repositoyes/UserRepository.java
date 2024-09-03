package com.ziphiro.podBot.repositoyes;

import com.ziphiro.podBot.entityes.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@EnableJpaRepositories
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByName (String name);
    Optional<User> findByPass(String pass);
    Optional<User> findOneByEmailAndPass(String email, String pass);
    User findByEmail(String email);
}
