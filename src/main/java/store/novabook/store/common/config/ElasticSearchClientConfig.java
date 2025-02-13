package store.novabook.store.common.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.RequiredArgsConstructor;

@Configuration
@ConditionalOnProperty(value = "elasticsearch.enabled", havingValue = "true")
@EnableElasticsearchRepositories(basePackages = "store.novabook.store.search")
@RequiredArgsConstructor
public class ElasticSearchClientConfig {

	@Value("${elasticsearch.enabled:true}") // 기본값 true
	private boolean elasticsearchEnabled;

	@Value("${elasticsearch.host}")
	private String host;

	@Value("${elasticsearch.port}")
	private int port;

	@Bean
	public ElasticsearchClient getRestClient() {
		if (!elasticsearchEnabled) {
			return null;
		}
		try {
			RestClient restClient = RestClient.builder(HttpHost.create(host + ":" + port)).build();
			ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
			return new ElasticsearchClient(transport);
		} catch (Exception e) {
			return null;
		}
	}

}