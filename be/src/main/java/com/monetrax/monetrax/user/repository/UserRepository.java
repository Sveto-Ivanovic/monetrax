package com.monetrax.monetrax.user.repository;

import com.monetrax.monetrax.user.entity.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    @Query("select count(u) > 0 from UserEntity u where u.userEmail = ?1")
    public boolean existsUserEmail(String userEmail);

    @Modifying
    @Transactional
    @Query("delete from UserEntity u where u.userId <> ?1")
    public void deleteAllUsersExceptSupperUser(UUID superId);

}
