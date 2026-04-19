
package resume.classes;

public class Manager extends Person {
    private String password;
    public Manager(String name, String phoneNumber, String email, String password) {
        super(name, phoneNumber, email);
        this.password = password;
    }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    @Override
    public String toString() {
        return super.toString() + " | Role: Manager\n";
    }
}
