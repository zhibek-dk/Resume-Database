package resume.Service;

import resume.classes.Manager;
import resume.validation.Validator;
import resume.classes.Person;
import resume.classes.JobApplicant;
import java.sql.*;
import java.util.Scanner;

class ResumeService {
    private final String url="jdbc:sqlite:C:\\Users\\Zhibek\\OneDrive\\Документы\\resume project database";
    Scanner scanner =new Scanner(System.in);
    public User getOrCreateUser(String email) {
        try (var conn = DriverManager.getConnection(url)) {
            var sql = "select id, role from person where email = ?";
            var ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            var rs = ps.executeQuery();
            if (rs.next()) {
                return new User(rs.getInt("id"), rs.getString("role"));
            }
            System.out.println("First time user → creating applicant profile");
            System.out.print("Enter name: ");
            String name = scanner.nextLine();
            System.out.print("Enter phone: ");
            String phone = scanner.nextLine();
            var insert = "insert into person(name, phone_number, email, role) values(?,?,?, 'applicant')";
            var ps2 = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
            ps2.setString(1, name);
            ps2.setString(2, phone);
            ps2.setString(3, email);
            ps2.executeUpdate();
            var keys = ps2.getGeneratedKeys();
            int id = keys.next() ? keys.getInt(1) : -1;
            return new User(id, "applicant");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    public User login(String email, String password) {
        var sql = " select person.id, person.role from person join user_auth on person.id = user_auth.id where person.email =? and user_auth.password = (?)";
        try (var conn = DriverManager.getConnection(url);
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            var rs = ps.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("role")
                );
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    private int resolveId(String role, int userId) {
        if (role.equalsIgnoreCase("applicant")) {
            return userId;}
        System.out.print("Enter ID: ");
        String input = scanner.nextLine();

        if (!Validator.isValidNumber(input)) {
            System.out.println("ID must be a number!");
            return -1;
        }
        return Integer.parseInt(input);
    }
    public void create(String role,int userID){
        var insertPerson = "insert into person(name, phone_number, email, role) values(?,?,?,?)";
        var updatePerson = "update person set name=?, phone_number=?, email=?, role=? where id=?";
        var sqlAuth = "insert into user_auth values(?,?)";
        var sqlJA="insert into job_applicants(id,education,work_experience,skills) values(?,?,?,?)";
        try(var conn=DriverManager.getConnection(url)){
            System.out.println("Please enter information.");
            System.out.println("Name: ");
            String name = scanner.nextLine();
            if(!Validator.isValidName(name)){
                System.out.println("Invalid name! Only letters allowed.");
                return;}
            System.out.print("Phone number: ");
            String phone = scanner.nextLine();
            if(!Validator.isValidNumber(phone)){
                System.out.println("Invalid phone! Only numbers allowed.");
                return;
            }
            System.out.print("Email: ");
            String email = scanner.nextLine();
            if (!Validator.isValidEmail(email)) {
                System.out.println("Invalid email format!");
                return;
            }
            String password = null;
            String chosenRole=role;
            if (role.equalsIgnoreCase("manager")) {
                chosenRole = "applicant";
            }
            if (role.equalsIgnoreCase("admin")) {
                System.out.print("Enter role (admin/manager/applicant): ");
                chosenRole = scanner.nextLine();
                if (!chosenRole.equalsIgnoreCase("applicant")) {
                    System.out.print("Password: ");
                    password = scanner.nextLine();
                }
            }
            System.out.print("Education: ");
            String education = scanner.nextLine();
            System.out.print("Experience: ");
            String experience = scanner.nextLine();
            System.out.print("Skills: ");
            String skills = scanner.nextLine();
            int targetId;
            if (role.equalsIgnoreCase("applicant")) {
                targetId = userID;
                var ps = conn.prepareStatement(updatePerson);
                ps.setString(1, name);
                ps.setString(2, phone);
                ps.setString(3, email);
                ps.setString(4, role);
                ps.setInt(5, targetId);
                ps.executeUpdate();

            }
            else {
                var ps = conn.prepareStatement(insertPerson, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, name);
                ps.setString(2, phone);
                ps.setString(3, email);
                ps.setString(4, chosenRole);
                ps.executeUpdate();
                var rs = ps.getGeneratedKeys();
                targetId = rs.next() ? rs.getInt(1) : -1;
                if (role.equalsIgnoreCase("admin") && !chosenRole.equalsIgnoreCase("applicant")) {
                    var ps2 = conn.prepareStatement(sqlAuth);
                    ps2.setInt(1, targetId);
                    ps2.setString(2, password);
                    ps2.executeUpdate();
                }
            }
            var check = conn.prepareStatement("select id from job_applicants where id=?");
            check.setInt(1, targetId);
            var rs2 = check.executeQuery();
            if (rs2.next()) {
                var updateJA = "update job_applicants set education=?, work_experience=?, skills=? where id=?";
                var ps3 = conn.prepareStatement(updateJA);
                ps3.setString(1, education);
                ps3.setString(2, experience);
                ps3.setString(3, skills);
                ps3.setInt(4, targetId);
                ps3.executeUpdate();
            } else {
                var ps3 = conn.prepareStatement(sqlJA);
                ps3.setInt(1, targetId);
                ps3.setString(2, education);
                ps3.setString(3, experience);
                ps3.setString(4, skills);
                ps3.executeUpdate();
            }
            System.out.println("User created successfully! ID: "+targetId);
        }catch (Exception e){
            System.out.println(e.getMessage());}

    }
    public void readAllApplicants( String role){
        if (!role.equalsIgnoreCase("admin") && !role.equalsIgnoreCase("manager")) {
            System.out.println("Access denied!");
            return;
        }
        var sAFP="select*from person left join job_applicants on person.id=job_applicants.id where lower(role)=lower('Applicant')";
        try(var conn= DriverManager.getConnection(url);
            var stnt=conn.createStatement();
            var rs=stnt.executeQuery(sAFP)){
            System.out.println("===== APPLICANTS =====");
            while (rs.next()){
                Person p = new JobApplicant(
                    rs.getString("name"),
                    rs.getString("phone_number"),
                    rs.getString("email"),
                    rs.getString("work_experience"),
                        rs.getString("education"),
                        rs.getString("skills"));
                System.out.println("ID: " + rs.getInt("id") + " | " + p.toString());
        }
    }catch (SQLException e){
        System.err.println(e.getMessage());}}
    public void readManagers(String role) {
        if (!role.equals("admin")) {
            System.out.println("Access denied!");
            return;
        }
        var sql = "select * from person left join user_auth on person.id=user_auth.id where lower(role)= lower('Manager')";
        try (var conn = DriverManager.getConnection(url);
             var st = conn.createStatement();
             var rs = st.executeQuery(sql)) {
            System.out.println("===== MANAGERS =====");
            while (rs.next()) {
               Person p=new Manager(
                        rs.getString("name"),
                        rs.getString("phone_number"),
                        rs.getString("email"),
                        rs.getString("password"));
                System.out.println("ID: " + rs.getInt("id") + " | " + p.toString());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public void readOne(String role,int userID){
        var sql="select person.id,person.name,person.email,person.phone_number,job_applicants.education,job_applicants.work_experience,job_applicants.skills from person left join job_applicants on person.id=job_applicants.id where person.id=?";
        try(var conn= DriverManager.getConnection(url);
            var ps=conn.prepareStatement(sql)){
            int id=resolveId(role,userID);
            if(id==-1)return;
            ps.setInt(1,id);
            var rs=ps.executeQuery();
            if(rs.next()){
                Person p = new JobApplicant(
                        rs.getString("name"),
                        rs.getString("phone_number"),
                        rs.getString("email"),
                        rs.getString("education"),
                        rs.getString("work_experience"),
                        rs.getString("skills"));
                System.out.println("ID: " + id);
                System.out.println(p.toString());
            }else{
                System.out.println("No record found.");}
        }catch (SQLException e){
            System.err.println(e.getMessage());}}
    public void delete(String role,int userID){
        var checkSql = "select id from person where id = ?";
        var sqlP="delete from person where id=(?)";
        var sqlJA="delete from job_applicants where id=(?)";
        var sqlAuth = "delete from user_auth where id = ?";
        try(var conn=DriverManager.getConnection(url)){
            int id= resolveId(role, userID);
            if (id == -1) return;
            var checkPs = conn.prepareStatement(checkSql);
            checkPs.setInt(1, id);
            var rs = checkPs.executeQuery();

            if (!rs.next()) {
                System.out.println("No such ID found.");
                return;
            }

            var ps1 = conn.prepareStatement(sqlP);
            ps1.setInt(1, id);
            ps1.executeUpdate();
            var ps2 = conn.prepareStatement(sqlJA);
            ps2.setInt(1, id);
            ps2.executeUpdate();
            var ps3 = conn.prepareStatement(sqlAuth);
            ps3.setInt(1, id);
            ps3.executeUpdate();
            System.out.println("Deleted successfully.");
        }catch (SQLException e){
            System.out.println(e.getMessage());
    }
   }
    public void updateAll(String role,int targetId){
       var sqlP = "update person set name=?, phone_number=?, email=? , role=? where id=?";
       var sqlJA =   "update job_applicants set education=?, work_experience=?, skills=? where id=?";
       try(var conn=DriverManager.getConnection(url)){
           int id = targetId;
           var roleCheck = conn.prepareStatement("select role from person where id=?");
           roleCheck.setInt(1, id);
           var rsRole = roleCheck.executeQuery();
           if (!rsRole.next()) {
               System.out.println("User not found!");
               return;
           }
           String targetRole = rsRole.getString("role");
           if (role.equalsIgnoreCase("manager") &&
                   (targetRole.equalsIgnoreCase("admin") || targetRole.equalsIgnoreCase("manager"))) {
               System.out.println("Managers can only update applicants!");
               return;
           }
           if (id == -1) return;
           System.out.println("Please enter information.");
           System.out.println("Name: ");
           String name = scanner.nextLine();
           if(!Validator.isValidName(name)){
               System.out.println("Invalid name! Only letters allowed.");
               return;
           }
           System.out.print("Phone: ");
           String phone = scanner.nextLine();
           if(!Validator.isValidNumber(phone)) {
               System.out.println("Invalid phone! Only numbers allowed.");
               return;
           }
           System.out.print("Email: ");
           String email = scanner.nextLine();
           if (!Validator.isValidEmail(email)) {
               System.out.println("Invalid email format!");
               return;
           }
           String newRole = targetRole;
           String newPassword=null;
           if (role.equalsIgnoreCase("admin")) {
               System.out.print("Role (admin/manager/applicant): ");
               newRole = scanner.nextLine();
               if (!newRole.equalsIgnoreCase("admin") &&
                       !newRole.equalsIgnoreCase("manager") &&
                       !newRole.equalsIgnoreCase("applicant")) {
                   System.out.println("Invalid role!");
                   return;
               }
               System.out.print("New password: ");
               newPassword = scanner.nextLine();
               if (newPassword.isBlank()) {
                   newPassword = null;
               }
           }
           System.out.print("Education: ");
           String education = scanner.nextLine();
           System.out.print("Experience: ");
           String experience = scanner.nextLine();
           System.out.print("Skills: ");
           String skills = scanner.nextLine();
           var ps1=conn.prepareStatement(sqlP);
           ps1.setString(1, name);
           ps1.setString(2, phone);
           ps1.setString(3, email);
           ps1.setString(4, newRole);
           ps1.setInt(5, id);
           ps1.executeUpdate();
           if (role.equalsIgnoreCase("admin") && newPassword != null) {
               var check = conn.prepareStatement("select id from user_auth where id=?");
               check.setInt(1, id);
               var rs = check.executeQuery();
               if (rs.next()) {
                   var psAuth = conn.prepareStatement("update user_auth set password=? where id=?");
                   psAuth.setString(1, newPassword);
                   psAuth.setInt(2, id);
                   psAuth.executeUpdate();
               } else {
                   var psAuth = conn.prepareStatement("insert into user_auth values(?,?)");
                   psAuth.setInt(1, id);
                   psAuth.setString(2, newPassword);
                   psAuth.executeUpdate();
               }
           }
           var ps2=conn.prepareStatement(sqlJA);
           ps2.setString(1, education);
           ps2.setString(2, experience);
           ps2.setString(3, skills);
           ps2.setInt(4, id);
           ps2.executeUpdate();
           System.out.println("Updated successfully");
       }catch (Exception e){
           System.out.println(e.getMessage());}
   }
   public void updateOne(String role,int userID){
       int targetId = userID;
       if (role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("manager")) {
           System.out.print("Enter user ID to update: ");
           String input = scanner.nextLine();
           if (!Validator.isValidNumber(input)) {
               System.out.println("Invalid ID!");
               return;
           }
           targetId = Integer.parseInt(input);
       }
       try (var conn = DriverManager.getConnection(url)) {
           var check = conn.prepareStatement("select role from person where id=?");
           check.setInt(1, targetId);
           var rs = check.executeQuery();
           if (!rs.next()) {
               System.out.println("User not found!");
               return;
           }
           String targetRole = rs.getString("role");
           if (role.equalsIgnoreCase("manager") &&
                   (targetRole.equalsIgnoreCase("admin") || targetRole.equalsIgnoreCase("manager"))) {
               System.out.println("Managers can only update applicants!");
               return;
           }
       } catch (Exception e) {
           System.out.println(e.getMessage());
           return;
       }
       System.out.println("Choose what to update: "+"\n1. Name; "+"\n2. Phone number;"+"\n3. Email;"+"\n4. Education;"+"\n5. Work experience;"+"\n6. Skills;");
       if (role.equalsIgnoreCase("admin")) {
           System.out.println("7. Role");
           System.out.println("8. Password");
       }
       System.out.println("0. Update all");
       String choiceInput = scanner.nextLine();
       if (!Validator.isValidNumber(choiceInput)) {
           System.out.println("Invalid choice!");
           return;
       }
       int choice=Integer.parseInt(choiceInput);
       var sql="";
       switch (choice){
           case 1:
               sql="UPDATE person SET name = ? WHERE id = ?";
               break;
           case 2:
               sql="update person set phone_number=? where id=?";
               break;
           case 3:
               sql="update person set email=? where id=?";
               break;
           case 4:
               sql="update job_applicants set education=? where id=?";
               break;
           case 5:
               sql="update job_applicants set work_experience=? where id=?";
               break;
           case 6:
               sql="update job_applicants set skills=? where id=?";
               break;
           case 7:
               if (!role.equalsIgnoreCase("admin")) {
                   System.out.println("Access denied!");
                   return;
               }
               sql = "update person set role=? where id=?";
               break;
           case 8:
               if (!role.equalsIgnoreCase("admin")) {
                   System.out.println("Access denied!");
                   return;
               }
               sql = "update user_auth set password=? where id=?";
               break;
           case 0:
               updateAll(role,targetId);
               return;
           default:
               System.out.println("Invalid choice.");
               return;
       }
       try (var conn=DriverManager.getConnection(url);
            var ps=conn.prepareStatement(sql)){
           System.out.println("Enter new value: ");
           String value=scanner.nextLine();
           if (choice == 1 && !Validator.isValidName(value)) {
               System.out.println("Invalid name!");
               return;
           }
           if (choice == 2 && !Validator.isValidNumber(value)) {
               System.out.println("Invalid phone!");
               return;
           }
           if (choice == 3 && !Validator.isValidEmail(value)) {
               System.out.println("Invalid email!");
               return;
           }
           ps.setString(1,value);
           ps.setInt(2,targetId);
           ps.executeUpdate();
           System.out.println("Updated successfully.");

       }catch (Exception e) {
           System.out.println(e.getMessage());
       }
   }
}
