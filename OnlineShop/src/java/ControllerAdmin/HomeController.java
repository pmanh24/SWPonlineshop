/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package ControllerAdmin;

import DAL.EmployeeDAO;
import Models.Customers;
import Models.Employees;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Hieu
 */
public class HomeController extends HttpServlet {

    private static final String ERROR_INVALID_DATE_FORMAT = "Invalid date format. Use YYYY-MM-DD.";
    private static final String ERROR_END_DATE_BEFORE_START = "End date must be on or after start date.";
    private static final String SUCCESS_NO_SALES_DATA = "Product sales data is unavailable for the selected period.";

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try ( PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet HomeController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet HomeController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();

        LocalDate today = LocalDate.now();
        // Tính toán ngày bắt đầu là 7 ngày trước
        LocalDate startDate = today.minusDays(6); // 6 ngày trước + 1 ngày hôm nay = 7 ngày
        LocalDate endDate = today; // Ngày hiện tại

        Employees user = (Employees) session.getAttribute("employee");
        String role = (String) session.getAttribute("role");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/employee/login");
        } else {
            EmployeeDAO dao = new EmployeeDAO();
            // Retrieve date parameters from the request
            String startDateParam = request.getParameter("startDate");
            String endDateParam = request.getParameter("endDate");
            if (startDateParam == null || startDateParam.isEmpty()) {
                startDateParam = startDate.toString(); // Set default start date to 7 days ago
            }
            if (endDateParam == null || endDateParam.isEmpty()) {
                endDateParam = endDate.toString(); // Set default end date to today
            }

            // Validate and parse date parameters
            String errorMessage = validateDates(startDateParam, endDateParam);
            if (errorMessage != null) {
                request.setAttribute("error", errorMessage);
                // Use either provided or default date range
                request.setAttribute("startDate", (startDateParam != null) ? startDateParam : startDate.toString());
                request.setAttribute("endDate", (endDateParam != null) ? endDateParam : endDate.toString());

            } else {
                // Parse the date values if no validation errors
                if (startDateParam != null) {
                    startDate = LocalDate.parse(startDateParam, DATE_FORMATTER);
                }
                if (endDateParam != null) {
                    endDate = LocalDate.parse(endDateParam, DATE_FORMATTER);
                }
                
                // Fetch statistics
                int orderCount = dao.getOrderCount(startDate.toString(), endDate.toString());
                long totalRevenue = dao.getRevenueTotal(startDate.toString(), endDate.toString());
                int customerCount = dao.getNewCustomersCount(startDate.toString(), endDate.toString());
                int fbCount = dao.getFeedbackCount(startDate.toString(), endDate.toString());
                
                Map<LocalDate, Integer> orderTrends = dao.getOrderTrends(endDate.toString());

                // order status
                Map<String, Integer> orderStatus = dao.getOrderStatus(startDate, endDate);

                // Create a new map to hold modified product names
                Map<String, Integer> modifiedStatus = new HashMap<>();

                for (Map.Entry<String, Integer> entry : orderStatus.entrySet()) {
                    String modifiedStatusName = entry.getKey()
                            //                        .replace("/", "_") // Replace slashes with underscores
                            .replace("'", "_"); // Replace apostrophes with underscores

//                        .replace(" ", "_");
                    modifiedStatus.put(modifiedStatusName, entry.getValue());
                }
                int totalStatus = modifiedStatus.values().stream().mapToInt(Integer::intValue).sum();
                request.setAttribute("orderStatus", modifiedStatus); // Use the new map
                request.setAttribute("totalStatus", totalStatus);
                // end status

                // cate
                Map<String, Long> revenueCate = dao.getRevenues(startDate, endDate);

                // Create a new map to hold modified product names
                Map<String, Long> modifiedCate = new HashMap<>();

                for (Map.Entry<String, Long> entry : revenueCate.entrySet()) {
                    String modifiedCateName = entry.getKey()
                            //                        .replace("/", "_") // Replace slashes with underscores
                            .replace("'", "_"); // Replace apostrophes with underscores

//                        .replace(" ", "_");
                    modifiedCate.put(modifiedCateName, entry.getValue());
                }
                long totalCate = modifiedCate.values().stream().mapToLong(Long::longValue).sum();
                request.setAttribute("revenueCate", modifiedCate); // Use the new map
                request.setAttribute("totalCate", totalCate);
                // end cate

                // bought
                Map<String, Integer> customerBought = dao.getCustomerPurchaseStatistics(startDate, endDate);

                // Create a new map to hold modified product names
                Map<String, Integer> modifiedBought = new HashMap<>();

                for (Map.Entry<String, Integer> entry : customerBought.entrySet()) {
                    String modifiedBoughtName = entry.getKey()
                            //                        .replace("/", "_") // Replace slashes with underscores
                            .replace("'", "_"); // Replace apostrophes with underscores

//                        .replace(" ", "_");
                    modifiedBought.put(modifiedBoughtName, entry.getValue());
                }
                long totalBought = modifiedBought.values().stream().mapToInt(Integer::intValue).sum();
                request.setAttribute("customerBought", modifiedBought); // Use the new map
                request.setAttribute("totalBought", totalBought);
                // end bought

                 //rate
                Map<String, double[]> rateCate = dao.getAverageStarsAndVotesByCategory(startDate, endDate);

// Create a new map to hold modified category names and their average star ratings and vote counts
                Map<String, Double> modifiedRate = new HashMap<>();
                Map<String, Integer> voteCounts = new HashMap<>();

                for (Map.Entry<String, double[]> entry : rateCate.entrySet()) {
                    String modifiedRateName = entry.getKey()
                            .replace("'", "_"); // Replace apostrophes with underscores

                    double averageStar = entry.getValue()[0]; // Lấy số sao trung bình
                    int voteCount = (int) entry.getValue()[1]; // Lấy số lượng vote

                    modifiedRate.put(modifiedRateName, averageStar); // Lưu số sao trung bình
                    voteCounts.put(modifiedRateName, voteCount); // Lưu số lượng vote
                }

// Tính tổng số sao trung bình
                int totalRate = voteCounts.values().stream().mapToInt(Integer::intValue).sum();

// Lưu trữ vào request
                request.setAttribute("rateCate", modifiedRate); // Dữ liệu số sao trung bình
                request.setAttribute("voteCounts", voteCounts); // Dữ liệu số lượng vote
                request.setAttribute("totalRate", totalRate); // Tổng số sao trung bình
   
                
                
                // Set statistics in request attributes
                request.setAttribute("orderCount", orderCount);
                request.setAttribute("totalRevenue", totalRevenue);
                request.setAttribute("customerCount", customerCount);
                request.setAttribute("fbCount", fbCount);
//                request.setAttribute("customerTrends", customerTrends);

//                // Check if customer trends are empty
                if (orderTrends == null || orderTrends.isEmpty()) {
                    request.setAttribute("info", SUCCESS_NO_SALES_DATA);
                } else {
                    // Convert customer trends to JSON
                    Gson gson = new Gson();
                    String orderTrendsJson = gson.toJson(orderTrends);
                    request.setAttribute("orderTrends", orderTrendsJson);
                }

                // Set other attributes
//                request.setAttribute("totalRevenue", "50000000"); // Replace with actual totalRevenue retrieval
                request.setAttribute("startDate", startDate.toString());
                request.setAttribute("endDate", endDate.toString());

            }

            request.getRequestDispatcher("Views/admin/home.jsp").forward(request, response);
        }
    }

    private String validateDates(String startDateParam, String endDateParam) {
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();

        try {
            if (startDateParam != null && !startDateParam.isEmpty()) {
                startDate = LocalDate.parse(startDateParam, DATE_FORMATTER);
            }
            if (endDateParam != null && !endDateParam.isEmpty()) {
                endDate = LocalDate.parse(endDateParam, DATE_FORMATTER);
            }

            if (endDate.isBefore(startDate)) {
                return ERROR_END_DATE_BEFORE_START;
            }
        } catch (DateTimeParseException e) {
            return ERROR_INVALID_DATE_FORMAT;
        }

        return null; // No errors
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}