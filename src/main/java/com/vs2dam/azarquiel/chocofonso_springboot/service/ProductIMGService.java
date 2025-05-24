package com.vs2dam.azarquiel.chocofonso_springboot.service;

import com.vs2dam.azarquiel.chocofonso_springboot.domain.ProductIMG;
import com.vs2dam.azarquiel.chocofonso_springboot.repository.ProductIMGRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductIMGService {
    @Autowired
    private ProductIMGRepository productIMGRepository;

    public ProductIMG save(ProductIMG img) {
        return productIMGRepository.save(img);
    }
    public void deleteImageById(Long imageId) {
        productIMGRepository.deleteById(imageId);
    }





}
