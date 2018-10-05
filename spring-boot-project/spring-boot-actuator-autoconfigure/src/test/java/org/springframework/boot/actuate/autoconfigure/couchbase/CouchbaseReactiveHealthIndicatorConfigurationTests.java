/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.boot.actuate.autoconfigure.couchbase;

import java.time.Duration;

import org.junit.Test;

import org.springframework.boot.actuate.autoconfigure.health.HealthIndicatorAutoConfiguration;
import org.springframework.boot.actuate.couchbase.CouchbaseHealthIndicator;
import org.springframework.boot.actuate.couchbase.CouchbaseReactiveHealthIndicator;
import org.springframework.boot.actuate.health.ApplicationHealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.core.RxJavaCouchbaseOperations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link CouchbaseReactiveHealthIndicatorConfiguration}.
 *
 * @author Mikalai Lushchytski
 */
public class CouchbaseReactiveHealthIndicatorConfigurationTests {

	private ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withUserConfiguration(CouchbaseMockConfiguration.class).withConfiguration(
					AutoConfigurations.of(CouchbaseHealthIndicatorAutoConfiguration.class,
							HealthIndicatorAutoConfiguration.class));

	@Test
	public void runShouldCreateIndicator() {
		this.contextRunner.run((context) -> assertThat(context)
				.hasSingleBean(CouchbaseReactiveHealthIndicator.class)
				.doesNotHaveBean(CouchbaseHealthIndicator.class)
				.doesNotHaveBean(ApplicationHealthIndicator.class));
	}

	@Test
	public void runWithCustomTimeoutShouldCreateIndicator() {
		this.contextRunner.withPropertyValues("management.health.couchbase.timeout=2s")
				.run((context) -> {
					assertThat(context)
							.hasSingleBean(CouchbaseReactiveHealthIndicator.class);
					assertThat(context.getBean(CouchbaseReactiveHealthIndicator.class))
							.hasFieldOrPropertyWithValue("timeout",
									Duration.ofSeconds(2));
				});
	}

	@Test
	public void runWhenDisabledShouldNotCreateIndicator() {
		this.contextRunner.withPropertyValues("management.health.couchbase.enabled:false")
				.run((context) -> assertThat(context)
						.doesNotHaveBean(CouchbaseReactiveHealthIndicator.class)
						.hasSingleBean(ApplicationHealthIndicator.class));
	}

	@Configuration
	protected static class CouchbaseMockConfiguration {

		@Bean
		public RxJavaCouchbaseOperations couchbaseOperations() {
			return mock(RxJavaCouchbaseOperations.class);
		}

	}

}