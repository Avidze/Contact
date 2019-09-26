package contacts;
import java.io.*;
import java.lang.reflect.Method;
import java.util.*;
import java.time.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class ContactBook implements Serializable {

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
    }


    public ArrayList<Contacto> book;

    ContactBook() {
        ArrayList<Contacto> book = new ArrayList<>();
        this.book = book;
    }

    public void list() {
        int bord = this.len();
        ArrayList<String> resultList = new ArrayList<>();
        for (int i = 0; i < bord; i++) {
            String[] arr = this.book.get(i).getFields();
            String result = (i+1)+".";
            for(int n = 0; n < arr.length/2;n++) {
                result+=" "+this.book.get(i).getFieldValue(arr[n]);
            }
            resultList.add(result);
            System.out.println(result);
        }
        System.out.println("\n[list] Enter action ([number], back): >");
        Scanner scanner = new Scanner(System.in);
        String s = scanner.nextLine();
        switch(s) {
            case "back":
                break;
            default:
                try {
                    int par = Integer.parseInt(s);
                    if (par <= bord && par > 0) {
                        this.manageContact(par - 1);
                    }
                } catch (Exception e) {
                    System.out.println("Wrong input!");
                }
        }
    }
    private void manageContact(int i) {
        Scanner scanner = new Scanner(System.in);
        boolean notExit = true;
        while (notExit) {
            this.book.get(i).info();
            System.out.println("[record] Enter action (edit, delete, menu): > ");
            String s = scanner.nextLine();
            switch (s) {
                case "edit":
                    this.book.get(i).edit();
                    break;
                case "delete":
                    this.book.remove(i);
                    notExit = false;
                    break;
                case "menu":
                    notExit = false;
            }
        }
    }
    public void search() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter search query: >");
        String query = scanner.nextLine();
        Pattern pattern = Pattern.compile(query, Pattern.CASE_INSENSITIVE);
        Matcher matcher;
        ArrayList<String> resultArr = new ArrayList<>();
        ArrayList<Integer> numInOriginalArr = new ArrayList<>();
        int bord = this.len();
        for (int i = 0; i < bord; i++) {
            String[] arr = this.book.get(i).getFields();
            String result = "";
            for(int n = 0; n < arr.length/2;n++) {
                result+=" "+this.book.get(i).getFieldValue(arr[n]);
            }
            String srch = result;
            for (int n = arr.length/2; n<arr.length;n++) {
                srch+=" "+this.book.get(i).getFieldValue(arr[n]);
            }
            srch+=" "+this.book.get(i).getNumber();
            matcher = pattern.matcher(srch);
            if (matcher.find()) {
                String temp = (resultArr.size()+1)+". "+result;
                resultArr.add(temp);
                numInOriginalArr.add(i);
            }
        }
        System.out.println("Found "+resultArr.size()+" results:");
        for (String el : resultArr) {
            System.out.println(el);
        }

        System.out.println("[search] Enter action ([number], back, again): > ");
        String s = scanner.nextLine();
        switch(s) {
            case "back":
                break;
            case "again":
                this.search();
                break;
            default:
                try {
                    int par = Integer.parseInt(s);
                    if (par <= resultArr.size() && par > 0) {
                        this.manageContact(numInOriginalArr.get(par - 1));
                    } else {
                        System.out.println("Bad input!");
                    }
                } catch (Exception e) {
                    System.out.println("Wrong input!");
                }
        }

    }

    private int len() {
        return this.book.size();
    }

    public void count() {
        System.out.println("The Phone Book has " + this.len() + " records.");
    }

    public Contacto build(String type) {
        switch(type) {
            case "person":
                Person contact = new Person();
                contact.setName();
                contact.setSurname();
                contact.setBirthDate();
                contact.setGender();
                contact.setNumber();
                contact.setTimeCreated();
                contact.setTimeEdited();
                return contact;
            case "organization":
                Company contactCom = new Company();
                contactCom.setOrgName();
                contactCom.setAddress();
                contactCom.setNumber();
                contactCom.setTimeCreated();
                contactCom.setTimeEdited();
                return contactCom;
        }
        return null;
    }

    public void add() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the type (person, organization): > ");
        String type = scanner.nextLine();
        Contacto contact = build(type);
        if (contact==null) {
            System.out.println("Bad type!");
        } else {
            System.out.println("The record added.\n");
            this.book.add(contact);
        }
    }
}

abstract class Contacto implements Serializable {
    public boolean isPerson;
    private String number ="";
    private LocalDateTime timeCreated;
    private LocalDateTime timeEdited;

    abstract public String[] getFields();
    abstract public void setField(String field);
    abstract public String getFieldValue(String field);

