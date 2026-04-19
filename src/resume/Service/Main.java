package resume.Service;

import resume.ImportExport.ImportExportService;

import java.util.Scanner;

public class Main {
private static String menu(String role){
    if (role.equals("admin")) {
        return "===ADMIN MENU===\n" +
                "1. Create record\n" +
                "2. View applicants\n" +
                "3. View managers\n" +
                "4. Find record by ID\n" +
                "5. Update record\n" +
                "6. Delete record\n" +
                "7. Export CSV\n" +
                "8. Export JSON\n" +
                "9. Import CSV\n"+
                "10. Import JSON\n" +
                "0. Exit";
    }
    if (role.equals("manager")) {
        return  "===MANAGER MENU===\n" +
                "1. Create record\n"+
                "2. View applicants\n" +
                "3. Find record by ID\n" +
                "4. Update record\n" +
                "5. Export CSV\n" +
                "6. Export JSON\n" +
                "7. Import CSV\n"+
                "8. Import JSON\n" +
                "0. Exit";
    }
    return "===APPLICANT MENU===\n" +
            "1. Create resume\n" +
            "2. View my resume\n" +
            "3. Update my resume\n" +
            "4. Delete my resume\n" +
            "0. Exit";}
    public static void pause(Scanner scanner) {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
    public static void main(String[] args) {
        ResumeService service = new ResumeService();
        ImportExportService iES = new ImportExportService();
        Scanner scanner=new Scanner(System.in);
        System.out.println("======HELLO!======\nWELCOME TO RESUME DATABASE APP!");
        System.out.println("Who are you?");
        System.out.println("1. Admin");
        System.out.println("2. Manager");
        System.out.println("3. Applicant");
        int roleChoice = scanner.nextInt();
        scanner.nextLine();
        String role;
        switch (roleChoice) {
            case 1:
                role = "admin";
                break;
            case 2:
                role = "manager";
                break;
            case 3:
                role = "applicant";
                break;
            default:
                System.out.println("Invalid choice.");
                return;}
        int userID=-1;
        User user=null;
        if (!role.equalsIgnoreCase("applicant")) {
            System.out.print("Enter email: ");
            String email = scanner.nextLine();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();
            user = service.login(email, password);
            if (user == null || !user.role.equalsIgnoreCase(role)) {
                System.out.println("Invalid login!");
                return;
            }
            userID = user.id;
        } else {
            System.out.print("Enter your email: ");
            String email = scanner.nextLine();

            user = service.getOrCreateUser(email);
            if (user == null) {
                System.out.println("User not found!");
                return;
            }

            userID = user.id;
        }
        System.out.println(menu(role));
        while(true){
            System.out.println("Choose option.");
            int choice=scanner.nextInt();
             scanner.nextLine();
            if (role.equalsIgnoreCase("admin")) {
                switch (choice) {
                    case 1:
                        System.out.println("===Create Record===");
                        service.create(role,userID);
                        pause(scanner);
                        break;
                    case 2:
                        System.out.println("===View Applicants===");
                        service.readAllApplicants(role);
                        pause(scanner);
                        break;
                    case 3:
                        System.out.println("===View Managers===");
                        service.readManagers(role);
                        pause(scanner);
                        break;
                    case 4:
                        System.out.println("===Find Record===");
                        service.readOne(role,userID);
                        pause(scanner);
                        break;
                    case 5:
                        System.out.println("===Update Record===");
                        service.updateOne(role,userID);
                        pause(scanner);
                        break;
                    case 6:
                        System.out.println("===Delete Record===");
                        service.delete(role,userID);
                        pause(scanner);
                        break;
                    case 7:
                        iES.exportApplicantsToCSV();
                        break;
                    case 8:
                        iES.exportApplicantsToJSON();
                        break;
                    case 9:
                        iES.importFromCSV();
                        break;
                    case 10:
                        iES.importFromJSON();
                        pause(scanner);
                        break;
                    case 0:
                        System.out.println("===Exit===");
                        return;
                    default:
                        System.out.println("Invalid choice.");
                        pause(scanner);
                }
            }
            else if (role.equalsIgnoreCase("manager")) {
                switch (choice) {
                    case 1:
                        System.out.println("===Create Record===");
                        service.create(role,userID);
                        pause(scanner);
                        break;
                    case 2:
                        System.out.println("===View Applicants===");
                        service.readAllApplicants(role);
                        pause(scanner);
                        break;
                    case 3:
                        System.out.println("===Find Record===");
                        service.readOne(role,userID);
                        pause(scanner);
                        break;
                    case 4:
                        System.out.println("===Update Record===");
                        service.updateOne(role,userID);
                        pause(scanner);
                        break;
                    case 5:
                        iES.exportApplicantsToCSV();
                        break;
                    case 6:
                        iES.exportApplicantsToJSON();
                        break;
                    case 7:
                        iES.importFromCSV();
                        break;
                    case 8:
                            iES.importFromJSON();
                            pause(scanner);
                            break;
                    case 0:
                        System.out.println("===Exit===");
                        return;
                    default:
                        System.out.println("Invalid choice.");
                        pause(scanner);
                }
            }else {
                switch (choice) {
                    case 1:
                        System.out.println("===Create Resume===");
                        service.create(role,userID);
                        pause(scanner);
                        break;
                    case 2:
                        System.out.println("===View My Resume===");
                        service.readOne(role,userID);
                        pause(scanner);
                        break;
                    case 3:
                        System.out.println("===Update Resume===");
                        service.updateOne(role,userID);
                        pause(scanner);
                        break;
                    case 4:
                        System.out.println("===Delete Resume===");
                        service.delete(role,userID);
                        pause(scanner);
                        break;
                    case 0:
                        System.out.println("===Exit===");
                        return;
                    default:
                        System.out.println("Invalid choice.");
                        pause(scanner);
                }
            }
        }
        }
    }



