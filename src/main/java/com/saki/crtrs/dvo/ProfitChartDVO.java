package com.saki.crtrs.dvo;

import lombok.Data;

import java.util.List;

@Data
public class ProfitChartDVO {
    private List<String> xAxisData; // x轴数据：近5个月的月份
    private List<String> yAxisData; // 对应月份的收益（单位：元）
}
