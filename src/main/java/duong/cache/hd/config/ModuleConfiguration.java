package duong.cache.hd.config;


import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import duong.cache.hd.config.AuditService.AuditingDateTimeProvider;
import duong.cache.hd.config.AuditService.AuditorAwareImpl;
import duong.cache.hd.config.AuditService.CurrentTimeDateTimeService;
import duong.cache.hd.config.AuditService.DateTimeService;

@Configuration(value = "myConfiguration")
@ComponentScan({
        "duong.cache.hd.controller",
        "duong.cache.hd.service"
})
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@EnableJpaRepositories({
        "duong.cache.hd.repository"
})
@EnableTransactionManagement
@EntityScan(basePackages = {
        "duong.cache.hd.model"})
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