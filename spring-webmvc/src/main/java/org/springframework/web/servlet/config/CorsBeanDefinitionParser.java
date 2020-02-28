/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.web.servlet.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.w3c.dom.Element;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link org.springframework.beans.factory.xml.BeanDefinitionParser} that parses a
 * {@code cors} element in order to set the CORS configuration in the various
 * {AbstractHandlerMapping} beans created by {@link AnnotationDrivenBeanDefinitionParser},
 * {@link ResourcesBeanDefinitionParser} and {@link ViewControllerBeanDefinitionParser}.
 * 跨域bean
 *
 * @author Sebastien Deleuze
 * @since 4.2
 */
public class CorsBeanDefinitionParser implements BeanDefinitionParser {

    @Override
    @Nullable
    public BeanDefinition parse(Element element, ParserContext parserContext) {

        Map<String, CorsConfiguration> corsConfigurations = new LinkedHashMap<>();
        List<Element> mappings = DomUtils.getChildElementsByTagName(element, "mapping");

        if (mappings.isEmpty()) {
            // 最简配置
            CorsConfiguration config = new CorsConfiguration().applyPermitDefaultValues();
            corsConfigurations.put("/**", config);
        } else {
            // 单个 mapping 处理
            // mvc:mapping 标签
            for (Element mapping : mappings) {
                // 跨域配置
                CorsConfiguration config = new CorsConfiguration();
                // 处理每个属性值,并且赋值
                if (mapping.hasAttribute("allowed-origins")) {
                    String[] allowedOrigins = StringUtils.tokenizeToStringArray(mapping.getAttribute("allowed-origins"), ",");
                    config.setAllowedOrigins(Arrays.asList(allowedOrigins));
                }
                if (mapping.hasAttribute("allowed-methods")) {
                    String[] allowedMethods = StringUtils.tokenizeToStringArray(mapping.getAttribute("allowed-methods"), ",");
                    config.setAllowedMethods(Arrays.asList(allowedMethods));
                }
                if (mapping.hasAttribute("allowed-headers")) {
                    String[] allowedHeaders = StringUtils.tokenizeToStringArray(mapping.getAttribute("allowed-headers"), ",");
                    config.setAllowedHeaders(Arrays.asList(allowedHeaders));
                }
                if (mapping.hasAttribute("exposed-headers")) {
                    String[] exposedHeaders = StringUtils.tokenizeToStringArray(mapping.getAttribute("exposed-headers"), ",");
                    config.setExposedHeaders(Arrays.asList(exposedHeaders));
                }
                if (mapping.hasAttribute("allow-credentials")) {
                    config.setAllowCredentials(Boolean.parseBoolean(mapping.getAttribute("allow-credentials")));
                }
                if (mapping.hasAttribute("max-age")) {
                    config.setMaxAge(Long.parseLong(mapping.getAttribute("max-age")));
                }
                corsConfigurations.put(mapping.getAttribute("path"), config.applyPermitDefaultValues());
            }
        }

        // 注册到 Spring
        MvcNamespaceUtils.registerCorsConfigurations(
                corsConfigurations, parserContext, parserContext.extractSource(element));
        return null;
    }

}
