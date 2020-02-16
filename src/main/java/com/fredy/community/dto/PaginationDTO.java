package com.fredy.community.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PaginationDTO {
    private List<QuestionDTO> questions;
    private boolean showPrevious;
    private boolean showFirstPage;
    private boolean showNext;
    private boolean showEndPage;
    private Integer page;
    private Integer totalPage;
    private List<Integer> pages = new ArrayList<>();

    public void setPagination(Integer totalPage, Integer page) {
        this.page = page;
        this.totalPage = totalPage;
        pages.add(page);
        if(page != 0){
            for(int i = 1; i <= 3; i++){
                if(page - i > 0){
                    pages.add(0, page - i);
                }
                if(page + i <= totalPage){
                    pages.add(page + i);
                }
            }
            showPrevious = page != 1;
            showNext = page != totalPage;
            showFirstPage = !pages.contains(1);
            showEndPage = !pages.contains(totalPage);
        }else{
            showPrevious = false;
            showNext = false;
            showFirstPage = false;
            showEndPage = false;
        }

    }
}
