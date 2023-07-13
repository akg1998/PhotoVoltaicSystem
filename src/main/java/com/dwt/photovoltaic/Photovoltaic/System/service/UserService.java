package com.dwt.photovoltaic.Photovoltaic.System.service;

import com.dwt.photovoltaic.Photovoltaic.System.model.*;
import com.dwt.photovoltaic.Photovoltaic.System.repository.CompanyRepository;
import com.dwt.photovoltaic.Photovoltaic.System.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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
            User userObj = userRepo.findByUsername(username);
            if (userObj != null && userObj.getStatus().equals("ACTIVE")) {
                List<Project> project = userObj.getProjects();
                if(userObj.getSubscription().equalsIgnoreCase("unlimited")) {
                    if (project == null || project.isEmpty()) {
                        // No existing data, create a new list and add the new object
                        List<Project> newProjectList = new ArrayList<>();
                        projectDetails.setStatus("ACTIVE");
                        newProjectList.add(projectDetails);
                        userObj.setProjects(newProjectList);
                    } else {
                        projectDetails.setStatus("ACTIVE");
                        project.add(projectDetails);
                        userObj.setProjects(project);
                    }
                    userRepo.save(userObj);
                    return new ResponseEntity<>(projectDetails, HttpStatus.OK);
                }
                else if (userObj.getSubscription().equalsIgnoreCase("free")) {
                    if (project == null || project.isEmpty()) {
                        // No existing data, create a new list and add the new object
                        List<Project> newProjectList = new ArrayList<>();
                        projectDetails.setStatus("ACTIVE");
                        newProjectList.add(projectDetails);
                        userObj.setProjects(newProjectList);
                    } else if(project.size() == 1 && project.size()<1){
                        projectDetails.setStatus("ACTIVE");
                        project.add(projectDetails);
                        userObj.setProjects(project);
                    }
                    else{
                        ResponseMessage responseMessage = new ResponseMessage();
                        responseMessage.setMessage("You can't create more than 1 project because you are a free user!");
                        return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
                    }
                    userRepo.save(userObj);
                    return new ResponseEntity<>(projectDetails, HttpStatus.OK);
                }
                else{
                    ResponseMessage responseMessage = new ResponseMessage();
                    responseMessage.setMessage("User type is not mentioned at the time of registration!");
                    return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
                }
            }
            else {
                ResponseMessage responseMessage = new ResponseMessage();
                responseMessage.setMessage("You are not valid user to perform this action!");
                return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
            }
    }

    // Two ways -  first scan whole project using projectId or second one is scan by userId and then ProjectId
    public ResponseEntity<?> saveProductDetails(Project projectObj, String username) {
        User user = userRepo.findByUsername(username);
        if (user != null && user.getStatus().equals("ACTIVE")) {
            List<Project> listOfProjects = user.getProjects();
            if (user.getSubscription().equalsIgnoreCase("free") && listOfProjects.size() == 1) {
                Project singleProject = listOfProjects.get(0);
                if(singleProject.getProducts()!=null && singleProject.getStatus().equalsIgnoreCase("active")) {
                    if (singleProject.getProducts().size() < 3) {
                        ResponseEntity<?> saveProducts = saveProductMethod(user, projectObj);
                        return new ResponseEntity<>(saveProducts, saveProducts.getStatusCode());
                    } else {
                        ResponseMessage responseMessage = new ResponseMessage();
                        responseMessage.setMessage("You can't create more than 3 products because you are a free user!");
                        return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
                    }
                }
                else if(singleProject.getStatus().equalsIgnoreCase("active")){
                    ResponseEntity<?> saveProducts = saveProductMethod(user, projectObj);
                    return new ResponseEntity<>(saveProducts, saveProducts.getStatusCode());
                }
                else{
                    ResponseMessage responseMessage = new ResponseMessage();
                    responseMessage.setMessage("Project is not active, please try to create new project");
                    return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
                }
            }
            else if(user.getSubscription().equalsIgnoreCase("unlimited")){
                ResponseEntity<?> saveProducts = saveProductMethod(user, projectObj);
                return new ResponseEntity<>(saveProducts, saveProducts.getStatusCode());
            }
            else{
                ResponseMessage responseMessage = new ResponseMessage();
                responseMessage.setMessage("User type is not mentioned at the time of registration!");
                return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
            }
        }
        else {
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage("You are not valid user to perform this action!");
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> saveProductMethod(User user, Project projectObj){
        if (projectObj.getProjectName() != null && projectObj.getStatus().equalsIgnoreCase("ACTIVE")) {
            Project project = user.getProjects().stream()
                    .filter(p -> p.getProjectName().equals(projectObj.getProjectName()))
                    .findFirst()
                    .orElse(null);
            if (project != null) {
                List<Product> products = projectObj.getProducts();
                if (project.getProducts() == null) {
                    project.setProducts(new ArrayList<>()); // Initialize the products list
                }
                for (Product product : products) {
                    boolean exists = userRepo.existsByProjectNameAndProductName(user.getUsername(), projectObj.getProjectName(), product.getProductName());
                    if (exists != true) {
                        project.getProducts().add(product);
                        userRepo.save(user);
                    }
                }
                return new ResponseEntity<>(products, HttpStatus.OK);
            } else {
                ResponseMessage responseMessage = new ResponseMessage();
                responseMessage.setMessage("No products given in request, please add some products!");
                return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
            }

        } else {
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage("Given project is not present or maybe READ-ONLY");
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
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
            //runForDailyElectricityProduced();
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

    public ResponseEntity<?> generateReport(Map<String, Object> projectObj, String username, boolean manualSyncUp) throws JsonProcessingException, ParseException {

        User userObj = userRepo.findByUsername(username);
        HashMap<String, Object> results = new HashMap<>();
        int numberOfdays = 0;
        // Process Object projectDetails
        ObjectMapper objectMapper = new ObjectMapper();
        String projectsJson = objectMapper.writeValueAsString(projectObj.get("projects"));

        // Deserialize the JSON string to a Project object
        Project projectDetails = objectMapper.readValue(projectsJson, Project.class);

        // Retrieve the string array from the map
        List<String> datesArray = (List<String>) projectObj.get("date");
        List<Date> convertedDates = new ArrayList<>();

        if(datesArray!=null) {
            // Convert date strings to Date objects and apply European time zone

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));

            for (String dateString : datesArray) {
                Date date = dateFormat.parse(dateString);
                convertedDates.add(date);
            }

        }
        if (userObj != null && userObj.getStatus().equals("ACTIVE")) {

            Project project = userObj.getProjects().stream()
                    .filter(p -> p.getProjectName().equals(projectDetails.getProjectName()))
                    .findFirst()
                    .orElse(null);
            // This is required to pass updated product after calculation for generateGraph function
            List<Product> updatedProducts = new ArrayList<>();

            // It means user clicked on any one of the product
            if (!projectDetails.getProducts().isEmpty() && !projectDetails.getStatus().equalsIgnoreCase("READ-ONLY")) {
                Product product = projectDetails.getProducts().get(0);
                if (!product.getStatus().equalsIgnoreCase("READ-ONLY")) {
                    Product productObj = project.getProducts().stream()
                            .filter(p -> p.getProductName().equals(product.getProductName()))
                            .findFirst()
                            .orElse(null);
                    List<PhotovoltaicCell> weatherInfo = productObj.getWeatherInfo();
                        // Custom Report Date Range implementation
                    if(datesArray !=null) {
                        if (datesArray.size() == 2) {
                            if (productObj.getWeatherInfo() != null) {
                                productObj.setWeatherInfo(new ArrayList<>());
                            }
                            Date fromDate = convertedDates.get(0);
                            Date toDate = convertedDates.get(1);
                            results = calculateElectricityProduced(product, productObj, 0, null, false, fromDate, toDate, false);
                            userRepo.save(userObj);
                        }
                    } else {
                        // If someone clicks Generate Report button after (for example) 6-7 calculations.
                        if (productObj.getWeatherInfo() != null) {
                            numberOfdays = productObj.getWeatherInfo().size();
                            if (numberOfdays < 30) {
                                results = calculateElectricityProduced(product, productObj, numberOfdays, weatherInfo, false, null, null,false);
                                userRepo.save(userObj);
                            }
                        } else {
                            results = calculateElectricityProduced(product, productObj, numberOfdays, null, false, null, null,false);
                            userRepo.save(userObj);
                        }
                    }
                    Product existingProduct = (Product) results.get("existingProduct");
                    updatedProducts.add(existingProduct);
                    generateExcelFileReport(updatedProducts);
                    ResponseEntity<?> responseStatusEmail = sendEmailWithAttachment(userObj, updatedProducts);
                    if (responseStatusEmail.getStatusCode() == HttpStatus.OK) {
                        existingProduct.setStatus("READ-ONLY");
                        userRepo.save(userObj);
                    }
                    if (results.size() > 0) {
                        ResponseMessage responseMessage = (ResponseMessage) results.get("responseMessage");
                        ResponseEntity<?> responseCode = (ResponseEntity<?>) results.get("response");
                        return new ResponseEntity<>(responseMessage, responseCode.getStatusCode());
                    } else {
                        ResponseMessage responseMessage = new ResponseMessage();
                        responseMessage.setMessage("No products are present to generate report or maybe report already generated for all products");
                        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
                    }
                } else {
                    ResponseMessage responseMessage = new ResponseMessage();
                    responseMessage.setMessage("Report Already Generated, Please Check your Email!");
                    return new ResponseEntity<>(responseMessage, HttpStatus.OK);
                }
            }
            // It means user clicked on Project, and it should generate report for all products
            else {
                if(project.getProducts()!=null && !project.getStatus().equalsIgnoreCase("READ-ONLY")){
                    List<Product> products = project.getProducts();
                    for(Product product: products){
                        if(!product.getStatus().equalsIgnoreCase("READ-ONLY")) {
                            List<PhotovoltaicCell> weatherInfo = product.getWeatherInfo();
                            if (weatherInfo != null) {
                                numberOfdays = weatherInfo.size();
                                if(manualSyncUp){
                                    results = calculateElectricityProduced(product, product, 0, weatherInfo,false,null,null, true);
                                    userRepo.save(userObj);
                                    ResponseEntity<?> responseCode = (ResponseEntity<?>) results.get("response");
                                    if (responseCode.getStatusCode() == HttpStatus.OK) {
                                        ResponseMessage responseMessage = new ResponseMessage();
                                        responseMessage.setMessage("Manual Sync up performed successfully!");
                                        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
                                    }
                                    else{
                                        ResponseMessage responseMessage = new ResponseMessage();
                                        responseMessage.setMessage("Manual Sync up already performed!");
                                        return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
                                    }
                                }
                                else if (numberOfdays < 30) {
                                    results = calculateElectricityProduced(product, product, numberOfdays, weatherInfo,false, null,null,false);
                                    userRepo.save(userObj);
                                }
                            } else {
                                if(manualSyncUp){
                                    results = calculateElectricityProduced(product, product, 0, null,false,null,null, true);
                                    userRepo.save(userObj);
                                    ResponseEntity<?> responseCode = (ResponseEntity<?>) results.get("response");
                                    if (responseCode.getStatusCode() == HttpStatus.OK) {
                                        ResponseMessage responseMessage = new ResponseMessage();
                                        responseMessage.setMessage("Manual Sync up performed successfully!");
                                        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
                                    }
                                    else{
                                        ResponseMessage responseMessage = new ResponseMessage();
                                        responseMessage.setMessage("Manual Sync up already performed!");
                                        return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
                                    }
                                }
                                else {
                                    results = calculateElectricityProduced(product, product, 0, null, false, null, null,false);
                                    userRepo.save(userObj);
                                }
                            }
                                ResponseEntity<?> responseCode = (ResponseEntity<?>) results.get("response");
                                if (responseCode.getStatusCode() == HttpStatus.OK) {
                                    product.setStatus("READ-ONLY");
                                    userRepo.save(userObj);
                                }

                        }
                    }
                    generateExcelFileReport(project.getProducts());
                    ResponseEntity<?> responseStatusEmail = sendEmailWithAttachment(userObj,project.getProducts());
                    if(responseStatusEmail.getStatusCode()==HttpStatus.OK){
                        project.setStatus("READ-ONLY");
                        userRepo.save(userObj);
                    }
                    if(results.size()>0) {
                        ResponseMessage responseMessage = (ResponseMessage) results.get("responseMessage");
                        ResponseEntity<?> responseCode = (ResponseEntity<?>) results.get("response");
                        return new ResponseEntity<>(responseMessage, responseCode.getStatusCode());
                    }
                    else{
                        ResponseMessage responseMessage = new ResponseMessage();
                        responseMessage.setMessage("No products are present to generate report or maybe report already generated for all products");
                        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
                    }
                }
                else{
                    ResponseMessage responseMessage = new ResponseMessage();
                    responseMessage.setMessage("No products are present to generate report or maybe report already generated for all products");
                    return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
                }
            }
        } else {
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage("Not valid User!");
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
    }

    // CRON JOB
    @Scheduled(cron = "0 0 0 * * ?")        // Syntax to run every night
    public void runForDailyElectricityProduced() {
        List<User> activeUsers = userRepo.showUsersbyStatus("ACTIVE");
        HashMap<String, Object> results = new HashMap<>();

        for (User activeUser : activeUsers) {
            if(activeUser.getProjects()!=null) {
                List<Project> activeProjects = activeUser.getProjects().stream()
                        .filter(project -> project != null && project.getStatus().equalsIgnoreCase("active"))
                        .collect(Collectors.toList());
                for (Project activeProject : activeProjects) {
                    if (activeProject.getProducts() != null) {

                        List<Product> activeProducts = activeProject.getProducts().stream()
                                .filter(product -> product != null && (product.getStatus() != null && product.getStatus().equalsIgnoreCase("ACTIVE")))
                                .collect(Collectors.toList());

                        for (Product activeProduct : activeProducts) {
                            if(activeProduct.getWeatherInfo()!=null) {
                                if (activeProduct.getWeatherInfo().size() < 30) {
                                    results = calculateElectricityProduced(activeProduct, activeProduct, 0, activeProduct.getWeatherInfo(), true, null,null, false);
                                    ResponseEntity<String> response = (ResponseEntity<String>) results.get("response");
                                    if (response.getStatusCode() == HttpStatus.OK) {
                                        userRepo.save(activeUser);
                                        System.out.println("Cron JOB ran successfully");
                                    }
                                    else if(response.getStatusCode()==HttpStatus.ALREADY_REPORTED){
                                        // This message will get printed for manual syncUp
                                        System.out.println(response.getBody());
                                    }
                                    if (activeProduct.getWeatherInfo().size() == 30) {
                                        activeProduct.setStatus("READ-ONLY");
                                        userRepo.save(activeUser);
                                    }
                                }
                                else{
                                    System.out.println("Data for 30 days calculated already");
                                }
                            }
                            else{
                                // This is for first time run when weatherInfo is null
                                results = calculateElectricityProduced(activeProduct, activeProduct, 0, null,true,null,null, false);
                                ResponseEntity<String> response = (ResponseEntity<String>) results.get("response");
                                if (response.getStatusCode() == HttpStatus.OK) {
                                    userRepo.save(activeUser);
                                    System.out.println("Cron JOB ran successfully");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public HashMap<String,Object> calculateElectricityProduced(Product product, Product existingProduct, int numberOfDaysLapsed, List<PhotovoltaicCell> weatherInfo, boolean CRON_FLAG, Date fromDate, Date toDate, boolean manualSyncUp) {
        HashMap<String, Object> parameters = new HashMap<>();
        List<PhotovoltaicCell> photovoltaicCells = new ArrayList<>();

        String apiUrl = "https://api.weatherbit.io/v2.0/history/daily?";
        String apiKey = "723c94998d0d40f1afa44885e6a3f1c2";

        double panelArea = new BigDecimal(String.valueOf(product.getArea())).doubleValue();
        double systemLoss = new BigDecimal(String.valueOf(product.getSystemLoss())).doubleValue();

        BigDecimal latitude = product.getLatitude();
        BigDecimal longitude = product.getLongitude();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String to = "";
        String from = "";
        if(fromDate!=null && toDate!=null){
            // Parse the input strings to LocalDateTime objects
            // Taking next date for API to fetch data for given range
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(toDate);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            Date nextDate = calendar.getTime();

            LocalDateTime fromDateTime = LocalDateTime.ofInstant(fromDate.toInstant(), ZoneId.systemDefault());
            LocalDateTime toDateTime = LocalDateTime.ofInstant(nextDate.toInstant(), ZoneId.systemDefault());
            to = toDateTime.format(formatter);
            from = fromDateTime.format(formatter);
        }
        else {
            LocalDate todayDate = LocalDate.now().minusDays(numberOfDaysLapsed);
            if (weatherInfo != null) {
                todayDate = LocalDate.parse(weatherInfo.get(0).getWeatherDate());
            }
            to = todayDate.format(formatter); // Today's Date
            LocalDate thirtyDaysBefore = LocalDate.now().minusDays(30);
            from = thirtyDaysBefore.format(formatter); // 30 days before date
        }
        boolean checkManualSyncUp = false;
        if(CRON_FLAG || manualSyncUp){
            List<PhotovoltaicCell> weatherInfoDetails =  product.getWeatherInfo();
            if(weatherInfoDetails!=null) {
                for (PhotovoltaicCell photoCell : product.getWeatherInfo()) {
                    LocalDate today = LocalDate.now();

                    // Get yesterday's date
                    LocalDate yesterday = today.minusDays(1);
                    String yesterdayFormatted = yesterday.format(formatter);

                    if (photoCell.getWeatherDate().equals(yesterdayFormatted)) {
                        checkManualSyncUp = true;
                        break;
                    }
                }
            }
            if(!checkManualSyncUp) {
                if(manualSyncUp || CRON_FLAG) {
                    LocalDate today = LocalDate.now();

                    // Get yesterday's date
                    LocalDate yesterday = today.minusDays(1);

                    // Get the day before yesterday's date

                    from = yesterday.format(formatter);
                    to = today.format(formatter);
                }
            }
            else{
                ResponseMessage responseMessage = new ResponseMessage();
                responseMessage.setMessage("Manual Sync up already performed!");
                ResponseEntity<?> response = new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
                parameters.put("response",response);
                return parameters;
            }
        }

        apiUrl += "lat=" + latitude + "&lon=" + longitude + "&start_date=" + from + "&end_date=" + to + "&key=" + apiKey;

        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, null, String.class);
        parameters.put("response", response);

        if (response.getStatusCode().is2xxSuccessful()) {
            String responseBody = response.getBody();
            JSONObject jsonObject = new JSONObject(responseBody);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                // DATA FROM WEATHER API
                int solarIrradiance = jsonArray.getJSONObject(i).getInt("max_dni");
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
            parameters.put("response", response);
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
                // Create a font with bold style and increased font size
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerFont.setFontHeightInPoints((short) 12); // Set the desired font size

                // Create a cell style for the header cells and set the font
                CellStyle headerCellStyle = workbook.createCellStyle();
                headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
                headerCellStyle.setFont(headerFont);

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

                // Apply the header cell style to the header row
                for (int i = 0; i <= 9; i++) {
                    Cell cell = headerRow2.getCell(i);
                    cell.setCellStyle(headerCellStyle);
                }

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

                        // Apply center alignment style to each cell in the row
                        for (int i = 0; i <= 9; i++) {
                            Cell cell = dataRow1.getCell(i);
                            CellStyle cellStyle = workbook.createCellStyle();
                            cellStyle.setAlignment(HorizontalAlignment.CENTER);
                            cell.setCellStyle(cellStyle);
                        }

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

    public ResponseEntity<?> sendEmailWithAttachment(User userObj, List<Product> products){
        String recipientEmail = userObj.getEmailId();
        for(Product product : products) {
            if(product.getWeatherInfo()!=null) {
                String subject = "Results of Photovoltaic System Product : " + product.getProductName();
                String body = "Here is your generated report for the product, Please find an attachment.";
                String attachmentFilePath = product.getProductName() + ".xlsx";
                emailService.sendEmailWithAttachment(recipientEmail, subject, body, attachmentFilePath);
            }
        }
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setMessage("Email Sent Successfully");
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    public ResponseEntity<?> getUpdatedProject(Project projectInfo, String username) {
        User userObj = userRepo.findByUsername(username);
        if (userObj != null && userObj.getStatus().equals("ACTIVE")) {
            Project project = userObj.getProjects().stream()
                    .filter(p -> p.getProjectName().equals(projectInfo.getProjectName()))
                    .findFirst()
                    .orElse(null);
            if (project != null) {
                project.setDescription(projectInfo.getDescription());
                userRepo.save(userObj);
                return new ResponseEntity<>(project, HttpStatus.OK);
            } else {
                ResponseMessage responseMessage = new ResponseMessage();
                responseMessage.setMessage("Project name is empty or Project might not present");
                return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
            }
        } else {
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage("Not valid User!");
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> deleteProject(Project projectInfo, String username) {
        User userObj = userRepo.findByUsername(username);
        if (userObj != null && userObj.getStatus().equals("ACTIVE")) {
            // To check if project is present or not in db
            Project project = userObj.getProjects().stream()
                    .filter(p -> p.getProjectName().equals(projectInfo.getProjectName()))
                    .findFirst()
                    .orElse(null);
            if (project != null) {
                List<Project> listOfProjects = userObj.getProjects();
                listOfProjects.removeIf(p -> p.getProjectName().equals(projectInfo.getProjectName()));
                userRepo.save(userObj);
                ResponseMessage responseMessage = new ResponseMessage();
                responseMessage.setMessage("Project deleted successfully");
                return new ResponseEntity<>(responseMessage, HttpStatus.OK);
            } else {
                ResponseMessage responseMessage = new ResponseMessage();
                responseMessage.setMessage("Project name is empty or Project might not present");
                return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
            }
        } else {
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage("Not valid User!");
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> generateGraphData(Project projectDetails, String username) throws FileNotFoundException {
        User userObj = userRepo.findByUsername(username);
        if (userObj != null && userObj.getStatus().equals("ACTIVE")) {
            // Assuming you have a data source containing weatherDate and electricityProduced values
            List<DataEntry> dataSource = getDataSource(projectDetails);

            // Create arrays to store weatherDate and electricityProduced values
            String[] weatherDates = new String[dataSource.size()];
            double[] electricityProducedValues = new double[dataSource.size()];

            // Iterate over the data source and populate the arrays
            for (int i = 0; i < dataSource.size(); i++) {
                DataEntry entry = dataSource.get(i);
                weatherDates[i] = entry.getWeatherDate();
                electricityProducedValues[i] = entry.getElectricityProduced();
            }
            // Return the arrays
            return new ResponseEntity<>(new DataArrays(weatherDates, electricityProducedValues), HttpStatus.OK);

        }
        else{
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage("Not valid User!");
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
    }

    public List<DataEntry> getDataSource(Project projectDetails) throws FileNotFoundException {
        List<DataEntry> dataSource = new ArrayList<>();
        String fileName = projectDetails.getProducts().get(0).getProductName() + ".xlsx";
        File file = new File(fileName);
        if(!file.exists()){
            generateExcelFileReport(projectDetails.getProducts());
        }
        try{
            ZipSecureFile.setMinInflateRatio(-1);
            // Load the workbook
            Workbook workbook = new XSSFWorkbook(file);

            // Get the first sheet
            Sheet sheet = workbook.getSheetAt(0);

            // Iterate over the rows
            Iterator<Row> rowIterator = sheet.iterator();
            if (rowIterator.hasNext()) {
                rowIterator.next(); // Move to the next row
            }
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                // Assuming weatherDate is in the first column (index 0) and electricityProduced is in the second column (index 1)
                Cell weatherDateCell = row.getCell(0);
                Cell electricityProducedCell = row.getCell(9);

                // Extract the values from the cells
                String weatherDate = weatherDateCell.getStringCellValue();
                double electricityProduced = electricityProducedCell.getNumericCellValue();

                // Create a DataEntry object and add it to the data source
                DataEntry entry = new DataEntry(weatherDate, electricityProduced);
                dataSource.add(entry);
            }

            // Close the workbook
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            throw new RuntimeException(e);
        }

        return dataSource;
    }

    public ResponseEntity<?> showActiveOrOldProjects(String type, String username) {
        User userObj = userRepo.findByUsername(username);
        if(userObj!=null){
            List<Project> listOfProjects = userObj.getProjects().stream()
                    .filter(project -> project != null && project.getStatus().equalsIgnoreCase(type))
                    .collect(Collectors.toList());
            if(listOfProjects!=null){
                return new ResponseEntity<>(listOfProjects, HttpStatus.OK);
            }
            else{
                ResponseMessage responseMessage = new ResponseMessage();
                responseMessage.setMessage("No "+type+" projects are present!");
                return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
            }
        }
        else {
            ResponseMessage responseMessage = new ResponseMessage();
            responseMessage.setMessage("Not valid User!");
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }
    }
}
