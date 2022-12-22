package com.udemy.estore.ProductsService.command.interceptors;

import com.udemy.estore.ProductsService.command.CreateProductCommand;
import com.udemy.estore.ProductsService.core.data.ProductLookupEntity;
import com.udemy.estore.ProductsService.core.data.ProductLookupRepository;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiFunction;

@Component
public class CreateProductCommandInterceptor implements MessageDispatchInterceptor<CommandMessage<?>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateProductCommandInterceptor.class);
    private final ProductLookupRepository productLookupRepository;

    public CreateProductCommandInterceptor(ProductLookupRepository productLookupRepository){
        this.productLookupRepository = productLookupRepository;
    }

    @Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(List<? extends CommandMessage<?>> list) {
        return (index, command) -> {
            LOGGER.info("Intercepted command : " + command.getPayloadType());

            if (CreateProductCommand.class.equals(command.getPayloadType())){

                CreateProductCommand createProductCommand = (CreateProductCommand) command.getPayload();

                // Check if i already have a product Lookup entity
                ProductLookupEntity productLookupEntity = productLookupRepository.findByProductIdOrTitle(createProductCommand.getProductId(), createProductCommand.getTitle());
                if (productLookupEntity != null){
                    throw  new IllegalStateException(String.format("Product with productId %s or title %s already exist", createProductCommand.getProductId(), createProductCommand.getTitle()));
                }
//                if (createProductCommand.getPrice().compareTo(BigDecimal.ZERO) <= 0){
//                    throw new IllegalArgumentException("Price cannot be less or equal than zero");
//                }
//
//                if (createProductCommand.getTitle() == null
//                        || createProductCommand.getTitle().isBlank()){
//                    throw new IllegalArgumentException("Title cannot be empty");
//                }


            }
            return command;
        };
    }
}
