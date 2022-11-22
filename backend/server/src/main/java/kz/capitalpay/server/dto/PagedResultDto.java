package kz.capitalpay.server.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public class PagedResultDto<T> {

    private Integer totalPages;
    private Long totalElements;
    private Integer currentPage;
    private Integer pageSize;
    private Boolean hasNext;
    private List<T> content;

    public PagedResultDto(Page<T> page, Integer currentPage) {
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.currentPage = currentPage;
        this.pageSize = page.getSize();
        this.hasNext = page.hasNext();
        this.content = page.getContent();
    }

    public PagedResultDto(Integer totalPages, Long totalElements, Integer currentPage, Integer pageSize, Boolean hasNext, List<T> content) {
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.hasNext = hasNext;
        this.content = content;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(Long totalElements) {
        this.totalElements = totalElements;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Boolean getHasNext() {
        return hasNext;
    }

    public void setHasNext(Boolean hasNext) {
        this.hasNext = hasNext;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }
}
