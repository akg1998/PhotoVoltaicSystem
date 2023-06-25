package com.dwt.photovoltaic.Photovoltaic.System.service;

import com.dwt.photovoltaic.Photovoltaic.System.model.*;
import com.dwt.photovoltaic.Photovoltaic.System.repository.CompanyRepository;
import com.dwt.photovoltaic.Photovoltaic.System.repository.UserRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepo;
    @Autowired
    CompanyRepository companyRepo;
    @Autowired
    private EmailService emailService;

    private final RestTemplate restTemplate;

    @Autowired
    public UserService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public boolean checkAvailability(String username){
        User userObj = userRepo.findByUsername(username);
        if(userObj!=null){
            return false;
        }
        else{
            Company companyObj = companyRepo.findByUsername(username);
            if(companyObj!=null){
                return false;
            }
        }
        return true;
    }
    public boolean authenticate(String username, String password) {
        if (username != null && password != null) {
            User user = userRepo.findByUsername(username);
            if (user != null) {
                return username.equals(user.getUsername()) && password.equals(user.getPassword());
            } else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    public User registerUser(User user){
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        user.setStatus("ACTIVE");
        User userObj = userRepo.save(user);
        return userObj;
    }


    public List<User> findAllUsers() {
        return userRepo.findAll();
    }

    public User getUserDetails(String username){
        User userObj = userRepo.findByUsername(username);
        return userObj;
    }

    public ResponseEntity<?> saveProjectDetails(String username, Project projectDetails) {
        if (username != null) {
            User userObj = userRepo.findByUsername(username);
            List<Project> project = userObj.getProjects();
           // projectDetails.setId(UUID.randomUUID().toString());
            if (userObj != null && userObj.getStatus().equals("ACTIVE")) {
                if (project == null || project.isEmpty()) {
                    // No existing data, create a new list and add the new object
                    List<Project> newProjectList = new ArrayList<>();
                    newProjectList.add(projectDetails);
                    userObj.setProjects(newProjectList);
                    userRepo.save(userObj);
                    return new ResponseEntity<>(projectDetails, HttpStatus.OK);
                } else {
                        project.add(projectDetails);
                        userObj.setProjects(project);
                        userRepo.save(userObj);
                        return new ResponseEntity<>(projectDetails, HttpStatus.OK);
                }
            }
            else {
                ResponseMessage responseMessage = new ResponseMessage();
                responseMessage.setMessage("User might be deleted or not valid user to perform this action");
                return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
            }
        }
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setMessage("You are not valid user to perform this action!");
        return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
    }

    // Two ways -  first scan whole project using projectId or second one is scan by userId and then ProjectId
    public ResponseEntity<?> saveProductDetails(Project projectObj, String username) {
        User user = userRepo.findByUsername(username);
        if (user != null && user.getStatus().equals("ACTIVE")) {
            if (projectObj.getProjectName() != null) {
                Project project = user.getProjects().stream()
                        .filter(p -> p.getProjectName().equals(projectObj.getProjectName()))
                        .findFirst()
                        .orElse(null);
                if (project != null) {
                        List<Product> products = projectObj.getProducts();
                    if (project.getProducts() == null) {
                        project.setProducts(new ArrayList<>()); // Initialize the products list
                    }
                        for(Product product : products) {
                            boolean exists = userRepo.existsByProjectNameAndProductName(username, projectObj.getProjectName(),product.getProductName());
                            if(exists != true) {
                                project.getProducts().add(product);
                                userRepo.save(user);
                            }
                        }
                    return new ResponseEntity<>(products, HttpStatus.OK);
                    }
                    else {
                        ResponseMessage responseMessage = new ResponseMessage();
                        responseMessage.setMessage("No products given in request, please add some products!");
                        return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
                    }

                }
                else{
                    ResponseMessage responseMessage = new ResponseMessage();
                    responseMessage.setMessage("Given project is not present in database, it might be deleted!");
                    return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
                }
            }
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setMessage("You are not valid user to perform this action!");
        return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> updateAccountForUser(User user, String username) {
        User userObj = userRepo.findByUsername(username);
        if(userObj!=null && userObj.getUsername().equals(user.getUsername()) && userObj.getStatus().equals("ACTIVE")){
            userObj.setUserType(user.getUserType());
            userObj.setFullName(user.getFullName());
            userObj.setEmailId(user.getEmailId());
            userObj.setContactNo(user.getContactNo());
            userObj = userRepo.save(userObj);
            return new ResponseEntity<>(userObj, HttpStatus.OK);
        }
        else{
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage("You are not valid user to perform this action!");
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> deleteAccountUser(String username) {
        if(username!=null){
            User userObj = userRepo.findByUsername(username);
            if(userObj!=null && userObj.getStatus().equals("ACTIVE")) {
                userObj.setStatus("DELETED");
                userObj = userRepo.save(userObj);
                return new ResponseEntity<>(userObj,HttpStatus.OK);
            }
            else{
                ResponseMessage responseMessage = new ResponseMessage();
                responseMessage.setMessage("User not present!");
                return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
            }
        }
        else{
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage("Invalid action against User, Contact Administrator!");
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> fetchAllProjects(String username) {
        if(username!=null){
            User userObj = userRepo.findByUsername(username);
            if(userObj!=null){
                List<Project> projects = userObj.getProjects();
                if(projects!=null){
                    return new ResponseEntity<>(projects, HttpStatus.OK);
                }
                else{
                    ResponseMessage responseMessage = new ResponseMessage();
                    responseMessage.setMessage("No Projects are created yet!");
                    return new ResponseEntity<>(responseMessage, HttpStatus.OK);
                }
            }
            else{
                ResponseMessage responseMessage = new ResponseMessage();
                responseMessage.setMessage("User not present!");
                return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
            }
        }
        else{
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage("Not valid User!");
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
    }

    public List<User> getAllDeletedUsers() {
        List<User> deletedUsers = userRepo.showUsersbyStatus("DELETED");
        return deletedUsers;
    }

    public ResponseEntity<?> updateProduct(Project updatedProduct, String username) {
        User userObj = userRepo.findByUsername(username);
        if(userObj!=null && userObj.getStatus().equals("ACTIVE")) {
            if (updatedProduct.getProjectName() != null) {
                Project project = userObj.getProjects().stream()
                        .filter(p -> p.getProjectName().equals(updatedProduct.getProjectName()))
                        .findFirst()
                        .orElse(null);
                if (project != null) {
                    List<Product> fetchProducts =  updatedProduct.getProducts();
                    List<Product> listOfProducts = new ArrayList<>();
                    for(Product product: fetchProducts){
                        product.setArea(product.getArea());
                        product.setInclination(product.getInclination());
                        product.setOrientation(product.getOrientation());
                        product.setLongitude(product.getLongitude());
                        product.setLatitude(product.getLatitude());
                        product.setSystemLoss(product.getSystemLoss());
                        listOfProducts.add(product);
                        project.setProducts(listOfProducts);
                    }
                    userRepo.save(userObj);
                    return new ResponseEntity<>(project, HttpStatus.OK);
                }
                else{
                    ResponseMessage responseMessage = new ResponseMessage();
                    responseMessage.setMessage("Project is not present it might be deleted or inactive");
                    return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
                }
            }
            else{
                ResponseMessage responseMessage = new ResponseMessage();
                responseMessage.setMessage("Project name is empty");
                return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
            }
        }
        else{
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage("Not valid User!");
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> deleteProduct(Project productDetails, String username) {
        User userObj = userRepo.findByUsername(username);
        if(userObj!=null && userObj.getStatus().equals("ACTIVE")) {
            if (productDetails.getProjectName() != null) {
                Project project = userObj.getProjects().stream()
                        .filter(p -> p.getProjectName().equals(productDetails.getProjectName()))
                        .findFirst()
                        .orElse(null);
                if (project != null) {
                    Product fetchProduct = productDetails.getProducts().get(0);
                    List<Product> listOfProducts = project.getProducts();
                    listOfProducts.removeIf(product -> product.getProductName().equals(fetchProduct.getProductName()));
                    userRepo.save(userObj);
                    ResponseMessage responseMessage = new ResponseMessage();
                    responseMessage.setMessage("Product deleted successfully");
                    return new ResponseEntity<>(responseMessage, HttpStatus.OK);
                }
                else{
                    ResponseMessage responseMessage = new ResponseMessage();
                    responseMessage.setMessage("Project is not present it might be deleted or inactive");
                    return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
                }
            }
            else{
                ResponseMessage responseMessage = new ResponseMessage();
                responseMessage.setMessage("Project name is empty");
                return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
            }
        }
        else{
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage("Not valid User!");
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> getAllProducts(String username) {
        User userObj = userRepo.findByUsername(username);
        Company companyObj = companyRepo.findByUsername(username);
        List<Product> products = new ArrayList<>();
        if(userObj!=null){
            List<Company> companies = companyRepo.findAll();
            if(companies!=null){
                for (Company company: companies){
                    if(company.getProducts() != null) {
                        products.addAll(company.getProducts());
                    }
                }
                return new ResponseEntity<>(products, HttpStatus.OK);
            }
            else{
                ResponseMessage responseMessage = new ResponseMessage();
                responseMessage.setMessage("No Companies are present!");
                return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
            }
        }
        else if(companyObj!=null){
            products = companyObj.getProducts();
            if(products!=null){
                return new ResponseEntity<>(products, HttpStatus.OK);
            }
            else{
                ResponseMessage responseMessage = new ResponseMessage();
                responseMessage.setMessage("No products present");
                return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
            }
        }
        else{
            ResponseMessage responseMessage = new ResponseMessage();
            if(userObj == null){
                responseMessage.setMessage("Invalid User Credentials");
            }
            else{
                responseMessage.setMessage("Invalid Company credentials");
            }
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> getProjectsByProjectName(String projectName, String username) {
        User userObj = userRepo.findByUsername(username);
        if (userObj != null && userObj.getStatus().equals("ACTIVE")) {
            if(projectName!=null){
                Project projectDetails = userObj.getProjects().stream()
                        .filter(p -> p.getProjectName().equals(projectName))
                        .findFirst()
                        .orElse(null);
                if(projectDetails!=null){
                    return new ResponseEntity<>(projectDetails, HttpStatus.OK);
                }
                else{
                    ResponseMessage responseMessage = new ResponseMessage();
                    responseMessage.setMessage("No such project present");
                    return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
                }
            }
            else{
                ResponseMessage responseMessage = new ResponseMessage();
                responseMessage.setMessage("Project name is empty");
                return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
            }
        }
        else{
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage("Not valid User!");
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
    }

    // TO-DOS Pending:- Once Report generated mark it "read-only"
    public ResponseEntity<?> generateReport(Project projectDetails, String username) {
        User userObj = userRepo.findByUsername(username);
        HashMap<String, Object> results = new HashMap<>();
        int numberOfdays = 0;
        if (userObj != null && userObj.getStatus().equals("ACTIVE")) {

            Project project = userObj.getProjects().stream()
                    .filter(p -> p.getProjectName().equals(projectDetails.getProjectName()))
                    .findFirst()
                    .orElse(null);

            // ***************************************************************  Check condition here project should not be "read-only"
            // It means user clicked on any one of the product
            if (projectDetails.getProducts() != null) {
                Product product = projectDetails.getProducts().get(0);
                Product productObj = project.getProducts().stream()
                        .filter(p -> p.getProductName().equals(product.getProductName()))
                        .findFirst()
                        .orElse(null);
                List<PhotovoltaicCell> weatherInfo = productObj.getWeatherInfo();
                // If someone clicks Generate Report button after (for example) 6-7 calculations.
                if (productObj.getWeatherInfo() != null) {
                    numberOfdays = productObj.getWeatherInfo().size();
                    if(numberOfdays < 30) {
                        results = calculateElectricityProduced(product, productObj, numberOfdays, weatherInfo);
                        userRepo.save(userObj);
                    }
                } else {
                    results = calculateElectricityProduced(product, productObj, numberOfdays, null);
                    userRepo.save(userObj);
                }
                generateExcelFileReport(project.getProducts());
                sendEmailWithAttachment(userObj,project.getProducts());
                ResponseMessage responseMessage = (ResponseMessage) results.get("responseMessage");
                ResponseEntity<?> responseCode = (ResponseEntity<?>) results.get("response");
                return new ResponseEntity<>(responseMessage, responseCode.getStatusCode());
            }
            // It means user clicked on Project, and it should generate report for all products
            else {
                // ********************************* Check condition for read-only
                if(project.getProducts()!=null){
                    List<Product> products = project.getProducts();
                    for(Product product: products){
                        List<PhotovoltaicCell> weatherInfo = product.getWeatherInfo();
                        if (weatherInfo != null) {
                            numberOfdays = weatherInfo.size();
                            if(numberOfdays < 30) {
                                results = calculateElectricityProduced(product, product, numberOfdays, weatherInfo);
                                userRepo.save(userObj);
                            }
                        } else {
                            results = calculateElectricityProduced(product, product, 0, null);
                            userRepo.save(userObj);
                        }
                    }
                    generateExcelFileReport(project.getProducts());
                   // sendEmailWithAttachment(userObj,project.getProducts());

                    // According to read-only message pleas change response message here else it will throw 500-Internal Server Error
                    ResponseMessage responseMessage = (ResponseMessage) results.get("responseMessage");
                    ResponseEntity<?> responseCode = (ResponseEntity<?>) results.get("response");
                    return new ResponseEntity<>(responseMessage, responseCode.getStatusCode());
                }
                else{
                    ResponseMessage responseMessage = new ResponseMessage();
                    responseMessage.setMessage("No products are present to generate report");
                    return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
                }
            }
        } else {
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage("Not valid User!");
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
    }

    public HashMap<String,Object> calculateElectricityProduced(Product product, Product existingProduct, int numberOfDaysLapsed, List<PhotovoltaicCell> weatherInfo) {
        HashMap<String, Object> parameters = new HashMap<>();
        List<PhotovoltaicCell> photovoltaicCells = new ArrayList<>();

        String apiUrl = "https://api.weatherbit.io/v2.0/history/daily?";
        String apiKey = "06a0af5f61ec416fbac8aeaeec4d7998";

        double panelArea = new BigDecimal(String.valueOf(product.getArea())).doubleValue();
        double systemLoss = new BigDecimal(String.valueOf(product.getSystemLoss())).doubleValue();

        BigDecimal latitude = product.getLatitude();
        BigDecimal longitude = product.getLongitude();
        LocalDate todayDate = LocalDate.now().minusDays(numberOfDaysLapsed);
        if(weatherInfo!=null){
            todayDate = LocalDate.parse(weatherInfo.get(0).getWeatherDate());
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String currentDate = todayDate.format(formatter); // Today's Date
        LocalDate thirtyDaysBefore = LocalDate.now().minusDays(30);
        String thirtyDaysBeforeDate = thirtyDaysBefore.format(formatter); // 30 days before date

        apiUrl += "lat=" + latitude + "&lon=" + longitude + "&start_date=" + thirtyDaysBeforeDate + "&end_date=" + currentDate + "&key=" + apiKey;

        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, null, String.class);
        parameters.put("response", response);

        if (response.getStatusCode().is2xxSuccessful()) {
            String responseBody = response.getBody();
            System.out.println(responseBody);
            JSONObject jsonObject = new JSONObject(responseBody);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                // DATA FROM WEATHER API
                int solarIrradiance = jsonArray.getJSONObject(i).getInt("solar_rad");
                int cloudCover = jsonArray.getJSONObject(i).getInt("clouds");
                String dateTime = jsonArray.getJSONObject(i).getString("datetime");
                long unixTimestamp = jsonArray.getJSONObject(i).getLong("max_temp_ts");
                Instant instant = Instant.ofEpochSecond(unixTimestamp);
                // Convert Instant to LocalDateTime in a specific time zone
                LocalDateTime dateTimeZone = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                // Calculate the number of hours
                int hours = dateTimeZone.getHour();

                // Calculation -- FORMULA , rounding off to two precision
                double electricityProduced = Math.round(((solarIrradiance * panelArea * (1 - (systemLoss / 100)) * (1 - (cloudCover / 100)) * hours) / 1000) * 100.0) / 100.0;

                PhotovoltaicCell photovoltaicCell = new PhotovoltaicCell();
                photovoltaicCell.setCloudCover(cloudCover);
                photovoltaicCell.setPanelArea(panelArea);
                photovoltaicCell.setSystemLoss(systemLoss);
                photovoltaicCell.setSunHours(hours);
                photovoltaicCell.setSolarIrradiance(solarIrradiance);
                photovoltaicCell.setWeatherDate(dateTime);
                photovoltaicCell.setElectricityProduced(electricityProduced);
                photovoltaicCell.setPowerPeak(solarIrradiance*panelArea);
                if (weatherInfo != null) {
                    weatherInfo.add(i, photovoltaicCell);
                } else {
                    photovoltaicCells.add(photovoltaicCell);
                }
            }

            existingProduct.setLocationOfProduct(jsonObject.getString("city_name")+","+jsonObject.getString("country_code"));
            // Here some product object should be fetched from existing product list then only it will update existing product else it will create new product (That's the TRICK)
            if(weatherInfo!=null){
                existingProduct.setWeatherInfo(weatherInfo);
            }
            else {
                existingProduct.setWeatherInfo(photovoltaicCells);
            }
            parameters.put("calculatedObj", photovoltaicCells);
            parameters.put("existingProduct", existingProduct);

            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage("Report Generated Successfully and sent it to your Email Address!");
            parameters.put("responseMessage", responseMessage);
            return parameters;
        }
        else{
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage(response.getBody());
            parameters.put("responseMessage", responseMessage);
            return parameters;
        }
    }

    public void generateExcelFileReport(List<Product> products){
        for(Product product : products) {
            String fileName = product.getProductName() + ".xlsx";
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Report for Photovoltaic product: " + product.getProductName());

                // Create the data rows
                Row headerRow2 = sheet.createRow(0);
                headerRow2.createCell(0).setCellValue("Date");
                headerRow2.createCell(1).setCellValue("Power Peak (in W)");
                headerRow2.createCell(2).setCellValue("Panel Area (in m^2)");
                headerRow2.createCell(3).setCellValue("System Loss (in %)");
                headerRow2.createCell(4).setCellValue("Cloud Cover (in %)");
                headerRow2.createCell(5).setCellValue("Sun Hours");
                headerRow2.createCell(6).setCellValue("Orientation");
                headerRow2.createCell(7).setCellValue("Inclination (in angle)");
                headerRow2.createCell(8).setCellValue("Location of Product (City, Country Code)");
                headerRow2.createCell(9).setCellValue("Electricity Produced (in kWh)");

                if (product.getWeatherInfo() != null) {
                    int rowIndex = 1; // Start the rowIndex at 2 (assuming you already have header row at index 0 and data row at index 1)
                    for (PhotovoltaicCell pCell : product.getWeatherInfo()) {
                        Row dataRow1 = sheet.createRow(rowIndex);
                        dataRow1.createCell(0).setCellValue(pCell.getWeatherDate());
                        dataRow1.createCell(1).setCellValue(pCell.getSolarIrradiance()*pCell.getPanelArea());
                        dataRow1.createCell(2).setCellValue(pCell.getPanelArea());
                        dataRow1.createCell(3).setCellValue(pCell.getSystemLoss());
                        dataRow1.createCell(4).setCellValue(pCell.getCloudCover());
                        dataRow1.createCell(5).setCellValue(pCell.getSunHours());
                        dataRow1.createCell(6).setCellValue(product.getOrientation());
                        dataRow1.createCell(7).setCellValue(product.getInclination().toString());
                        dataRow1.createCell(8).setCellValue(product.getLocationOfProduct());
                        dataRow1.createCell(9).setCellValue(pCell.getElectricityProduced());
                        rowIndex++;
                    }
                }
                    // Auto-size the columns
                    sheet.autoSizeColumn(0);
                    sheet.autoSizeColumn(1);
                    sheet.autoSizeColumn(2);
                    sheet.autoSizeColumn(3);
                    sheet.autoSizeColumn(4);
                    sheet.autoSizeColumn(5);
                    sheet.autoSizeColumn(6);
                    sheet.autoSizeColumn(7);
                    sheet.autoSizeColumn(8);
                    sheet.autoSizeColumn(9);


                // Save the workbook to a file
                    try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
                        workbook.write(fileOut);
                    }
                } catch(IOException e){
                    e.printStackTrace();
                }
            }
    }

    public void sendEmailWithAttachment(User userObj, List<Product> products){
        String recipientEmail = userObj.getEmailId();
        for(Product product : products) {
            if(product.getWeatherInfo()!=null) {
                String subject = "Results of Photovoltaic System Product : " + product.getProductName();
                String body = "Here is your generated report for the product, Please find an attachment.";
                String attachmentFilePath = "D:\\Web Engineering\\DWT\\Photovoltaic-System\\" + product.getProductName() + ".xlsx";
                emailService.sendEmailWithAttachment(recipientEmail, subject, body, attachmentFilePath);
            }
        }
    }
}
