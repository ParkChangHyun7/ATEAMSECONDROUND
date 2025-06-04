package seoul.its.info.services.boards.comments.exception;

public class CommentPermissionDeniedException extends RuntimeException {
    public CommentPermissionDeniedException(String message) {
        super(message);
    }
}