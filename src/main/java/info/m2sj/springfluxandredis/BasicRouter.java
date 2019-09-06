package info.m2sj.springfluxandredis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class BasicRouter {
    private final BasicService basicService;

    public BasicRouter(BasicService basicService) {
        this.basicService = basicService;
    }

    @Bean
    RouterFunction<ServerResponse> empRouterList() {
        return route()
                .GET("/list", accept(APPLICATION_JSON), serverRequest -> ServerResponse.ok().body(BodyInserters.fromObject("list!!")))
                .GET("/load", accept(APPLICATION_JSON), serverRequest -> {
                    basicService.loadData();
                    return ServerResponse.ok().body(BodyInserters.fromObject("Load Data Completed"));
                })
                .build();
    }
}
