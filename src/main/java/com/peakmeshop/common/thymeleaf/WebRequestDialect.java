package com.peakmeshop.common.thymeleaf;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.dialect.IExpressionObjectDialect;
import org.thymeleaf.expression.IExpressionObjectFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Spring Boot 3.0+ 환경에서 Thymeleaf에서 request, response, session, servletContext에
 * 접근할 수 있도록 해주는 커스텀 Dialect
 */
public class WebRequestDialect extends AbstractDialect implements IExpressionObjectDialect {

    public WebRequestDialect() {
        super("webRequest");
    }

    @Override
    public IExpressionObjectFactory getExpressionObjectFactory() {
        return new WebRequestExpressionObjectFactory();
    }

    /**
     * 표현식 객체 팩토리 구현
     */
    private static class WebRequestExpressionObjectFactory implements IExpressionObjectFactory {

        private static final String REQUEST_EXPRESSION_OBJECT_NAME = "req";
        private static final String RESPONSE_EXPRESSION_OBJECT_NAME = "res";
        private static final String SESSION_EXPRESSION_OBJECT_NAME = "sess";
        private static final String SERVLET_CONTEXT_EXPRESSION_OBJECT_NAME = "ctx";

        @Override
        public Set<String> getAllExpressionObjectNames() {
            Set<String> names = new HashSet<>();
            names.add(REQUEST_EXPRESSION_OBJECT_NAME);
            names.add(RESPONSE_EXPRESSION_OBJECT_NAME);
            names.add(SESSION_EXPRESSION_OBJECT_NAME);
            names.add(SERVLET_CONTEXT_EXPRESSION_OBJECT_NAME);
            return names;
        }

        @Override
        public Object buildObject(IExpressionContext context, String expressionObjectName) {
            // Spring의 RequestContextHolder를 사용하여 현재 요청에 접근
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes == null) {
                return null;
            }

            HttpServletRequest request = attributes.getRequest();
            HttpServletResponse response = attributes.getResponse();
            HttpSession session = request.getSession(false);
            ServletContext servletContext = request.getServletContext();

            switch (expressionObjectName) {
                case REQUEST_EXPRESSION_OBJECT_NAME:
                    return new RequestObject(request);
                case RESPONSE_EXPRESSION_OBJECT_NAME:
                    return response != null ? new ResponseObject(response) : null;
                case SESSION_EXPRESSION_OBJECT_NAME:
                    return session != null ? new SessionObject(session) : null;
                case SERVLET_CONTEXT_EXPRESSION_OBJECT_NAME:
                    return servletContext != null ? new ServletContextObject(servletContext) : null;
                default:
                    return null;
            }
        }

        @Override
        public boolean isCacheable(String expressionObjectName) {
            return false;
        }
    }
}

