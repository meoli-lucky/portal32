package com.fds.flex.core.portal.action;

import com.fds.flex.core.portal.model.Footer;
import com.fds.flex.core.portal.service.FooterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class FooterAction {
    private final FooterService footerService;

    public Mono<Footer> findBySiteId(Long siteId) {
        return footerService.findBySiteId(siteId)
                .flatMap(footer -> {
                    // Thêm logic xử lý trước khi trả về footer
                    return Mono.just(footer);
                });
    }

    public Mono<Footer> create(Footer footer) {
        return validateFooter(footer)
                .flatMap(validatedFooter -> footerService.save(validatedFooter));
    }

    public Mono<Footer> update(Long id, Footer footer) {
        footer.setId(id);
        return validateFooter(footer)
                .flatMap(validatedFooter -> footerService.save(validatedFooter));
    }

    public Mono<Void> delete(Long id) {
        return footerService.delete(id);
    }

    private Mono<Footer> validateFooter(Footer footer) {
        // Thêm logic validate footer
        if (footer.getSiteId() == null) {
            return Mono.error(new IllegalArgumentException("Site ID is required"));
        }
        return Mono.just(footer);
    }
} 