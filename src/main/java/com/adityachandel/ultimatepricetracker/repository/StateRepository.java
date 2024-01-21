package com.adityachandel.ultimatepricetracker.repository;


import com.adityachandel.ultimatepricetracker.model.entity.StateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StateRepository extends JpaRepository<StateEntity, String> {

}
