package com.ehocam.api_gateway.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ehocam.api_gateway.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.email = :email AND u.authProvider = :authProvider")
    Optional<User> findByEmailAndAuthProvider(@Param("email") String email, @Param("authProvider") User.AuthProvider authProvider);

    @Query(value = "SELECT * FROM users WHERE devices::text LIKE %:fcmToken%", nativeQuery = true)
    Optional<User> findByFcmToken(@Param("fcmToken") String fcmToken);

    boolean existsByEmail(String email);
}
