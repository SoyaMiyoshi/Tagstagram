package com.example.demo.service;

import java.util.List;

import com.example.demo.domein.Uploaded;
import com.example.demo.repository.UploadedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UploadedService {
    @Autowired
    private UploadedRepository uploadedRepository;

    public List<Uploaded> findAll() {
        return uploadedRepository.findAll();
    }

    public Uploaded findOne(Long id) {
        return uploadedRepository.findById(id).orElse(null);
    }

    public Uploaded save(Uploaded uploaded) {
        return uploadedRepository.save(uploaded);
    }

    public void delete(Long id) {
        uploadedRepository.deleteById(id);
    }

    public List<Uploaded> finduploadeds(String query) {
        return uploadedRepository.findAllByOwnerLike("%" + query + "%");
    }

}
