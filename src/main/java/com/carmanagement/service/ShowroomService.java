package com.carmanagement.service;

import com.carmanagement.entity.Showroom;

import java.util.List;

public interface ShowroomService {
    List<Showroom> findAllActive();
    Showroom findById(Long id);
    Showroom save(Showroom showroom);
}
