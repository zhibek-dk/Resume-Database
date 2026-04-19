package resume.validation;

public class Validator {
    public static boolean isValidName(String value){
        return value!=null&&value.matches("[a-zA-Z ]+");
    }
    public static boolean isValidNumber(String value) {
        return value != null && value.matches("\\d+");
    }
    public static boolean isValidEmail(String value) {
        return value != null && value.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

}
