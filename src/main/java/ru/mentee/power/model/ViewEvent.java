package ru.mentee.power.model;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViewEvent {
    private String contentId;
    private String userId;
    private Date timestamp;
    private String ipAddress;
}
