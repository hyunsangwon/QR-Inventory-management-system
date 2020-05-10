package pro.cntech.inventory.vo;

import lombok.Data;

@Data
public class StatisticsVO
{
    private int totalCnt;
    private int outerCount; //외부 자산
    private int innerCount; //내주 자산
}
