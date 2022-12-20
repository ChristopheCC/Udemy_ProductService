package com.udemy.estore.ProductService.query;

import com.udemy.estore.ProductService.core.data.ProductEntity;
import com.udemy.estore.ProductService.core.data.ProductsRepository;
import com.udemy.estore.ProductService.core.events.ProductCreatedEvent;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class ProductEventsHandler {

    private final ProductsRepository productsRepository;

    public ProductEventsHandler(ProductsRepository productsRepository){
        this.productsRepository = productsRepository;
    }

    @EventHandler
    public void on(ProductCreatedEvent event){
        ProductEntity productEntity = new ProductEntity();
        BeanUtils.copyProperties(event, productEntity);
        productsRepository.save(productEntity);
    }

}
