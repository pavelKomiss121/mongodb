package ru.mentee.power.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportCriteria {
    private TimePeriod period;
    private List<String> contentTypes;
    private List<String> tags;
    private String status;
}
