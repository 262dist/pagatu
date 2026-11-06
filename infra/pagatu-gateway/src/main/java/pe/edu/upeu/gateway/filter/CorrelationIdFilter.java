package pe.edu.upeu.gateway.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(CorrelationIdFilter.class);

    public static final String TRACE_ID_HEADER = "X-Trace-ID";
    public static final String MDC_KEY = "traceId";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(TRACE_ID_HEADER);
        String traceId = (header == null || header.isBlank()) ? UUID.randomUUID().toString() : header;
        long inicio = System.currentTimeMillis();
        try {
            MDC.put(MDC_KEY, traceId);
            response.setHeader(TRACE_ID_HEADER, traceId);
            filterChain.doFilter(new TraceIdRequest(request, traceId), new TraceIdResponse(response));
        } finally {
            // Prometheus raspa /actuator/prometheus cada pocos segundos: registrarlo llenaría el log de ruido.
            if (!request.getRequestURI().startsWith("/actuator")) {
                log.info("method={} path={} status={} durationMs={}",
                        request.getMethod(), request.getRequestURI(), response.getStatus(),
                        System.currentTimeMillis() - inicio);
            }
            MDC.remove(MDC_KEY);
        }
    }

    // El microservicio de destino devuelve el mismo X-Trace-ID que recibió; sin este
    // envoltorio, el proxy lo agrega a la respuesta y el cliente lo recibe duplicado.
    private static class TraceIdResponse extends HttpServletResponseWrapper {
        TraceIdResponse(HttpServletResponse response) {
            super(response);
        }

        @Override
        public void setHeader(String name, String value) {
            if (!TRACE_ID_HEADER.equalsIgnoreCase(name)) {
                super.setHeader(name, value);
            }
        }

        @Override
        public void addHeader(String name, String value) {
            if (!TRACE_ID_HEADER.equalsIgnoreCase(name)) {
                super.addHeader(name, value);
            }
        }
    }

    // El proxy reenvía las cabeceras de la petición entrante: sin este envoltorio, el
    // microservicio de destino no recibe el traceId y genera uno distinto.
    private static class TraceIdRequest extends HttpServletRequestWrapper {
        private final String traceId;

        TraceIdRequest(HttpServletRequest request, String traceId) {
            super(request);
            this.traceId = traceId;
        }

        @Override
        public String getHeader(String name) {
            return TRACE_ID_HEADER.equalsIgnoreCase(name) ? traceId : super.getHeader(name);
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            return TRACE_ID_HEADER.equalsIgnoreCase(name)
                    ? Collections.enumeration(Set.of(traceId))
                    : super.getHeaders(name);
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            Set<String> nombres = new LinkedHashSet<>(Collections.list(super.getHeaderNames()));
            nombres.add(TRACE_ID_HEADER);
            return Collections.enumeration(nombres);
        }
    }
}
