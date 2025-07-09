package ca.bazlur.chithi.dto;

public record EmailResponse(
    String result,
    boolean success,
    String error
) {
    public static EmailResponse success(String result) {
        return new EmailResponse(result, true, null);
    }
    
    public static EmailResponse error(String error) {
        return new EmailResponse(null, false, error);
    }
}