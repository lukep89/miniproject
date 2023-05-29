package ibf2022.batch1.project.server.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import ibf2022.batch1.project.server.JWT.JwtFilter;
import ibf2022.batch1.project.server.model.Bill;
import ibf2022.batch1.project.server.repository.BillRepository;
import ibf2022.batch1.project.server.utils.CafeUtils;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BillService {

    @Autowired
    BillRepository billRepo;

    @Autowired
    JwtFilter jwtFilter;

    public ResponseEntity<String> generateReport(String payload) {
        log.info(">>>> Inside generateReport - payload: {} \n", payload);

        try {

            if (validateBillPayload(payload)) {

                JsonObject obj = CafeUtils.jsonStringToJsonObj(payload);
                log.info("Inside generateReport - obj: {} \n", obj);

                String fileName;

                if (obj.containsKey("isGenerate") && !obj.getBoolean("isGenerate")) {
                    fileName = obj.getString("uuid");

                } else {
                    fileName = CafeUtils.getUuid();
                    JsonObject updateObj = Json.createObjectBuilder(obj)
                            .add("uuid", fileName)
                            .add("name", obj.getString("name"))
                            .add("email", obj.getString("email"))
                            .add("contactNumber", obj.getString("contactNumber"))
                            .add("paymentMethod", obj.getString("paymentMethod"))
                            .add("totalAmount", obj.getString("totalAmount"))
                            .add("productDetails", obj.getString("productDetails"))
                            .build();

                    log.info("Inside generateReport - updateObj: {} \n", updateObj);

                    // save to bill db
                    insertBill(updateObj);
                }

                // to create report pdf and store to mongo
                pdfDocumentAndSaveToMongo(obj, fileName);

                return new ResponseEntity<String>("{\"uuid\":\"" + fileName + "\"}",
                        HttpStatus.OK);

            } else {
                return CafeUtils.getRespEntity(HttpStatus.BAD_REQUEST, "Required data not found");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getRespEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong!");
    }

    private boolean validateBillPayload(String payload) {
        JsonObject obj = CafeUtils.jsonStringToJsonObj(payload);

        return obj.containsKey("name")
                && obj.containsKey("contactNumber")
                && obj.containsKey("email")
                && obj.containsKey("paymentMethod")
                && obj.containsKey("productDetails")
                && obj.containsKey("totalAmount");
    }

    private void insertBill(JsonObject obj) {

        try {
            Bill bill = new Bill();

            bill.setUuid(obj.getString("uuid"));
            bill.setName(obj.getString("name"));
            bill.setEmail(obj.getString("email"));
            bill.setContactNumber(obj.getString("contactNumber"));
            bill.setPaymentMethod(obj.getString("paymentMethod"));
            bill.setTotalAmount(Float.parseFloat(obj.getString("totalAmount")));
            bill.setProductDetails(obj.getString("productDetails"));
            bill.setCreatedBy(jwtFilter.getCurrentUser());

            billRepo.save(bill);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setRectangleBorderInPdf(Document pdfDocument) throws DocumentException {
        // log.info("Inside setRectangleBorderInPdf");

        Rectangle rect = new Rectangle(577, 825, 18, 15);
        rect.enableBorderSide(1);
        rect.enableBorderSide(2);
        rect.enableBorderSide(4);
        rect.enableBorderSide(8);
        rect.setBorderColor(BaseColor.BLACK);
        rect.setBorderWidth(1);

        pdfDocument.add(rect);
    }

    private Font getFont(String type) {
        // log.info("Inside getFont");

        switch (type) {
            case "Header":
                Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 18, BaseColor.BLACK);
                headerFont.setStyle(Font.BOLD);
                return headerFont;

            case "Data":
                Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 11, BaseColor.BLACK);
                dataFont.setStyle(Font.BOLD);
                return dataFont;

            default:
                return new Font();

        }
    }

    private void addTableHeader(PdfPTable table) {
        // log.info("Inside addTableHeader");

        Stream.of("Name", "Category", "Quantity", "Price", "Sub Total")
                .forEach(columTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columTitle));
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    header.setVerticalAlignment(Element.ALIGN_CENTER);

                    table.addCell(header);
                });
    }

    private void addRow(PdfPTable table, JsonObject data) {
        log.info("Inside addRow - table, Map: {} : {}", table, data);

        table.addCell(data.getString("name"));
        table.addCell(data.getString("category"));
        table.addCell(data.getString("quantity"));
        table.addCell(data.getString("price"));
        table.addCell(data.getString("total"));

    }

    public ResponseEntity<List<Bill>> getBills() {
        List<Bill> bills = new ArrayList<>();

        if (jwtFilter.isAdmin()) {
            bills = billRepo.getAllBill();

        } else {
            bills = billRepo.getBillByUsername(jwtFilter.getCurrentUser());
        }
        return new ResponseEntity<List<Bill>>(bills, HttpStatus.OK);
    }

    public ResponseEntity<String> deleteBill(Integer id) {
        try {
            Optional<Bill> opt = billRepo.getBillById(id);

            if (opt.isPresent()) {
                billRepo.deleteBillById(id);
                billRepo.deletePdf(opt.get().getUuid());

                return CafeUtils.getRespEntity(HttpStatus.OK, "Deleted bill successfully");

            } else {
                return CafeUtils.getRespEntity(HttpStatus.OK, "Bill id does not exist");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getRespEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong!");
    }

    public Document pdfDocumentAndSaveToMongo(JsonObject obj, String fileName) throws DocumentException, IOException {

        Document pdfDocument = new Document();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        PdfWriter.getInstance(pdfDocument,
                // create a new file
                outputStream);

        pdfDocument.open();

        setRectangleBorderInPdf(pdfDocument);

        String data = "Bill Id: " + fileName + "\n" +
                "Name: " + obj.getString("name") + "\n" +
                "Contact Number: " + obj.getString("contactNumber") + "\n" +
                "Emali: " + obj.getString("email") + "\n" +
                "Payment Method: " + obj.getString("paymentMethod");

        Paragraph chunk = new Paragraph("Cafe Managment System", getFont("Header"));
        chunk.setAlignment(Element.ALIGN_CENTER);
        pdfDocument.add(chunk);

        Paragraph paragraph = new Paragraph(data + "\n\n", getFont("Data"));
        pdfDocument.add(paragraph);

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        addTableHeader(table);

        JsonArray jsonArray = CafeUtils.getJsonArrayFromString(obj.getString("productDetails"));
        log.info("Inside pdfDocument - jsonArray: {} \n", jsonArray.size());

        for (int i = 0; i < jsonArray.size(); i++) {

            JsonObject jsonObject = jsonArray.getJsonObject(i);
            // log.info("Inside pdfDocument - jsonObject: {} \n", jsonObject);

            addRow(table, jsonObject);
        }

        pdfDocument.add(table);

        double totalAmount = Double.parseDouble(obj.getString("totalAmount"));
        String formattedTotalAmount = String.format("%.2f", totalAmount);

        Paragraph footer = new Paragraph(
                "Total: " + formattedTotalAmount + "\n\n" +
                        "Thank you for visiting. Please come again!",
                getFont("Data"));

        pdfDocument.add(footer);

        pdfDocument.close();

        byte[] pdfBytes = outputStream.toByteArray();

        billRepo.savePdf(pdfBytes, fileName);

        return pdfDocument;
    }

    public ResponseEntity<byte[]> getPdfFromMongo(String payload) {
        log.info("Inside getPdfDO - payload {}", payload);

        try {
            byte[] byteArray = new byte[0];
            JsonObject obj = CafeUtils.jsonStringToJsonObj(payload);

            if (!obj.containsKey("uuid") && validateBillPayload(payload)) {
                return new ResponseEntity<byte[]>(byteArray, HttpStatus.BAD_REQUEST);
            }

            String fileName = obj.getString("uuid");

            // get pdf from mongo
            Optional<byte[]> opt = billRepo.getPdfDocument(fileName);

            if (opt.isPresent()) {
                byteArray = opt.get();
                log.info("Inside getPdfDO - byteArray {}", byteArray.length);

                return new ResponseEntity<byte[]>(byteArray, HttpStatus.OK);

            } else {
                // If the file doesn't exist in mongo, generate and upload it
                JsonObject updateObj = Json.createObjectBuilder(obj)
                        .add("uuid", obj.getString("uuid"))
                        .add("name", obj.getString("name"))
                        .add("email", obj.getString("email"))
                        .add("contactNumber", obj.getString("contactNumber"))
                        .add("paymentMethod", obj.getString("paymentMethod"))
                        .add("totalAmount", obj.getString("totalAmount"))

                        .add("productDetails", obj.getString("productDetails"))
                        .add("isGenerate", false)
                        .build();

                generateReport(updateObj.toString());

                // Retrieve the generated PDF file from mongo
                Optional<byte[]> opt2 = billRepo.getPdfDocument(fileName);
                byteArray = opt2.get();

                return new ResponseEntity<byte[]>(byteArray, HttpStatus.OK);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
