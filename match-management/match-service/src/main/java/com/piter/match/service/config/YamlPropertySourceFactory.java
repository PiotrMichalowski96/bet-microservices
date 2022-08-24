package com.piter.match.service.config;

import java.io.IOException;
import java.util.List;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.lang.Nullable;

public class YamlPropertySourceFactory extends DefaultPropertySourceFactory {

  @Override
  public PropertySource<?> createPropertySource(@Nullable String name, EncodedResource resource) throws IOException {
    List<PropertySource<?>> propertySourceList = new YamlPropertySourceLoader().load(resource.getResource().getFilename(), resource.getResource());
    if (!propertySourceList.isEmpty()) {
      return propertySourceList.iterator().next();
    }
    return super.createPropertySource(name, resource);
  }
}
