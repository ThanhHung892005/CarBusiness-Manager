package com.carmanagement.service.impl;

import com.carmanagement.entity.Showroom;
import com.carmanagement.exception.ResourceNotFoundException;
import com.carmanagement.repository.ShowroomRepository;
import com.carmanagement.service.ShowroomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShowroomServiceImpl implements ShowroomService {

    private final ShowroomRepository showroomRepository;

    @Override
    public List<Showroom> findAllActive() {
        return showroomRepository.findByActiveTrue();
    }

    @Override
    public Showroom findById(Long id) {
        return showroomRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Showroom", id));
    }

    @Override
    @Transactional
    public Showroom save(Showroom showroom) {
        return showroomRepository.save(showroom);
    }
}
