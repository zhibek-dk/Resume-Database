package resume.ImportExport;
import java.sql.DriverManager;
import java.sql.Statement;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.FileReader;

public class ImportExportService {
    private static final String url="jdbc:sqlite:C:\\Users\\Zhibek\\OneDrive\\Документы\\resume project database";

    public void exportApplicantsToCSV(){
        var sql="select * from person left join job_applicants on person.id=job_applicants.id where lower(role)=lower('applicant')";
        try(var conn=DriverManager.getConnection(url);
            var st=conn.createStatement();
            var rs=st.executeQuery(sql);
            var writer=new java.io.FileWriter("Applicants.csv")){
            writer.write("id,name,phone,email,education,experience,skills\n");
            while(rs.next()){
                writer.write(
                        escape(rs.getInt("id")+"")+","
                                +escape(rs.getString("name"))+","
                                +escape(rs.getString("phone_number"))+","
                                +escape(rs.getString("email"))+","
                                +escape(rs.getString("education"))+","
                                +escape(rs.getString("work_experience"))+","
                                +escape(rs.getString("skills"))+"\n"
                );
            }
            System.out.println("Exported to Applicants.csv");
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    private String escape(String value){
        if(value==null)return"\"\"";
        return"\""+value.replace("\"","\"\"")+"\"";
    }

    public void exportApplicantsToJSON(){
        var sql="select * from person left join job_applicants on person.id=job_applicants.id where lower(person.role)=lower('applicant')";
        try(var conn=DriverManager.getConnection(url);
            var st=conn.createStatement();
            var rs=st.executeQuery(sql);
            var writer=new java.io.FileWriter("Applicants.json")){
            JsonArray arr=new JsonArray();
            while(rs.next()){
                JsonObject obj=new JsonObject();
                obj.addProperty("id",rs.getInt("id"));
                obj.addProperty("name",rs.getString("name"));
                obj.addProperty("phone",rs.getString("phone_number"));
                obj.addProperty("email",rs.getString("email"));
                obj.addProperty("education",rs.getString("education"));
                obj.addProperty("experience",rs.getString("work_experience"));
                obj.addProperty("skills",rs.getString("skills"));
                arr.add(obj);
            }
            writer.write(new Gson().toJson(arr));
            System.out.println("Exported to Applicants.json");
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void importFromCSV(){
        try(var conn=DriverManager.getConnection(url);
            var reader=new java.io.BufferedReader(new java.io.FileReader("Applicants.csv"))){
            reader.readLine();
            String line;
            int imported=0,skipped=0;
            while((line=reader.readLine())!=null){
                if(line.isBlank())continue;
                String[]data=parseCSVLine(line);
                if(data.length<7){
                    skipped++;
                    continue;
                }
                String email=data[3];
                var dupCheck=conn.prepareStatement("select id from person where email=?");
                dupCheck.setString(1,email);
                if(dupCheck.executeQuery().next()){
                    skipped++;
                    continue;
                }
                var ps=conn.prepareStatement("insert into person(name,phone_number,email,role)values(?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
                ps.setString(1,data[1]);
                ps.setString(2,data[2]);
                ps.setString(3,email);
                ps.setString(4,"applicant");
                ps.executeUpdate();
                var rs=ps.getGeneratedKeys();
                int id=rs.next()?rs.getInt(1):0;
                var ps2=conn.prepareStatement("insert into job_applicants(id,education,work_experience,skills)values(?,?,?,?)");
                ps2.setInt(1,id);
                ps2.setString(2,data[4]);
                ps2.setString(3,data[5]);
                ps2.setString(4,data[6]);
                ps2.executeUpdate();
                imported++;
            }
            System.out.println("Import done. Imported:"+imported+", skipped:"+skipped);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    private String[] parseCSVLine(String line){
        java.util.List<String>fields=new java.util.ArrayList<>();
        boolean inQuotes=false;
        StringBuilder current=new StringBuilder();
        for(int i=0;i<line.length();i++){
            char c=line.charAt(i);
            if(c=='"'){
                if(inQuotes&&i+1<line.length()&&line.charAt(i+1)=='"'){
                    current.append('"');
                    i++;
                }else{
                    inQuotes=!inQuotes;
                }
            }else if(c==','&&!inQuotes){
                fields.add(current.toString());
                current.setLength(0);
            }else{
                current.append(c);
            }
        }
        fields.add(current.toString());
        return fields.toArray(new String[0]);
    }

    public void importFromJSON(){
        try(var conn=DriverManager.getConnection(url);
            var reader=new FileReader("Applicants.json")){
            Gson gson=new Gson();
            JsonArray arr=gson.fromJson(reader,JsonArray.class);
            int imported=0,skipped=0;
            for(var el:arr){
                var obj=el.getAsJsonObject();
                String name=obj.get("name").getAsString();
                String phone=obj.get("phone").getAsString();
                String email=obj.get("email").getAsString();
                String education=obj.get("education").getAsString();
                String experience=obj.get("experience").getAsString();
                String skills=obj.get("skills").getAsString();
                var dupCheck=conn.prepareStatement("select id from person where email=?");
                dupCheck.setString(1,email);
                if(dupCheck.executeQuery().next()){
                    skipped++;
                    continue;
                }
                var ps=conn.prepareStatement("insert into person(name,phone_number,email,role)values(?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
                ps.setString(1,name);
                ps.setString(2,phone);
                ps.setString(3,email);
                ps.setString(4,"applicant");
                ps.executeUpdate();
                var rs=ps.getGeneratedKeys();
                int id=rs.next()?rs.getInt(1):0;
                var ps2=conn.prepareStatement("insert into job_applicants(id,education,work_experience,skills)values(?,?,?,?)");
                ps2.setInt(1,id);
                ps2.setString(2,education);
                ps2.setString(3,experience);
                ps2.setString(4,skills);
                ps2.executeUpdate();
                imported++;
            }
            System.out.println("Import done. Imported:"+imported+", skipped:"+skipped);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}


