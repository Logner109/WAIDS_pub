package stu.fei.ids.process;

import com.gembox.spreadsheet.*;
import stu.fei.ids.database.DatabaseRequests;
import stu.fei.ids.item.Report;
import stu.fei.ids.item.Request;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ReportGenerator {

    public void create_file(Report report) throws IOException {
        SpreadsheetInfo.setLicense("FREE-LIMITED-KEY");

        ExcelFile workbook = new ExcelFile();
        ExcelWorksheet worksheet = workbook.addWorksheet("Report");
        List<Request> requests = new ArrayList<>();

        //Get data from DB
        DatabaseRequests.get_requests(requests, report.getReport_query(), 0);

        //Add data to worksheet
        int i = 1;
        worksheet.getCell(0, 0).setValue("Created");
        worksheet.getCell(0, 1).setValue("Method");
        worksheet.getCell(0, 2).setValue("Path");
        worksheet.getCell(0, 3).setValue("Version");
        worksheet.getCell(0, 4).setValue("Path params");
        worksheet.getCell(0, 5).setValue("Headers");
        worksheet.getCell(0, 6).setValue("Body params");
        worksheet.getCell(0, 7).setValue("Tags");
        worksheet.getCell(0, 8).setValue("Source IPv4");
        worksheet.getCell(0, 9).setValue("Score");
        for(Request r : requests){
            worksheet.getCell(i, 0).setValue(r.getCreated());
            worksheet.getCell(i, 1).setValue(r.getMethod());
            worksheet.getCell(i, 2).setValue(r.getPath());
            worksheet.getCell(i, 3).setValue(r.getVersion());
            worksheet.getCell(i, 4).setValue(r.getPath_params().toString());
            worksheet.getCell(i, 5).setValue(r.getHeaders().toString());
            worksheet.getCell(i, 6).setValue(r.getBody().toString());
            worksheet.getCell(i, 7).setValue(r.getTags().toString());
            worksheet.getCell(i, 8).setValue(r.getIp());
            worksheet.getCell(i, 9).setValue(r.getScore());
            i++;
        }

        //Ste Columns width
        worksheet.getColumn(0).setWidth(350, LengthUnit.PIXEL);
        worksheet.getColumn(0).setWidth(100, LengthUnit.PIXEL);
        worksheet.getColumn(0).setWidth(150, LengthUnit.PIXEL);
        worksheet.getColumn(0).setWidth(50, LengthUnit.PIXEL);

        workbook.save("/home/marek/report_tmp/report" + report.getId() + "." + report.getFormat());

    }

    public void send_report(Report report){
        String USER_NAME = "[email@adress]";
        String PASSWORD = "[password]";

        String subject = "WAIDS report id: #" + report.getId() + "";

        send_from_gmail(USER_NAME, PASSWORD, report.getReceiver(), subject, report.getId(), report.getFormat());
    }

    private void send_from_gmail(String from, String pass, String to, String subject, int id, String format){
        Properties props = new Properties();
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.starttls.enable", true);
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(from, pass);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);

            MimeBodyPart messageBodyPart = new MimeBodyPart();
            Multipart multipart = new MimeMultipart();

            String file = "/home/marek/report_tmp/report" + id + "." + format;
            String fileName = "report" + id + "." + format;
            DataSource source = new FileDataSource(file);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(fileName);
            multipart.addBodyPart(messageBodyPart);

            message.setContent(multipart);

            System.out.println("Sending report ...");

            Transport.send(message);

            System.out.println("Sent successfully !");
            File sent_report = new File("/home/marek/report_tmp/report" + id + "." + format);
            if(sent_report.delete())
                System.out.println("report" + id + "." + format + " removed successfully");
            else
                System.out.println("report" + id + "." + format + " couldn't be removed");
            
        } catch (MessagingException ae) {
            ae.printStackTrace();
        }
    }
}
