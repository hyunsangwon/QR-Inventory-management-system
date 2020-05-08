package pro.cntech.inventory.vo;

import lombok.Data;

@Data
public class PagingVO
{
    private int limitcount;
    private int contentnum;
    private int pageNum;
    private String search;
}
