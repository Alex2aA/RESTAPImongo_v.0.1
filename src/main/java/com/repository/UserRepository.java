package com.repository;

import com.model.SystemUser;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<SystemUser, ObjectId> {
    Optional<SystemUser> findByLogin(String login);
    boolean existsByLogin(String login);
    void deleteByLogin(String login);
}