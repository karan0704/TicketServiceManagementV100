package ticketmanagement.ticketservicemanagementv100.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collections; // Import Collections for unmodifiable lists

/**
 * Configuration class for Cross-Origin Resource Sharing (CORS).
 * CORS is a security feature that allows a web page from one domain to access a resource
 * in another domain. Without proper CORS configuration, browsers would block such requests
 * for security reasons (Same-Origin Policy).
 *
 * This configuration defines a global CORS policy for the entire application.
 */
@Configuration // Marks this class as a Spring configuration class
public class CorsConfig {

    /**
     * Defines and configures the CorsFilter bean.
     * This filter intercepts incoming requests and applies the defined CORS rules.
     *
     * @return A CorsFilter instance configured with the CORS policy.
     */
    @Bean // Marks this method as a bean definition, meaning Spring will manage its lifecycle
    public CorsFilter corsFilter() {
        // Create a new CorsConfiguration object to define the CORS policy.
        CorsConfiguration config = new CorsConfiguration();

        // 1. Allowed Origins: Specifies which origins (domains) are allowed to access resources.
        // In development, "*" is often used for convenience, allowing requests from any origin.
        // However, in production, this should be restricted to known frontend domains for security.
        // Example for production: config.setAllowedOrigins(Arrays.asList("https://yourfrontend.com", "http://localhost:3000"));
        // Using setAllowedOriginPatterns allows for more flexible patterns, including wildcards within parts of the domain.
        // For example, "http://*.yourdomain.com"
        config.setAllowedOriginPatterns(Collections.singletonList("*")); // Allows all origins (use with caution in production)

        // 2. Allowed Headers: Specifies which HTTP headers are allowed in the actual request.
        // "*" allows all headers, which is generally fine for development.
        // In a production environment, you might restrict this to only headers your application expects.
        // Example: config.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization", "X-Requested-With"));
        config.setAllowedHeaders(Collections.singletonList("*")); // Allows all headers

        // 3. Allowed Methods: Specifies which HTTP methods (GET, POST, PUT, DELETE, etc.) are allowed.
        // "*" allows all standard HTTP methods.
        // Restricting this to only the methods your API supports is a good practice.
        // "OPTIONS" method is crucial for CORS preflight requests, so it should almost always be included.
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")); // Allows common HTTP methods

        // 4. Allow Credentials: Indicates whether the actual request can include user credentials (like cookies,
        // HTTP authentication, or client-side SSL certificates).
        // Set to 'true' if your frontend needs to send cookies or authorization headers (e.g., JWT in Authorization header).
        // If set to true, 'allowedOriginPatterns' cannot contain '*' for security reasons;
        // you must specify explicit origins.
        config.setAllowCredentials(true);

        // 5. Exposed Headers: Specifies which headers, other than the simple response headers,
        // are exposed to the browser. By default, only a few simple headers are accessible.
        // If your backend sends custom headers that your frontend needs to read (e.g., custom error codes,
        // pagination info), you must expose them here.
        // Example: config.setExposedHeaders(Arrays.asList("X-Custom-Header", "X-Pagination-Total-Count"));
        // config.setExposedHeaders(Collections.emptyList()); // No custom headers exposed by default

        // 6. Max Age: Specifies how long the results of a preflight request (OPTIONS) can be cached.
        // The browser will cache the preflight response for this duration, reducing the number of
        // OPTIONS requests for subsequent actual requests. Value is in seconds.
        // A common value is 1 hour (3600 seconds).
        // config.setMaxAge(3600L); // Cache preflight response for 1 hour

        // Create a UrlBasedCorsConfigurationSource, which maps CORS configurations to URL patterns.
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // Register the CORS configuration for all paths ("/**").
        // This means the defined 'config' will apply to all incoming requests to your application.
        source.registerCorsConfiguration("/**", config);

        // Return a new CorsFilter with the configured source.
        return new CorsFilter(source);
    }

    /**
     * Alternative/Additional approach: WebMvcConfigurer for more granular control or integration
     * with Spring MVC. This allows defining CORS rules directly within the MVC configuration.
     *
     * Uncomment and modify if you prefer this approach or need more specific path-based CORS rules.
     *
     * @Bean
     * public WebMvcConfigurer corsConfigurer() {
     * return new WebMvcConfigurer() {
     * @Override
     * public void addCorsMappings(CorsRegistry registry) {
     * registry.addMapping("/**") // Apply to all paths
     * .allowedOrigins("http://localhost:3000", "https://yourfrontend.com") // Specific origins
     * .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Specific methods
     * .allowedHeaders("*") // All headers
     * .allowCredentials(true) // Allow credentials
     * .maxAge(3600); // Cache preflight for 1 hour
     * }
     * };
     * }
     */
}