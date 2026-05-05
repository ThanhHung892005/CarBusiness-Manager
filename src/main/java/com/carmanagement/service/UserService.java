package com.carmanagement.service;

import com.carmanagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Page<User> search(String keyword, String roleName, Pageable pageable);
    User findById(Long id);
    User save(User user);
}
