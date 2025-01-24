package io.github.susimsek.springbootgraalvmnativeexample.config.client

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
import reactor.netty.http.client.HttpClient

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(WebClientProperties::class)
class WebClientConfig(
    private val webClientProperties: WebClientProperties
) {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    fun webClientBuilder(customizerProvider: ObjectProvider<WebClientCustomizer>): WebClient.Builder {
        val httpClient = HttpClient.create()
            .responseTimeout(webClientProperties.readTimeout)
            .option(
                ChannelOption.CONNECT_TIMEOUT_MILLIS,
                webClientProperties.connectTimeout?.toMillis()?.toInt()
            )

        val builder = WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(httpClient))

        customizerProvider.orderedStream().forEach { customizer -> customizer.customize(builder) }

        return builder
    }
}
