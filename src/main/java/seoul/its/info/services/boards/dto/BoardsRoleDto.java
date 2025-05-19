package seoul.its.info.services.boards.dto;

import lombok.Data;

@Data
public class BoardsRoleDto {
    private Long id;
    private Integer readRole;
    private Integer writeRole;
    private Integer isActive;
}