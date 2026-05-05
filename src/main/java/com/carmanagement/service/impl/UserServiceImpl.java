package com.carmanagement.service.impl;

import com.carmanagement.entity.User;
import com.carmanagement.exception.ResourceNotFoundException;
import com.carmanagement.repository.UserRepository;
import com.carmanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Page<User> search(String keyword, String roleName, Pageable pageable) {
        return userRepository.search(
            keyword == null ? "" : keyword,
            roleName == null ? "" : roleName,
            pageable
        );
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng ID: " + id));
    }

    @Override
    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }
}
