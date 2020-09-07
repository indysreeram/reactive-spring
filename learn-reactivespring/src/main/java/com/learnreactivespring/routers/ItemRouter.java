package com.learnreactivespring.routers;

import com.learnreactivespring.handlers.ItemHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static com.learnreactivespring.constants.ItemConstant.ITEM_FUNCTIONAL_END_POINT_v1;
import static com.learnreactivespring.constants.ItemConstant.ITEM_STREAM_FUNCTIONAL_END_POINT_v1;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_STREAM_JSON_VALUE;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;


@Configuration
public class ItemRouter {

    @Bean
    public RouterFunction<ServerResponse> itemsRoute(ItemHandler itemHandler){

        return RouterFunctions
                .route(GET(ITEM_FUNCTIONAL_END_POINT_v1).and(accept(APPLICATION_JSON)),
                        itemHandler::getAllItems)
                        .andRoute(GET(ITEM_FUNCTIONAL_END_POINT_v1.concat("/{id}"))
                                .and(accept(APPLICATION_JSON)),itemHandler::getItem)
                        .andRoute(POST(ITEM_FUNCTIONAL_END_POINT_v1).and(accept(APPLICATION_JSON)),
                         itemHandler::createItem)
                        .andRoute(DELETE(ITEM_FUNCTIONAL_END_POINT_v1.concat("/{id}"))
                        .and(accept(APPLICATION_JSON)),
                                itemHandler::deleteItem)
                        .andRoute(PUT(ITEM_FUNCTIONAL_END_POINT_v1.concat("/{id}")).and(accept(APPLICATION_JSON)),
                                itemHandler::updateItem);



    }

    @Bean
    public  RouterFunction<ServerResponse> errorRoute(ItemHandler itemHandler) {

        return RouterFunctions
                .route(GET("/fun/runtimeException").and(accept(APPLICATION_JSON)),
                        itemHandler::itemException);
    }

    @Bean
    public RouterFunction<ServerResponse> itemStreamRoute(ItemHandler itemHandler) {

        return RouterFunctions
                .route(GET(ITEM_STREAM_FUNCTIONAL_END_POINT_v1).and(accept(APPLICATION_JSON)),
                        itemHandler::itemsStream);
    }

  }
