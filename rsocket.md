


## RSocket Using Spring Boot
## 在`spring boot`中使用RSocket
## 作者：baeldung
Last modified: May 23, 2019

by baeldung Spring Boot 

### 1: 概述
1. Overview
###### `RSocket`应用层协议支持 `Reactive Streams`语义， 例如：用RSocket作为HTTP的一种替代方案。 
RSocket is an application protocol providing Reactive Streams semantics – it functions, for example, as an alternative to HTTP.
###### 在本教程中， 我们将查看`RSocket`用于spring boot中，尤其是springboot 如何帮助抽象出更低级别的RSocket API。
In this tutorial, we’re going to look at RSocket using Spring Boot, and specifically how it helps abstract away the lower-level RSocket API.
### 2: 依赖
###### 让我们从添加`spring-boot-starter-rsocket`依赖开始：
2. Dependencies
Let’s start with adding the spring-boot-starter-rsocket dependency:

```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-rsocket</artifactId>
</dependency>
```
###### 这个依赖会传递性的拉取RSocket相关的依赖，比如：`rsocket-core` 和 `rsocket-transport-netty`
This will transitively pull in RSocket related dependencies such as rsocket-core and rsocket-transport-netty.
### 3: 示例的应用程序
###### 现在让我继续我们的简单应用程序。为了突出RSocket提供的交互模式，我们打算创建一个交易应用程序， 交易应用程序包括客户端和服务器。
3. Sample Application
Now we’ll continue with our sample application. To highlight the interaction models RSocket provides, we’re going to create a trader application. Our trader application will consist of a client and a server.
### 3.1: 服务器设置 
###### 首先， 我们设置由springboot应用程序引导的`RSocket server`服务器。 因为我们有`spring-boot-starter-rsocket dependency`依赖，springboot会给我们自动配置`RSocket server`
###### 跟平常一样， 我们可以用属性驱动的方式修改`RSocket server`默认配置值。例如：我们通过增加如下配置在`application.properties中，来修改RSocket端口的
```
spring.rsocket.server.port=7000
```
###### 我也可以根据需要进一步修改服务器的其他属性
3.1. Server Setup
First, let’s set up the server, which will be a Spring Boot application bootstrapping an RSocket server.

Since we have the spring-boot-starter-rsocket dependency, Spring Boot autoconfigures an RSocket server for us. As usual with Spring Boot, we can change default configuration values for the RSocket server in a property-driven fashion.

For example, let’s change the port of our RSocket server by adding the following line to our application.properties file:
 
### 3.2：设置客户端
###### 接下来，我们来设置客户端，也是一个springboot应用程序。
###### 虽然springboot自动配置大部分RSocket相关的组件，我也要自定义一些对象来完成设置。
3.2. Client Setup
Next, let’s set up the client which will also be a Spring Boot application.

Although Spring Boot auto-configures most of the RSocket related components, we should also define some beans to complete the setup:

 ```
@Configuration
public class ClientConfiguration {
 
    @Bean
    public RSocket rSocket() {
        return RSocketFactory
          .connect()
          .mimeType(MimeTypeUtils.APPLICATION_JSON_VALUE, MimeTypeUtils.APPLICATION_JSON_VALUE)
          .frameDecoder(PayloadDecoder.ZERO_COPY)
          .transport(TcpClientTransport.create(7000))
          .start()
          .block();
    }
 
    @Bean
    RSocketRequester rSocketRequester(RSocketStrategies rSocketStrategies) {
        return RSocketRequester.wrap(rSocket(), MimeTypeUtils.APPLICATION_JSON, rSocketStrategies);
    }
}
```
###### 这儿我们正在创建RSocket客户端并且配置TCP端口为：7000. 注意： 这是我们预先配置的服务器端口。
###### 接下来我们定义了一个RSocket的装饰器对象` RSocketRequester`。 这个对象在我们跟`RSocket server`交互式会为我们提供帮助。
###### 定义这些对象配置后，我们还只是有了一个骨架。在接下来，我们将暴露不同的交互模式， 并看看springboot在这个地方如何帮助我的。
Here we’re creating the RSocket client and configuring it to use TCP transport on port 7000. Note that this is the server port we’ve configured previously.

Next, we’re defining an RSocketRequester bean which is a wrapper around RSocket. This bean will help us while interacting with the RSocket server.

After defining these bean configurations, we have a bare-bones structure.

Next, we’ll explore different interaction models and see how Spring Boot helps us there.

### 4: `RSocket` 和 `springboot`中的 `Request/Response`
###### 我们从`Request/Response`开始， 由于`HTTP`也使用这种通信方式，这很可能也是最常见的、最相似的交互模式。
###### 在这中交互模式里， 由客户端初始化通信并发送一个请求。之后，服务器端执行操作并返回一个响应给客户端--这时通信完成。
###### 在我们的交易应用程序里， 一个客户端询问一个给定的股票的当前的市场数据。 作为回复，服务器会传递请求的数据。


4. Request/Response with RSocket and Spring Boot
Let’s start with Request/Response. This is probably the most common and familiar interaction model since HTTP also employs this type of communication.

