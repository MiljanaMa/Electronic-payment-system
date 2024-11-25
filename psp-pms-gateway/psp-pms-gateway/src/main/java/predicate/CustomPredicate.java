package predicate;

import java.util.function.*;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.web.server.ServerWebExchange;
public class CustomPredicate extends AbstractRoutePredicateFactory<CustomPredicate.Config> {
    public static class Config {
        // Configuration properties if any
    }

    public CustomPredicate() {
        super(Config.class);
    }

    @Override
    public Predicate<ServerWebExchange> apply(Config config) {
        return exchange -> {
            // Custom predicate logic
            return exchange.getRequest().getHeaders().containsKey("X-Custom-Header");
        };
    }
}