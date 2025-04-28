package cn.polister.dianmeetingsystem.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomOrderDto {
    private Long roomId;
    private Date startTime;
    private Date endTime;
}
