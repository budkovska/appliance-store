package com.epam.rd.autocode.assessment.appliances.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ListQuery {

    private static final List<Integer> PAGE_SIZES = List.of(5, 10, 20, 50);
    private static final int DEFAULT_SIZE = 10;
    private static final int MAX_SEARCH_LENGTH = 100;

    private String search = "";
    private int page = 0;
    private int size = DEFAULT_SIZE;
    private String sort = "id";
    private String dir = "asc";

    public List<Integer> getPageSizes() {
        return PAGE_SIZES;
    }

    public Pageable resolve(Map<String, String> sortable, String defaultKey) {
        search = search == null ? "" : search.trim();
        if (search.length() > MAX_SEARCH_LENGTH) {
            search = search.substring(0, MAX_SEARCH_LENGTH);
        }
        page = Math.max(page, 0);
        if (!PAGE_SIZES.contains(size)) {
            size = DEFAULT_SIZE;
        }
        if (sort == null || !sortable.containsKey(sort)) {
            sort = defaultKey;
        }
        dir = "desc".equalsIgnoreCase(dir) ? "desc" : "asc";

        String property = sortable.get(sort);
        Sort order = Sort.by(Sort.Direction.fromString(dir), property);
        if (!"id".equals(property)) {
            order = order.and(Sort.by(Sort.Direction.ASC, "id"));
        }
        return PageRequest.of(page, size, order);
    }

    protected void addFilterParams(Map<String, Object> params) {
    }

    public String link(String path, Integer newPage, Integer newSize, String newSort, String newDir) {
        Map<String, Object> params = new LinkedHashMap<>();
        if (search != null && !search.isBlank()) {
            params.put("search", search);
        }
        addFilterParams(params);
        params.put("page", newPage != null ? newPage : page);
        params.put("size", newSize != null ? newSize : size);
        params.put("sort", newSort != null ? newSort : sort);
        params.put("dir", newDir != null ? newDir : dir);

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(path);
        params.keySet().forEach(key -> builder.queryParam(key, "{" + key + "}"));
        return builder.encode().buildAndExpand(new HashMap<>(params)).toUriString();
    }
}