In this interaction model, the client initiates the communication and sends a request. Afterward, the server performs the operation and returns a response to the client – thus the communication completes.

In our trader application, a client will ask for the current market data of a given stock. In return, the server will pass the requested data.
#### 4.1：服务器
###### 在服务器这边，我们首先应该创建一个`controller` 来持有我们的处理器方法。 我们会使用 `@MessageMapping`注解来代替像SpringMVC中的`@RequestMapping`或者`@GetMapping`注解
4.1. Server
On the server side, we should first create a controller to hold our handler methods. But instead of @RequestMapping or @GetMapping annotations like in Spring MVC, we will use the @MessageMapping annotation:
```
@Controller
public class MarketDataRSocketController {
 
    private final MarketDataRepository marketDataRepository;
 
    public MarketDataRSocketController(MarketDataRepository marketDataRepository) {
        this.marketDataRepository = marketDataRepository;
    }
 
    @MessageMapping("currentMarketData")
    public Mono<MarketData> currentMarketData(MarketDataRequest marketDataRequest) {
        return marketDataRepository.getOne(marketDataRequest.getStock());
    }
}
```
###### 那么让我们来考察我们的控制器。 我们将使用`@Controller`注解来定义一个控制器来处理进入RSocket的请求。 另外，注解`@MessageMapping`让我们定义我们感兴趣的路由和如何响应一个请求。
###### 在这个示例中， 服务器监听路由`currentMarketData`， 并响应一个单一的结果`Mono<MarketData>`给客户端。
So let’s investigate our controller.

We’re using the @Controller annotation to define a handler which should process incoming RSocket requests. Additionally, the @MessageMapping annotation lets us define which route we’re interested in and how to react upon a request.

In this case, the server listens for the currentMarketData route, which returns a single result to the client as a Mono<MarketData>.
#### 4.2： 客户端
###### 接下来， 我们的RSocket客户端应该询问一直股票的价格并得到一个单一的响应。
###### 为了初始化请求， 我们该使用`RSocketRequester`类，如下：
4.2. Client
Next, our RSocket client should ask for the current price of a stock and get a single response.

To initiate the request, we should use the RSocketRequester class:
```
@RestController
public class MarketDataRestController {
 
    private final RSocketRequester rSocketRequester;
 
    public MarketDataRestController(RSocketRequester rSocketRequester) {
        this.rSocketRequester = rSocketRequester;
    }
 
    @GetMapping(value = "/current/{stock}")
    public Publisher<MarketData> current(@PathVariable("stock") String stock) {
        return rSocketRequester
          .route("currentMarketData")
          .data(new MarketDataRequest(stock))
          .retrieveMono(MarketData.class);
    }
}
```
###### 注意：在们我的示例中，RSocket客户端也是一个`REST`风格的`controller`，以此来访问我们的RSocket服务器。因此，我们使用` @RestController`和`@GetMapping`注解来定义我我们的请求/响应端点。
###### 在端点方法中， 我们使用的是类`RSocketRequester`并指定了路由。 事实上，这个是服务器端`RSocket`所期望的路由，然后我们传递请求数据。最后， 当我我们调用`retrieveMono()`方法的时候，Springboot会帮我们初始化一个请求/响应交互。
Note that in our case, the RSocket client is also a REST controller from which we call our RSocket server. So, we’re using @RestController and @GetMapping to define our request/response endpoint.

In the endpoint method, we’re using RSocketRequester and specifying the route. In fact, this is the route which the RSocket server expects.
 Then we’re passing the request data. And lastly, when we call the retrieveMono() method, Spring Boot initiates a request/response interaction.
### 5: `RSocket`和`Spring Boot`中的`Fire And Forget`模式
###### 接下来我们将查看 `Fire And Forget`交互模式。正如名字提示的一样，客户端发送一个请求给服务器，当时不期望服务器的一个返回响应回来。
###### 在我们的交易程序中， 一些客户端会作为数据资源服务，并且推送市场数据给服务器端。
5. Fire And Forget with RSocket and Spring Boot
Next, we’ll look at the fire-and-forget interaction model. As the name implies, the client sends a request to the server but doesn’t expect a response back.

In our trader application, some clients will serve as a data source and will push market data to the server.
#### 5.1：服务器端
###### 我们来创建另外一个端点在我们的服务器应用程序中，如下：
5.1. Server
Let’s create another endpoint in our server application:
```
@MessageMapping("collectMarketData")
public Mono<Void> collectMarketData(MarketData marketData) {
    marketDataRepository.add(marketData);
    return Mono.empty();
}
```
###### 我们又一次定义了一个新的`@MessageMapping`路由为`collectMarketData`。此外， Spring Boot自动转换传入的负载为一个`MarketData`实例。
###### 但是，这儿最大的不同是我们返回一个`Mono<Void>`，因为客户端不需要服务器的返回。
Again, we’re defining a new @MessageMapping with the route value of collectMarketData. Furthermore, Spring Boot automatically converts the incoming payload to a MarketData instance.

