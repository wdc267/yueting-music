package com.yueting.entity.po;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class UserPlayHistory {
  private Long id;
  private Long userId;
  private Long songId;
  private Integer playDuration;
  private Integer playPercent;
  private LocalDateTime playTime;
}