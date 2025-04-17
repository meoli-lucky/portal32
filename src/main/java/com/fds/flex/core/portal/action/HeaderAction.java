package com.fds.flex.core.portal.action;

import com.fds.flex.core.portal.model.Header;
import com.fds.flex.core.portal.service.HeaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class HeaderAction {
    private final HeaderService headerService;

    public Mono<Header> findBySiteId(Long siteId) {
        return headerService.findBySiteId(siteId)
                .flatMap(header -> {
                    // Thêm logic xử lý trước khi trả về header
                    return Mono.just(header);
                });
    }

    public Mono<Header> create(Header header) {
        return validateHeader(header)
                .flatMap(validatedHeader -> headerService.save(validatedHeader));
    }

    public Mono<Header> update(Long id, Header header) {
        header.setId(id);
        return validateHeader(header)
                .flatMap(validatedHeader -> headerService.save(validatedHeader));
    }

    public Mono<Void> delete(Long id) {
        return headerService.delete(id);
    }

    private Mono<Header> validateHeader(Header header) {
        // Thêm logic validate header
        if (header.getSiteId() == null) {
            return Mono.error(new IllegalArgumentException("Site ID is required"));
        }
        return Mono.just(header);
    }
} 