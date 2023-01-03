package com.udemy.estore.ProductsService.query;

import com.udemy.estore.ProductsService.core.data.ProductEntity;
import com.udemy.estore.ProductsService.core.data.ProductsRepository;
import com.udemy.estore.ProductsService.core.events.ProductCreatedEvent;
import com.udemy.estore.core.events.ProductReservationCancelledEvent;
import com.udemy.estore.core.events.ProductReservedEvent;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.ResetHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@ProcessingGroup("product-group")
public class ProductEventsHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductEventsHandler.class);

    private final ProductsRepository productsRepository;

    public ProductEventsHandler(ProductsRepository productsRepository){
        this.productsRepository = productsRepository;
    }

    @ExceptionHandler(resultType = Exception.class)
    public void handle(Exception exception) throws Exception {
        throw exception;
    }

    @ExceptionHandler(resultType = IllegalArgumentException.class)
    public void handle(IllegalArgumentException exception){
        // Log error Message
    }

    @EventHandler
    public void on(ProductCreatedEvent event) throws Exception {
        ProductEntity productEntity = new ProductEntity();
        BeanUtils.copyProperties(event, productEntity);
        try {
            productsRepository.save(productEntity);
        } catch (IllegalArgumentException ex){
            ex.printStackTrace();
        }
//        if (true)
//            throw new Exception("An error took place in the Event Handler class");
//        }

    }

    @EventHandler
    public void on(ProductReservedEvent productReservedEvent){
        ProductEntity productEntity = productsRepository.findByProductId(productReservedEvent.getProductId());
        LOGGER.info("ProductReservedEvent : current product quantity is : " + productEntity.getQuantity());
        productEntity.setQuantity(productEntity.getQuantity() - productReservedEvent.getQuantity());
        productsRepository.save(productEntity);
        LOGGER.info("ProductReservedEvent : new product quantity is : " + productEntity.getQuantity());
        LOGGER.info("ProductReservedEvent is called for orderId : " + productReservedEvent.getOrderId()
                + " and productId : "+ productReservedEvent.getProductId());
    }

    @EventHandler
    public void on(ProductReservationCancelledEvent productReservationCancelledEvent){
        ProductEntity currentStoredProduct = productsRepository.findByProductId(productReservationCancelledEvent.getProductId());
        LOGGER.info("ProductReservationCancelledEvent : current product quantity is : " + currentStoredProduct.getQuantity());
        int newQuantity = currentStoredProduct.getQuantity() + productReservationCancelledEvent.getQuantity();
        currentStoredProduct.setQuantity(newQuantity);
        productsRepository.save(currentStoredProduct);
        LOGGER.info("ProductReservationCancelledEvent : new product quantity is : " + currentStoredProduct.getQuantity());

    }

    @ResetHandler
    public void reset() {
        productsRepository.deleteAll();
    }
}
