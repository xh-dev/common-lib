package me.xethh.libs.spring.web.security.toolkits.feign.log;

import feign.Request;
import org.slf4j.Logger;

public interface RawRequestLogging {
    void log(Logger logger, Request request);
}