The big difference here, though, is that we return a Mono<Void> since the client doesn’t need a response from us.
#### 5.2 客户端
###### 来看看我们如何初始化我们的`fire-and-forget`模式的请求。
###### 我们将创建另外一个REST风格的端点，如下：

5.2. Client
Let’s see how we can initiate our fire-and-forget request.

We’ll create another REST endpoint:
```
@GetMapping(value = "/collect")
public Publisher<Void> collect() {
    return rSocketRequester
      .route("collectMarketData")
      .data(getMarketData())
      .send();
}
```
###### 这儿我们指定路由和负载将是一个`MarketData`实例。 由于我们使用`send()`方法来代替`retrieveMono()`，所有交互模式变成了`fire-and-forget`模式。
Here we’re specifying our route and our payload will be a MarketData instance. Since we’re using the send() method to initiate the request instead of retrieveMono(), the interaction model becomes fire-and-forget.
### 6：`RSocket`和`Spring Boot`中的`Request Stream`
###### 请求流是一种更复杂的交互模式， 这个模式中客户端发送一个请求，但是在一段时间内从服务器端获取到多个响应。
###### 为了模拟这种交互模式， 客户端会询问给定股票的所有食材数据。
6. Request Stream with RSocket and Spring Boot
Request streaming is a more involved interaction model, where the client sends a request but gets multiple responses over the course of time from the server.

To simulate this interaction model, a client will ask for all market data of a given stock.
#### 6.1：服务器端
###### 我们从服务器端开始。 我们将添加另外一个消息映射方法，如下：
6.1. Server
Let’s start with our server. We’ll add another message mapping method:

```
@MessageMapping("feedMarketData")
public Flux<MarketData> feedMarketData(MarketDataRequest marketDataRequest) {
    return marketDataRepository.getAll(marketDataRequest.getStock());
}
```
###### 正如所见， 这个处理器方法跟其他的处理器方法非常类似。 不同的部分是我们返回一个`Flux<MarketData>`来代替`Mono<MarketData>`。 最后我们的RSocket服务器会返回多个响应给客户端。
As we can see, this handler method is very similar to the other ones. The different part is that we returning a Flux<MarketData> instead of a Mono<MarketData>. In the end, our RSocket server will send multiple responses to the client.
#### 6.2： 客户端
###### 在客户端这边， 我们该创建一个端点来初始化请求/响应通信，如下：
6.2. Client
On the client side, we should create an endpoint to initiate our request/stream communication:
```
@GetMapping(value = "/feed/{stock}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Publisher<MarketData> feed(@PathVariable("stock") String stock) {
    return rSocketRequester
      .route("feedMarketData")
      .data(new MarketDataRequest(stock))
      .retrieveFlux(MarketData.class);
}
```
###### 让我们来考察我们扥的RSocket请求。 首先我们定义了路由和请求负载。 然后，我们定义了使用`retrieveFlux()`调用的响应期望。这部分决定了交互模式。
###### 另外注意：由于我们的客户端也是`REST`风格的服务器，客户端也定义了响应媒介类型`MediaType.TEXT_EVENT_STREAM_VALUE`。
Let’s investigate our RSocket request.

First, we’re defining the route and request payload. Then, we’re defining our response expectation with the retrieveFlux() method call. This is the part which determines the interaction model.

Also note that, since our client is also a REST server, it defines response media type as MediaType.TEXT_EVENT_STREAM_VALUE.
### 7：异常的处理
###### 现在让我们看看在服务器程序中，如何以声明式的方式处理异常。 当处理请求/响应式， 我可以简单的使用`@MessageExceptionHandler`注解，如下：
7. Exception Handling
Now let’s see how we can handle exceptions in our server application in a declarative way.

When doing request/response, we can simply use the @MessageExceptionHandler annotation:
```
@MessageExceptionHandler
public Mono<MarketData> handleException(Exception e) {
    return Mono.just(MarketData.fromException(e));
}
```
###### 这里我们给异常处理方法标记注解为`@MessageExceptionHandler`。作为结果， 这个方法将处理所有类型的异常， 因为`Exception`是所有其他类型的异常的超类。
###### 我们也可以明确地创建更多的不同类型的，不同的异常处理方法。 这当然是请求/响应模式，并且我们返回的是`Mono<MarketData>`。我们期望这里的响应类型跟我们的交互模式的返回类型相匹配。
### 8：总结
###### 在本教程中， 我们介绍了springboot的RSocket支持，并详细列出了RSocket提供的不同交互模式。查看所有示例代码在GitHub上
Here we’ve annotated our exception handler method with @MessageExceptionHandler. As a result, it will handle all types of exceptions since the Exception class is the superclass of all others.

We can be more specific and create different exception handler methods for different exception types.

This is of course for the request/response model, and so we’re returning a Mono<MarketData>. We want our return type here to match the return type of our interaction model.

8. Summary
In this tutorial, we’ve covered Spring Boot’s RSocket support and detailed different interaction models RSocket provides.

Check out all the code samples over on GitHub.