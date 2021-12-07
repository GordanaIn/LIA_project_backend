package com.liserabackend.controllers;

import com.liserabackend.dto.*;
import com.liserabackend.entity.Company;
import com.liserabackend.entity.Employee;
import com.liserabackend.entity.InternshipAdvert;
import com.liserabackend.entity.User;
import com.liserabackend.exceptions.UseException;
import com.liserabackend.exceptions.UseExceptionType;
import com.liserabackend.services.CompanyServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders ="*")
@RequestMapping("/api/company")
public class CompanyController {
    private final CompanyServiceImpl companyService;

    @GetMapping()
    public List<CompanyDTO> getCompanies() {
        return companyService.getCompanies()
                .map(this::toCompanyDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{userId}")
    public CompanyDTO getCompanyByEmployeeUserId(@PathVariable("userId") String userId) throws UseException {
        return toCompanyDTO(companyService.getCompanyByEmployeeUserId(userId));
    }

    @PostMapping("/addCompany")
    public ResponseEntity<?> addCompany(@RequestBody CreateCompany createCompany) throws UseException {
          return ResponseEntity.ok(companyService.addCompany(createCompany)
                  .map(this::toCompanyDTO));
    }
    @PatchMapping("/updateCompanyInfo")
    public CompanyDTO updateCompanyInfo(@RequestBody CreateCompany company) throws UseException {
        return companyService.updateCompanyInformation(company)
                .map(this::toCompanyDTO)
                .orElseThrow(() -> new UseException(UseExceptionType.COMPANY_NOT_FOUND));
    }

    //employee save + edit

    @PostMapping()
    public Optional<InternshipAdvertDTO> addInternship(@RequestBody CreateInternship createInternship) throws UseException {
        return companyService.addInternship(createInternship).map(InternshipAdvertEntityToDTO::getInternshipAdvertDTO);
    }
    @PatchMapping("/updateInternship/{employeeId}")
    public InternshipAdvertDTO updateInternship(@PathVariable("employeeId") String employeeId,
                                                @RequestBody InternshipAdvert internship) throws UseException {
        return companyService.updateInternship(employeeId,internship).map(InternshipAdvertEntityToDTO::getInternshipAdvertDTO).get();
    }
    @DeleteMapping("/deleteInternship/{employerId}/{internshipId}")
    public void delete(@PathVariable("employerId") String employerId,
                       @PathVariable("internshipId") String internshipId) throws Exception {
        companyService.deleteInternship(employerId, internshipId);
    }

    private CompanyDTO toCompanyDTO(Company company) {

        return new CompanyDTO(
                company.getId(),
                company.getName(),
                company.getOrgNumber(),
                (List<Employee>) company.getEmployees()

        );
    }


}
