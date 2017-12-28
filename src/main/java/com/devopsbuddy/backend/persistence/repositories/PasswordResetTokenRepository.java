package com.devopsbuddy.backend.persistence.repositories;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.devopsbuddy.backend.persistence.domain.backend.PasswordResetToken;

@Repository
public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetToken, Long> {

    public static final String PASSWORD_RESET_TOKEN_SQL = "select ptr from PasswordResetToken ptr " + "inner join ptr.user u "
                                                          + "where u.id = ?1 ";

    public PasswordResetToken findByToken(String token);

    @Query(PASSWORD_RESET_TOKEN_SQL)
    public Set<PasswordResetToken> findAllByUserId(long userId);

}
