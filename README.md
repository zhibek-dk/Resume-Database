# RESUME DATABASE APP

Done by: Zhibek Dzhunusheva 

Group: COMSE-25
## Description
Resume Database App is a Java application that manages a database of job applicants, managers, and admins. Users can create, view, update, and delete resume records stored in a SQLite database. The system supports three roles — Admin, Manager, and Applicant — each with different levels of access and permissions. Data can also be exported to and imported from CSV and JSON files.
## Objectives

- Build a fully functional CRUD system for managing resume records via a command-line interface.
- Implement role-based authentication so that Admins, Managers, and Applicants each have appropriate access.
- Ensure all user input is validated before being saved to the database.
- Persist all data in a relational SQLite database using JDBC.
- Demonstrate core OOP principles: encapsulation, inheritance, and polymorphism.
- Provide import and export functionality for CSV and JSON formats.
- Organize the codebase into logical, reusable packages and classes.
## Project Requirement List

1. **CRUD Operations** — Users can create, read, update, and delete resume records.
2. **Command-Line Interface** — Clear role-based menus with prompts guide the user through all operations.
3. **Input Validation** — Name (letters only), phone (digits only), and email (regex format) are validated before saving.
4. **Database Persistence** — All data is stored in a SQLite database via JDBC; data survives between sessions.
5. **Authentication** — Admins and Managers must log in with email and password before accessing the system.
6. **User Roles** — Three roles (Admin, Manager, Applicant) with distinct menus and permission levels.
7. **Export to CSV** — Applicant records can be exported to `Applicants.csv` with proper field escaping.
8. **Export to JSON** — Applicant records can be exported to `Applicants.json` with all fields included.
9. **Import from CSV** — Records can be imported from a CSV file; duplicate emails are skipped automatically.
10. **Import from JSON** — Records can be imported from a JSON file; duplicate emails are skipped automatically.
11. **Modular Design** — Code is split into packages: `resume.service`, `resume.importexport`, `resume.validation`, `resume.classes`.
12. **OOP Principles** — Encapsulation, Inheritance, and Polymorphism are demonstrated.
13. **Error Handling** — All database and file operations are wrapped in try/catch blocks.
## Documentation

### Packages and Classes

**resume.service**
- Main.java — Entry point. Handles role selection, login, menu display, and routes user input.
- ResumeService.java — Core CRUD logic and authentication methods.
- User.java — Stores authenticated user id and role.

**resume.importexport**
- ImportExportService.java — Handles CSV/JSON export and import.

**resume.validation**
- Validator.java — Input validation methods.

**resume.classes**
- Person.java — Base class.
- JobApplicant.java — Extends Person.
- Manager.java — Extends Person.
### Database Schema
person          — id, name, phone_number, email, role

job_applicants  — id (FK → person), education, work_experience, skills

user_auth       — id (FK → person), password
## Algorithms, Data Structures, Functions/Modules, and Challenges

### Algorithms Used

**Role-based access control algorithm**  
The system determines user permissions based on their role (admin, manager, applicant). Each operation checks the role before execution to restrict or allow access.

**ID resolution algorithm (`resolveId`)**  
If the user is an applicant, their own ID is used automatically. For admins and managers, the system prompts for a target ID and validates it as a number before continuing.

**Selective update algorithm (`updateOne`)**  
The user selects a field to update from a menu. Based on the selection, only the corresponding SQL UPDATE query is executed. If option 0 is selected, the system redirects to `updateAll()`.

**CSV parsing algorithm (`parseCSVLine`)**  
The line is read character by character. A boolean flag tracks whether the parser is inside quotes. Commas inside quotes are ignored as separators. Double quotes inside quoted fields are handled as escaped characters.

**Duplicate prevention algorithm**  
Before inserting imported records, the system checks the database for an existing email. If the email already exists, the record is skipped.


### Data Structures Used

- **SQLite relational tables**
  - `person` (stores general user data)
  - `job_applicants` (stores resume details)
  - `user_auth` (stores passwords for managers/admins)

- **Java Collections**
  - `List<String>` used in CSV parsing to store dynamically split fields
  - `JsonArray` and `JsonObject` from Gson for JSON handling

- **Object-oriented structures**
  - `Person` (base class)
  - `JobApplicant` (extends Person)
  - `Manager` (extends Person)

---

### Functions / Modules Used

**ResumeService**
- `create()` — creates or updates records depending on role
- `readOne()` — retrieves a single record by ID
- `readAllApplicants()` — displays all applicants
- `readManagers()` — displays all managers (admin only)
- `updateOne()` — updates a single field
- `updateAll()` — updates full record
- `delete()` — removes records from all related tables
- `login()` — authenticates users
- `getOrCreateUser()` — creates applicant if not found

