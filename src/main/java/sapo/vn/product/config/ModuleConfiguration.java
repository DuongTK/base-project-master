package sapo.vn.product.config;


import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.juli.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import sapo.vn.product.config.AuditService.AuditingDateTimeProvider;
import sapo.vn.product.config.AuditService.AuditorAwareImpl;
import sapo.vn.product.config.AuditService.CurrentTimeDateTimeService;
import sapo.vn.product.config.AuditService.DateTimeService;

@Configuration(value = "myConfiguration")
@ComponentScan({
        "sapo.vn.product.controller",
        "sapo.vn.product.service"
})
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@EnableJpaRepositories({
        "sapo.vn.product.repository"
})
@EnableTransactionManagement
@EntityScan(basePackages = {
        "sapo.vn.product.model"})
@Slf4j
public class ModuleConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(ModuleConfiguration.class);

    @Bean
    DateTimeService currentTimeDateTimeService() {
        return new CurrentTimeDateTimeService();
    }

    @Bean
    DateTimeProvider dateTimeProvider(DateTimeService dateTimeService) {
        return new AuditingDateTimeProvider(dateTimeService);
    }

    @Bean
    AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();
    }

    @PostConstruct
    public void postConstruct() {
        logger.info("init sapo.vn.product.config complete");
    }
}