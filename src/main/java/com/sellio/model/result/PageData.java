package com.sellio.model.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PageData<T> {
    private int totalPages;
    private long totalElements;
    private boolean firstPage;
    private boolean lastPage;
    private int size;
    private int page;
    private List<T> content;
}