**ImportExportService**
- `exportApplicantsToCSV()` — exports data to CSV
- `exportApplicantsToJSON()` — exports data to JSON using Gson
- `importFromCSV()` — imports CSV with validation and duplicate checking
- `importFromJSON()` — imports JSON using Gson parsing

**Validator**
- `isValidName()` — checks letters only
- `isValidNumber()` — checks numeric input
- `isValidEmail()` — validates email format using regex


### Challenges Faced
- **Database learning difficulty** — Understanding how to work with SQLite and JDBC was challenging at the beginning, especially managing connections, statements, and result sets correctly.

- **Role-based access complexity** — Implementing different permissions for admin, manager, and applicant required careful handling of conditions in almost every method to ensure correct access rights.

- **Export and import functionality** — Working with CSV and JSON files was difficult, especially handling formatting, parsing, and ensuring data consistency during import/export operations.

- **System complexity management** — Keeping track of all features was challenging because each role (admin, manager, applicant) has different allowed actions, and all logic had to stay consistent across the system.

- **Coordinating database and application logic** — Making sure database operations, file handling, and role permissions worked together without conflicts required a lot of testing and debugging.
## Test Cases

This section contains test inputs and expected outputs for the Resume Database App covering authentication, CRUD operations, role-based access, validation, and import/export functionality.

**Admin Login**

Input:

Role option: 1(Admin)

Email: top.admin@gmail.com

Password: ap12345678

Output:

===ADMIN MENU=== displayed

**Manager Login**

Input:

Role option: 2(Manager)

Email: kate@gmail.com

Password: 1234

Output:

===MANAGER MENU=== displayed

**Applicant First-Time Login**

Input:

Role option: 3 (Applicant)

Email: newuser@mail.com

Name:John Brown

Phone: 0501234567

Output:

First time user → creating applicant profile

===APPLICANT MENU=== displayed

**Create Record**

Input:

Name: Anna Lee

Phone: 0501234567

Email: anna@mail.com

Role: applicant(this part shows only for admins)

Education: BSc CS

Experience: 2 years

Skills: Java

Output:

User created successfully! ID: X

**View Applicants**
 (Shows all for admin and manager, for applicant only his own)

Input:

Output:

===== APPLICANTS =====  

ID: X 

Name: Anna Lee  

Phone: 0501234567

Email: anna@mail.com

Education: BSc CS 

Experience: 2 years 

Skills: Java

**View Managers (Admin only)**

Input:  


Output:  

===== MANAGERS =====  
ID: X 

Name: Kate 

Phone: ... 

Email: kate@gmail.com 

Role: Manager  

....

....

**Find Record by ID**

Input:  

ID: X  

Output:  

ID: X  

Name: Anna Lee 

Phone: 0501234567 

Email: anna@mail.com 

Education: BSc CS 

Experience: 2 years 

Skills: Java  

**Delete Record**

Input:  

ID: X  

Output:  

Deleted successfully.  

**Export CSV**

Input:  

(Menu option: Export CSV  )

Output: 

Exported to Applicants.csv  

**Export JSON**

Input:  

(Menu option: Export JSON  )

Output:  

Exported to Applicants.json  


**Import CSV**

Input:  

(Menu option: Import CSV  )

Output:  

Import done. Imported: X, skipped: Y  

Skipping duplicate email: anna@mail.com  


**Import JSON**

Input:  

(Menu option: Import JSON ) 

Output:  

Import done. Imported: X, skipped: Y  

**Role Access Restriction**

Input:  

Manager tries to access admin-only function  

Output:  

Access denied! Managers can only update applicants!

**Validation Test**

Input:  (separatly)

Name: Anna123  

Email: test@wrong  

Phone: 12ab45  

Output:  (separate for all)

Invalid name! Only letters allowed.  

Invalid email format! 

Invalid phone! Only numbers allowed.  
<img width="1920" height="1200" alt="Снимок экрана (28)" src="https://github.com/user-attachments/assets/67b3ccd9-ece4-44e3-964d-4ba2489e6b9d" />
<img width="1920" height="1200" alt="Снимок экрана (29)" src="https://github.com/user-attachments/assets/e9a79b7b-b691-4304-9067-f0df7c7a2d90" />
<img width="1920" height="1200" alt="Снимок экрана (30)" src="https://github.com/user-attachments/assets/b0ff01b5-41c4-488f-90d3-f1c89dd1af16" />
<img width="1920" height="1200" alt="Снимок экрана (31)" src="https://github.com/user-attachments/assets/a93d93ff-3110-4c1b-80c9-f83956bacf5b" />
<img width="1920" height="1200" alt="Снимок экрана (32)" src="https://github.com/user-attachments/assets/a33d12c0-1f5d-4954-b5c1-7f7b3f52d68e" />
<img width="1920" height="1200" alt="Снимок экрана (33)" src="https://github.com/user-attachments/assets/95e00484-bcfc-4d0b-bd91-643c14cfa6ae" />



