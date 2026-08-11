package com.example.registration.controller;

import com.example.registration.entity.Product;
import com.example.registration.entity.User;
import com.example.registration.security.JwtUtil;
import com.example.registration.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.example.registration.repository.UserRepository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AdminController(AdminService adminService, UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.adminService = adminService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginAdmin(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null && passwordEncoder.matches(password, user.getPasswordHash())) {
            if ("ADMIN".equals(user.getRole())) {
                String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getFullName());
                Map<String, Object> response = new HashMap<>();
                response.put("token", token);
                response.put("id", user.getId());
                response.put("fullName", user.getFullName());
                response.put("email", user.getEmail());
                response.put("role", user.getRole());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(403).body(Map.of("message", "Access denied. Admin role required."));
            }
        }
        return ResponseEntity.status(401).body(Map.of("message", "Invalid email or password"));
    }

    @PostMapping("/products")
    public ResponseEntity<?> addProduct(@RequestBody Map<String, Object> payload) {
        try {
            String name = (String) payload.get("name");
            String description = (String) payload.get("description");
            BigDecimal price = new BigDecimal(payload.get("price").toString());
            Integer stock = (Integer) payload.get("stock");
            Integer categoryId = (Integer) payload.get("categoryId");
            
            Product product = adminService.addProduct(name, description, price, stock, categoryId, null);
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer id) {
        try {
            adminService.deleteProduct(id);
            return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @RequestBody Map<String, String> payload) {
        try {
            String fullName = payload.get("fullName");
            String email = payload.get("email");
            String role = payload.get("role");
            User updatedUser = adminService.updateUser(id, fullName, email, role);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/analytics/daily")
    public ResponseEntity<?> getDailyRevenue(@RequestParam String date) {
        return ResponseEntity.ok(Map.of("revenue", adminService.getDailyRevenue(date)));
    }

    @GetMapping("/analytics/monthly")
    public ResponseEntity<?> getMonthlyRevenue(@RequestParam int year, @RequestParam int month) {
        return ResponseEntity.ok(Map.of("revenue", adminService.getMonthlyRevenue(year, month)));
    }

    @GetMapping("/analytics/yearly")
    public ResponseEntity<?> getYearlyRevenue(@RequestParam int year) {
        return ResponseEntity.ok(Map.of("revenue", adminService.getYearlyRevenue(year)));
    }

    @GetMapping("/analytics/overall")
    public ResponseEntity<?> getOverallRevenue() {
        return ResponseEntity.ok(Map.of("revenue", adminService.getOverallRevenue()));
    }
}
