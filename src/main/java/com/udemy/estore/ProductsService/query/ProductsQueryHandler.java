package com.udemy.estore.ProductsService.query;

import com.udemy.estore.ProductsService.core.data.ProductEntity;
import com.udemy.estore.ProductsService.core.data.ProductsRepository;
import com.udemy.estore.ProductsService.query.rest.ProductRestModel;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductsQueryHandler {

    private final ProductsRepository productsRepository;

    public ProductsQueryHandler(ProductsRepository productsRepository){
        this.productsRepository = productsRepository;
    }

    @QueryHandler
    public List<ProductRestModel> findProducts(FindProductsQuery query){
        List<ProductRestModel> productsRest = new ArrayList<>();
        List<ProductEntity> storedProducts = productsRepository.findAll();
        for (ProductEntity productEntity : storedProducts){
            ProductRestModel productRestModel = new ProductRestModel();
            BeanUtils.copyProperties(productEntity, productRestModel);
            productsRest.add(productRestModel);
        }
        return productsRest;
    }
}
