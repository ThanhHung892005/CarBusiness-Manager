package com.carmanagement.repository;

import com.carmanagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.username = :username")
    Optional<User> findByUsernameWithRoles(@Param("username") String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query(value = """
        SELECT DISTINCT u FROM User u LEFT JOIN u.roles r
        WHERE (LOWER(u.username) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
            OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
            OR LOWER(u.email) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')))
        AND (:roleName = '' OR r.name = :roleName)
        """,
        countQuery = """
        SELECT COUNT(DISTINCT u) FROM User u LEFT JOIN u.roles r
        WHERE (LOWER(u.username) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
            OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%'))
            OR LOWER(u.email) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')))
        AND (:roleName = '' OR r.name = :roleName)
        """)
    Page<User> search(@Param("keyword") String keyword,
                      @Param("roleName") String roleName,
                      Pageable pageable);
}
