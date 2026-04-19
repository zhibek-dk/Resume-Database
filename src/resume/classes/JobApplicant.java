package resume.classes;

public class JobApplicant extends Person {
    private String workExperience;
    private String education;
    private String skills;
    public JobApplicant(String name, String phoneNumber, String email,
                 String workExperience, String education, String skills) {
        super(name, phoneNumber, email);
        this.workExperience = workExperience;
        this.education = education;
        this.skills = skills;
    }
    public String getEducation() {
        return education;
    }
    public String getWorkExperience() {
        return workExperience;
    }
    public String getSkills() {
        return skills;
    }
    public void setWorkExperience(String workExperience) {
        this.workExperience = workExperience;
    }
    public void setEducation(String education) {
        this.education = education;
    }
    public void setSkills(String skills) {
        this.skills = skills;
    }
    @Override
    public String toString() {
        return super.toString() +
                "\nExperience: " + workExperience +
                "\nEducation: " + education +
                "\nSkills: " + skills+"\n";
    }
}
