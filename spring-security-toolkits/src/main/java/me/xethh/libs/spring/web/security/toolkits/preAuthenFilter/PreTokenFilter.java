package me.xethh.libs.spring.web.security.toolkits.preAuthenFilter;

import me.xethh.libs.spring.web.security.toolkits.authenticationModel.ApiTokenAuthenticate;
import me.xethh.libs.spring.web.security.toolkits.preAuthenFilter.exceptionModel.GeneralExceptionModelImpl;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;

import static me.xethh.libs.spring.web.security.toolkits.frontFilter.FirstFilter.TRANSACTION_SESSION_ID;
import static org.springframework.session.FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME;

public class PreTokenFilter extends OncePerRequestFilter {
    ExceptionSetter advice;
    public PreTokenFilter(ExceptionSetter advice){
        this.advice = advice;
    }


    FindByIndexNameSessionRepository findByIndexNameSessionRepository;

    public void setFindByIndexNameSessionRepository(FindByIndexNameSessionRepository findByIndexNameSessionRepository) {
        this.findByIndexNameSessionRepository = findByIndexNameSessionRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String header = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if(header!=null && header.length()>7 && header.substring(0,7).toLowerCase().equals("bearer ")){
            String token = header.substring(7);

            Session session = findByIndexNameSessionRepository.findById(token);
            if(session==null){
                advice.setException(httpServletResponse, new GeneralExceptionModelImpl.TokenNotValid());
                return;
            }
            session.setLastAccessedTime(Instant.now());
            findByIndexNameSessionRepository.save(session);
            if(!session.isExpired()){
                SecurityContextHolder.getContext().setAuthentication(new ApiTokenAuthenticate(session.getAttribute(PRINCIPAL_NAME_INDEX_NAME),token, Arrays.asList(ApiTokenAuthenticate.ApiTokenAuthority.of("ROLE_api_user"))));
                MDC.put(TRANSACTION_SESSION_ID,token);
            }
            else{
                advice.setException(httpServletResponse, new GeneralExceptionModelImpl.TokenNotValid());
                return;
            }
        }
        filterChain.doFilter(httpServletRequest,httpServletResponse);
    }
}

