package seoul.its.info.services.boards.posts.dto;

import lombok.Data;

@Data
public class PostRequestDto {
    private Long boardId;
    private String title;
    private String content;
    private Integer isAnonymous;
    private Integer isNotice;
    private Integer fileIncluded;
    private Integer imageIncluded;
} 