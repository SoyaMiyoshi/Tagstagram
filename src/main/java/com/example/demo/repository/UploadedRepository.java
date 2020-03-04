package com.example.demo.repository;

import com.example.demo.domein.Uploaded;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UploadedRepository extends JpaRepository<Uploaded, Long> {

    List<Uploaded> findAllByOwnerLike(String owner);

}