package io.github.susimsek.springbootgraalvmnativeexample.config.client

import io.github.susimsek.springbootgraalvmnativeexample.client.TodoClient
import io.github.susimsek.springbootgraalvmnativeexample.config.logging.filter.WebClientLoggingFilter
import io.netty.channel.ChannelOption
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory
import reactor.netty.http.client.HttpClient

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(WebClientProperties::class)
class WebClientConfig(
    private val webClientProperties: WebClientProperties
) {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    fun webClientBuilder(
        customizerProvider: ObjectProvider<WebClientCustomizer>,
        webClientLoggingFilter: WebClientLoggingFilter
    ): WebClient.Builder {
        val httpClient = HttpClient.create()
            .responseTimeout(webClientProperties.readTimeout)
            .option(
                ChannelOption.CONNECT_TIMEOUT_MILLIS,
                webClientProperties.connectTimeout?.toMillis()?.toInt()
            )

        val builder = WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .filter(webClientLoggingFilter)

        customizerProvider.orderedStream().forEach { customizer -> customizer.customize(builder) }

        return builder
    }

    @Bean
    fun todoClient(webClientBuilder: WebClient.Builder): TodoClient {
        val todoClientConfig = webClientProperties.clients["todoClient"]
            ?: error("todoClient configuration is missing")
        val webClient = webClientBuilder.baseUrl(
            todoClientConfig.url
        ).build()
        val factory = HttpServiceProxyFactory.builderFor(WebClientAdapter.create(webClient))
            .build()
        return factory.createClient(TodoClient::class.java)
    }
}
