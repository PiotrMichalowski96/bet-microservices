package com.piter.bet.event.aggregator.util;

import java.io.IOException;
import java.util.List;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;

public class YamlPropertySourceFactory extends DefaultPropertySourceFactory {

  @Override
  public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
    Resource yamlResource = resource.getResource();
    List<PropertySource<?>> propertySources = new YamlPropertySourceLoader().load(yamlResource.getFilename(), yamlResource);
    if (!propertySources.isEmpty()) {
      return propertySources.iterator().next();
    }
    return super.createPropertySource(name, resource);
  }
}
