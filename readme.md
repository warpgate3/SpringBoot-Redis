## Reactive Programing

요즘 리엑티브 또는 Rx(RxJava, RxJs 등..) 라는 용어를 많이 듣게 됩니다. 한마디로 말하면 논 블로킹 이벤트 기반 비동기 스트림 데이터 프로그래밍을 위한 패러다임입니다. 사실 이렇게 말해도 감은 잘 오지 않습니다. 사실 Reactive 한 프로그래밍 개발을 위해서 필요한 개념이나 API 등을 쉽게 배울수 있는건 아닌 것 같습니다. 그래서 이 문서에서 해볼 건 "그래서 실제로 Reactive 하게 프로그래밍을 하면 어떻게 되는데?" 라는 질문에 대답을 아주 간단한 코드로 알아보려 합니다. 물론 내용은 아 뭐 이런 게 되는구나 정도이지 이건 Reactive Programing의 극히 일부입니다.

## Spring 5(WebFlux) + Spring Boot 2.x

Spring 5 부터 Pivotal 의 Project Reactor라는 이름의 Reactive Programing을 위한 오픈소스 라이브러리들을 공식적으로 지원하고 있습니다. 특히 Spring boot2.x 에서 WebFlux + Netty를 이용해서 Reactive Webapplication을 개발할 수 있습니다.

## Reactive Redis

웹 어플리케이션이 각 Layer들이 Reactive 하게 개발됐더라도 Database 가 Reactive를 지원하지 않는다면 결국 Database 에서 blocking 되기 때문에 사실 의미가 없습니다. 이제 많은 JDBC API 에서 Non Blocking API 를 지원을 하거나 시작하고 있습니다. 하지만 오늘은 In Memory Data Key-Value stroage 인 Redis를 이용해서 진행할 것 입니다.

## 구현

### build.gradle

### BasicEmbeddedRedisConfig

별도의 설치 없이 SpringBoot에 Embedded 돼 있는 Redis를 활용하기 위한 JavaConfiguration 입니다.

### BasicRedisConfig

### BasicRouter

Spring5 부터는 웹어플리케이션의 개발을 위한 Servlet 명세가 아닌(사용은 가능합니다.) 새로운 웹프레임워크를 제공합니다. 기존의 Controller 가 아닌 RouterFunction, HandleFunction을 이용해서 함수형 스타일로 개발을 할 수 있습니다.

```
package info.m2sj.springfluxandredis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

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
                .GET("/reactive-list", serverRequest -> ServerResponse.ok().contentType(MediaType.TEXT_HTML).body(basicService.findReactorList(), String.class))
                .GET("/normal-list", serverRequest -> ServerResponse.ok().contentType(MediaType.TEXT_HTML).body(basicService.findNormalList(), String.class))
                .GET("/load", serverRequest -> {
                    basicService.loadData();
                    return ServerResponse.ok().body(BodyInserters.fromObject("Load Data Completed"));
                })
                .build();
    }
}
```

### BasicService

실제 Redis를 연결해서 데이터 조회를 하는 서비스 클래스입니다.

### application.yml

SpringBoot의 redis 연결정보 설정입니다.

### Data Load

10만건의 테스트 데이터를 적재합니다.

```
http://localhost:8080/data
```

### Blocking 호출

10만건의 데이터를 조회가 끝날 때까지 브라우저가 Pending 한다.

```
http://localhost:8080/normal-list
```

### Reactive 호출

10만건의 데이터를 실시간으로 Streaming 하게 응답 한다. 브라우저에서 pending 없이 실시간으로 처리한다.

```
http://localhost:8080/reactive-list
```

## 결론

리엑티브 프로그래밍 된 간단한 리스트 조회 API를 구현해봤습니다. 샘플 소스에는 10만건의 데이터를 테스트 했지만 100만건으로 테스트 해도 브라우저의 렌더링 시점은 그리 크게 늦지 않습니다. 100만건의 데이터를 한번의 조회하는 경우가 흔하지 않겠지만 그러한 큰 데이터를 조회하는데도 사용자에게 빠른 응답을 줄수 있다는 건 정말 매력적인 것 같습니다.

## Reference

_[https://spring.io/guides/gs/spring-data-reactive-redis/](https://spring.io/guides/gs/spring-data-reactive-redis/)_  
_[https://www.baeldung.com/spring-embedded-redis](https://www.baeldung.com/spring-embedded-redis)_  
_[https://jojoldu.tistory.com/297](https://jojoldu.tistory.com/297)_
