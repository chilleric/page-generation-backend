package com.chillleric.page_generation.repository.user;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.chillleric.page_generation.repository.AbstractMongoRepo;

@Repository
public class UserRepositoryImpl extends AbstractMongoRepo implements UserRepository {

    @Override
    public Optional<List<User>> getUsers(Map<String, String> allParams, String keySort, int page, int pageSize,
            String sortField) {
        Query query = generateQueryMongoDB(allParams, User.class, keySort, sortField, page, pageSize);
        return replaceFind(query, User.class);
    }

    @Override
    public void insertAndUpdate(User user) {
        authenticationTemplate.save(user, "users");
    }

    @Override
    public long getTotalPage(Map<String, String> allParams) {
        return getTotalPage(allParams, User.class);
    }
}