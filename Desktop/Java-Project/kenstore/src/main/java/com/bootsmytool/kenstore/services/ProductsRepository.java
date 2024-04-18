package com.bootsmytool.kenstore.services;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bootsmytool.kenstore.models.Product;

public interface ProductsRepository extends JpaRepository<Product, Integer>{

}
