package Model;

public class ValidationResult {
    private final boolean isValid;
    private String message;

    public ValidationResult(boolean isValid) {
        this.isValid = isValid;
    }
    public ValidationResult(boolean isValid, String message) {
        this.isValid = isValid;
        this.message = message;
    }

    public boolean isValid(){
        return isValid;
    }

    public String getMessage(){
        return message;
    }
}
