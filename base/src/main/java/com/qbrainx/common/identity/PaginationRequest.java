package com.qbrainx.common.identity;

import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Optional;

@Setter
public class PaginationRequest {

    private Optional<String> search = Optional.empty();
    private Optional<Integer> pageNo = Optional.empty();
    private Optional<Integer> pageSize = Optional.empty();
    private Optional<String> sortBy = Optional.empty();
    private Optional<String> sortOrder = Optional.empty();

    public Optional<String> getSearch() {
        return search;
    }

    public Integer getPageNo() {
        return pageNo.orElse(0);
    }

    public Integer getPageSize() {
        return pageSize.orElse(10);
    }

    public String getSortBy() {
        return sortBy.orElse("pkId");
    }

    public String getSortOrder() {
        return sortOrder.orElse("ASC");
    }

    public PageRequest toPageRequest() {
        final Sort sort = Sort.by(this.getSortBy());

        return PageRequest.of(
            this.getPageNo(),
            this.getPageSize(),
            "DESC".equalsIgnoreCase(this.getSortOrder()) ? sort.descending() : sort);
    }
}