    public LocalDateTime getTimeCreated() {
        return timeCreated;
    }
    public void setTimeCreated() {
        LocalDateTime time = LocalDateTime.now();
        this.timeCreated = time;
    }
    public LocalDateTime getTimeEdited() {
        return timeEdited;
    }
    public void setTimeEdited() {
        LocalDateTime time = LocalDateTime.now();
        this.timeEdited = time;
    }
    public String getNumber() {
        if (number=="") {
            return "[no number]";
        }
        return number;
    }
    public void edit() {
        Scanner scanner = new Scanner(System.in);
        String[] arr = this.getFields();
        String message = "Select a field (";
        for (String el :arr) {
            message+=el+", ";
        }
        message+="number): >";
        System.out.println(message);
        String s = scanner.nextLine();
        switch (s) {
            case "number":
                this.setNumber();
                break;
            default:
                this.setField(s);
        }
        this.setTimeEdited();
        System.out.println("Saved");
    }
    public void info() {

        String[] arr = this.getFields();
        for (String el: arr) {
            switch(el) {
                case "birthDate":
                    System.out.println("Birth date: "+this.getFieldValue(el));
                    break;
                case "orgName":
                    System.out.println("Organization name: "+this.getFieldValue(el));
                    break;
                default:
                    System.out.println(el.substring(0, 1).toUpperCase() + el.substring(1) + ": " + this.getFieldValue(el));
            }
        }
        System.out.println("Number: "+this.getNumber());
        System.out.println("Time created: "+this.getTimeCreated());
        System.out.println("Time last edit: "+this.getTimeEdited()+"\n");
    }
    public void setNumber() {
        Scanner scanner = new Scanner(System.in);
        String s;
        System.out.println("Enter the number:");
        s = scanner.nextLine();
        String firstGroup = "([a-zA-Z0-9]|[a-zA-Z0-9]{2,})";
        String ex = "([ -][a-zA-Z0-9]{2,})*";
        String phoneRegex = "^\\+?"+firstGroup+ex+"$|^\\+?\\("+firstGroup+"\\)"+ex+"$|^\\+?"+firstGroup+"[ -]\\([a-zA-Z0-9]{2,}\\)"+ex+"$";
        if (s.matches(phoneRegex)) {
            this.number = s;
        } else {
            this.number = "";
            System.out.println("Wrong number format!");
        }

    }
}
class Person extends Contacto {
    private String name;
    private String surname;
    private String birthDate = "";
    private String gender = "";
    public String[] getFields() {
        String[] arr = {"name","surname","birthDate","gender"};
        return arr;
    }
    public void setField(String fieldName)  {
        try {
            String methodName = "set"+ fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
            Method method = Person.class.getMethod(methodName);
            method.invoke(this );
        } catch (Exception e) {
            System.out.println("Contact format error");
        }
    }
    public String getFieldValue(String fieldName) {
        String result = "";
        try {
            String methodName = "get"+ fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
            Method method = Person.class.getMethod(methodName);
            result = (String) method.invoke(this);
        } catch (Exception e) {
            System.out.println("Contact format error");
        }
        return result;
    }
    public String getName() {
        return name;
    }
    public void setName() {
        Scanner scanner = new Scanner(System.in);
        String s;
        System.out.println("Enter the name:");
        s = scanner.nextLine();
        this.name = s;
    }
    public String getSurname() {
        return surname;
    }
    public void setSurname() {
        Scanner scanner = new Scanner(System.in);
        String s;
        System.out.println("Enter the surname:");
        s = scanner.nextLine();
        this.surname = s;
    }
    public String getBirthDate() {
        if (birthDate=="") {
            return "[no data]";
        }
        return birthDate;
    }
    public void setBirthDate() {
        Scanner scanner = new Scanner(System.in);
        String s;
        System.out.println("Enter the birth date:");
        s = scanner.nextLine();
        String date = "";
        if (s.matches("[1-9]\\d{0,3}-(0[1-9]|1[012])-([12]?[1-9]|3[01])")) {
            date = s;
        } else {
            System.out.println("Bad birth date!");
        }
        this.birthDate = date;
    }
    public String getGender() {
        if (gender=="") {
            return "[no data]";
        }
        return gender;
    }
    public void setGender() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the gender (M, F): >");
        String gender = scanner.nextLine();
        if (gender.equals("M") || gender.equals("F")) {
            this.gender = gender;
        } else {
            System.out.println("Bad gender!");
        }
    }
}
class Company extends Contacto {
    private String orgName;
    private String address;
    public String[] getFields() {
        String[] arr = {"orgName","address"};
        return arr;
    }
    public void setField(String fieldName)  {
        try {
            String methodName = "set"+ fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
            Method method = Company.class.getMethod(methodName);
            method.invoke(this);
        } catch (Exception e) {
            System.out.println("Contact format error");
        }
    }
    public String getFieldValue(String fieldName) {
        String result = "";
        try {
            String methodName = "get"+ fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
            Method method = Company.class.getMethod(methodName);
            result = (String) method.invoke(this);
        } catch (Exception e) {
            System.out.println("Contact format error");
        }
        return result;
    }
    public String getOrgName() {
        return orgName;
    }
    public void setOrgName() {
        System.out.println("Enter the organization name: >");
        Scanner scanner = new Scanner(System.in);
        String s = scanner.nextLine();
        this.orgName = s;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress() {
        System.out.println("Enter the address: >");
        Scanner scanner = new Scanner(System.in);
        String s = scanner.nextLine();
        this.address = s;
    }
}
public class Contact {
    public static boolean toFile;
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        String s;
        ObjectOutputStream oos = null;
        File file = null;
        ContactBook book;
        if (args.length!=0) {
            file = new File(args[0]);
            if (file.exists()) {
                FileInputStream path = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(path);
                book = (ContactBook) ois.readObject();
                ois.close();
            } else {
                book = new ContactBook();
            }
            file.createNewFile();

            toFile = true;
        } else {
            toFile = false;
            book = new ContactBook();
        }
        boolean finita = false;
        while (true) {
            System.out.println("[menu] Enter action (add, list, search, count, exit): > ");
            s = scanner.nextLine();
            switch (s) {
                case "add":
                    book.add();
                    break;
                case "list":
                    book.list();
                    break;
                case "search":
                    book.search();
                    break;
                case "count":
                    book.count();
                    break;
                case "exit":
                    finita = true;
            }
            if (toFile) {
                FileOutputStream path = new FileOutputStream(file);
                oos = new ObjectOutputStream(path);
                oos.writeObject(book);
                oos.close();
            }
            if (finita) {
                break;
            }
        }
    }
}
